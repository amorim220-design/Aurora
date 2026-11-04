package com.example

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.game.audio.SoundManager
import com.example.game.engine.Game3DRenderer
import com.example.game.engine.GameSurfaceView
import com.example.game.model.MovementState
import com.example.game.model.PlayerStats
import com.example.game.ui.GameHud
import com.example.game.ui.GodotHubDialog
import com.example.ui.theme.MyApplicationTheme

/**
 * Atividade principal de Projeto Aurora: Fase 1 — O Despertar.
 * Inicializa a renderização 3D em OpenGL ES 3.0 e a interface de controles táteis em Jetpack Compose.
 */
class MainActivity : ComponentActivity() {

  private lateinit var soundManager: SoundManager
  private lateinit var gameRenderer: Game3DRenderer
  private var surfaceView: GameSurfaceView? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    hideSystemUI()

    soundManager = SoundManager(this)
    gameRenderer = Game3DRenderer(this, soundManager)

    setContent {
      MyApplicationTheme {
        Box(modifier = Modifier.fillMaxSize()) {
          GameScreen(
            renderer = gameRenderer,
            onSurfaceCreated = { sv -> surfaceView = sv }
          )
        }
      }
    }
  }

  private fun hideSystemUI() {
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      window.insetsController?.let { controller ->
        controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
      }
    } else {
      @Suppress("DEPRECATION")
      window.decorView.systemUiVisibility = (
        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
          or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
          or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
          or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
          or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
          or View.SYSTEM_UI_FLAG_FULLSCREEN
      )
    }
  }

  override fun onResume() {
    super.onResume()
    surfaceView?.onResume()
  }

  override fun onPause() {
    super.onPause()
    surfaceView?.onPause()
  }
}

@Composable
fun GameScreen(
  renderer: Game3DRenderer,
  onSurfaceCreated: (GameSurfaceView) -> Unit
) {
  var stats by remember { mutableStateOf(PlayerStats()) }
  var state by remember { mutableStateOf(MovementState.IDLE) }
  var distanceToAltar by remember { mutableFloatStateOf(65f) }
  var hasReachedAltar by remember { mutableStateOf(false) }
  var cooldownLeft by remember { mutableFloatStateOf(0f) }
  var cooldownTotal by remember { mutableFloatStateOf(3.5f) }
  var currentCameraMode by remember { mutableStateOf(Game3DRenderer.CameraMode.ISOMETRIC) }
  var showGodotHub by remember { mutableStateOf(false) }

  DisposableEffect(renderer) {
    renderer.onStatsChanged = { newStats -> stats = newStats }
    renderer.onStateChanged = { newState -> state = newState }
    renderer.onDistanceChanged = { dist, reached ->
      distanceToAltar = dist
      hasReachedAltar = reached
    }
    renderer.onMagicCooldownChanged = { left, total ->
      cooldownLeft = left
      cooldownTotal = total
    }
    onDispose {
      renderer.onStatsChanged = null
      renderer.onStateChanged = null
      renderer.onDistanceChanged = null
      renderer.onMagicCooldownChanged = null
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    // 1. Superfície 3D nativa OpenGL ES 3.0
    AndroidView(
      factory = { ctx ->
        GameSurfaceView(ctx, renderer).also { sv ->
          onSurfaceCreated(sv)
        }
      },
      modifier = Modifier.fillMaxSize()
    )

    // 2. Interface Touch (HUD)
    GameHud(
      playerStats = stats,
      movementState = state,
      distanceToAltar = distanceToAltar,
      hasReachedAltar = hasReachedAltar,
      magicCooldownLeft = cooldownLeft,
      magicCooldownTotal = cooldownTotal,
      isCinematicMode = currentCameraMode == Game3DRenderer.CameraMode.CINEMATIC,
      cameraModeLabel = currentCameraMode.label,
      onMoveInput = { x, z, run ->
        renderer.inputMoveX = x
        renderer.inputMoveZ = z
        renderer.inputIsRunning = run
      },
      onAttack = { renderer.executeAttack() },
      onDodge = { renderer.executeDodge() },
      onJump = { renderer.executeJump() },
      onMagic = { renderer.executeMagicAbility() },
      onToggleCamera = {
        currentCameraMode = when (currentCameraMode) {
          Game3DRenderer.CameraMode.ISOMETRIC -> Game3DRenderer.CameraMode.OVER_THE_SHOULDER
          Game3DRenderer.CameraMode.OVER_THE_SHOULDER -> Game3DRenderer.CameraMode.CINEMATIC
          Game3DRenderer.CameraMode.CINEMATIC -> Game3DRenderer.CameraMode.ISOMETRIC
        }
        renderer.cameraMode = currentCameraMode
      },
      onOpenGodotHub = { showGodotHub = true }
    )

    // 3. Modal de Inspeção do Projeto Godot 4 & Exportação
    if (showGodotHub) {
      GodotHubDialog(onDismiss = { showGodotHub = false })
    }
  }
}
