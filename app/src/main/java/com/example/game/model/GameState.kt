package com.example.game.model

enum class MovementState(val label: String) {
  IDLE("Parado"),
  WALK("Caminhando"),
  RUN("Correndo"),
  JUMP("Pulando"),
  FALL("Caindo"),
  LAND("Aterrissando"),
  DODGE("Esquivando"),
  ATTACK("Atacando"),
  CAST_MAGIC("Conjurando Magia")
}

data class PlayerStats(
  val currentHp: Float = 100f,
  val maxHp: Float = 100f,
  val currentMp: Float = 100f,
  val maxMp: Float = 100f,
  val isInvulnerable: Boolean = false
)

data class MagicSkillState(
  val name: String = "Aurora Burst",
  val manaCost: Float = 25f,
  val cooldownTotal: Float = 3.5f,
  val currentCooldown: Float = 0f,
  val isReady: Boolean = true
)

data class ObjectiveState(
  val title: String = "Fase 1: O Despertar",
  val description: String = "Atravesse a Floresta Antiga e alcance o Altar na Clareira Misteriosa.",
  val distanceToAltar: Float = 65f,
  val hasReachedAltar: Boolean = false,
  val isAwakened: Boolean = false
)
