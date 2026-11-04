package com.example.game.engine

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.example.game.audio.SoundManager
import com.example.game.model.MovementState
import com.example.game.model.PlayerStats
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Motor de renderização 3D nativo em OpenGL ES 3.0 para Projeto Aurora: Fase 1.
 * Renderiza floresta antiga com névoa volumétrica, ruínas, clareira, personagem em 3ª pessoa,
 * animações procedurais e partículas arcanas.
 */
class Game3DRenderer(
  private val context: Context,
  val soundManager: SoundManager
) : GLSurfaceView.Renderer {

  // Callbacks para sincronização com a UI em Jetpack Compose
  var onStatsChanged: ((PlayerStats) -> Unit)? = null
  var onMagicCooldownChanged: ((Float, Float) -> Unit)? = null
  var onStateChanged: ((MovementState) -> Unit)? = null
  var onDistanceChanged: ((Float, Boolean) -> Unit)? = null

  // Programas de Shader
  private var shaderProgram: Int = 0
  private var particleProgram: Int = 0

  // Handles de Shader Principal
  private var uMVPMatrixHandle: Int = 0
  private var uMVMatrixHandle: Int = 0
  private var uModelMatrixHandle: Int = 0
  private var uColorHandle: Int = 0
  private var uEmissionColorHandle: Int = 0
  private var uLightPosHandle: Int = 0
  private var aPositionHandle: Int = 0
  private var aNormalHandle: Int = 0

  // Handles de Shader de Partículas
  private var uParticleMVPHandle: Int = 0
  private var uParticleColorHandle: Int = 0
  private var aParticlePositionHandle: Int = 0

  // Matrizes de Transformação
  private val projectionMatrix = FloatArray(16)
  private val viewMatrix = FloatArray(16)
  private val modelMatrix = FloatArray(16)
  private val mvMatrix = FloatArray(16)
  private val mvpMatrix = FloatArray(16)

  // Estado do Jogador
  var playerX = 0f
  var playerY = 0f
  var playerZ = 0f
  private var playerVelocityX = 0f
  private var playerVelocityY = 0f
  private var playerVelocityZ = 0f
  private var playerRotationY = 180f
  var currentState = MovementState.IDLE
    private set

  // Status do Protagonista
  var currentHp = 100f
  val maxHp = 100f
  var currentMp = 100f
  val maxMp = 100f
  private val manaRegenRate = 9f

  // Habilidade Mágica (Aurora Burst)
  val magicCooldownTotal = 3.5f
  var magicCooldownLeft = 0f
  private val magicCost = 25f

  // Esquiva e Combate
  private var dodgeTimer = 0f
  private val dodgeDuration = 0.45f
  private var dodgeDirX = 0f
  private var dodgeDirZ = -1f
  private var isInvulnerable = false
  private var attackTimer = 0f
  private var magicAnimTimer = 0f
  private var landingTimer = 0f
  private var wasInAir = false

  // Modos de Câmera (Isométrica Roguelite Wizard of Legend 2, Sobre o Ombro e Cinemática)
  enum class CameraMode(val label: String) {
    ISOMETRIC("Isométrica"),
    OVER_THE_SHOULDER("3ª Pessoa"),
    CINEMATIC("Cinematográfica")
  }

  var cameraMode: CameraMode = CameraMode.ISOMETRIC
  var cameraYaw = 0f
  var cameraPitch = 38f
  var cameraDistance = 7.0f
  var isCinematicMode = false
    get() = cameraMode == CameraMode.CINEMATIC
    set(value) {
      field = value
      if (value) cameraMode = CameraMode.CINEMATIC
    }
  private var smoothCamX = 0f
  private var smoothCamY = 5f
  private var smoothCamZ = 6f
  private var cinematicTimer = 0f

  // Entrada de Movimento Virtual (do analógico)
  var inputMoveX = 0f
  var inputMoveZ = 0f
  var inputIsRunning = false

  // Geometrias e Buffers
  private lateinit var cubeVertexBuffer: FloatBuffer
  private lateinit var cubeNormalBuffer: FloatBuffer
  private lateinit var cubeIndexBuffer: ShortBuffer

  private lateinit var cylinderVertexBuffer: FloatBuffer
  private lateinit var cylinderNormalBuffer: FloatBuffer

  private lateinit var planeVertexBuffer: FloatBuffer
  private lateinit var planeNormalBuffer: FloatBuffer

  // Partículas de Magia
  private class ArcaneParticle(
    var x: Float, var y: Float, var z: Float,
    var vx: Float, var vy: Float, var vz: Float,
    var life: Float, var maxLife: Float,
    var size: Float
  )
  private val activeParticles = mutableListOf<ArcaneParticle>()
  private var particleBuffer: FloatBuffer? = null

  // Cenário da Floresta (Posições estáticas das árvores e ruínas)
  data class ForestTree(val x: Float, val z: Float, val height: Float, val isPine: Boolean)
  private val trees = listOf(
    ForestTree(-6f, -10f, 9f, false),
    ForestTree(7f, -14f, 10f, true),
    ForestTree(-9f, -24f, 8.5f, false),
    ForestTree(8f, -28f, 11f, true),
    ForestTree(-12f, -38f, 10.5f, true),
    ForestTree(10f, -42f, 9f, false),
    ForestTree(-8f, -52f, 11.5f, true),
    ForestTree(7f, -56f, 8.5f, false),
    ForestTree(-14f, -16f, 7.5f, false),
    ForestTree(15f, -22f, 9.5f, true),
    ForestTree(-16f, -46f, 12f, false),
    ForestTree(14f, -50f, 10f, true),
    ForestTree(-11f, -62f, 9f, true),
    ForestTree(12f, -64f, 8.5f, false)
  )

  // Altar da Clareira (Local de Despertar)
  val altarZ = -65f
  var hasReachedAltar = false
  private var crystalRotY = 0f

  // Tempo delta
  private var lastFrameTime = System.nanoTime()
  private var walkCycleTime = 0f
  private var footstepTimer = 0f

  override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
    GLES30.glClearColor(0.047f, 0.070f, 0.121f, 1.0f) // Twilight dark fantasy sky
    GLES30.glEnable(GLES30.GL_DEPTH_TEST)
    GLES30.glDepthFunc(GLES30.GL_LEQUAL)
    GLES30.glEnable(GLES30.GL_CULL_FACE)
    GLES30.glCullFace(GLES30.GL_BACK)

    initShaders()
    initGeometries()
  }

  override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
    GLES30.glViewport(0, 0, width, height)
    val ratio = width.toFloat() / height.coerceAtLeast(1)
    Matrix.perspectiveM(projectionMatrix, 0, 60.0f, ratio, 0.15f, 180.0f)
  }

  override fun onDrawFrame(gl: GL10?) {
    val currentTime = System.nanoTime()
    val delta = ((currentTime - lastFrameTime) / 1_000_000_000.0f).coerceIn(0.001f, 0.05f)
    lastFrameTime = currentTime

    updateGameLogic(delta)

    GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
    setupCamera()

    GLES30.glUseProgram(shaderProgram)

    // Luz da lua/crepúsculo + ponto de luz arcana
    val lightPos = floatArrayOf(20f, 40f, 15f)
    GLES30.glUniform3fv(uLightPosHandle, 1, lightPos, 0)

    renderEnvironment()
    renderStoneRuins()
    renderAwakeningAltar()
    renderCharacter()

    renderArcaneParticles()
  }

  // --- LÓGICA DE JOGO, FÍSICA E COLISÃO ---

  private fun updateGameLogic(delta: Float) {
    // Regeneração de Mana
    if (currentMp < maxMp) {
      currentMp = (currentMp + manaRegenRate * delta).coerceAtMost(maxMp)
    }

    // Cooldown da Magia
    if (magicCooldownLeft > 0f) {
      magicCooldownLeft = (magicCooldownLeft - delta).coerceAtLeast(0f)
      onMagicCooldownChanged?.invoke(magicCooldownLeft, magicCooldownTotal)
    }

    // Temporizadores de Ação
    if (dodgeTimer > 0f) {
      dodgeTimer -= delta
      if (dodgeTimer <= 0f) {
        isInvulnerable = false
        currentState = MovementState.IDLE
      }
    }

    if (landingTimer > 0f) {
      landingTimer -= delta
      if (landingTimer <= 0f && currentState == MovementState.LAND) {
        currentState = MovementState.IDLE
      }
    }

    if (attackTimer > 0f) {
      attackTimer -= delta
      if (attackTimer <= 0f && currentState == MovementState.ATTACK) {
        currentState = MovementState.IDLE
      }
    }

    if (magicAnimTimer > 0f) {
      magicAnimTimer -= delta
      if (magicAnimTimer <= 0f && currentState == MovementState.CAST_MAGIC) {
        currentState = MovementState.IDLE
      }
    }

    // Gravidade
    if (playerY > 0f) {
      playerVelocityY -= 22f * delta
      playerY += playerVelocityY * delta
      if (playerY <= 0f) {
        playerY = 0f
        playerVelocityY = 0f
        if (wasInAir) {
          wasInAir = false
          landingTimer = 0.25f
          currentState = MovementState.LAND
          soundManager.playLanding()
        }
      } else if (!wasInAir && playerVelocityY < 0f && currentState != MovementState.DODGE) {
        wasInAir = true
        currentState = MovementState.FALL
      }
    }

    // Processamento de Movimentação
    if (currentState == MovementState.DODGE) {
      val dodgeSpeed = 13.5f
      playerVelocityX = dodgeDirX * dodgeSpeed
      playerVelocityZ = dodgeDirZ * dodgeSpeed
      playerX += playerVelocityX * delta
      playerZ += playerVelocityZ * delta
    } else if (currentState != MovementState.ATTACK && currentState != MovementState.CAST_MAGIC && currentState != MovementState.LAND) {
      processInputMovement(delta)
    }

    // Resolução de Colisões com Árvores e Ruínas
    resolveCollisions()

    // Atualização de Partículas
    updateParticles(delta)

    // Rotação do Cristal Místico
    crystalRotY += delta * 45f

    // Checagem do Altar na Clareira
    val distToAltar = sqrt(playerX * playerX + (playerZ - altarZ) * (playerZ - altarZ))
    if (distToAltar < 4.5f && !hasReachedAltar) {
      hasReachedAltar = true
      spawnAltarAwakeningEffect()
    }
    onDistanceChanged?.invoke(distToAltar, hasReachedAltar)

    // Notificação de Estado e Stats para a UI
    onStateChanged?.invoke(currentState)
    onStatsChanged?.invoke(PlayerStats(currentHp, maxHp, currentMp, maxMp, isInvulnerable))
  }

  private fun processInputMovement(delta: Float) {
    if (abs(inputMoveX) > 0.05f || abs(inputMoveZ) > 0.05f) {
      // Direção relativa ao ângulo da câmera
      val radYaw = Math.toRadians(cameraYaw.toDouble()).toFloat()
      val cosY = cos(radYaw)
      val sinY = sin(radYaw)

      val worldDirX = inputMoveX * cosY + inputMoveZ * sinY
      val worldDirZ = -inputMoveX * sinY + inputMoveZ * cosY

      val speed = if (inputIsRunning) 8.0f else 4.2f
      val accel = 12f
      playerVelocityX += (worldDirX * speed - playerVelocityX) * accel * delta
      playerVelocityZ += (worldDirZ * speed - playerVelocityZ) * accel * delta

      playerX += playerVelocityX * delta
      playerZ += playerVelocityZ * delta

      // Suavização do ângulo do personagem
      val targetAngle = Math.toDegrees(atan2(-worldDirX.toDouble(), -worldDirZ.toDouble())).toFloat()
      playerRotationY = lerpAngle(playerRotationY, targetAngle, 12f * delta)

      if (playerY <= 0.01f) {
        currentState = if (inputIsRunning) MovementState.RUN else MovementState.WALK
        walkCycleTime += delta * (if (inputIsRunning) 14f else 8f)
        footstepTimer += delta
        if (footstepTimer > (if (inputIsRunning) 0.28f else 0.45f)) {
          footstepTimer = 0f
          soundManager.playFootstep()
        }
      }
    } else {
      playerVelocityX *= (1f - 10f * delta).coerceAtLeast(0f)
      playerVelocityZ *= (1f - 10f * delta).coerceAtLeast(0f)
      if (playerY <= 0.01f && currentState in listOf(MovementState.WALK, MovementState.RUN)) {
        currentState = MovementState.IDLE
      }
    }
  }

  private fun resolveCollisions() {
    // Colisão com troncos de árvores
    for (tree in trees) {
      val dx = playerX - tree.x
      val dz = playerZ - tree.z
      val dist = sqrt(dx * dx + dz * dz)
      val minRadius = 1.35f
      if (dist < minRadius && dist > 0.001f) {
        val overlap = minRadius - dist
        playerX += (dx / dist) * overlap
        playerZ += (dz / dist) * overlap
      }
    }

    // Colisão com os pilares das ruínas (z = -20 e z = -38)
    val pillars = listOf(
      Pair(-2.5f, -19.5f), Pair(2.5f, -19.5f),
      Pair(-3.5f, -38f), Pair(3.5f, -38f)
    )
    for (p in pillars) {
      val dx = playerX - p.first
      val dz = playerZ - p.second
      val dist = sqrt(dx * dx + dz * dz)
      val minRadius = 1.2f
      if (dist < minRadius && dist > 0.001f) {
        val overlap = minRadius - dist
        playerX += (dx / dist) * overlap
        playerZ += (dz / dist) * overlap
      }
    }
  }

  // --- AÇÕES DO JOGADOR DISPARADAS PELA UI ---

  fun executeJump() {
    if (playerY <= 0.01f && currentState !in listOf(MovementState.DODGE, MovementState.ATTACK, MovementState.CAST_MAGIC)) {
      playerVelocityY = 8.5f
      wasInAir = true
      currentState = MovementState.JUMP
      soundManager.playJump()
    }
  }

  fun executeDodge() {
    if (dodgeTimer <= 0f && currentState !in listOf(MovementState.ATTACK, MovementState.CAST_MAGIC)) {
      dodgeTimer = dodgeDuration
      isInvulnerable = true
      currentState = MovementState.DODGE

      val rad = Math.toRadians(playerRotationY.toDouble()).toFloat()
      dodgeDirX = -sin(rad)
      dodgeDirZ = -cos(rad)

      soundManager.playDodge()

      // Gera partículas espectrais azuis no rastro de esquiva
      for (i in 0 until 18) {
        activeParticles.add(
          ArcaneParticle(
            playerX + (Random.nextFloat() - 0.5f) * 0.8f,
            playerY + 0.8f + (Random.nextFloat() - 0.5f) * 0.6f,
            playerZ + (Random.nextFloat() - 0.5f) * 0.8f,
            -dodgeDirX * 2.5f + (Random.nextFloat() - 0.5f),
            0.5f + Random.nextFloat(),
            -dodgeDirZ * 2.5f + (Random.nextFloat() - 0.5f),
            0.4f, 0.4f, 0.25f
          )
        )
      }
    }
  }

  fun executeAttack() {
    if (currentState !in listOf(MovementState.DODGE, MovementState.CAST_MAGIC)) {
      attackTimer = 0.48f
      currentState = MovementState.ATTACK
      soundManager.playAttackSlash()
    }
  }

  fun executeMagicAbility() {
    if (magicCooldownLeft <= 0f && currentMp >= magicCost && currentState != MovementState.DODGE) {
      currentMp -= magicCost
      magicCooldownLeft = magicCooldownTotal
      magicAnimTimer = 0.55f
      currentState = MovementState.CAST_MAGIC
      soundManager.playMagicBurst()

      // Emissão de 100+ partículas luminosas azuis em 360 graus
      val rad = Math.toRadians(playerRotationY.toDouble()).toFloat()
      val originX = playerX - sin(rad) * 0.9f
      val originY = playerY + 1.2f
      val originZ = playerZ - cos(rad) * 0.9f

      for (i in 0 until 110) {
        val theta = Random.nextFloat() * 2f * PI.toFloat()
        val phi = (Random.nextFloat() - 0.5f) * PI.toFloat()
        val speed = 3.5f + Random.nextFloat() * 7.0f
        val vx = cos(phi) * sin(theta) * speed
        val vy = sin(phi) * speed + 1.5f
        val vz = cos(phi) * cos(theta) * speed
        val life = 0.6f + Random.nextFloat() * 0.5f
        activeParticles.add(
          ArcaneParticle(originX, originY, originZ, vx, vy, vz, life, life, 0.35f + Random.nextFloat() * 0.25f)
        )
      }
    }
  }

  private fun spawnAltarAwakeningEffect() {
    soundManager.playMagicBurst()
    for (i in 0 until 180) {
      val theta = Random.nextFloat() * 2f * PI.toFloat()
      val speed = 2.0f + Random.nextFloat() * 8.0f
      val vx = cos(theta) * speed
      val vy = 3.0f + Random.nextFloat() * 8.0f
      val vz = sin(theta) * speed
      val life = 1.2f + Random.nextFloat() * 0.8f
      activeParticles.add(
        ArcaneParticle(0f, 1.8f, altarZ, vx, vy, vz, life, life, 0.45f)
      )
    }
  }

  // --- CÂMERA EM TERCEIRA PESSOA ---

  private fun setupCamera() {
    val targetX: Float
    val targetY: Float
    val targetZ: Float

    when (cameraMode) {
      CameraMode.CINEMATIC -> {
        cinematicTimer += 0.015f
        targetX = playerX
        targetY = playerY + 1.5f
        targetZ = playerZ

        val dist = 5.5f
        val eyeX = playerX + cos(cinematicTimer) * dist
        val eyeY = playerY + 2.4f + sin(cinematicTimer * 0.5f) * 0.8f
        val eyeZ = playerZ + sin(cinematicTimer) * dist

        Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, targetX, targetY, targetZ, 0f, 1f, 0f)
        return
      }
      CameraMode.ISOMETRIC -> {
        // Câmera Isométrica Roguelite (estilo Wizard of Legend 2)
        // Ângulo elevado com projeção suave de perspectiva
        val isoPitch = 42f
        val isoDist = 7.5f
        val radYaw = Math.toRadians(cameraYaw.toDouble()).toFloat()
        val radPitch = Math.toRadians(isoPitch.toDouble()).toFloat()

        targetX = playerX
        targetY = playerY + 0.9f
        targetZ = playerZ - 0.8f // Pequeno avanço para visão de combate frontal

        val idealCamX = targetX + sin(radYaw) * cos(radPitch) * isoDist
        val idealCamY = targetY + sin(radPitch) * isoDist
        val idealCamZ = targetZ + cos(radYaw) * cos(radPitch) * isoDist

        smoothCamX += (idealCamX - smoothCamX) * 0.18f
        smoothCamY += (idealCamY - smoothCamY) * 0.18f
        smoothCamZ += (idealCamZ - smoothCamZ) * 0.18f

        Matrix.setLookAtM(viewMatrix, 0, smoothCamX, smoothCamY, smoothCamZ, targetX, targetY, targetZ, 0f, 1f, 0f)
        return
      }
      CameraMode.OVER_THE_SHOULDER -> {
        // Câmera sobre o ombro (Over-The-Shoulder em 3ª pessoa)
        val radYaw = Math.toRadians(cameraYaw.toDouble()).toFloat()
        val radPitch = Math.toRadians(cameraPitch.toDouble()).toFloat()

        val cosP = cos(radPitch)
        val sinP = sin(radPitch)
        val cosY = cos(radYaw)
        val sinY = sin(radYaw)

        val shoulderOffsetX = 0.55f * cosY
        val shoulderOffsetZ = -0.55f * sinY

        targetX = playerX + shoulderOffsetX
        targetY = playerY + 1.45f
        targetZ = playerZ + shoulderOffsetZ

        val idealCamX = targetX + sinY * cosP * cameraDistance
        val idealCamY = targetY + sinP * cameraDistance
        val idealCamZ = targetZ + cosY * cosP * cameraDistance

        // Amortecimento suave
        smoothCamX += (idealCamX - smoothCamX) * 0.22f
        smoothCamY += (idealCamY - smoothCamY) * 0.22f
        smoothCamZ += (idealCamZ - smoothCamZ) * 0.22f

        Matrix.setLookAtM(viewMatrix, 0, smoothCamX, smoothCamY, smoothCamZ, targetX, targetY, targetZ, 0f, 1f, 0f)
        return
      }
    }
  }

  // --- RENDERIZAÇÃO DE CENÁRIOS E MODELOS ---

  private fun renderEnvironment() {
    // Solo da floresta (grama sombria com musgo e trilha central)
    Matrix.setIdentityM(modelMatrix, 0)
    Matrix.translateM(modelMatrix, 0, 0f, -0.05f, -35f)
    Matrix.scaleM(modelMatrix, 0, 70f, 1f, 110f)
    drawMesh(planeVertexBuffer, planeNormalBuffer, 6, floatArrayOf(0.08f, 0.11f, 0.09f, 1f), floatArrayOf(0f, 0f, 0f, 0f))

    // Trilha sinuosa central mais clara
    Matrix.setIdentityM(modelMatrix, 0)
    Matrix.translateM(modelMatrix, 0, 0f, 0.01f, -35f)
    Matrix.scaleM(modelMatrix, 0, 4.5f, 1f, 110f)
    drawMesh(planeVertexBuffer, planeNormalBuffer, 6, floatArrayOf(0.14f, 0.13f, 0.11f, 1f), floatArrayOf(0f, 0f, 0f, 0f))

    // Árvores antigas e pinheiros sombrios
    for (tree in trees) {
      // Tronco
      Matrix.setIdentityM(modelMatrix, 0)
      Matrix.translateM(modelMatrix, 0, tree.x, tree.height * 0.4f, tree.z)
      Matrix.scaleM(modelMatrix, 0, 1.1f, tree.height * 0.8f, 1.1f)
      drawMesh(cylinderVertexBuffer, cylinderNormalBuffer, 36, floatArrayOf(0.18f, 0.13f, 0.10f, 1f), floatArrayOf(0f, 0f, 0f, 0f))

      if (tree.isPine) {
        // Três camadas cônicas de copa de pinheiro
        for (i in 0..2) {
          Matrix.setIdentityM(modelMatrix, 0)
          Matrix.translateM(modelMatrix, 0, tree.x, tree.height * 0.6f + (i * 2.2f), tree.z)
          val s = (3.2f - (i * 0.8f))
          Matrix.scaleM(modelMatrix, 0, s, 2.5f, s)
          drawMesh(cubeVertexBuffer, cubeNormalBuffer, 36, floatArrayOf(0.05f, 0.13f, 0.08f, 1f), floatArrayOf(0f, 0f, 0f, 0f))
        }
      } else {
        // Carvalho antigo com copa frondosa arredondada
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, tree.x, tree.height * 0.85f, tree.z)
        Matrix.scaleM(modelMatrix, 0, 4.5f, 3.8f, 4.5f)
        drawMesh(cubeVertexBuffer, cubeNormalBuffer, 36, floatArrayOf(0.07f, 0.15f, 0.10f, 1f), floatArrayOf(0f, 0f, 0f, 0f))
      }
    }
  }

  private fun renderStoneRuins() {
    // Arco de Pedra ancestral em z = -20
    val archZ = -20f
    // Pilar Esquerdo
    Matrix.setIdentityM(modelMatrix, 0)
    Matrix.translateM(modelMatrix, 0, -2.5f, 2.8f, archZ)
    Matrix.scaleM(modelMatrix, 0, 1.3f, 5.6f, 1.3f)
    drawMesh(cubeVertexBuffer, cubeNormalBuffer, 36, floatArrayOf(0.24f, 0.27f, 0.32f, 1f), floatArrayOf(0.05f, 0.2f, 0.4f, 0.4f))

    // Pilar Direito
    Matrix.setIdentityM(modelMatrix, 0)
    Matrix.translateM(modelMatrix, 0, 2.5f, 2.8f, archZ)
    Matrix.scaleM(modelMatrix, 0, 1.3f, 5.6f, 1.3f)
    drawMesh(cubeVertexBuffer, cubeNormalBuffer, 36, floatArrayOf(0.24f, 0.27f, 0.32f, 1f), floatArrayOf(0.05f, 0.2f, 0.4f, 0.4f))

    // Dintel Superior
    Matrix.setIdentityM(modelMatrix, 0)
    Matrix.translateM(modelMatrix, 0, 0f, 5.8f, archZ)
    Matrix.scaleM(modelMatrix, 0, 6.5f, 1.1f, 1.5f)
    drawMesh(cubeVertexBuffer, cubeNormalBuffer, 36, floatArrayOf(0.22f, 0.25f, 0.30f, 1f), floatArrayOf(0f, 0f, 0f, 0f))

    // Pilares Quebrados com Runas em z = -38
    val ruinZ = -38f
    Matrix.setIdentityM(modelMatrix, 0)
    Matrix.translateM(modelMatrix, 0, -3.5f, 1.8f, ruinZ)
    Matrix.scaleM(modelMatrix, 0, 1.2f, 3.6f, 1.2f)
    drawMesh(cubeVertexBuffer, cubeNormalBuffer, 36, floatArrayOf(0.25f, 0.28f, 0.34f, 1f), floatArrayOf(0.1f, 0.5f, 0.9f, 0.8f))

    Matrix.setIdentityM(modelMatrix, 0)
    Matrix.translateM(modelMatrix, 0, 3.5f, 2.3f, ruinZ)
    Matrix.scaleM(modelMatrix, 0, 1.2f, 4.6f, 1.2f)
    drawMesh(cubeVertexBuffer, cubeNormalBuffer, 36, floatArrayOf(0.25f, 0.28f, 0.34f, 1f), floatArrayOf(0.1f, 0.5f, 0.9f, 0.8f))
  }

  private fun renderAwakeningAltar() {
    // Altar Circular na Clareira em z = -65
    Matrix.setIdentityM(modelMatrix, 0)
    Matrix.translateM(modelMatrix, 0, 0f, 0.35f, altarZ)
    Matrix.scaleM(modelMatrix, 0, 9.5f, 0.7f, 9.5f)
    drawMesh(cylinderVertexBuffer, cylinderNormalBuffer, 36, floatArrayOf(0.21f, 0.24f, 0.30f, 1f), floatArrayOf(0.08f, 0.35f, 0.65f, 0.5f))

    // 4 Monólitos de sustentação
    val angles = listOf(45f, 135f, 225f, 315f)
    for (deg in angles) {
      val r = Math.toRadians(deg.toDouble()).toFloat()
      val px = cos(r) * 3.8f
      val pz = altarZ + sin(r) * 3.8f
      Matrix.setIdentityM(modelMatrix, 0)
      Matrix.translateM(modelMatrix, 0, px, 2.0f, pz)
      Matrix.scaleM(modelMatrix, 0, 0.9f, 4.0f, 0.9f)
      drawMesh(cubeVertexBuffer, cubeNormalBuffer, 36, floatArrayOf(0.19f, 0.22f, 0.28f, 1f), floatArrayOf(0.15f, 0.6f, 1.0f, 0.8f))
    }

    // Cristal Flutuante de Aurora
    Matrix.setIdentityM(modelMatrix, 0)
    val floatY = 2.4f + sin(crystalRotY * 0.05f) * 0.25f
    Matrix.translateM(modelMatrix, 0, 0f, floatY, altarZ)
    Matrix.rotateM(modelMatrix, 0, crystalRotY, 0f, 1f, 0f)
    Matrix.rotateM(modelMatrix, 0, 25f, 1f, 0f, 0f)
    Matrix.scaleM(modelMatrix, 0, 1.1f, 1.9f, 1.1f)
    drawMesh(cubeVertexBuffer, cubeNormalBuffer, 36, floatArrayOf(0.22f, 0.82f, 1.0f, 1f), floatArrayOf(0.35f, 0.85f, 1.0f, 1.8f))
  }

  private fun renderCharacter() {
    val charBaseX = playerX
    val charBaseY = playerY
    val charBaseZ = playerZ

    // Rotação do personagem
    val rotMatrix = FloatArray(16)
    Matrix.setIdentityM(rotMatrix, 0)
    Matrix.translateM(rotMatrix, 0, charBaseX, charBaseY, charBaseZ)
    Matrix.rotateM(rotMatrix, 0, playerRotationY, 0f, 1f, 0f)

    // Efeito de compressão na aterrissagem
    val landSquashY = if (currentState == MovementState.LAND) 0.78f else 1.0f

    // 1. Torso com Armadura Sombria
    val torsoM = rotMatrix.clone()
    Matrix.translateM(torsoM, 0, 0f, 1.15f * landSquashY, 0f)
    Matrix.scaleM(torsoM, 0, 0.72f, 0.85f * landSquashY, 0.45f)
    val bodyColor = if (isInvulnerable) floatArrayOf(0.3f, 0.75f, 1.0f, 1f) else floatArrayOf(0.14f, 0.18f, 0.26f, 1f)
    drawModel(torsoM, cubeVertexBuffer, cubeNormalBuffer, 36, bodyColor, floatArrayOf(0f, 0f, 0f, 0f))

    // 2. Capa Sombria (ondulação dinâmica)
    val capeM = rotMatrix.clone()
    val capeTilt = if (currentState == MovementState.RUN) 32f else 10f
    Matrix.translateM(capeM, 0, 0f, 1.1f * landSquashY, 0.28f)
    Matrix.rotateM(capeM, 0, capeTilt, 1f, 0f, 0f)
    Matrix.scaleM(capeM, 0, 0.78f, 1.15f, 0.12f)
    drawModel(capeM, cubeVertexBuffer, cubeNormalBuffer, 36, floatArrayOf(0.08f, 0.10f, 0.16f, 1f), floatArrayOf(0f, 0f, 0f, 0f))

    // 3. Cabeça e Capuz
    val headM = rotMatrix.clone()
    Matrix.translateM(headM, 0, 0f, 1.82f * landSquashY, 0f)
    Matrix.scaleM(headM, 0, 0.42f, 0.45f, 0.42f)
    drawModel(headM, cubeVertexBuffer, cubeNormalBuffer, 36, floatArrayOf(0.12f, 0.15f, 0.22f, 1f), floatArrayOf(0.1f, 0.4f, 0.9f, 0.4f))

    // 4. Pernas e Passadas (Balanço sinusoidal)
    val legSwing = if (currentState in listOf(MovementState.WALK, MovementState.RUN)) sin(walkCycleTime) * 35f else 0f

    // Perna Esquerda
    val legLM = rotMatrix.clone()
    Matrix.translateM(legLM, 0, -0.22f, 0.45f * landSquashY, 0f)
    Matrix.rotateM(legLM, 0, legSwing, 1f, 0f, 0f)
    Matrix.scaleM(legLM, 0, 0.25f, 0.85f * landSquashY, 0.28f)
    drawModel(legLM, cubeVertexBuffer, cubeNormalBuffer, 36, floatArrayOf(0.10f, 0.12f, 0.18f, 1f), floatArrayOf(0f, 0f, 0f, 0f))

    // Perna Direita
    val legRM = rotMatrix.clone()
    Matrix.translateM(legRM, 0, 0.22f, 0.45f * landSquashY, 0f)
    Matrix.rotateM(legRM, 0, -legSwing, 1f, 0f, 0f)
    Matrix.scaleM(legRM, 0, 0.25f, 0.85f * landSquashY, 0.28f)
    drawModel(legRM, cubeVertexBuffer, cubeNormalBuffer, 36, floatArrayOf(0.10f, 0.12f, 0.18f, 1f), floatArrayOf(0f, 0f, 0f, 0f))

    // 5. Espada Rúnica Mística
    val swordM = rotMatrix.clone()
    if (currentState == MovementState.ATTACK) {
      val attackProgress = 1.0f - (attackTimer / 0.48f)
      val slashAngle = sin(attackProgress * PI.toFloat()) * 95f
      Matrix.translateM(swordM, 0, 0.45f, 1.1f, -0.55f)
      Matrix.rotateM(swordM, 0, -35f + slashAngle, 0f, 1f, 0f)
      Matrix.scaleM(swordM, 0, 0.12f, 1.5f, 0.08f)
      drawModel(swordM, cubeVertexBuffer, cubeNormalBuffer, 36, floatArrayOf(0.3f, 0.85f, 1.0f, 1f), floatArrayOf(0.4f, 0.9f, 1.0f, 2.5f))
    } else {
      // Espada embainhada nas costas
      Matrix.translateM(swordM, 0, 0.24f, 1.35f, 0.32f)
      Matrix.rotateM(swordM, 0, 35f, 0f, 0f, 1f)
      Matrix.scaleM(swordM, 0, 0.1f, 1.4f, 0.08f)
      drawModel(swordM, cubeVertexBuffer, cubeNormalBuffer, 36, floatArrayOf(0.55f, 0.65f, 0.75f, 1f), floatArrayOf(0.15f, 0.65f, 1.0f, 0.7f))
    }
  }

  private fun drawModel(m: FloatArray, vBuf: FloatBuffer, nBuf: FloatBuffer, count: Int, col: FloatArray, emi: FloatArray) {
    Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, m, 0)
    Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvMatrix, 0)

    GLES30.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, mvpMatrix, 0)
    GLES30.glUniformMatrix4fv(uMVMatrixHandle, 1, false, mvMatrix, 0)
    GLES30.glUniformMatrix4fv(uModelMatrixHandle, 1, false, m, 0)
    GLES30.glUniform4fv(uColorHandle, 1, col, 0)
    GLES30.glUniform4fv(uEmissionColorHandle, 1, emi, 0)

    vBuf.position(0)
    GLES30.glVertexAttribPointer(aPositionHandle, 3, GLES30.GL_FLOAT, false, 0, vBuf)
    GLES30.glEnableVertexAttribArray(aPositionHandle)

    nBuf.position(0)
    GLES30.glVertexAttribPointer(aNormalHandle, 3, GLES30.GL_FLOAT, false, 0, nBuf)
    GLES30.glEnableVertexAttribArray(aNormalHandle)

    GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, count)
  }

  private fun drawMesh(vBuf: FloatBuffer, nBuf: FloatBuffer, count: Int, col: FloatArray, emi: FloatArray) {
    drawModel(modelMatrix, vBuf, nBuf, count, col, emi)
  }

  // --- SISTEMA DE PARTÍCULAS ARCANAS ---

  private fun updateParticles(delta: Float) {
    val it = activeParticles.iterator()
    while (it.hasNext()) {
      val p = it.next()
      p.life -= delta
      if (p.life <= 0f) {
        it.remove()
      } else {
        p.x += p.vx * delta
        p.y += p.vy * delta
        p.z += p.vz * delta
        p.vy -= 4.0f * delta // gravidade suave
      }
    }
  }

  private fun renderArcaneParticles() {
    if (activeParticles.isEmpty()) return

    GLES30.glUseProgram(particleProgram)
    GLES30.glEnable(GLES30.GL_BLEND)
    GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE) // Aditivo para brilho mágico
    GLES30.glDepthMask(false)

    val pData = FloatArray(activeParticles.size * 3)
    var idx = 0
    for (p in activeParticles) {
      pData[idx++] = p.x
      pData[idx++] = p.y
      pData[idx++] = p.z
    }

    if (particleBuffer == null || particleBuffer!!.capacity() < pData.size) {
      particleBuffer = ByteBuffer.allocateDirect(pData.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
    }
    particleBuffer!!.clear()
    particleBuffer!!.put(pData)
    particleBuffer!!.position(0)

    Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
    GLES30.glUniformMatrix4fv(uParticleMVPHandle, 1, false, mvpMatrix, 0)
    GLES30.glUniform4f(uParticleColorHandle, 0.25f, 0.78f, 1.0f, 0.9f)

    GLES30.glVertexAttribPointer(aParticlePositionHandle, 3, GLES30.GL_FLOAT, false, 0, particleBuffer)
    GLES30.glEnableVertexAttribArray(aParticlePositionHandle)

    GLES30.glDrawArrays(GLES30.GL_POINTS, 0, activeParticles.size)

    GLES30.glDepthMask(true)
    GLES30.glDisable(GLES30.GL_BLEND)
  }

  // --- INICIALIZAÇÃO DE SHADERS E GEOMETRIA ---

  private fun initShaders() {
    val vertexShaderCode = """#version 300 es
      layout(location = 0) in vec3 a_Position;
      layout(location = 1) in vec3 a_Normal;

      uniform mat4 u_MVPMatrix;
      uniform mat4 u_MVMatrix;
      uniform mat4 u_ModelMatrix;

      out vec3 v_Position;
      out vec3 v_Normal;
      out float v_Distance;

      void main() {
        vec4 worldPos = u_ModelMatrix * vec4(a_Position, 1.0);
        v_Position = worldPos.xyz;
        v_Normal = mat3(u_ModelMatrix) * a_Normal;
        vec4 eyePos = u_MVMatrix * vec4(a_Position, 1.0);
        v_Distance = length(eyePos.xyz);
        gl_Position = u_MVPMatrix * vec4(a_Position, 1.0);
      }
    """.trimIndent()

    val fragmentShaderCode = """#version 300 es
      precision mediump float;

      in vec3 v_Position;
      in vec3 v_Normal;
      in float v_Distance;

      uniform vec4 u_Color;
      uniform vec4 u_EmissionColor;
      uniform vec3 u_LightPos;

      out vec4 fragColor;

      void main() {
        vec3 N = normalize(v_Normal);
        vec3 L = normalize(u_LightPos - v_Position);
        float diff = max(dot(N, L), 0.18);

        vec3 base = u_Color.rgb * diff;
        base += u_EmissionColor.rgb * u_EmissionColor.a;

        // Névoa volumétrica atmosférica de fantasia sombria
        vec3 fogColor = vec3(0.047, 0.070, 0.121);
        float fogDensity = 0.024;
        float fogFactor = clamp(exp(-pow(v_Distance * fogDensity, 1.4)), 0.0, 1.0);

        vec3 finalColor = mix(fogColor, base, fogFactor);
        fragColor = vec4(finalColor, u_Color.a);
      }
    """.trimIndent()

    shaderProgram = createProgram(vertexShaderCode, fragmentShaderCode)
    uMVPMatrixHandle = GLES30.glGetUniformLocation(shaderProgram, "u_MVPMatrix")
    uMVMatrixHandle = GLES30.glGetUniformLocation(shaderProgram, "u_MVMatrix")
    uModelMatrixHandle = GLES30.glGetUniformLocation(shaderProgram, "u_ModelMatrix")
    uColorHandle = GLES30.glGetUniformLocation(shaderProgram, "u_Color")
    uEmissionColorHandle = GLES30.glGetUniformLocation(shaderProgram, "u_EmissionColor")
    uLightPosHandle = GLES30.glGetUniformLocation(shaderProgram, "u_LightPos")
    aPositionHandle = 0
    aNormalHandle = 1

    // Shader para partículas arcanas pontuais
    val particleVCode = """#version 300 es
      layout(location = 0) in vec3 a_Position;
      uniform mat4 u_MVPMatrix;
      void main() {
        gl_Position = u_MVPMatrix * vec4(a_Position, 1.0);
        gl_PointSize = 18.0;
      }
    """.trimIndent()

    val particleFCode = """#version 300 es
      precision mediump float;
      uniform vec4 u_Color;
      out vec4 fragColor;
      void main() {
        vec2 coord = gl_PointCoord - vec2(0.5);
        float dist = length(coord);
        if (dist > 0.5) discard;
        float alpha = (1.0 - dist * 2.0) * u_Color.a;
        fragColor = vec4(u_Color.rgb, alpha);
      }
    """.trimIndent()

    particleProgram = createProgram(particleVCode, particleFCode)
    uParticleMVPHandle = GLES30.glGetUniformLocation(particleProgram, "u_MVPMatrix")
    uParticleColorHandle = GLES30.glGetUniformLocation(particleProgram, "u_Color")
    aParticlePositionHandle = 0
  }

  private fun initGeometries() {
    // 1. Cubo Padrão (36 vértices)
    val cubeVertices = floatArrayOf(
      // Front
      -0.5f, -0.5f,  0.5f,   0.5f, -0.5f,  0.5f,   0.5f,  0.5f,  0.5f,
      -0.5f, -0.5f,  0.5f,   0.5f,  0.5f,  0.5f,  -0.5f,  0.5f,  0.5f,
      // Back
      -0.5f, -0.5f, -0.5f,  -0.5f,  0.5f, -0.5f,   0.5f,  0.5f, -0.5f,
      -0.5f, -0.5f, -0.5f,   0.5f,  0.5f, -0.5f,   0.5f, -0.5f, -0.5f,
      // Left
      -0.5f, -0.5f, -0.5f,  -0.5f, -0.5f,  0.5f,  -0.5f,  0.5f,  0.5f,
      -0.5f, -0.5f, -0.5f,  -0.5f,  0.5f,  0.5f,  -0.5f,  0.5f, -0.5f,
      // Right
       0.5f, -0.5f, -0.5f,   0.5f,  0.5f, -0.5f,   0.5f,  0.5f,  0.5f,
       0.5f, -0.5f, -0.5f,   0.5f,  0.5f,  0.5f,   0.5f, -0.5f,  0.5f,
      // Top
      -0.5f,  0.5f, -0.5f,  -0.5f,  0.5f,  0.5f,   0.5f,  0.5f,  0.5f,
      -0.5f,  0.5f, -0.5f,   0.5f,  0.5f,  0.5f,   0.5f,  0.5f, -0.5f,
      // Bottom
      -0.5f, -0.5f, -0.5f,   0.5f, -0.5f, -0.5f,   0.5f, -0.5f,  0.5f,
      -0.5f, -0.5f, -0.5f,   0.5f, -0.5f,  0.5f,  -0.5f, -0.5f,  0.5f
    )

    val cubeNormals = floatArrayOf(
      0f,0f,1f, 0f,0f,1f, 0f,0f,1f, 0f,0f,1f, 0f,0f,1f, 0f,0f,1f,
      0f,0f,-1f, 0f,0f,-1f, 0f,0f,-1f, 0f,0f,-1f, 0f,0f,-1f, 0f,0f,-1f,
      -1f,0f,0f, -1f,0f,0f, -1f,0f,0f, -1f,0f,0f, -1f,0f,0f, -1f,0f,0f,
      1f,0f,0f, 1f,0f,0f, 1f,0f,0f, 1f,0f,0f, 1f,0f,0f, 1f,0f,0f,
      0f,1f,0f, 0f,1f,0f, 0f,1f,0f, 0f,1f,0f, 0f,1f,0f, 0f,1f,0f,
      0f,-1f,0f, 0f,-1f,0f, 0f,-1f,0f, 0f,-1f,0f, 0f,-1f,0f, 0f,-1f,0f
    )

    cubeVertexBuffer = ByteBuffer.allocateDirect(cubeVertices.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(cubeVertices)
    cubeVertexBuffer.position(0)
    cubeNormalBuffer = ByteBuffer.allocateDirect(cubeNormals.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(cubeNormals)
    cubeNormalBuffer.position(0)

    // 2. Cilindro Padrão (para troncos de árvore e altar)
    val segments = 12
    val cylVertList = mutableListOf<Float>()
    val cylNormList = mutableListOf<Float>()

    for (i in 0 until segments) {
      val angle1 = (i.toFloat() / segments) * 2f * PI.toFloat()
      val angle2 = ((i + 1).toFloat() / segments) * 2f * PI.toFloat()

      val x1 = cos(angle1) * 0.5f
      val z1 = sin(angle1) * 0.5f
      val x2 = cos(angle2) * 0.5f
      val z2 = sin(angle2) * 0.5f

      // Triângulo 1 lateral
      cylVertList.addAll(listOf(x1, -0.5f, z1,  x2, -0.5f, z2,  x2, 0.5f, z2))
      cylNormList.addAll(listOf(x1*2f, 0f, z1*2f,  x2*2f, 0f, z2*2f,  x2*2f, 0f, z2*2f))

      // Triângulo 2 lateral
      cylVertList.addAll(listOf(x1, -0.5f, z1,  x2, 0.5f, z2,  x1, 0.5f, z1))
      cylNormList.addAll(listOf(x1*2f, 0f, z1*2f,  x2*2f, 0f, z2*2f,  x1*2f, 0f, z1*2f))
    }

    val cylVerts = cylVertList.toFloatArray()
    val cylNorms = cylNormList.toFloatArray()

    cylinderVertexBuffer = ByteBuffer.allocateDirect(cylVerts.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(cylVerts)
    cylinderVertexBuffer.position(0)
    cylinderNormalBuffer = ByteBuffer.allocateDirect(cylNorms.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(cylNorms)
    cylinderNormalBuffer.position(0)

    // 3. Plano (Solo da floresta)
    val planeVerts = floatArrayOf(
      -0.5f, 0f, -0.5f,   0.5f, 0f, -0.5f,   0.5f, 0f,  0.5f,
      -0.5f, 0f, -0.5f,   0.5f, 0f,  0.5f,  -0.5f, 0f,  0.5f
    )
    val planeNorms = floatArrayOf(
      0f, 1f, 0f,  0f, 1f, 0f,  0f, 1f, 0f,
      0f, 1f, 0f,  0f, 1f, 0f,  0f, 1f, 0f
    )
    planeVertexBuffer = ByteBuffer.allocateDirect(planeVerts.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(planeVerts)
    planeVertexBuffer.position(0)
    planeNormalBuffer = ByteBuffer.allocateDirect(planeNorms.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(planeNorms)
    planeNormalBuffer.position(0)
  }

  private fun createProgram(vCode: String, fCode: String): Int {
    val vShader = loadShader(GLES30.GL_VERTEX_SHADER, vCode)
    val fShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fCode)
    val prog = GLES30.glCreateProgram()
    GLES30.glAttachShader(prog, vShader)
    GLES30.glAttachShader(prog, fShader)
    GLES30.glLinkProgram(prog)
    return prog
  }

  private fun loadShader(type: Int, code: String): Int {
    val shader = GLES30.glCreateShader(type)
    GLES30.glShaderSource(shader, code)
    GLES30.glCompileShader(shader)
    return shader
  }

  private fun lerpAngle(from: Float, to: Float, weight: Float): Float {
    var diff = (to - from) % 360f
    if (diff > 180f) diff -= 360f
    if (diff < -180f) diff += 360f
    return from + diff * weight.coerceIn(0f, 1f)
  }
}
