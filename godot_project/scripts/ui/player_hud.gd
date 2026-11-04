class_name PlayerHUD
extends CanvasLayer

## Interface de usuário (HUD) para mobile inspirada em jogos AAA de fantasia sombria.
## Conecta barras de vida e energia mágica, botões de ação e bússola de objetivo.

@export_group("Barras de Status")
@export var health_bar: ProgressBar
@export var mana_bar: ProgressBar
@export var health_label: Label
@export var mana_label: Label

@export_group("Botões de Ação")
@export var attack_button: TouchButton
@export var dodge_button: TouchButton
@export var jump_button: TouchButton
@export var magic_button: TouchButton

@export_group("Controles")
@export var virtual_joystick: VirtualJoystick
@export var player_controller: PlayerController
@export var cinematic_camera: CinematicCamera

@export_group("Objetivo e Notificações")
@export var objective_label: Label
@export var notification_label: Label
@export var compass_arrow: Control
@export var altar_target: Node3D

func _ready() -> void:
	_connect_controls()

func _connect_controls() -> void:
	if virtual_joystick and player_controller:
		virtual_joystick.joystick_moved.connect(player_controller.set_virtual_joystick_input)
		virtual_joystick.joystick_released.connect(func(): player_controller.set_virtual_joystick_input(Vector2.ZERO, false))
		
	if attack_button and player_controller:
		attack_button.button_executed.connect(player_controller.perform_attack)
		
	if dodge_button and player_controller:
		dodge_button.button_executed.connect(player_controller.perform_dodge)
		
	if jump_button and player_controller:
		jump_button.button_executed.connect(player_controller.perform_jump)
		
	if magic_button and player_controller:
		magic_button.button_executed.connect(player_controller.perform_magic)
		
	if player_controller and player_controller.stats:
		player_controller.stats.hp_changed.connect(update_health)
		player_controller.stats.mp_changed.connect(update_mana)
		
	if player_controller and player_controller.magic_ability:
		player_controller.magic_ability.cooldown_updated.connect(update_magic_cooldown)

func _process(_delta: float) -> void:
	_update_objective_compass()

func update_health(current: float, maximum: float) -> void:
	if health_bar:
		health_bar.max_value = maximum
		health_bar.value = current
	if health_label:
		health_label.text = "HP %d / %d" % [int(current), int(maximum)]

func update_mana(current: float, maximum: float) -> void:
	if mana_bar:
		mana_bar.max_value = maximum
		mana_bar.value = current
	if mana_label:
		mana_label.text = "MP %d / %d" % [int(current), int(maximum)]

func update_magic_cooldown(time_left: float, total_cooldown: float) -> void:
	if magic_button:
		magic_button.update_cooldown(time_left, total_cooldown)

func show_notification(message: String) -> void:
	if notification_label:
		notification_label.text = message
		notification_label.visible = true
		get_tree().create_timer(3.0).timeout.connect(func(): notification_label.visible = false)

func _update_objective_compass() -> void:
	if not player_controller or not altar_target or not compass_arrow:
		return
		
	var player_pos = player_controller.global_position
	var target_pos = altar_target.global_position
	var diff = target_pos - player_pos
	var distance = Vector2(diff.x, diff.z).length()
	
	if objective_label:
		objective_label.text = "Altar do Despertar: %.0fm" % distance
