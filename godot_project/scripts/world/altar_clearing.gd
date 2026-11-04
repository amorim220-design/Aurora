class_name AltarClearing
extends Node3D

## Monolito e Altar Ancestral na clareira ao final do percurso da Fase 1.
## Ativa a cutscene ou efeito de despertar do poder arcano.

signal altar_awakened

@export var rune_light: OmniLight3D
@export var crystal_mesh: MeshInstance3D
@export var trigger_area: Area3D

var is_awakened: bool = false
var float_time: float = 0.0

func _ready() -> void:
	if trigger_area:
		trigger_area.body_entered.connect(_on_player_approached)

func _process(delta: float) -> void:
	float_time += delta
	# Flutuação e rotação suave do cristal de aurora
	if crystal_mesh:
		crystal_mesh.position.y = 1.6 + sin(float_time * 2.0) * 0.15
		crystal_mesh.rotate_y(delta * 0.8)

func _on_player_approached(body: Node3D) -> void:
	if is_awakened or not (body is PlayerController):
		return
		
	is_awakened = true
	if rune_light:
		rune_light.light_energy = 4.0
		
	altar_awakened.emit()
