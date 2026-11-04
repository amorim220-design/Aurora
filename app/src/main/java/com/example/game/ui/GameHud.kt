package com.example.game.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NordicWalking
import androidx.compose.material.icons.filled.North
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.model.MovementState
import com.example.game.model.PlayerStats
import com.example.ui.theme.ArcaneBlue
import com.example.ui.theme.ArcaneCyan
import com.example.ui.theme.ArcaneGlow
import com.example.ui.theme.CrimsonHealth
import com.example.ui.theme.CrimsonHealthDim
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DeepAbyss
import com.example.ui.theme.RunicGold
import com.example.ui.theme.SapphireMana
import com.example.ui.theme.SapphireManaLight
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Interface mobile (HUD) completa para Projeto Aurora: Fase 1.
 * Controles táteis analógicos, botões de combate, magia com cooldown, barras e bússola.
 */
@Composable
fun GameHud(
  playerStats: PlayerStats,
  movementState: MovementState,
  distanceToAltar: Float,
  hasReachedAltar: Boolean,
  magicCooldownLeft: Float,
  magicCooldownTotal: Float = 3.5f,
  isCinematicMode: Boolean = false,
  cameraModeLabel: String = "Isométrica",
  onMoveInput: (Float, Float, Boolean) -> Unit,
  onAttack: () -> Unit,
  onDodge: () -> Unit,
  onJump: () -> Unit,
  onMagic: () -> Unit,
  onToggleCamera: () -> Unit,
  onOpenGodotHub: () -> Unit
) {
  Box(modifier = Modifier.fillMaxSize()) {

    // 1. Barra de Status do Jogador (Canto Superior Esquerdo)
    PlayerStatusBar(
      stats = playerStats,
      state = movementState,
      modifier = Modifier
        .align(Alignment.TopStart)
        .padding(start = 24.dp, top = 20.dp)
    )

    // 2. Painel de Missão e Bússola da Clareira (Topo Central)
    ObjectiveBanner(
      distance = distanceToAltar,
      hasReachedAltar = hasReachedAltar,
      modifier = Modifier
        .align(Alignment.TopCenter)
        .padding(top = 16.dp)
    )

    // 3. Botões de Ferramentas e Godot Hub (Canto Superior Direito)
    TopActionButtons(
      cameraModeLabel = cameraModeLabel,
      onToggleCamera = onToggleCamera,
      onOpenGodotHub = onOpenGodotHub,
      modifier = Modifier
        .align(Alignment.TopEnd)
        .padding(end = 24.dp, top = 16.dp)
    )

    // 4. Analógico Virtual Touch (Metade Inferior Esquerda)
    VirtualTouchJoystick(
      onMoveInput = onMoveInput,
      modifier = Modifier
        .align(Alignment.BottomStart)
        .padding(start = 36.dp, bottom = 32.dp)
    )

    // 5. Botões de Combate e Ação (Metade Inferior Direita)
    ActionButtonsCluster(
      canCastMagic = magicCooldownLeft <= 0f && playerStats.currentMp >= 25f,
      magicCooldownLeft = magicCooldownLeft,
      magicCooldownTotal = magicCooldownTotal,
      onAttack = onAttack,
      onDodge = onDodge,
      onJump = onJump,
      onMagic = onMagic,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(end = 36.dp, bottom = 28.dp)
    )

    // 6. Alerta de Despertar Concluído na Clareira
    AnimatedVisibility(
      visible = hasReachedAltar,
      enter = fadeIn(),
      exit = fadeOut(),
      modifier = Modifier
        .align(Alignment.Center)
        .padding(horizontal = 32.dp)
    ) {
      AwakeningCelebrationCard(onOpenGodotHub = onOpenGodotHub)
    }
  }
}

