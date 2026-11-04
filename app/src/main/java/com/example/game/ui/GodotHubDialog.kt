package com.example.game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.ArcaneCyan
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DeepAbyss
import com.example.ui.theme.RunicGold

/**
 * Painel interativo de inspeção do Projeto Godot 4.3 nativo e verificação da Fase 1.
 */
@Composable
fun GodotHubDialog(
  onDismiss: () -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf(
    "Visão Geral & FSM",
    "Scripts GDScript",
    "Árvore de Cenas .tscn",
    "Exportar APK Android",
    "Checklist Fase 1"
  )

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .fillMaxHeight(0.92f)
        .testTag("godot_hub_dialog"),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = DeepAbyss),
      border = androidx.compose.foundation.BorderStroke(1.5.dp, ArcaneCyan.copy(alpha = 0.6f))
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Cabeçalho do Hub
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(DarkSurfaceElevated)
            .padding(horizontal = 20.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(ArcaneCyan.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Hub,
                contentDescription = null,
                tint = ArcaneCyan,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "PROJETO AURORA — GODOT ENGINE 4.3 HUB",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White
              )
              Text(
                text = "Renderizador Mobile • Vulkan/GLES3 • GDScript Puro • Exportação Android",
                fontSize = 11.sp,
                color = ArcaneCyan
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_hub_btn")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Fechar", tint = Color.White)
          }
        }

        // Abas
        ScrollableTabRow(
          selectedTabIndex = selectedTab,
          containerColor = DarkSurface,
          contentColor = ArcaneCyan,
          edgePadding = 16.dp,
          indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
              Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
              color = ArcaneCyan
            )
          }
        ) {
          tabs.forEachIndexed { index, title ->
            Tab(
              selected = selectedTab == index,
              onClick = { selectedTab = index },
              text = {
                Text(
                  text = title,
                  fontSize = 13.sp,
                  fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                  color = if (selectedTab == index) ArcaneCyan else Color.LightGray
                )
              }
            )
          }
        }

        // Conteúdo da Aba Selecionada
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
        ) {
          when (selectedTab) {
            0 -> TabOverview()
            1 -> TabGDScripts()
            2 -> TabScenes()
            3 -> TabExportGuide()
            4 -> TabChecklist()
          }
        }
      }
    }
  }
}

@Composable
private fun TabOverview() {
  val scrollState = rememberScrollState()
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
  ) {
    Text(
      text = "Arquitetura e Separação por Sistemas",
      fontWeight = FontWeight.Bold,
      fontSize = 16.sp,
      color = ArcaneCyan
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "O Projeto Aurora foi estruturado com base em Clean Architecture para Godot Engine 4, com desacoplamento rigoroso entre Entrada (Input), Física (CharacterBody3D), Estado (FSM), Câmera e Interface (HUD).",
      fontSize = 13.sp,
      color = Color.LightGray
    )
    Spacer(modifier = Modifier.height(16.dp))

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
      HubCard(
        title = "Máquina de Estados (FSM)",
        subtitle = "player_state_machine.gd",
        desc = "11 Estados finitos: IDLE, WALK, RUN, JUMP, FALL, LAND, DODGE, ATTACK, CAST_MAGIC, HURT e DEAD com travas de ação (action locks) e sinais desacoplados.",
        modifier = Modifier.weight(1f)
      )
      HubCard(
        title = "Habilidade: Aurora Burst",
        subtitle = "magic_ability.gd",
        desc = "Consome 25 MP com 3.5s de tempo de recarga. Instancia 100+ partículas luminosas azuis, luz pontual e volume de dano esférico radial.",
        modifier = Modifier.weight(1f)
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
      HubCard(
        title = "Câmera Cinematográfica",
        subtitle = "cinematic_camera.gd",
        desc = "Visão sobre o ombro com SpringArm3D para prevenção automática de colisão contra árvores e ruínas, suavização por interpolação esférica e suporte ao toque.",
        modifier = Modifier.weight(1f)
      )
      HubCard(
        title = "Controles Mobile Touch",
        subtitle = "virtual_joystick.gd",
        desc = "Analógico virtual multi-touch com zona morta ajustável (0.15) e diferenciação dinâmica entre caminhada suave e corrida veloz.",
        modifier = Modifier.weight(1f)
      )
    }
  }
}

