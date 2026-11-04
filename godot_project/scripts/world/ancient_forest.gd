class_name AncientForest
extends Node3D

## Controlador do bioma da Floresta Antiga.
## Gerencia iluminação crepuscular, densidade de névoa atmosférica e áreas de exploração.

@export var world_environment: WorldEnvironment
@export var directional_light: DirectionalLight3D
@export var clearing_trigger: Area3D

signal player_entered_clearing

func _ready() -> void:
	if clearing_trigger:
		clearing_trigger.body_entered.connect(_on_clearing_entered)

func _on_clearing_entered(body: Node3D) -> void:
	if body is PlayerController:
		player_entered_clearing.emit()