@Composable
private fun PlayerStatusBar(
  stats: PlayerStats,
  state: MovementState,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.testTag("player_status_bar"),
    shape = RoundedCornerShape(12.dp),
    color = DarkSurface.copy(alpha = 0.88f),
    border = androidx.compose.foundation.BorderStroke(1.5.dp, ArcaneCyan.copy(alpha = 0.45f))
  ) {
    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
      // Nome e Classe
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(if (stats.isInvulnerable) ArcaneCyan else RunicGold)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Protagonista • O Desperto",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "[${state.label}]",
          fontSize = 11.sp,
          color = ArcaneCyan
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Barra de Vida (HP)
      Column {
        Row(
          modifier = Modifier.width(190.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(text = "VIDA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CrimsonHealth)
          Text(
            text = "${stats.currentHp.toInt()} / ${stats.maxHp.toInt()}",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Box(
          modifier = Modifier
            .width(190.dp)
            .height(10.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(CrimsonHealthDim.copy(alpha = 0.4f))
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth(stats.currentHp / stats.maxHp)
              .height(10.dp)
              .background(
                Brush.horizontalGradient(
                  listOf(CrimsonHealthDim, CrimsonHealth)
                )
              )
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Barra de Energia Arcana (MP)
      Column {
        Row(
          modifier = Modifier.width(190.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(text = "ENERGIA MÁGICA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ArcaneCyan)
          Text(
            text = "${stats.currentMp.toInt()} / ${stats.maxMp.toInt()}",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Box(
          modifier = Modifier
            .width(190.dp)
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(SapphireMana.copy(alpha = 0.3f))
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth(stats.currentMp / stats.maxMp)
              .height(8.dp)
              .background(
                Brush.horizontalGradient(
                  listOf(SapphireMana, SapphireManaLight)
                )
              )
          )
        }
      }
    }
  }
}

@Composable
private fun ObjectiveBanner(
  distance: Float,
  hasReachedAltar: Boolean,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.testTag("objective_banner"),
    shape = RoundedCornerShape(20.dp),
    color = DarkSurface.copy(alpha = 0.85f),
    border = androidx.compose.foundation.BorderStroke(1.dp, ArcaneCyan.copy(alpha = 0.5f))
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.Navigation,
        contentDescription = null,
        tint = if (hasReachedAltar) ArcaneCyan else RunicGold,
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = if (hasReachedAltar) "Altar Alcançado — O Despertar Arcana!" else "Floresta Antiga: Altar a ${distance.toInt()}m",
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.White
      )
    }
  }
}

@Composable
private fun TopActionButtons(
  cameraModeLabel: String,
  onToggleCamera: () -> Unit,
  onOpenGodotHub: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
    // Botão Alternar Câmera
    FilledTonalButton(
      onClick = onToggleCamera,
      colors = ButtonDefaults.filledTonalButtonColors(
        containerColor = if (cameraModeLabel != "3ª Pessoa") ArcaneBlue else DarkSurface.copy(alpha = 0.85f)
      ),
      shape = RoundedCornerShape(12.dp),
      modifier = Modifier
        .height(40.dp)
        .testTag("toggle_camera_btn")
    ) {
      Icon(
        Icons.Default.Videocam,
        contentDescription = "Câmera",
        tint = if (cameraModeLabel != "3ª Pessoa") Color.White else ArcaneCyan,
        modifier = Modifier.size(18.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "Câmera: $cameraModeLabel",
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.White
      )
    }

    // Botão Hub Godot 4
    Button(
      onClick = onOpenGodotHub,
      colors = ButtonDefaults.buttonColors(containerColor = ArcaneBlue),
      shape = RoundedCornerShape(12.dp),
      modifier = Modifier
        .height(40.dp)
        .testTag("open_godot_hub_btn")
    ) {
      Icon(Icons.Default.Hub, contentDescription = "Godot Hub", tint = Color.White, modifier = Modifier.size(18.dp))
      Spacer(modifier = Modifier.width(6.dp))
      Text(text = "Godot 4 Hub", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
  }
}

@Composable
private fun VirtualTouchJoystick(
  onMoveInput: (Float, Float, Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  val baseRadius = 65f
  var knobOffset by remember { mutableStateOf(Offset.Zero) }

  Box(
    modifier = modifier
      .size(130.dp)
      .testTag("virtual_joystick")
      .clip(CircleShape)
      .background(DarkSurface.copy(alpha = 0.65f))
      .border(2.dp, ArcaneCyan.copy(alpha = 0.5f), CircleShape)
      .pointerInput(Unit) {
        detectDragGestures(
          onDragStart = { offset ->
            val center = Offset(baseRadius, baseRadius)
            val diff = offset - center
            val dist = diff.getDistance()
            val clampedOffset = if (dist > baseRadius) diff * (baseRadius / dist) else diff
            knobOffset = clampedOffset
            val normX = clampedOffset.x / baseRadius
            val normY = -clampedOffset.y / baseRadius // Invert Y for forward
            val isRunning = (dist / baseRadius) > 0.8f
            onMoveInput(normX, normY, isRunning)
          },
          onDrag = { change, dragAmount ->
            change.consume()
            val newOffset = knobOffset + dragAmount
            val dist = newOffset.getDistance()
            val clampedOffset = if (dist > baseRadius) newOffset * (baseRadius / dist) else newOffset
            knobOffset = clampedOffset
            val normX = clampedOffset.x / baseRadius
            val normY = -clampedOffset.y / baseRadius
            val isRunning = (dist / baseRadius) > 0.8f
            onMoveInput(normX, normY, isRunning)
          },
          onDragEnd = {
            knobOffset = Offset.Zero
            onMoveInput(0f, 0f, false)
          },
          onDragCancel = {
            knobOffset = Offset.Zero
            onMoveInput(0f, 0f, false)
          }
        )
      },
    contentAlignment = Alignment.Center
  ) {
    // Marcador central sutil
    Box(
      modifier = Modifier
        .size(8.dp)
        .clip(CircleShape)
        .background(ArcaneCyan.copy(alpha = 0.35f))
    )

    // Manopla (Knob) do Analógico
    Box(
      modifier = Modifier
        .offset { IntOffset(knobOffset.x.toInt(), knobOffset.y.toInt()) }
        .size(54.dp)
        .clip(CircleShape)
        .background(
          Brush.radialGradient(
            listOf(ArcaneCyan, ArcaneBlue)
          )
        )
        .border(1.5.dp, Color.White.copy(alpha = 0.8f), CircleShape)
    )
  }
}

@Composable
private fun ActionButtonsCluster(
  canCastMagic: Boolean,
  magicCooldownLeft: Float,
  magicCooldownTotal: Float,
  onAttack: () -> Unit,
  onDodge: () -> Unit,
  onJump: () -> Unit,
  onMagic: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(modifier = modifier.size(190.dp)) {
    // Botão de Pulo (Superior Direito)
    ActionButton(
      label = "PULO",
      icon = Icons.Default.North,
      sizeDp = 56,
      bgColor = DarkSurfaceElevated,
      accentColor = RunicGold,
      onClick = onJump,
      modifier = Modifier
        .align(Alignment.TopEnd)
        .testTag("jump_button")
    )

    // Botão de Esquiva (DODGE / ROLL) (Superior Esquerdo)
    ActionButton(
      label = "ESQUIVA",
      icon = Icons.Default.Security,
      sizeDp = 56,
      bgColor = DarkSurfaceElevated,
      accentColor = ArcaneCyan,
      onClick = onDodge,
      modifier = Modifier
        .align(Alignment.TopStart)
        .testTag("dodge_button")
    )

    // Botão de Magia: AURORA BURST (Inferior Esquerdo)
    Box(
      modifier = Modifier
        .align(Alignment.BottomStart)
        .size(68.dp)
    ) {
      ActionButton(
        label = "MAGIA",
        icon = Icons.Default.AutoAwesome,
        sizeDp = 68,
        bgColor = if (canCastMagic) ArcaneBlue else DarkSurface,
        accentColor = if (canCastMagic) Color.White else Color.Gray,
        onClick = { if (canCastMagic) onMagic() },
        modifier = Modifier.testTag("magic_ability_button")
      )

      // Indicador radial de Cooldown da Magia
      if (magicCooldownLeft > 0f) {
        val progress = magicCooldownLeft / magicCooldownTotal
        CircularProgressIndicator(
          progress = { progress },
          modifier = Modifier.fillMaxSize(),
          color = ArcaneCyan,
          strokeWidth = 3.dp
        )
        Text(
          text = String.format("%.1fs", magicCooldownLeft),
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White,
          modifier = Modifier.align(Alignment.Center)
        )
      }
    }

    // Botão de Ataque Físico Principal (Inferior Direito)
    ActionButton(
      label = "ATAQUE",
      icon = Icons.Default.FlashOn,
      sizeDp = 76,
      bgColor = CrimsonHealth,
      accentColor = Color.White,
      onClick = onAttack,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .testTag("attack_button")
    )
  }
}

@Composable
private fun ActionButton(
  label: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  sizeDp: Int,
  bgColor: Color,
  accentColor: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isPressed by remember { mutableStateOf(false) }
  val scale by animateFloatAsState(if (isPressed) 0.88f else 1f, label = "button_scale")

  Surface(
    onClick = {
      isPressed = true
      onClick()
      isPressed = false
    },
    modifier = modifier
      .size(sizeDp.dp)
      .scale(scale),
    shape = CircleShape,
    color = bgColor,
    border = androidx.compose.foundation.BorderStroke(2.dp, accentColor.copy(alpha = 0.8f)),
    shadowElevation = 6.dp
  ) {
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = accentColor,
        modifier = Modifier.size((sizeDp * 0.4f).dp)
      )
      Text(
        text = label,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        color = accentColor
      )
    }
  }
}

@Composable
private fun AwakeningCelebrationCard(
  onOpenGodotHub: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = DeepAbyss.copy(alpha = 0.94f),
    border = androidx.compose.foundation.BorderStroke(2.dp, ArcaneCyan)
  ) {
    Column(
      modifier = Modifier.padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ArcaneCyan, modifier = Modifier.size(38.dp))
      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = "FASE 1 CONCLUÍDA: O DESPERTAR!",
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = Color.White
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "Você atravessou a Floresta Antiga e alcançou o Altar Ancestral. O poder mágico de Aurora foi despertado!",
        fontSize = 13.sp,
        color = Color.LightGray,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
      )
      Spacer(modifier = Modifier.height(14.dp))
      Button(
        onClick = onOpenGodotHub,
        colors = ButtonDefaults.buttonColors(containerColor = ArcaneBlue)
      ) {
        Text("Inspecionar Arquitetura Godot 4 & Exportar APK", color = Color.White)
      }
    }
  }
}
