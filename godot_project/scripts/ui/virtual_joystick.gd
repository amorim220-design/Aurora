class_name VirtualJoystick
extends Control

## Controle analógico virtual flutuante para telas touchscreen no Android.
## Suporta multi-touch, zona morta, raio máximo e normalização suave.

signal joystick_moved(output_vector: Vector2, is_running: bool)
signal joystick_released

@export_group("Configuração")
@export var max_radius: float = 75.0
@export var deadzone: float = 0.15
@export var run_threshold: float = 0.85
@export var is_floating: bool = true

@export_group("Elementos Visuais")
@export var base_ring: Control
@export var knob: Control

var touch_index: int = -1
var start_position: Vector2 = Vector2.ZERO
var current_output: Vector2 = Vector2.ZERO

func _ready() -> void:
	if not is_floating and base_ring:
		start_position = base_ring.position
	_reset_joystick()

func _input(event: InputEvent) -> void:
	if event is InputEventScreenTouch:
		if event.pressed:
			# Ativa apenas toques na metade esquerda da tela
			var screen_size = get_viewport_rect().size
			if event.position.x < (screen_size.x * 0.5):
				if touch_index == -1:
					touch_index = event.index
					if is_floating:
						global_position = event.position - size * 0.5
						visible = true
					_update_knob_position(event.position)
		else:
			if event.index == touch_index:
				_reset_joystick()
				
	elif event is InputEventScreenDrag:
		if event.index == touch_index:
			_update_knob_position(event.position)

func _update_knob_position(touch_pos: Vector2) -> void:
	var center = global_position + (size * 0.5)
	var offset = touch_pos - center
	var distance = offset.length()
	
	if distance > max_radius:
		offset = offset.normalized() * max_radius
		
	if knob:
		knob.position = (size * 0.5) + offset - (knob.size * 0.5)
		
	# Calcula vetor normalizado com zona morta
	var normalized_dist = offset.length() / max_radius
	if normalized_dist < deadzone:
		current_output = Vector2.ZERO
	else:
		var remapped = (normalized_dist - deadzone) / (1.0 - deadzone)
		var dir = offset.normalized()
		# Inverte Y para corresponder a Frente (Y positivo no mundo 3D / Godot)
		current_output = Vector2(dir.x, -dir.y) * remapped
		
	var is_sprinting = normalized_dist >= run_threshold
	joystick_moved.emit(current_output, is_sprinting)

func _reset_joystick() -> void:
	touch_index = -1
	current_output = Vector2.ZERO
	if knob:
		knob.position = (size * 0.5) - (knob.size * 0.5)
	if is_floating:
		visible = true
	joystick_released.emit()
