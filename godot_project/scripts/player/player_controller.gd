class_name PlayerController
extends CharacterBody3D

## Controlador 3D principal do protagonista de Projeto Aurora.
## Implementa movimentação em 8 direções, esquiva, pulo, aterrissagem, combate e magia.

@export_group("Velocidade")
@export var walk_speed: float = 4.0
@export var run_speed: float = 8.0
@export var acceleration: float = 12.0
@export var friction: float = 10.0
@export var rotation_speed: float = 12.0

@export_group("Pulo e Gravidade")
@export var jump_velocity: float = 7.5
@export var gravity: float = 20.0
@export var landing_recovery_time: float = 0.22

@export_group("Esquiva (Dodge)")
@export var dodge_speed: float = 12.5
@export var dodge_duration: float = 0.45
@export var dodge_cooldown: float = 0.8

@export_group("Nós e Referências")
@export var camera_pivot: Node3D
@export var mesh_root: Node3D
@export var stats: CharacterStats
@export var state_machine: PlayerStateMachine
@export var magic_ability: Node
@export var ground_ray: RayCast3D
@export var anim_player: AnimationPlayer

# Variáveis internas de controle
var move_input: Vector2 = Vector2.ZERO
var is_running: bool = false
var dodge_timer: float = 0.0
var dodge_direction: Vector3 = Vector3.FORWARD
var can_dodge: bool = true
var was_in_air: bool = false
var landing_timer: float = 0.0
var attack_combo_index: int = 0
var attack_timer: float = 0.0

# Sinais para feedback audiovisual
signal stepped(foot_position: Vector3, is_running: bool)
signal jumped
signal landed(impact_velocity: float)
signal dodged(direction: Vector3)
signal attacked(combo_step: int)

func _ready() -> void:
	if not stats:
		stats = get_node_or_null("CharacterStats")
	if not state_machine:
		state_machine = get_node_or_null("PlayerStateMachine")
	if not magic_ability:
		magic_ability = get_node_or_null("MagicAbility")

func _physics_process(delta: float) -> void:
	_handle_timers(delta)
	_apply_gravity(delta)
	
	if state_machine.current_state == PlayerStateMachine.State.DODGE:
		_process_dodge_movement(delta)
	elif not state_machine.is_action_locked():
		_process_normal_movement(delta)
	
	move_and_slide()
	_check_landing_impact()

## Atualiza temporizadores de esquiva, combo e recuperação
func _handle_timers(delta: float) -> void:
	if dodge_timer > 0.0:
		dodge_timer -= delta
		if dodge_timer <= 0.0:
			stats.is_invulnerable = false
			state_machine.change_state(PlayerStateMachine.State.IDLE)
			
	if landing_timer > 0.0:
		landing_timer -= delta
		if landing_timer <= 0.0 and state_machine.current_state == PlayerStateMachine.State.LAND:
			state_machine.change_state(PlayerStateMachine.State.IDLE)

	if attack_timer > 0.0:
		attack_timer -= delta
		if attack_timer <= 0.0 and state_machine.current_state == PlayerStateMachine.State.ATTACK:
			state_machine.change_state(PlayerStateMachine.State.IDLE)

## Aplica gravidade contínua
func _apply_gravity(delta: float) -> void:
	if not is_on_floor():
		velocity.y -= gravity * delta
		if not was_in_air and velocity.y < 0.0:
			was_in_air = true
			if state_machine.current_state != PlayerStateMachine.State.DODGE:
				state_machine.change_state(PlayerStateMachine.State.FALL)

## Movimento durante esquiva (Roll / Dash com invulnerabilidade)
func _process_dodge_movement(delta: float) -> void:
	var target_vel = dodge_direction * dodge_speed
	velocity.x = target_vel.x
	velocity.z = target_vel.z
	
	# Rotaciona personagem rapidamente na direção da esquiva
	if mesh_root and dodge_direction.length_squared() > 0.01:
		var target_rot = atan2(-dodge_direction.x, -dodge_direction.z)
		mesh_root.rotation.y = lerp_angle(mesh_root.rotation.y, target_rot, rotation_speed * 1.5 * delta)

