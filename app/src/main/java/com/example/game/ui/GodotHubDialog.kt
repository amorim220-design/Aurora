package com.example.game.ui

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.ui.theme.ArcaneCyan
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DeepAbyss
import com.example.ui.theme.RunicGold

/**
 * Painel interativo de inspeção do Projeto Godot 4.3 nativo, Referências Visuais e Verificação.
 */
@Composable
fun GodotHubDialog(
  onDismiss: () -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(1) } // Default to Visual References tab!
  val tabs = listOf(
    "Visão Geral & FSM",
    "Referências Visuais (Art Bible)",
    "Scripts GDScript",
    "Árvore de Cenas .tscn",
    "Exportar APK Android",
    "Checklist do Projeto"
  )

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.96f)
        .fillMaxHeight(0.94f)
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
                imageVector = Icons.Default.Palette,
                contentDescription = null,
                tint = ArcaneCyan,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "PROJETO AURORA — HUB DE ARTE & ARQUITETURA GODOT 4",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White
              )
              Text(
                text = "Roguelite 3D Isométrico • Fantasia Sombria • Concept Art • Guia de Produção 3D",
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
            1 -> TabVisualReferences()
            2 -> TabGDScripts()
            3 -> TabScenes()
            4 -> TabExportGuide()
            5 -> TabChecklist()
          }
        }
      }
    }
  }
}

