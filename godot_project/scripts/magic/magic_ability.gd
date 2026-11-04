class_name MagicAbility
extends Node3D

## Habilidade mágica inaugural do protagonista: "Aurora Arcana / Arcane Burst".
## Dispara uma explosão radial de energia mística azul com partículas luminosas.

signal ability_activated(ability_name: String, remaining_mana: float)
signal cooldown_updated(time_left: float, total_cooldown: float)
signal ability_ready

@export_group("Parâmetros da Habilidade")
@export var ability_name: String = "Aurora Burst"
@export var mana_cost: float = 25.0
@export var cooldown_time: float = 3.5
@export var damage_radius: float = 6.0
@export var base_damage: float = 45.0

@export_group("Instanciação de Efeitos")
@export var burst_particle_scene: PackedScene
@export var cast_marker: Marker3D

var current_cooldown: float = 0.0

func _process(delta: float) -> void:
	if current_cooldown > 0.0:
		current_cooldown -= delta
		cooldown_updated.emit(current_cooldown, cooldown_time)
		if current_cooldown <= 0.0:
			current_cooldown = 0.0
			ability_ready.emit()

## Tenta conjurar a magia verificando MP e tempo de recarga
func activate_ability(player: CharacterBody3D) -> bool:
	if current_cooldown > 0.0:
		return false
		
	var stats: CharacterStats = player.get_node_or_null("CharacterStats")
	if not stats:
		return false
		
	if not stats.consume_mana(mana_cost):
		return false
		
	# Inicia tempo de recarga
	current_cooldown = cooldown_time
	
	# Posição de origem da magia
	var spawn_pos: Vector3 = player.global_position + Vector3(0, 1.0, 0)
	if cast_marker:
		spawn_pos = cast_marker.global_position
		
	_spawn_visual_particles(spawn_pos)
	_apply_area_effect(spawn_pos)
	
	ability_activated.emit(ability_name, stats.current_mana)
	return true

## Gera partículas luminosas azuis e onda de choque
func _spawn_visual_particles(origin: Vector3) -> void:
	if burst_particle_scene:
		var burst_instance = burst_particle_scene.instantiate()
		get_tree().current_scene.add_child(burst_instance)
		if burst_instance is Node3D:
			burst_instance.global_position = origin

## Aplica dano em área a inimigos e destrutíveis na camada correspondente
func _apply_area_effect(origin: Vector3) -> void:
	var space_state = get_world_3d().direct_space_state
	var shape = SphereShape3D.new()
	shape.radius = damage_radius
	
	var query = PhysicsShapeQueryParameters3D.new()
	query.shape = shape
	query.transform = Transform3D(Basis(), origin)
	query.collision_mask = 4 # Layer 3 / 4 para inimigos e alvos mágicos
	
	var results = space_state.intersect_shape(query, 16)
	for hit in results:
		var collider = hit.get("collider")
		if collider and collider.has_method("take_damage"):
			collider.take_damage(base_damage)