## Movimentação padrão em 8 direções e aceleração suave
func _process_normal_movement(delta: float) -> void:
	# Vetor de entrada baseado nos controles touch ou teclado
	var input_vec = _get_combined_input()
	
	# Direção calculada em relação à câmera
	var cam_transform = Transform3D.IDENTITY
	if camera_pivot:
		cam_transform = camera_pivot.global_transform
	
	var forward = -cam_transform.basis.z
	var right = cam_transform.basis.x
	forward.y = 0.0
	right.y = 0.0
	forward = forward.normalized()
	right = right.normalized()
	
	var target_dir = (right * input_vec.x + forward * input_vec.y).normalized()
	var current_speed = run_speed if is_running else walk_speed
	
	if target_dir.length_squared() > 0.01:
		velocity.x = lerp(velocity.x, target_dir.x * current_speed, acceleration * delta)
		velocity.z = lerp(velocity.z, target_dir.z * current_speed, acceleration * delta)
		
		# Suavização de rotação para encarar direção de movimento
		if mesh_root:
			var target_angle = atan2(-target_dir.x, -target_dir.z)
			mesh_root.rotation.y = lerp_angle(mesh_root.rotation.y, target_angle, rotation_speed * delta)
		
		if is_on_floor():
			state_machine.change_state(PlayerStateMachine.State.RUN if is_running else PlayerStateMachine.State.WALK)
	else:
		velocity.x = lerp(velocity.x, 0.0, friction * delta)
		velocity.z = lerp(velocity.z, 0.0, friction * delta)
		if is_on_floor() and state_machine.is_grounded_state():
			state_machine.change_state(PlayerStateMachine.State.IDLE)

## Detecta aterrissagem após queda ou pulo
func _check_landing_impact() -> void:
	if is_on_floor():
		if was_in_air:
			was_in_air = false
			landing_timer = landing_recovery_time
			state_machine.change_state(PlayerStateMachine.State.LAND)
			landed.emit(absf(velocity.y))

## Executa Pulo
func perform_jump() -> void:
	if is_on_floor() and not state_machine.is_action_locked():
		velocity.y = jump_velocity
		was_in_air = true
		state_machine.change_state(PlayerStateMachine.State.JUMP)
		jumped.emit()

## Executa Esquiva (Dodge) com frames de invencibilidade
func perform_dodge() -> void:
	if not can_dodge or state_machine.is_action_locked() or not is_on_floor():
		return
		
	var input_vec = _get_combined_input()
	var cam_transform = camera_pivot.global_transform if camera_pivot else Transform3D.IDENTITY
	var forward = -cam_transform.basis.z
	var right = cam_transform.basis.x
	forward.y = 0.0
	right.y = 0.0
	
	if input_vec.length_squared() > 0.05:
		dodge_direction = (right.normalized() * input_vec.x + forward.normalized() * input_vec.y).normalized()
	else:
		dodge_direction = -mesh_root.global_transform.basis.z if mesh_root else forward.normalized()
		
	can_dodge = false
	dodge_timer = dodge_duration
	stats.is_invulnerable = true
	state_machine.change_state(PlayerStateMachine.State.DODGE)
	dodged.emit(dodge_direction)
	
	# Permite nova esquiva após cooldown
	get_tree().create_timer(dodge_cooldown).timeout.connect(func(): can_dodge = true)

## Executa Ataque Melee Físico (Combo 1-2)
func perform_attack() -> void:
	if state_machine.is_action_locked() and state_machine.current_state != PlayerStateMachine.State.ATTACK:
		return
		
	attack_combo_index = (attack_combo_index % 2) + 1
	attack_timer = 0.55
	state_machine.change_state(PlayerStateMachine.State.ATTACK)
	attacked.emit(attack_combo_index)

## Dispara Habilidade Mágica (Aurora Burst)
func perform_magic() -> void:
	if state_machine.is_action_locked():
		return
	if magic_ability and magic_ability.has_method("activate_ability"):
		var success: bool = magic_ability.activate_ability(self)
		if success:
			state_machine.change_state(PlayerStateMachine.State.CAST_MAGIC)
			get_tree().create_timer(0.45).timeout.connect(func():
				if state_machine.current_state == PlayerStateMachine.State.CAST_MAGIC:
					state_machine.change_state(PlayerStateMachine.State.IDLE)
			)

## Recebe input do joystick virtual ou teclado físico
func set_virtual_joystick_input(vec: Vector2, run_flag: bool = false) -> void:
	move_input = vec
	is_running = run_flag or vec.length() > 0.85

func _get_combined_input() -> Vector2:
	if move_input.length_squared() > 0.01:
		return move_input
		
	var keyboard_vec = Vector2.ZERO
	if Input.is_action_pressed("move_right"): keyboard_vec.x += 1.0
	if Input.is_action_pressed("move_left"): keyboard_vec.x -= 1.0
	if Input.is_action_pressed("move_forward"): keyboard_vec.y += 1.0
	if Input.is_action_pressed("move_backward"): keyboard_vec.y -= 1.0
	
	if keyboard_vec.length_squared() > 0.01:
		is_running = Input.is_key_pressed(KEY_SHIFT)
		return keyboard_vec.normalized()
		
	return Vector2.ZERO
