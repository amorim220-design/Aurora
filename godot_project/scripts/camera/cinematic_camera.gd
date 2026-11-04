class_name CinematicCamera
extends Node3D

## Câmera cinematográfica em terceira pessoa por cima do ombro.
## Suporta amortecimento suave, toque mobile para rotação orbital,
## prevenção de colisão com árvores/ruínas e transição de modos cinematográficos.

@export_group("Alvo e Acompanhamento")
@export var target: Node3D
@export var follow_smoothness: float = 10.0
@export var rotation_speed_mobile: float = 0.005
@export var rotation_speed_mouse: float = 0.003

@export_group("Posicionamento")
@export var default_distance: float = 4.2
@export var min_distance: float = 1.2
@export var max_distance: float = 6.0
@export var shoulder_offset: Vector3 = Vector3(0.5, 1.45, 0.0)
@export var min_pitch: float = -60.0
@export var max_pitch: float = 45.0

@export_group("Colisão")
@export var collision_margin: float = 0.25
@export var collision_mask: int = 1 # Camada de colisão do mundo

@export_group("Nós Filhos")
@export var spring_arm: SpringArm3D
@export var camera_3d: Camera3D

# Estado interno
var current_yaw: float = 0.0
var current_pitch: float = -12.0
var current_distance: float = 4.2
var is_cinematic_mode: bool = false
var cinematic_timer: float = 0.0

func _ready() -> void:
	if not spring_arm:
		spring_arm = get_node_or_null("SpringArm3D")
	if not camera_3d and spring_arm:
		camera_3d = spring_arm.get_node_or_null("Camera3D")
		
	if spring_arm:
		spring_arm.spring_length = default_distance
		spring_arm.margin = collision_margin
		current_distance = default_distance

func _physics_process(delta: float) -> void:
	if not target:
		return
		
	# Acompanha a posição do protagonista suavemente
	var target_pos = target.global_position + shoulder_offset
	global_position = global_position.lerp(target_pos, follow_smoothness * delta)
	
	if is_cinematic_mode:
		_process_cinematic_behavior(delta)
	else:
		_apply_rotation()

## Aplica rotação orbital baseada em yaw e pitch
func _apply_rotation() -> void:
	rotation.y = deg_to_rad(current_yaw)
	rotation.x = deg_to_rad(current_pitch)
	rotation.z = 0.0

## Comportamento dinâmico em modo cinematográfico (pan sutil e profundidade)
func _process_cinematic_behavior(delta: float) -> void:
	cinematic_timer += delta * 0.4
	var auto_yaw = current_yaw + sin(cinematic_timer) * 15.0
	var auto_pitch = -8.0 + cos(cinematic_timer * 0.7) * 4.0
	rotation.y = deg_to_rad(auto_yaw)
	rotation.x = deg_to_rad(auto_pitch)
	
	if spring_arm:
		spring_arm.spring_length = lerp(spring_arm.spring_length, default_distance + 1.2, delta * 2.0)

## Recebe arrasto de toque na metade direita da tela (Android Touch Orbit)
func rotate_camera_touch(relative_delta: Vector2) -> void:
	if is_cinematic_mode:
		return
		
	current_yaw -= relative_delta.x * rotation_speed_mobile * 100.0
	current_pitch = clampf(current_pitch - (relative_delta.y * rotation_speed_mobile * 100.0), min_pitch, max_pitch)

## Ajusta distância da câmera (zoom dinâmico)
func adjust_zoom(amount: float) -> void:
	current_distance = clampf(current_distance + amount, min_distance, max_distance)
	if spring_arm:
		spring_arm.spring_length = current_distance

## Alterna entre câmera padrão de combate e enquadramento cinematográfico
func toggle_cinematic_mode() -> void:
	is_cinematic_mode = !is_cinematic_mode
	if not is_cinematic_mode and spring_arm:
		spring_arm.spring_length = current_distance