@Composable
private fun TabVisualReferences() {
  val scrollState = rememberScrollState()
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(scrollState),
    verticalArrangement = Arrangement.spacedBy(20.dp)
  ) {
    // Introdução da Bíblia de Arte
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = DarkSurfaceElevated,
      border = androidx.compose.foundation.BorderStroke(1.dp, ArcaneCyan.copy(alpha = 0.4f))
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ArcaneCyan, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Direção Artística: Fantasia Sombria Roguelite (Inspirado em Wizard of Legend 2)",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color.White
          )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Pacote oficial de referências conceituais para produção de modelos 3D no Blender e integração na Godot 4.3. Todas as imagens foram geradas e salvas em 'assets/concept_art/' e 'app/src/main/res/drawable/'.",
          fontSize = 13.sp,
          color = Color.LightGray,
          lineHeight = 18.sp
        )
      }
    }

    // 1. Herói Protagonista
    ConceptArtCard(
      title = "A. Protagonista (Malachai / O Desperto)",
      category = "assets/concept_art/hero/hero_character_design_sheet.jpg",
      description = "Battle-mage em armadura de placas de aço escurecido e couro com capuz índigo rasgado. Lâmina rúnica de duas mãos com canalização mágica azul. Silhueta otimizada para visão isométrica com capa e contraste de brilho rúnico.",
      drawableId = R.drawable.img_concept_hero,
      polyTarget = "12.000 - 15.000 triângulos (Blender) • 42 bones (Humanoid + Cape IK) • Texturas PBR 2K",
      promptUsed = "Full body concept art of an original dark fantasy battle-mage protagonist for a 3D isometric action game, wearing stylized obsidian-slate plate armor with leather straps and an indigo-navy hooded cloak with subtle glowing cyan arcane runes on the trim, holding a runic silver and obsidian greatsword surging with ethereal blue magic particles, front and three-quarter view, dramatic rim lighting, AAA concept art character design sheet."
    )

    // 2. Chefão: Vorghan, o Colosso de Pedra
    ConceptArtCard(
      title = "B. Chefão Monumental: Vorghan, o Colosso de Pedra",
      category = "assets/concept_art/bosses/boss_vorghan_the_stone_colossus.jpg",
      description = "Gigante de 25 a 30 metros de altura composto por monólitos e granito escuro rachado. Elmo fechado com fenda exibindo olhos azuis cintilantes. Núcleo arcano pulsante no peito (ponto vulnerável) e machado monumental de lâmina dupla.",
      drawableId = R.drawable.img_concept_vorghan,
      polyTarget = "35.000 - 45.000 triângulos • 64 bones (IK no machado e correntes) • Texturas 4K • Enquadramento cinematográfico",
      promptUsed = "Epic cinematic concept art of Vorghan the Stone Colossus, a monumental 25-meter tall ancient stone giant boss for a dark fantasy 3D game. Massive hulking body made of cracked dark granite and mossy monoliths, wearing an enclosed stone helmet visor with two piercing cyan glowing eyes visible in the dark slit. A blazing blue arcane energy core pulses inside his massive cracked chest cage. Wielding a monumental jagged stone and iron battleaxe. Tiny hero for scale, AAA boss fight concept art."
    )

    // 3. Cenário: Capítulo 01 - O Despertar
    ConceptArtCard(
      title = "C. Capítulo 01: O Despertar (Floresta Antiga e Ruínas)",
      category = "assets/concept_art/environments/chapter_01/chapter_01_ancient_forest_ruins.jpg",
      description = "Floresta milenar crepuscular com carvalhos retorcidos, musgo azul bioluminescente, arcos de pedra com inscrições arcanas, trilha sinuosa de pedra e a clareira com altar circular sob feixe de luz celestial.",
      drawableId = R.drawable.img_concept_env_awakening,
      polyTarget = "Modular Kit (Trim Sheets de pedra, árvores de 1.5k tris, shader de névoa volumétrica 'fog_atmospheric.gdshader')",
      promptUsed = "Cinematic landscape concept art of The Ancient Forest and Forgotten Ruins for Chapter 1 The Awakening in a dark fantasy 3D game. Massive twisted ancient oak trees covered with blue moss, crumbling gothic stone arches with carved glowing cyan runic inscriptions, winding stone path through twilight mist and volumetric fog, circular ritual altar in clearing, AAA environment concept art."
    )

    // 4. Cenário: Capítulo 02 e 03 - Reino das Cinzas & Arena do Colosso
    ConceptArtCard(
      title = "D. Capítulos 02 & 03: Reino das Cinzas e Arena de Vorghan",
      category = "assets/concept_art/environments/chapter_02/ & chapter_03/",
      description = "Fortaleza de obsidiana estilhaçada em penhascos vulcânicos com rios de lava e brasas suspensas. Ao centro, o anfiteatro circular monumental de pedra negra onde ocorre a batalha contra Vorghan.",
      drawableId = R.drawable.img_concept_env_ash_realm,
      polyTarget = "25.000 triângulos para arena central • Efeitos de partículas de fumaça, calor e lava fluindo com shaders PBR",
      promptUsed = "Cinematic concept art of The Ash Realm and Colossus Arena for a dark fantasy 3D game. Massive shattered obsidian fortress walls surrounded by jagged volcanic basalt spires, glowing cracks with molten lava, ash and burning embers floating in the dark smoky twilight sky. Colossal circular combat arena paved with black cracked stone and glowing crimson-orange runes, AAA concept art."
    )

    // 5. Interface e Gameplay Isométrico
    ConceptArtCard(
      title = "E. Gameplay e HUD Isométrico Roguelite",
      category = "assets/concept_art/ui/gameplay_isometric_combat_hud.jpg",
      description = "Mockup de gameplay 3D isométrico no estilo Wizard of Legend 2: herói conjurando explosão arcana radial com relâmpagos azuis contra inimigos. HUD adaptado para mobile com orbes de HP/MP, analógico virtual e botões de habilidades com recarga.",
      drawableId = R.drawable.img_concept_gameplay_ui,
      polyTarget = "Câmera isométrica em 45° com projeção de perspectiva ampla • Sistema de magias em slots modulares",
      promptUsed = "In-game concept screenshot mockup of a 3D isometric action roguelite dark fantasy game inspired by Wizard of Legend 2. Angled isometric perspective showing the battle-mage in center arena unleashing a brilliant radial blue arcane burst with shockwaves against enemies. Mobile HUD with HP/MP orbs, virtual joystick, action buttons for dodge, attack and spells with cooldown, AAA game mockup."
    )

    // Tabela do Bestiário Completo & Prompts
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = DarkSurface,
      border = androidx.compose.foundation.BorderStroke(1.dp, SlateStone.copy(alpha = 0.4f))
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = "Bestiário e Prompts dos Inimigos (Documentados na Bíblia de Arte)",
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = RunicGold
        )
        Spacer(modifier = Modifier.height(10.dp))
        EnemyRow("1. Guerreiro Corpo a Corpo", "Cavaleiro das Cinzas", "Silhueta pesada, escudo de madeira petrificada e clava com pontas incandescentes.")
        EnemyRow("2. Mago Inimigo", "Canalizador das Sombras", "Mantos negros flutuantes, máscara de porcelana rachada e orbes sombrios.")
        EnemyRow("3. Criatura Rápida", "Espreitador Rúnico (Lurker)", "Besta quadrúpede ágil com carapaça de obsidiana e múltiplos olhos vermelhos.")
        EnemyRow("4. Criatura de Fogo", "Salamandra Ígnea / Magma Hound", "Cão vulcânico de rocha derretida que explode em brasas ao morrer.")
        EnemyRow("5. Criatura de Gelo", "Golias do Gelo Eterno", "Golem blindado de gelo glacial translúcido com socos congelantes.")
        EnemyRow("6. Criatura Corrompida", "Horror Abissal / Void Spawn", "Aberração eldritch com tentáculos e cistos negros corrompidos.")
      }
    }
  }
}

