class_name CharacterStats
extends Node

## Gerenciamento de atributos e status do protagonista em fantasia sombria.
## Emite sinais para a interface (HUD) e sistemas de combate.

signal hp_changed(current: float, maximum: float)
signal mp_changed(current: float, maximum: float)
signal player_died
signal ability_cast_success(ability_name: String, cost: float)
signal ability_cast_failed(reason: String)

@export_group("Vida")
@export var max_health: float = 100.0
@export var current_health: float = 100.0

@export_group("Energia Arcana")
@export var max_mana: float = 100.0
@export var current_mana: float = 100.0
@export var mana_regen_rate: float = 8.0 # Recuperação de MP por segundo

@export_group("Combate")
@export var attack_power: float = 25.0
@export var is_invulnerable: bool = false

func _ready() -> void:
	current_health = max_health
	current_mana = max_mana
	hp_changed.emit(current_health, max_health)
	mp_changed.emit(current_mana, max_mana)

func _process(delta: float) -> void:
	# Regeneração passiva de energia mágica
	if current_mana < max_mana:
		current_mana = minf(max_mana, current_mana + (mana_regen_rate * delta))
		mp_changed.emit(current_mana, max_mana)

func take_damage(amount: float) -> void:
	if is_invulnerable:
		return
		
	current_health = maxf(0.0, current_health - amount)
	hp_changed.emit(current_health, max_health)
	
	if current_health <= 0.0:
		player_died.emit()

func heal(amount: float) -> void:
	current_health = minf(max_health, current_health + amount)
	hp_changed.emit(current_health, max_health)

func consume_mana(amount: float) -> bool:
	if current_mana >= amount:
		current_mana -= amount
		mp_changed.emit(current_mana, max_mana)
		ability_cast_success.emit("Arcane Burst", amount)
		return true
	else:
		ability_cast_failed.emit("Energia insuficiente!")
		return false
