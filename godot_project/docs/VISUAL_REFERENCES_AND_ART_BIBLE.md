# PROJETO AURORA — BÍBLIA DE ARTE & PACOTE DE REFERÊNCIAS VISUAIS
### Direção de Arte, Concept Art e Guia de Produção 3D para Godot Engine 4 (Android)

---

## 1. Identidade Visual e Filosofia de Design

* **Gênero**: Ação e Aventura Roguelite 3D.
* **Plataforma**: Android APK nativo (Godot Engine 4.3+ Mobile Renderer / OpenGL ES 3.0 / Vulkan).
* **Câmera**: Isométrica em perspectiva elevada ($45^\circ$), com transições dinâmicas para enquadramentos cinematográficos nas lutas de chefões.
* **Inspiração de Gameplay**: *Wizard of Legend 2* (combate mágico ágil, combos elementais, esquivas rápidas e salas conectadas), porém com identidade visual **100% original em Fantasia Sombria Cinematográfica**.
* **Paleta de Cores Mestre**:
  - *Azul Arcana / Aurora*: `#38BDF8` / `#0284C7` (elemento unificador de poder e despertar).
  - *Sombra e Obsidiana*: `#070B12` / `#0D131F` / `#151D2E` (estruturas, armaduras e ambiente).
  - *Pedra e Ruínas*: `#334155` / `#64748B` (granito antigo, musgo e colunas desmoronadas).
  - *Chama Vulcânica*: `#EA580C` / `#DC2626` / `#FBBF24` (contraste no Reino das Cinzas).

---

## 2. Herói Protagonista (O Desperto / Malachai)

### 2.1. Arquivo de Imagem Gerada
- **Localização**: `assets/concept_art/hero/hero_character_design_sheet.jpg`
- **Também disponível no app**: `app/src/main/res/drawable/img_concept_hero.jpg`

### 2.2. Características Anatômicas e Equipamento
- **Silhueta**: Proporção heroica atlética com capa longa rasgada na barra, ombros reforçados por dragonas de aço escurecido e capuz que projeta sombra sobre o rosto, deixando em evidência a barba cerrada e o olhar concentrado.
- **Armadura e Trajes**: Placas de aço temperado e couro reforçado sobre cota de malha flexível. Bolsas de alquimia e correias de couro cruzadas no peito para poções e pergaminhos.
- **Foco Mágico / Arma**: Espada de duas mãos com lâmina de corte largo gravada com runas célticas/arcanas que pulsam com partículas azuis incandescentes. A empunhadura permite canalização direta de feitiços através das manoplas rúnicas.
- **Silhueta Isométrica**: Capa esvoaçante e lâmina brilhante garantem legibilidade instantânea mesmo na visão isométrica distante com múltiplos inimigos na tela.

### 2.3. Prompts Adicionais para Produção 3D
- **Turnaround Ortográfico T-Pose**:
  ```text
  Orthographic character model sheet turnaround of the battle-mage protagonist Malachai, showing exact front view, side profile view, and back view in neutral A-pose. Slate plate armor with leather straps, hooded navy cloak with glowing cyan runes, runic broadsword on back. Clean neutral lighting, plain gray background, precise alignment lines, AAA 3D character modeling reference sheet.
  ```
- **Close-up Facial e Detalhe do Capuz**:
  ```text
  Extreme close-up portrait of the dark fantasy protagonist Malachai, rugged bearded battle-mage in his late 20s with intense piercing hazel eyes and a subtle glowing blue rune scar over his cheekbone, wearing a weathered deep navy hooded cowl with glowing cyan runic embroidery along the seams, dramatic volumetric rim lighting, AAA game cinematic face reference.
  ```

---

## 3. Inimigos (Bestiário e Escala)

Cada criatura foi projetada com contraste visual extremo para que o jogador identifique o padrão de ataque em milissegundos na visão isométrica.

### 3.1. Guerreiro Corpo a Corpo (*Cavaleiro das Cinzas Decadente*)
- **Papel**: Atacante frontal agressivo, avança com investidas e golpes pesados de clava.
- **Visual**: Armadura de ferro enferrujado com fendas incandescentes, elmo com chifres quebrados e escudo de madeira petrificada.
- **Prompt de Referência**:
  ```text
  Full body concept art of a decayed ash warrior enemy for an isometric dark fantasy roguelite game. Hulking humanoid skeleton encased in rusted iron plate armor, carrying a jagged spiked mace and a charred wooden tower shield, glowing embers leaking from helmet seams, clear silhouette, dramatic dark fantasy lighting, isolated on dark background, AAA game enemy sheet.
  ```