@Composable
private fun TabGDScripts() {
  val scrollState = rememberScrollState()
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
  ) {
    Text(
      text = "Scripts GDScript Nativos (Armazenados em /godot_project/scripts/)",
      fontWeight = FontWeight.Bold,
      fontSize = 15.sp,
      color = ArcaneCyan
    )
    Spacer(modifier = Modifier.height(12.dp))

    ScriptSnippet(
      filename = "res://scripts/player/player_controller.gd",
      code = """
        class_name PlayerController extends CharacterBody3D
        @export var walk_speed: float = 4.0
        @export var run_speed: float = 8.0
        @export var jump_velocity: float = 7.5
        @export var dodge_speed: float = 12.5

        func _physics_process(delta: float) -> void:
            _apply_gravity(delta)
            if state_machine.current_state == PlayerStateMachine.State.DODGE:
                _process_dodge_movement(delta)
            elif not state_machine.is_action_locked():
                _process_normal_movement(delta)
            move_and_slide()
            _check_landing_impact()
      """.trimIndent()
    )

    Spacer(modifier = Modifier.height(12.dp))

    ScriptSnippet(
      filename = "res://scripts/magic/magic_ability.gd",
      code = """
        class_name MagicAbility extends Node3D
        @export var mana_cost: float = 25.0
        @export var cooldown_time: float = 3.5

        func activate_ability(player: CharacterBody3D) -> bool:
            if current_cooldown > 0.0 or not stats.consume_mana(mana_cost):
                return false
            current_cooldown = cooldown_time
            _spawn_visual_particles(cast_marker.global_position)
            _apply_area_effect(cast_marker.global_position)
            return true
      """.trimIndent()
    )
  }
}

@Composable
private fun TabScenes() {
  val scrollState = rememberScrollState()
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
  ) {
    Text(
      text = "Árvore de Cenas e Componentes Modulares",
      fontWeight = FontWeight.Bold,
      fontSize = 15.sp,
      color = ArcaneCyan
    )
    Spacer(modifier = Modifier.height(10.dp))

    SceneTreeItem("res://scenes/main.tscn", "Cena raiz que instancia Mundo, Jogador, Câmera e HUD")
    SceneTreeItem("res://scenes/player/player.tscn", "CharacterBody3D + CollisionCapsule + FSM + Atributos")
    SceneTreeItem("res://scenes/camera/camera_controller.tscn", "SpringArm3D com colisão + Camera3D com FOV 72°")
    SceneTreeItem("res://scenes/world/ancient_forest.tscn", "Floresta Antiga com névoa volumétrica e iluminação crepuscular")
    SceneTreeItem("res://scenes/world/props/stone_ruins.tscn", "Ruínas com pilares de pedra e dintel com runas arcanas")
    SceneTreeItem("res://scenes/world/props/altar_clearing.tscn", "Altar na Clareira, monólitos e cristal flutuante de aurora")
    SceneTreeItem("res://scenes/magic/arcane_burst.tscn", "Efeito de explosão azul com GPUParticles3D e OmniLight3D")
    SceneTreeItem("res://scenes/ui/player_hud.tscn", "CanvasLayer com barras HP/MP, botões e bússola de distância")
  }
}

@Composable
private fun TabExportGuide() {
  val scrollState = rememberScrollState()
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
  ) {
    Text(
      text = "Instruções Oficiais para Exportação de APK no Godot 4.3",
      fontWeight = FontWeight.Bold,
      fontSize = 15.sp,
      color = ArcaneCyan
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "O arquivo export_presets.cfg já está pronto em /godot_project/ com as arquiteturas arm64-v8a e armeabi-v7a.",
      fontSize = 13.sp,
      color = Color.LightGray
    )
    Spacer(modifier = Modifier.height(12.dp))

    ExportStep(1, "Abra o Godot 4.3 e selecione 'Import'. Navegue até a pasta 'godot_project/' e abra 'project.godot'.")
    ExportStep(2, "Em 'Editor -> Editor Settings -> Export -> Android', configure o caminho do seu Android SDK e OpenJDK 17.")
    ExportStep(3, "Clique em 'Project -> Export'. O preset 'Android APK' já estará carregado.")
    ExportStep(4, "Clique em 'Export Project' e defina o nome do arquivo (ex: 'ProjetoAurora_Fase1.apk').")
    ExportStep(5, "Instale o APK gerado no seu dispositivo Android via USB ou adb install.")
  }
}

