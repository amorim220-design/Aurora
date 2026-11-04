package com.example

import com.example.game.model.MovementState
import com.example.game.model.PlayerStats
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.sqrt

/**
 * Testes unitários para a mecânica de jogo de Projeto Aurora: Fase 1 - O Despertar.
 */
class ExampleUnitTest {

  @Test
  fun testPlayerStatsInitialization() {
    val stats = PlayerStats()
    assertEquals(100f, stats.maxHp, 0.001f)
    assertEquals(100f, stats.currentHp, 0.001f)
    assertEquals(100f, stats.maxMp, 0.001f)
    assertEquals(100f, stats.currentMp, 0.001f)
    assertFalse(stats.isInvulnerable)
  }

  @Test
  fun testManaConsumptionForAuroraBurst() {
    var stats = PlayerStats()
    val magicCost = 25f
    assertTrue(stats.currentMp >= magicCost)

    // Simula disparo da habilidade
    stats = stats.copy(currentMp = stats.currentMp - magicCost)
    assertEquals(75f, stats.currentMp, 0.001f)
  }

  @Test
  fun testDistanceCalculationToAltar() {
    val playerX = 0f
    val playerZ = 0f
    val altarZ = -65f

    val distance = sqrt(playerX * playerX + (playerZ - altarZ) * (playerZ - altarZ))
    assertEquals(65f, distance, 0.001f)

    // Quando o jogador se aproxima do altar
    val nearPlayerZ = -62f
    val nearDistance = sqrt(playerX * playerX + (nearPlayerZ - altarZ) * (nearPlayerZ - altarZ))
    assertEquals(3f, nearDistance, 0.001f)
    assertTrue("O jogador alcançou a clareira do despertar", nearDistance < 4.5f)
  }

  @Test
  fun testMovementStatesCoverage() {
    val states = MovementState.values()
    assertTrue(states.contains(MovementState.IDLE))
    assertTrue(states.contains(MovementState.WALK))
    assertTrue(states.contains(MovementState.RUN))
    assertTrue(states.contains(MovementState.JUMP))
    assertTrue(states.contains(MovementState.FALL))
    assertTrue(states.contains(MovementState.LAND))
    assertTrue(states.contains(MovementState.DODGE))
    assertTrue(states.contains(MovementState.ATTACK))
    assertTrue(states.contains(MovementState.CAST_MAGIC))
  }
}