### 3.2. Mago Inimigo (*Canalizador das Sombras*)
- **Papel**: Atacante à distância, conjura orbes sombrios teleguiados e poças de lentidão.
- **Visual**: Mantos esfarrapados flutuantes, máscara de porcelana rachada, cajado de osso retorcido com crânio estelar.
- **Prompt de Referência**:
  ```text
  Concept art of a Shadow Channeler enemy cultist mage for an isometric 3D game. Floating gaunt figure in ragged charcoal robes and an ornate cracked porcelain mask, holding an ancient crooked bone staff topped with a swirling violet dark energy sphere, volumetric fog, dramatic rim light, clear readable silhouette, AAA concept art.
  ```

### 3.3. Criatura Rápida (*Espreitador Rúnico / Lurker*)
- **Papel**: Emboscador veloz que salta pelas costas do jogador e esquiva de projéteis.
- **Visual**: Besta quadrúpede esguia com carapaça de obsidiana, cauda chicote e olhos vermelhos múltiplos.
- **Prompt de Referência**:
  ```text
  Full body concept art of a fast quadrupedal Shadow Lurker beast for an isometric roguelite game. Sleek predatory alien-canine silhouette with chitinous obsidian carapace, whip-like spiked tail, multiple glowing crimson eyes, claws scraping the stone ground, dynamic stalking pose, dark fantasy lighting, AAA creature design sheet.
  ```

### 3.4. Criatura de Fogo (*Salamandra Ígnea / Magma Hound*)
- **Papel**: Inimigo de área que explode ao morrer e deixa trilhas de fogo.
- **Visual**: Cão de caça vulcânico feito de rocha derretida e placas basálticas, com mandíbula de lava viva.
- **Prompt de Referência**:
  ```text
  Concept art of a Magma Hound elemental fire creature for a 3D isometric dark fantasy game. Muscular hound-like monster formed of cracked black volcanic basalt rock with glowing orange lava flowing between cracks, flaming maw dripping molten slag, isolated on dark smoky background, high visual contrast for isometric view.
  ```

### 3.5. Criatura de Gelo (*Golias do Gelo Eterno / Frost Shard*)
- **Papel**: Miniboss tanque com alta armadura que dispara espinhos congelantes e congela o solo.
- **Visual**: Golem compacto de gelo glacial translúcido azul-claro com fragmentos de pedra nórdica incrustados.
- **Prompt de Referência**:
  ```text
  Concept art of a Frost Golem enemy for an isometric action game. Stocky ancient golem made of jagged translucent glacial ice and frozen blue granite, icy mist swirling around its spiky crystal fists, heavy stomp pose, high contrast cool blue and white palette on dark background, AAA creature reference.
  ```

### 3.6. Criatura Corrompida (*Horror Abissal / Void Spawn*)
- **Papel**: Inimigo aberrante com tentáculos que puxa o herói e cria barreiras de tentáculos.
- **Visual**: Massa pulsante de carne cinzenta, dentes expostos e tentáculos cobertos de runas negras corrompidas.
- **Prompt de Referência**:
  ```text
  Concept art of a Corrupted Void Horror abomination for a dark fantasy roguelite game. Grotesque Eldritch entity with writhing shadow tentacles, exposed jagged bone teeth, pulsing purple-black corruption cysts, readable silhouette for isometric top-down gameplay, dramatic dark lighting.
  ```

---

## 4. Chefões Monumentais

### 4.1. Vorghan, o Colosso de Pedra (Chefão do Capítulo 3)
- **Arquivo de Imagem Gerada**: `assets/concept_art/bosses/boss_vorghan_the_stone_colossus.jpg`
- **Também disponível no app**: `app/src/main/res/drawable/img_concept_vorghan.jpg`
- **Conceito Original e Dimensões**:
  - **Altura**: 25 a 30 metros de altura (o herói tem a altura de um dos dedos dos seus pés).
  - **Elmo**: Elmo integral fechado de granito esculpido, sem rosto visível, exceto pela fenda do visor onde brilham dois olhos azuis arcanos penetrantes.
  - **Tórax**: Gaiola torácica de pedra rachada contendo o **Núcleo de Aurora**, um vórtice estelar de energia azul pulsante que serve como ponto vulnerável em fases específicas.
  - **Arma**: Machado colossal de lâmina dupla forjado em pedra rúnica e ferro com correntes ancestrais.
