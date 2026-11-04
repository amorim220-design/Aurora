# PROJETO AURORA — FASE 1: O DESPERTAR
## Documentação Técnica, Arquitetura e Guia de Exportação para Android (Godot Engine 4)

Este diretório contém o projeto nativo completo e modular desenvolvido para **Godot Engine 4.3+** com suporte ao renderizador **Mobile (Vulkan / OpenGL ES 3.0)** e exportação para **Android APK**.

---

### 1. Estrutura de Pastas e Separação por Sistemas

```text
godot_project/
├── project.godot                     # Configurações globais, inputs e viewport (1280x720 Landscape)
├── export_presets.cfg                 # Preset de exportação Android APK (arm64-v8a / armeabi-v7a)
├── scenes/
│   ├── main.tscn                     # Cena mestre integradora
│   ├── player/
│   │   └── player.tscn               # Protagonista (CharacterBody3D, Colisão, FSM, Atributos)
│   ├── camera/
│   │   └── camera_controller.tscn    # Câmera cinematográfica orbital (SpringArm3D)
│   ├── magic/
│   │   └── arcane_burst.tscn         # Partículas, iluminação e área de dano da Aurora Arcana
│   ├── world/
│   │   ├── ancient_forest.tscn       # Floresta Antiga, névoa volumétrica e iluminação crepuscular
│   │   └── props/
│   │       ├── stone_ruins.tscn      # Ruínas de pedra ancestrais com runas
│   │       └── altar_clearing.tscn   # Altar de despertar e cristal na clareira final
│   └── ui/
│       ├── virtual_joystick.tscn     # Analógico virtual multi-touch
│       └── player_hud.tscn           # HUD mobile (Barras HP/MP, botões de ação e bússola)
├── scripts/
│   ├── player/
│   │   ├── player_controller.gd      # Movimentação 8 direções, pulo, esquiva, combo e aterrissagem
│   │   ├── player_state_machine.gd   # Máquina de estados finitos (IDLE, WALK, RUN, JUMP, DODGE, etc.)
│   │   └── character_stats.gd        # Gerenciamento de vida (HP), mana (MP) e regeneração
│   ├── camera/
│   │   └── cinematic_camera.gd       # Câmera sobre o ombro, zoom e prevenção de colisão
│   ├── magic/
│   │   ├── magic_ability.gd          # Custos de mana, tempo de recarga e disparo
│   │   └── magic_projectile.gd       # Partículas luminosas e dissipação de luz
│   ├── ui/
│   │   ├── virtual_joystick.gd       # Cálculo normalizado de vetor e zona morta
│   │   ├── touch_button.gd           # Feedback tátil, escala e animação de cooldown
│   │   └── player_hud.gd             # Atualização visual da interface do jogador
│   └── world/
│       ├── ancient_forest.gd         # Ambiente e transições de iluminação
│       └── altar_clearing.gd         # Evento de despertar ao alcançar a clareira
├── shaders/
│   ├── cel_shading_dark_fantasy.gdshader # Shader de borda e reflexo para personagens e pedras
│   ├── arcane_particles.gdshader         # Partículas azuis místicas aditivas com pulso
│   └── fog_atmospheric.gdshader          # Névoa atmosférica de fantasia sombria
└── android/build/
    └── AndroidManifest.xml           # Manifesto para compilação nativa no Android
```

---

### 2. Instruções Passo a Passo para Abrir e Exportar no Godot 4

#### Requisitos no Computador do Desenvolvedor:
1. **Godot Engine 4.3 Standard (64-bit)** (ou superior).
2. **Android Studio** ou **Android Command Line Tools** com:
   - Android SDK Platform 34 (ou 33/34/35).
   - Android SDK Build-Tools.
   - Android SDK Platform-Tools (adb).
   - Android NDK (recomendado r25c+).
3. **OpenJDK 17** (Temurin ou Oracle).

#### Passo 1: Abrir o Projeto na Godot
1. Abra a Godot Engine 4.
2. Na janela do Gerenciador de Projetos, clique em **Import** (Importar).
3. Selecione o arquivo `project.godot` dentro da pasta `godot_project/`.
4. Clique em **Import & Edit** (Importar e Editar).

#### Passo 2: Configurar o Editor para Android (uma única vez)
1. No menu superior da Godot: `Editor -> Editor Settings` (Configurações do Editor).
2. Na aba esquerda, navegue até `Export -> Android`:
   - **Android SDK Path**: aponte para sua pasta do SDK (ex: `C:/Users/SEU_USUARIO/AppData/Local/Android/Sdk` no Windows ou `~/Android/Sdk` no Linux/macOS).
   - **Debug Keystore**: aponte para seu arquivo `debug.keystore` (ou gere um com o comando do keytool).
   - **Java SDK Path**: aponte para a instalação do OpenJDK 17.

#### Passo 3: Exportar o APK
1. No menu superior da Godot: `Project -> Export` (Projeto -> Exportar).
2. O preset **"Android APK"** já está pré-configurado com as arquiteturas `arm64-v8a` e `armeabi-v7a`.
3. Clique em **Export Project** (Exportar Projeto).
4. Escolha o local de saída para salvar o arquivo `.apk` (ex: `ProjetoAurora_Fase1.apk`).
5. Transfira o APK gerado para seu celular Android ou instale via ADB com:
   ```bash
   adb install -r ProjetoAurora_Fase1.apk
   ```

---

### 3. Integração com Blender (Próximos Passos)
Para a Fase 2 (Modelagem e Animações AAA):
- Os modelos exportados do Blender devem usar o formato **glTF 2.0 (.glb)** com materiais PBR.
- As animações esqueléticas (Idle, Walk, Run, Jump, Land, Dodge, Attack_1, Attack_2, Cast_Spell) devem ser organizadas em NLA Tracks ou Actions.
- Ao salvar os arquivos `.glb` dentro de `godot_project/models/`, a Godot 4 importa automaticamente a malha, materiais e a biblioteca de animações.