@Composable
private fun ConceptArtCard(
  title: String,
  category: String,
  description: String,
  drawableId: Int,
  polyTarget: String,
  promptUsed: String
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
    border = androidx.compose.foundation.BorderStroke(1.5.dp, ArcaneCyan.copy(alpha = 0.5f))
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ArcaneCyan)
      Text(text = category, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = RunicGold)
      Spacer(modifier = Modifier.height(10.dp))

      // Imagem Conceitual Real Renderizada
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(260.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(DeepAbyss)
      ) {
        Image(
          painter = painterResource(id = drawableId),
          contentDescription = title,
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Fit
        )
      }

      Spacer(modifier = Modifier.height(12.dp))
      Text(text = description, fontSize = 13.sp, color = Color.White, lineHeight = 18.sp)
      Spacer(modifier = Modifier.height(8.dp))

      // Especificações para Blender / 3D
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = DeepAbyss,
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateStone.copy(alpha = 0.3f))
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Text(text = "Guia de Produção 3D (Blender -> Godot 4):", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = ArcaneCyan)
          Text(text = polyTarget, fontSize = 11.sp, color = Color.LightGray)
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      // Prompt usado
      Text(
        text = "Prompt Conceitual: \"$promptUsed\"",
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace,
        color = Color(0xFF94A3B8),
        lineHeight = 15.sp
      )
    }
  }
}

@Composable
private fun EnemyRow(name: String, archetype: String, details: String) {
  Column(modifier = Modifier.padding(vertical = 4.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
      Spacer(modifier = Modifier.width(6.dp))
      Text("• $archetype", fontSize = 11.sp, color = ArcaneCyan)
    }
    Text(details, fontSize = 11.sp, color = Color.LightGray)
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
        title = "Câmera Roguelite / Isométrica",
        subtitle = "cinematic_camera.gd",
        desc = "Suporte híbrido a visão isométrica em 45° (Wizard of Legend 2), visão sobre o ombro e enquadramento cinematográfico de chefão (Vorghan).",
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
      text = "Checklist de Requisitos — Fase 1 & Atualização Visual",
      fontWeight = FontWeight.Bold,
      fontSize = 15.sp,
      color = ArcaneCyan
    )
    Spacer(modifier = Modifier.height(10.dp))

    ChecklistItem(true, "1. Pacote de Concept Art gerado e organizado em 'assets/concept_art/'.")
    ChecklistItem(true, "2. Herói: Model sheet frontal/traseiro, foco mágico e silhueta isométrica.")
    ChecklistItem(true, "3. Inimigos: Bestiário com 6 arquétipos documentados para combate roguelite.")
    ChecklistItem(true, "4. Chefões: Vorghan (25-30m, elmo de pedra, núcleo azul, machado colossal).")
    ChecklistItem(true, "5. Fases: Capítulos 01 (Despertar), 02 (Cinzas), 03 (Colosso), 04 (Coração).")
    ChecklistItem(true, "6. Câmera Isométrica Roguelite (Wizard of Legend 2) e Cinemática de Chefão.")
    ChecklistItem(true, "7. Pipeline 3D para Blender (orçamento de polígonos, rigs e glTF 2.0 PBR).")
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