- **Enquadramento de Câmera**: Transição dinâmica de câmera: a visão isométrica sobe e afasta suavemente ($60^\circ$ de inclinação, $18\text{m}$ de distância) com *camera shake* em cada pisada e golpe de machado.

### 4.2. Ignisur, o Senhor das Cinzas (Chefão do Capítulo 2)
- **Visual**: Gigante alado feito de ferro forjado e fumaça viva, com asas compostas por cinzas flamejantes e espada bastarda incandescente de 8 metros.
- **Prompt de Referência**:
  ```text
  Full body concept art of boss Ignisur the Ash Lord, an 18-meter tall fallen archangel covered in scorched black plate armor with smoldering coal wings dripping burning ash, brandishing an 8-meter blazing greatsword of white-hot magma, dramatic low-angle view with the tiny hero standing below, volcanic ruins arena, AAA boss concept art.
  ```

### 4.3. Val'Keth, o Guardião Celestial Corrompido (Chefão do Capítulo 4)
- **Visual**: Ser celestial de seis braços feito de mármore translúcido e ouro desgastado, cujas asas foram quebradas e substituídas por cristais de corrupção azul-violeta.
- **Prompt de Referência**:
  ```text
  Epic concept art of boss Val'Keth the Corrupted Celestial Guardian for a dark fantasy roguelite game. Six-armed divine statue entity 20 meters tall carved from pristine cracked white marble and gold filigree, corrupted by deep indigo cosmic energy crystals growing from its back, hovering above a celestial temple platform, radiant lighting contrast, AAA concept art.
  ```

---

## 5. Fases e Cenários (Os 4 Capítulos Roguelite)

### 5.1. Capítulo 01: O Despertar
- **Arquivo de Imagem Gerada**: `assets/concept_art/environments/chapter_01/chapter_01_ancient_forest_ruins.jpg`
- **Ambiente**: Floresta milenar com árvores gnarled cobertas de musgo azul, ruínas góticas com arcos de pedra e inscrições rúnicas brilhantes, trilhas de paralelepípedos úmidos e clareira final banhada por raios de sol celestial.
- **Mecânicas da Arena**: Pilares destrutíveis que atordoam inimigos ao caírem, tochas arcanas que recarregam MP mais rápido quando o herói fica próximo.

### 5.2. Capítulo 02: Reino das Cinzas
- **Arquivo de Imagem Gerada**: `assets/concept_art/environments/chapter_02/chapter_02_ash_realm_volcanic_fortress.jpg`
- **Ambiente**: Fortaleza de obsidiana estilhaçada encravada entre penhascos vulcânicos, cascatas de lava fluindo nas bordas da arena, fumaça e brasas suspensas.
- **Mecânicas da Arena**: Rachaduras no piso que liberam jatos de lava em intervalos periódicos e plataformas de basalto flutuantes.

### 5.3. Capítulo 03: O Colosso (Arena de Vorghan)
- **Arquivo de Imagem Gerada**: `assets/concept_art/environments/chapter_03/chapter_03_colossus_arena.jpg`
- **Ambiente**: Anfiteatro circular monumental de pedra negra desgastada, cercado por abismos sem fim e tempestade de relâmpagos azuis.
- **Mecânicas da Arena**: As batidas de machado de Vorghan criam crateras e ondas de choque que partem o piso da arena, forçando esquivas precisas.

### 5.4. Capítulo 04: O Coração de Aurora
- **Ambiente**: Templo suspenso no vácuo estelar, piso de mármore reflexivo com cristais gigantes de Aurora flutuando e aurora boreal mística no horizonte cósmico.
- **Prompt de Referência**:
  ```text
  Cinematic environment concept art of The Heart of Aurora celestial temple for Chapter 4. Polished obsidian and marble floating sky platforms connected by bridges of solid light, giant floating luminescent cyan crystals pulsing with cosmic mana, infinite starfield and aurora borealis in the cosmic void background, divine grandeur, AAA environment art.
  ```

---

## 6. Magias e Efeitos Visuais (VFX Pipeline)

A magia em *Projeto Aurora* combina estética física de impacto com faíscas estelares azuis:

1. **Aurora Bolt (Projétil Mágico Azul)**:
   - *Visual*: Orbe denso azul-safira com rastro de cometa e arcos elétricos azuis.
   - *Impacto*: Ao colidir, fragmenta-se em dezenas de fagulhas aditivas com distorção esférica rápida.
