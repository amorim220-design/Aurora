class_name PlayerStateMachine
extends Node

## Máquina de estados finitos do protagonista.
## Controla transições entre movimentação, combate e magia.

enum State {
	IDLE,
	WALK,
	RUN,
	JUMP,
	FALL,
	LAND,
	DODGE,
	ATTACK,
	CAST_MAGIC,
	HURT,
	DEAD
}

signal state_changed(old_state: State, new_state: State)

var current_state: State = State.IDLE
var previous_state: State = State.IDLE

func change_state(new_state: State) -> void:
	if current_state == new_state:
		return
	if current_state == State.DEAD:
		return
		
	previous_state = current_state
	current_state = new_state
	state_changed.emit(previous_state, current_state)

func is_action_locked() -> bool:
	return current_state in [State.DODGE, State.ATTACK, State.CAST_MAGIC, State.LAND, State.DEAD]

func is_grounded_state() -> bool:
	return current_state in [State.IDLE, State.WALK, State.RUN, State.LAND]

func get_state_name() -> String:
	match current_state:
		State.IDLE: return "Parado"
		State.WALK: return "Caminhando"
		State.RUN: return "Correndo"
		State.JUMP: return "Pulando"
		State.FALL: return "Caindo"
		State.LAND: return "Aterrissando"
		State.DODGE: return "Esquivando"
		State.ATTACK: return "Atacando"
		State.CAST_MAGIC: return "Conjurando Magia"
		State.HURT: return "Ferido"
		State.DEAD: return "Derrotado"
		_: return "Desconhecido"
