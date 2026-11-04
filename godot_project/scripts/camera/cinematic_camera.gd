class_name CinematicCamera
extends Node3D

## Câmera em perspectiva para Projeto Aurora (Godot Engine 4.3).
## Suporta:
## 1. Modo Isométrico Roguelite (estilo Wizard of Legend 2: ângulo elevado 42°, distância 7.5m)
## 2. Modo Sobre o Ombro (3ª Pessoa clássica: combate imersivo)
## 3. Modo Cinematográfico (enquadramento dinâmico e orbital para chefões como Vorghan)

enum CameraMode {
	ISOMETRIC,
	OVER_THE_SHOULDER,
	CINEMATIC
}

@export_group("Modo de Visualização")
@export var current_mode: CameraMode = CameraMode.ISOMETRIC

@export_group("Alvo e Acompanhamento")
@export var target: Node3D
@export var follow_smoothness: float = 10.0
@export var rotation_speed_mobile: float = 0.005
@export var rotation_speed_mouse: float = 0.003

@export_group("Posicionamento Isométrico")
@export var isometric_pitch: float = -42.0
@export var isometric_distance: float = 7.5
@export var isometric_offset: Vector3 = Vector3(0.0, 0.8, -0.8)

@export_group("Posicionamento Sobre o Ombro")
@export var default_distance: float = 4.2
@export var min_distance: float = 1.2
@export var max_distance: float = 9.0
@export var shoulder_offset: Vector3 = Vector3(0.55, 1.45, 0.0)
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
var current_pitch: float = -42.0
var current_distance: float = 7.5
var cinematic_timer: float = 0.0

func _ready() -> void:
	if not spring_arm:
		spring_arm = get_node_or_null("SpringArm3D")
	if not camera_3d and spring_arm:
		camera_3d = spring_arm.get_node_or_null("Camera3D")
		
	set_camera_mode(current_mode)

func _physics_process(delta: float) -> void:
	if not target:
		return
		
	match current_mode:
		CameraMode.ISOMETRIC:
			_process_isometric_mode(delta)
		CameraMode.OVER_THE_SHOULDER:
			_process_over_the_shoulder_mode(delta)
		CameraMode.CINEMATIC:
			_process_cinematic_mode(delta)

func _process_isometric_mode(delta: float) -> void:
	var target_pos = target.global_position + isometric_offset
	global_position = global_position.lerp(target_pos, follow_smoothness * delta)
	rotation.y = deg_to_rad(current_yaw)
	rotation.x = deg_to_rad(isometric_pitch)
	rotation.z = 0.0
	if spring_arm:
		spring_arm.spring_length = lerp(spring_arm.spring_length, isometric_distance, delta * 8.0)

func _process_over_the_shoulder_mode(delta: float) -> void:
	var target_pos = target.global_position + shoulder_offset
	global_position = global_position.lerp(target_pos, follow_smoothness * delta)
	rotation.y = deg_to_rad(current_yaw)
	rotation.x = deg_to_rad(current_pitch)
	rotation.z = 0.0
	if spring_arm:
		spring_arm.spring_length = lerp(spring_arm.spring_length, default_distance, delta * 8.0)

func _process_cinematic_mode(delta: float) -> void:
	var target_pos = target.global_position + Vector3(0.0, 1.6, 0.0)
	global_position = global_position.lerp(target_pos, follow_smoothness * 0.7 * delta)
	
	cinematic_timer += delta * 0.45
	var auto_yaw = current_yaw + sin(cinematic_timer) * 18.0
	var auto_pitch = -10.0 + cos(cinematic_timer * 0.6) * 5.0
	rotation.y = deg_to_rad(auto_yaw)
	rotation.x = deg_to_rad(auto_pitch)
	
	if spring_arm:
		spring_arm.spring_length = lerp(spring_arm.spring_length, default_distance + 1.8, delta * 3.0)

## Alterna ciclicamente entre Isométrica, Sobre o Ombro e Cinemática
func cycle_camera_mode() -> void:
	match current_mode:
		CameraMode.ISOMETRIC:
			set_camera_mode(CameraMode.OVER_THE_SHOULDER)
		CameraMode.OVER_THE_SHOULDER:
			set_camera_mode(CameraMode.CINEMATIC)
		CameraMode.CINEMATIC:
			set_camera_mode(CameraMode.ISOMETRIC)

func set_camera_mode(mode: CameraMode) -> void:
	current_mode = mode
	match current_mode:
		CameraMode.ISOMETRIC:
			current_pitch = isometric_pitch
			current_distance = isometric_distance
		CameraMode.OVER_THE_SHOULDER:
			current_pitch = -14.0
			current_distance = default_distance
		CameraMode.CINEMATIC:
			current_distance = default_distance + 1.8

## Recebe arrasto de toque na metade direita da tela (Android Touch Orbit)
func rotate_camera_touch(relative_delta: Vector2) -> void:
	if current_mode == CameraMode.CINEMATIC:
		return
		
	current_yaw -= relative_delta.x * rotation_speed_mobile * 100.0
	if current_mode == CameraMode.OVER_THE_SHOULDER:
		current_pitch = clampf(current_pitch - (relative_delta.y * rotation_speed_mobile * 100.0), min_pitch, max_pitch)

## Ajusta distância da câmera (zoom dinâmico)
func adjust_zoom(amount: float) -> void:
	current_distance = clampf(current_distance + amount, min_distance, max_distance)
	if spring_arm:
		spring_arm.spring_length = current_distance