2. **Combustão Ígnea (Magia de Fogo)**:
   - *Visual*: Explosão radial em flor de lótus de fogo laranja e amarelo, com fuligem negra voando em vórtice.
3. **Lanças de Gelo Glacial**:
   - *Visual*: Cristais afiados brotando do solo em linha reta com névoa fria esbranquiçada e estilhaços pontiagudos.
4. **Arco Voltaico (Relâmpago)**:
   - *Visual*: Descarga elétrica ramificada instantânea que ricocheteia entre até 4 alvos adjacentes.
5. **Escudo Arcana (Campo de Energia)**:
   - *Visual*: Cúpula geométrica translúcida com malha hexagonal que absorve impactos e brilha intensamente no ponto de choque.
6. **Ethereal Dash (Esquiva com Rastro)**:
   - *Visual*: O herói se desfaz momentaneamente em uma silhueta etérea esfumaçada em azul-elétrico com partículas que desaceleram no ar.

---

## 7. Interface e Gameplay Roguelite (UI/UX)

- **Arquivo de Imagem Gerada**: `assets/concept_art/ui/gameplay_isometric_combat_hud.jpg`
- **Também disponível no app**: `app/src/main/res/drawable/img_concept_gameplay_ui.jpg`

### 7.1. Componentes do HUD
- **Orbe de Vida (Superior Esquerdo)**: Globo de rubi com líquido carmesim e moldura de prata antiga com ranhuras de nível e runas ativas.
- **Orbe de Mana (Superior Esquerdo)**: Globo de safira com energia azul em turbilhão líquido e indicador numérico.
- **Minimapa Roguelite (Superior Direito)**: Esquema de salas conectadas indicando salas normais, salas de tesouro, fonte de cura e a câmara do Colosso.
- **Analógico Virtual Flutuante (Inferior Esquerdo)**: Anel com glifos arcanos que se acendem na direção do movimento, adaptando-se ergonomicamente ao polegar do jogador.
- **Cluster de Ações (Inferior Direito)**:
  - Botão de Ataque Físico / Foco Mágico.
  - Botão de Esquiva com contador de recarga em segundos.
  - 3 Slots de Feitiços Elementais adquiridos no decorrer da run (Fogo, Raio, Gelo, Força).
  - Botão de Magia Suprema (*Ultimate: Aurora Awakened*).

### 7.2. Telas Adicionais Roguelite (Prompts de Referência)
- **Tela de Seleção de Magias (Grimório de Draft)**:
  ```text
  UI concept art of a spell drafting reward screen for a 3D dark fantasy roguelite game. Ancient ornate stone grimoire opened on screen, presenting 3 glowing elemental spell cards (Fire Storm, Frost Spear, Arcane Nova) with detailed runic illustrations, rarity borders (Rare, Epic, Legendary), stats and descriptions, dark vignette background, AAA game UI mockup.
  ```
- **Tela de Vitória / Derrota**:
  ```text
  UI concept art of a victory and defeat death screen for a dark fantasy roguelite game. Ornate gothic metallic banner reading 'O DESPERTO CAIU' with cracked stone details, runic progression stats (Enemies Slain, Mana Shards Collected, Run Time), a glowing crystal revive button, dark moody atmospheric backdrop with floating blue embers.
  ```

---

## 8. Diretrizes Técnicas para Modelagem e Exportação 3D (Blender -> Godot 4.3)

| Elemento | Orçamento de Polígonos (Triângulos) | Rig / Bones | Mapas de Textura (PBR) | LODs |
|---|---|---|---|---|
| **Protagonista** | 12.000 - 15.000 tris | 42 bones (Humanoid + Cape) | Albedo (2K), Normal (2K), ORM (2K), Emission (1K) | LOD0: 100%, LOD1: 50% |
| **Inimigos Normais** | 4.000 - 6.000 tris | 28 - 34 bones | Albedo (1K), Normal (1K), ORM (1K), Emission (512) | LOD0: 100%, LOD1: 40% |
| **Vorghan (Colosso)**| 35.000 - 45.000 tris | 64 bones (IK chains no machado) | Albedo (4K), Normal (4K), ORM (4K), Emission (2K) | LOD0: 100%, LOD1: 60%, LOD2: 25% |
| **Arenas / Salas** | 20.000 - 30.000 tris | Estático (StaticBody3D) | Trim sheets modulares de pedra + Vertex Color blend | Occlusion Culling |

*Formato de Exportação Obrigatório*: **glTF 2.0 (.glb)** com materiais acoplados e NLA Tracks de animações esqueléticas para importação nativa na Godot 4.
