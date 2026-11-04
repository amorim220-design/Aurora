class_name TouchButton
extends TextureButton

## Botão tátil customizado para telas de toque Android.
## Inclui animação de pressão (scale feedback), feedback de cooldown e vibração haptics.

signal button_executed

@export var action_name: String = ""
@export var scale_on_press: float = 0.88
@export var haptic_feedback: bool = true
@export var cooldown_overlay: TextureProgressBar

var is_in_cooldown: bool = false

func _ready() -> void:
	button_down.connect(_on_button_down)
	button_up.connect(_on_button_up)
	pivot_offset = size * 0.5

func _on_button_down() -> void:
	scale = Vector2(scale_on_press, scale_on_press)
	if haptic_feedback and OS.has_feature("mobile"):
		Input.vibrate_handheld(25)

func _on_button_up() -> void:
	scale = Vector2.ONE
	if not is_in_cooldown:
		button_executed.emit()

func update_cooldown(time_left: float, total_time: float) -> void:
	if cooldown_overlay:
		if time_left > 0.0:
			is_in_cooldown = true
			cooldown_overlay.visible = true
			cooldown_overlay.value = (time_left / total_time) * 100.0
		else:
			is_in_cooldown = false
			cooldown_overlay.visible = false