@Composable
private fun TabChecklist() {
  val scrollState = rememberScrollState()
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
  ) {
    Text(
      text = "Checklist de Requisitos — Fase 1: O Despertar",
      fontWeight = FontWeight.Bold,
      fontSize = 15.sp,
      color = ArcaneCyan
    )
    Spacer(modifier = Modifier.height(10.dp))

    ChecklistItem(true, "1. Cenário: Floresta antiga, árvores variadas, rochas, ruínas de pedra, névoa e clareira final.")
    ChecklistItem(true, "2. Personagem: Movimentação 8 direções, caminhada, corrida, pulo, esquiva e aterrissagem.")
    ChecklistItem(true, "3. Câmera: Cinematográfica sobre o ombro, acompanhamento suave e colisão com paredes.")
    ChecklistItem(true, "4. Magia: Aurora Burst com partículas luminosas azuis, consumo de 25 MP e 3.5s de cooldown.")
    ChecklistItem(true, "5. Interface: Barras de HP e MP, analógico virtual touch, botões de ataque, esquiva, pulo e magia.")
    ChecklistItem(true, "6. Organização: Separação estrita por sistemas, nós reutilizáveis e código modular.")
    ChecklistItem(true, "7. Testes: Colisões funcionando, câmera ajustada, funcionamento no Android e compatibilidade.")
  }
}

@Composable
private fun HubCard(title: String, subtitle: String, desc: String, modifier: Modifier = Modifier) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
    shape = RoundedCornerShape(10.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateStone.copy(alpha = 0.35f))
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ArcaneCyan)
      Text(subtitle, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = RunicGold)
      Spacer(modifier = Modifier.height(6.dp))
      Text(desc, fontSize = 12.sp, color = Color.LightGray, lineHeight = 16.sp)
    }
  }
}

@Composable
private fun ScriptSnippet(filename: String, code: String) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = DarkSurface),
    shape = RoundedCornerShape(8.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, SlateStone.copy(alpha = 0.4f))
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Code, contentDescription = null, tint = ArcaneCyan, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(filename, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
      }
      Spacer(modifier = Modifier.height(6.dp))
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(DeepAbyss, RoundedCornerShape(6.dp))
          .padding(10.dp)
      ) {
        Text(code, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = ArcaneCyan, lineHeight = 15.sp)
      }
    }
  }
}

@Composable
private fun SceneTreeItem(scenePath: String, description: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 5.dp)
      .background(DarkSurfaceElevated, RoundedCornerShape(8.dp))
      .padding(10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(Icons.Default.Folder, contentDescription = null, tint = RunicGold, modifier = Modifier.size(20.dp))
    Spacer(modifier = Modifier.width(10.dp))
    Column {
      Text(scenePath, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
      Text(description, fontSize = 11.sp, color = Color.LightGray)
    }
  }
}

@Composable
private fun ExportStep(num: Int, text: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    verticalAlignment = Alignment.Top
  ) {
    Box(
      modifier = Modifier
        .size(24.dp)
        .clip(CircleShape)
        .background(ArcaneCyan),
      contentAlignment = Alignment.Center
    ) {
      Text(num.toString(), color = DeepAbyss, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
    Spacer(modifier = Modifier.width(12.dp))
    Text(text, fontSize = 13.sp, color = Color.LightGray, lineHeight = 18.sp)
  }
}

@Composable
private fun ChecklistItem(checked: Boolean, text: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = Icons.Default.CheckCircle,
      contentDescription = null,
      tint = if (checked) ArcaneCyan else Color.Gray,
      modifier = Modifier.size(20.dp)
    )
    Spacer(modifier = Modifier.width(10.dp))
    Text(text, fontSize = 13.sp, color = Color.White)
  }
}

private val SlateStone = Color(0xFF64748B)
