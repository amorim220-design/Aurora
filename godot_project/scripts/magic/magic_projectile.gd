class_name MagicProjectile
extends Node3D

## Gerencia o ciclo de vida e dissipação da explosão de energia arcana azul.

@export var lifetime: float = 1.2
@export var omni_light: OmniLight3D
@export var particles: GPUParticles3D

var elapsed_time: float = 0.0

func _ready() -> void:
	if particles:
		particles.emitting = true
	if omni_light:
		omni_light.light_energy = 3.5

func _process(delta: float) -> void:
	elapsed_time += delta
	
	# Efeito pulsante de atenuação da luz luminosa
	if omni_light:
		omni_light.light_energy = lerp(3.5, 0.0, elapsed_time / lifetime)
		
	if elapsed_time >= lifetime:
		queue_free()
