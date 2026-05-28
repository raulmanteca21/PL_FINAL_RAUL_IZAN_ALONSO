# CAMBIO_DEFINITIVO_VISUAL

## 1. Objetivo visual

El objetivo de este documento es fijar una transformacion visual definitiva para la interfaz JavaFX de Casino Escape sin modificar gameplay, reglas, datos ni arquitectura central.

La meta es que la demo parezca una mesa de casino universitaria cuidada: mas ambientada, mas clara, mas coherente y mas profesional, manteniendo el proyecto dentro de un alcance realista para JavaFX y entrega academica.

La transformacion debe apoyarse en la interfaz existente:

- `CasinoEscapeApp` arranca desde `config/game_config.json` mediante `GameConfigLoader`.
- `GameController` compone la pantalla con `BorderPane`.
- El centro usa `RoomHeaderView` y `RoomGridView`.
- El tablero sigue siendo `GridPane`.
- Los paneles actuales se mantienen: jugador, inventario, ruta, enemigos, log y acciones.
- La UI sigue mostrando estado y solicitando acciones a `Game`.

No se busca crear un motor grafico ni un videojuego visualmente complejo. Se busca una interfaz clara, presentable y tematizada.

## 2. Filosofia estetica

La estetica debe inspirarse en un casino clasico y una mesa de poker:

- Fondo general verde oscuro, como tapete.
- Bordes dorados para simular decoracion de casino.
- Rojo granate para cabecera, combate y riesgo.
- Negro elegante para barra de acciones y contrastes.
- Crema claro para paneles de informacion, como cartas o fichas sobre la mesa.
- Acentos de cartas: `♠`, `♥`, `♦`, `♣`.

Principios visuales:

- Claridad antes que decoracion.
- Pocos colores, siempre consistentes.
- Textos cortos en el tablero.
- Paneles con jerarquia clara.
- Sin ruido visual que complique explicar BFS, turnos, inventario o ruta.
- Mantener la demo usable a 1200x800.

No se debe perseguir realismo extremo. Una apariencia tipo cartel de casino y mesa de juego es suficiente.

## 3. Restricciones tecnicas

Restricciones absolutas:

- No tocar `Game.java`.
- No tocar logica de juego.
- No tocar BFS.
- No tocar movimiento normal ni movimiento especial.
- No tocar `MovementService`.
- No tocar `PathFinder`.
- No tocar `ReachableCellsCalculator`.
- No tocar IA enemiga.
- No tocar combate.
- No tocar turnos.
- No tocar persistencia.
- No tocar JSON.
- No tocar `config/game_config.json`.
- No tocar estructuras propias.
- No tocar tests.
- No tocar condicion de victoria.
- No tocar grafo, salas, puertas, matriz ni transitabilidad.

Restricciones de interfaz:

- Mantener JavaFX actual.
- Mantener `BorderPane` general.
- Mantener `GridPane` para tablero.
- Mantener paneles actuales.
- No cambiar a FXML.
- No usar `Canvas`.
- No introducir librerias externas.
- No introducir musica, sonidos, particulas ni animaciones complejas.
- No usar imagenes pesadas.
- No convertir `RoomGridView` en un controlador secundario.
- No duplicar reglas de juego en JavaFX.

La UI puede:

- Cambiar colores, bordes, fuentes, tamanos y textos.
- Mostrar simbolos decorativos.
- Aplicar estilos a botones y paneles.
- Mejorar tooltips.
- Prefiltrar visualmente disponibilidad ya expuesta por `Game`, como ya ocurre con tienda, ruleta y movimiento en linea.

## 4. Estado actual

Estado actual detectado en `src/main/java/casinoescape/ui`:

- `CasinoEscapeApp.java`: crea escena 1200x800 y carga partida inicial desde `config/game_config.json`.
- `GameController.java`: usa `BorderPane`, panel izquierdo de 245 px, panel derecho de 300 px, centro con cabecera y tablero, barra inferior de acciones.
- `RoomHeaderView.java`: muestra `Sala n - Nombre` con fondo crema y borde dorado.
- `RoomGridView.java`: tablero `GridPane`, botones de 82 px, simbolos ASCII y colores inline por `CellType`.
- `PlayerPanelView.java`: panel `Mesa del jugador`, datos de sala, vida, ataque, defensa, movimiento, fichas, turnos y resultado final.
- `InventoryPanelView.java`: panel `Caja de fichas`, lista textual con etiquetas `[ARMA]`, `[ARMADURA]`, `[CONSUMIBLE]`, `[LLAVE]`, y `[EQUIPADO]`.
- `RoutePanelView.java`: panel `Plano del casino`, muestra sala actual, conexiones, una caja `Mapa textual:`, ruta recomendada, distancia, siguiente sala y distancia a puerta/salida.
- `EnemyInfoPanelView.java`: panel `Mesa de enemigos`, click en enemigo muestra estadisticas sin atacar.
- `LogPanelView.java`: panel `Registro de la mesa`, usa `TextArea` plano.
- `ActionPanelView.java`: barra inferior con botones contextuales, tooltips y `Volver a jugar` al finalizar.

Punto critico actual:

- `RoutePanelView` contiene un label `minimap` que genera una caja con `Mapa textual:` y todas las conexiones. Esta caja debe eliminarse completamente de la UI definitiva.

## 5. Transformacion visual completa

### 5.1 Estructura general

Mantener composicion actual:

- Izquierda: jugador e inventario.
- Centro: cabecera casino y tablero.
- Derecha: plano, enemigos y registro.
- Abajo: acciones.

Cambios visuales concretos:

- Fondo raiz: verde casino muy oscuro `#06281F`.
- Separacion entre zonas: mantener 10-12 px.
- Paneles: fondo crema `#FFF4D6` o verde oscuro secundario segun jerarquia.
- Bordes: dorado `#D4AF37`, 2-3 px.
- Titulos: granate/dorado, con peso bold.

No cambiar tamaños estructurales de paneles inicialmente. El panel derecho ya tiene 300 px y el izquierdo 245 px; cambiar eso puede provocar que el tablero de 7x7 pierda espacio.

### 5.2 Cabecera central casino

Transformar la cabecera actual:

```text
Sala 1 - Hall / Entrada
```

En un cartel tipo casino:

```text
♠ ♥ ♦ ♣  SALA 1 - HALL / ENTRADA  ♣ ♦ ♥ ♠
```

Estilo recomendado:

- Fondo: granate casino `#6E1B1B`.
- Borde: dorado `#D4AF37`, 4 px.
- Texto: dorado claro `#F6D36B`.
- Fuente: 24-26 px, bold.
- Alineacion: centrada.
- Padding: 12-14 px.
- Efecto opcional seguro: borde doble simulado con `-fx-border-width: 3;` y fondo oscuro.

No usar descripcion larga de sala. La cabecera debe orientar, no contar lore.

### 5.3 Panel Mesa del jugador

Mantener `PlayerPanelView`, pero hacer que parezca una tarjeta de jugador en mesa de casino.

Titulo recomendado:

```text
♠ Mesa del jugador
```

Representacion recomendada:

- Sala: `Mesa: Sala 1 - Hall / Entrada`.
- Vida: `♥ Vida: 30/30`.
- Ataque: `♦ Ataque: 4`.
- Defensa: `♣ Escudo: 0`.
- Movimiento: `→ Movimiento: 3` o `Mov.: 3` si falta espacio.
- Fichas: `$ Fichas: 0` o `Fichas: 0` si se prefiere ASCII puro.
- Turnos: `Turnos: 100`.
- Resultado final: `Resultado: Victoria` en dorado/verde o `Resultado: Derrota` en rojo.

Estilo recomendado:

- Fondo: crema carta `#FFF4D6`.
- Borde: dorado `#D4AF37`, 3 px.
- Titulo: granate `#5A1717`, 18 px bold.
- Texto principal: negro suave `#17130A`, 13-14 px.
- Resultado victoria: verde `#1F7A3A`.
- Resultado derrota: rojo `#8B1E1E`.

Riesgo:

- Unicode como `♥`, `♦`, `♣` suele funcionar en JavaFX/Windows, pero debe probarse. Si no renderiza bien, volver a ASCII: `[VIDA]`, `[ATQ]`, `[ESC]`.

### 5.4 Panel Caja de fichas

Mantener `InventoryPanelView` y sus etiquetas actuales.

Titulo recomendado:

```text
♦ Caja de fichas
```

Etiquetas obligatorias a conservar:

- `[ARMA]`
- `[ARMADURA]`
- `[CONSUMIBLE]`
- `[LLAVE]`
- `[EQUIPADO]`

Mejora visual concreta:

- Mantener `ListView<String>`.
- Fondo del panel: crema `#FFF4D6`.
- Fondo de lista: blanco calido `#FFFDF2`.
- Borde lista: dorado apagado `#B88A2A`.
- Texto equipado: mantener `[EQUIPADO]` al final para no tocar seleccion ni modelo.
- Labels de equipamiento:
  - `Arma activa: Botella rota`.
  - `Armadura activa: ninguna`.

No hacer:

- No crear tarjetas por objeto.
- No implementar drag and drop.
- No cambiar la seleccion de `ListView`.
- No tocar `Inventory`.

### 5.5 Tablero central

Mantener `RoomGridView` con `GridPane` y botones de 82 px salvo que una prueba manual demuestre texto cortado grave.

Simbolos recomendados:

- EMPTY: `.`
- OBSTACLE: `#`
- PLAYER: `J`
- ENEMY: `E`
- ITEM: `OBJ`
- DOOR: `P->n`
- DOOR bloqueada: `LOCK->n`, con alternativa `L->n` si se corta.
- NPC: `NPC`, con alternativa `N` si se corta.
- TRAP: `TRAP`
- SHOP: `BAR`
- EXIT: `SALIDA`, con alternativa `OUT` si se corta.
- MINIGAME: `RULETA`, con alternativa `RUL` si se corta.

Colores recomendados por celda:

- EMPTY no alcanzable: verde tapete `#0F4A36`.
- EMPTY alcanzable: verde luminoso `#4FD06B`.
- OBSTACLE: negro carbon `#1B1B1B`.
- PLAYER: dorado `#D4AF37` con texto negro `#111111`.
- ENEMY: rojo casino `#9E1B1B` con texto crema `#FFF4D6`.
- ITEM: amarillo ficha `#F2C94C` con texto negro.
- DOOR: azul acero `#2F6F9F` o dorado oscuro `#8C6A21`.
- DOOR bloqueada: gris azulado `#455A64` con borde rojo `#8B1E1E`.
- NPC: naranja madera `#B66A2C`.
- TRAP: rojo peligro `#C0392B`.
- SHOP: dorado bar `#E0B84F`.
- EXIT: verde victoria `#2E8B57`.
- MINIGAME: morado ruleta `#6C4AB6`.

Bordes recomendados:

- Borde normal de celda: verde muy oscuro `#06281F`, 2 px.
- Borde jugador: dorado claro `#F6D36B`, 3 px.
- Borde enemigo: rojo oscuro `#4A0D0D`, 2 px.
- Borde puerta bloqueada: rojo `#8B1E1E`, 3 px.

Riesgo de legibilidad:

- `LOCK->3`, `RULETA` y `SALIDA` son los textos mas propensos a cortarse en 82 px.
- No reducir fuente por debajo de 12 px.
- Si se corta, preferir abreviar texto antes que aumentar mucho la celda.

### 5.6 Puertas

Mantener obligatoriamente el formato:

```text
P->2
```

Puerta normal:

- Texto: `P->n`.
- Fondo: azul acero `#2F6F9F`.
- Texto: crema `#FFF4D6`.
- Borde: dorado `#D4AF37`.

Puerta bloqueada:

- Texto principal: `LOCK->3`.
- Alternativa si se corta: `L->3`.
- Fondo: gris pizarra `#455A64`.
- Texto: dorado claro `#F6D36B`.
- Borde: rojo oscuro `#8B1E1E`, 3 px.

No modificar:

- `Door`.
- `Game.useDoorAt(...)`.
- Validacion de llave.
- Transitabilidad de puertas.
- Reglas de cambio de sala.

### 5.7 Panel Plano del casino

Cambio obligatorio:

- Eliminar completamente la caja `Mapa textual:`.
- No mostrar el listado completo de conexiones por cada sala.
- No mostrar un pseudo-grafo grande.

Estructura limpia recomendada:

```text
♣ Plano del casino
Sala actual: 5 - Bar
Conexiones: 2 -> 4 -> 6 -> 7
Ruta recomendada: 5 -> 7 -> 8
Siguiente sala: 7
Distancia salas: 2
Distancia puerta/salida: 4
```

Estilo:

- Fondo: verde oscuro secundario `#0B3D2E`.
- Borde: dorado `#D4AF37`, 3 px.
- Titulo: dorado claro `#F6D36B`, 18 px bold.
- Texto principal: crema `#FFF4D6`.
- Ruta recomendada: dorado `#F2C94C`, bold.
- Distancias: verde claro `#A7D7A9`.

Recomendacion tecnica:

- En `RoutePanelView`, quitar el label `minimap` del layout.
- Eliminar o dejar sin uso `formatMinimap(...)` solo si se implementa despues. Como este documento no implementa, la decision futura debe ser: no renderizar `minimap`.
- Mantener `formatPath(...)` para conexiones y ruta recomendada.

Riesgo:

- Si se elimina demasiada informacion, el panel puede parecer pobre. Se compensa con buena jerarquia visual y ruta recomendada clara.

### 5.8 Panel Mesa de enemigos

Mantener `EnemyInfoPanelView`.

Titulo recomendado:

```text
♥ Mesa de enemigos
```

Representacion recomendada:

- `Enemigo: Crupier de Blackjack`.
- `♥ Vida: 14/14`.
- `♦ Ataque: 4`.
- `♣ Escudo: 1`.
- `Movimiento: aproximacion por BFS`.
- `Recompensa: 15 fichas, Traje con escudo`.
- `Atacar solo con boton Atacar`.

Estilo:

- Fondo: crema `#FFF4D6` o rojo muy oscuro `#2A0F0F` si se quiere mas dramatismo.
- Recomendacion segura: crema para legibilidad.
- Borde: rojo casino `#9E1B1B` y dorado `#D4AF37`.
- Titulo: rojo oscuro `#5A1717`, 18 px bold.
- Hint: gris oscuro `#4A4030`, 12 px, wrap.

No hacer:

- No atacar desde click.
- No mostrar `Alert` al click si ya existe panel lateral.
- No implementar hover complejo.
- No calcular dano esperado en UI.

### 5.9 Panel Registro de la mesa

Mantener `LogPanelView` con `TextArea`.

Titulo recomendado:

```text
♠ Registro de la mesa
```

Estilo:

- Panel: crema `#FFF4D6`.
- Borde: dorado `#D4AF37`.
- `TextArea` fondo: negro verdoso `#071A14`.
- Texto log: crema `#FFF4D6`.
- Fuente log: `Consolas`, 12-13 px.
- Borde interno: dorado oscuro `#8C6A21`.

Estilos de mensajes recomendados, sin tocar `GameLog`:

- Movimiento: mantener texto actual, opcionalmente prefijar visualmente en UI si contiene `Movimiento`.
- Combate: detectar texto con `Ataque`, `dano`, `Enemigo derrotado` para prefijo superficial `[COMBATE]`.
- Objetos: detectar `Objeto`, `Compra`, `recogido` para `[OBJ]`.
- Sistema: detectar `Victoria`, `Derrota`, `Cambio de sala` para `[SISTEMA]`.

Recomendacion honesta:

- No hacer categorizacion por colores por linea ahora. `TextArea` no permite estilos por linea de forma simple.
- Cambiar a `ListView<String>` seria posible, pero es un cambio moderado y no imprescindible.
- Para esta fase, basta con estilo global del `TextArea` y mejor titulo.

### 5.10 Botones

Mantener `ActionPanelView` y `FlowPane`.

Estilo base:

- Altura minima: 34-38 px.
- Padding: 8 px horizontal.
- Fuente: 12-13 px bold.
- Borde: dorado `#D4AF37`, 1-2 px.
- Radio opcional: 4 px si se mantiene sobrio.

Prioridad visual por boton:

- `Atacar`: rojo `#9E1B1B`, texto crema `#FFF4D6`.
- `Finalizar turno`: verde `#1F7A3A`, texto crema.
- `Guardar`: dorado `#D4AF37`, texto negro.
- `Cargar`: dorado oscuro `#B88A2A`, texto negro.
- `Volver a jugar`: dorado claro `#F6D36B`, texto granate `#5A1717`, borde rojo.
- `Tienda`: amarillo ficha `#F2C94C`, texto negro.
- `Ruleta`: morado `#6C4AB6`, texto crema.
- `Usar puerta`: azul `#2F6F9F`, texto crema.
- `Recoger`, `Usar objeto`, `Equipar arma`, `Equipar armadura`, `Interactuar`: crema `#FFF4D6`, texto negro, borde dorado.
- `Movimiento linea` activo: verde luminoso `#4FD06B`, texto negro.
- `Requiere Pastilla` deshabilitado: gris oscuro `#3A3A3A`, texto gris claro `#9E9E9E`.

Estados deshabilitados:

- Fondo: `#333333`.
- Texto: `#8A8A8A`.
- Borde: `#555555`.
- No ocultar botones contextuales salvo `Volver a jugar`, que ya aparece solo al finalizar.

Iconografia de botones recomendada:

- `Atacar`: `Atacar` o `♦ Atacar`.
- `Finalizar turno`: `Finalizar turno` o `♣ Finalizar turno`.
- `Guardar`: `Guardar`.
- `Cargar`: `Cargar`.
- `Tienda`: `BAR / Tienda` si cabe.
- `Ruleta`: `RULETA`.

No usar emojis de espada, disquete, calavera o ficha porque pueden renderizar distinto y romper alineacion.

## 6. Nueva paleta de colores

Paleta definitiva recomendada:

```text
Fondo principal casino:        #06281F
Fondo secundario verde:        #0B3D2E
Fondo tapete tablero:          #0F4A36
Fondo panel crema:             #FFF4D6
Fondo texto/log oscuro:        #071A14
Texto principal oscuro:        #17130A
Texto principal claro:         #FFF4D6
Texto secundario:              #4A4030
Dorado principal:              #D4AF37
Dorado claro:                  #F6D36B
Dorado oscuro:                 #8C6A21
Amarillo ficha:                #F2C94C
Rojo casino:                   #9E1B1B
Rojo oscuro:                   #5A1717
Rojo peligro:                  #C0392B
Verde accion:                  #1F7A3A
Verde alcanzable:              #4FD06B
Verde victoria:                #2E8B57
Azul puerta:                   #2F6F9F
Gris puerta bloqueada:         #455A64
Gris deshabilitado fondo:      #333333
Gris deshabilitado texto:      #8A8A8A
Negro elegante:                #141414
Negro obstaculo:               #1B1B1B
Morado ruleta:                 #6C4AB6
Naranja NPC:                   #B66A2C
```

Uso obligatorio de la paleta:

- No introducir colores nuevos sin motivo.
- No usar colores saturados fuera de peligro, ruta o acciones.
- Mantener contraste alto en tablero.

## 7. Iconografia recomendada

Decision recomendada:

- Usar ASCII para tablero.
- Usar Unicode simple de palos de cartas en titulos y paneles: `♠`, `♥`, `♦`, `♣`.
- Evitar emojis.

Permitido:

- `♠`, `♥`, `♦`, `♣` en cabecera y titulos.
- `J`, `E`, `OBJ`, `TRAP`, `BAR`, `RULETA`, `SALIDA`, `P->n`, `LOCK->n` en tablero.
- `$` para fichas si se quiere ASCII estable.
- `->` para rutas y puertas.

Evitar:

- Emojis como puerta, llave, calavera, ficha, bomba, corazon emoji o ruleta emoji.
- Simbolos que cambien tamaño de botones.
- Iconos externos.

Compatibilidad:

- Los palos de cartas suelen funcionar en JavaFX en Windows, pero deben probarse en IntelliJ y Maven.
- Si algun simbolo falla, sustituir por ASCII: `[PICAS]`, `[CORAZON]` no merece la pena; mejor quitar decoracion.

## 8. Tipografia recomendada

Usar fuentes del sistema para evitar problemas:

- Titulos principales: `System`, bold, 18-26 px.
- Cabecera central: 24-26 px, bold.
- Titulos de panel: 18 px, bold.
- Texto de panel: 13-14 px.
- Texto de tablero: 12-13 px, bold.
- Log: `Consolas`, 12-13 px.
- Tooltips: default JavaFX.

Jerarquia:

- Cabecera central debe ser lo mas visible.
- Titulos de panel deben ser consistentes.
- Tablero debe priorizar legibilidad sobre decoracion.
- Log no debe competir visualmente con tablero.

No usar fuentes externas. No cargar `.ttf`.

## 9. Cambios por archivo

Archivos UI que se pueden tocar en una implementacion futura:

- `src/main/java/casinoescape/ui/GameController.java`
- `src/main/java/casinoescape/ui/RoomHeaderView.java`
- `src/main/java/casinoescape/ui/RoomGridView.java`
- `src/main/java/casinoescape/ui/PlayerPanelView.java`
- `src/main/java/casinoescape/ui/InventoryPanelView.java`
- `src/main/java/casinoescape/ui/RoutePanelView.java`
- `src/main/java/casinoescape/ui/EnemyInfoPanelView.java`
- `src/main/java/casinoescape/ui/LogPanelView.java`
- `src/main/java/casinoescape/ui/ActionPanelView.java`

Cambios concretos por archivo:

- `GameController.java`: ajustar fondo raiz, separaciones y, solo si hace falta, anchos de paneles. No tocar handlers ni flujo de acciones salvo estilos/composicion.
- `RoomHeaderView.java`: convertir cabecera en cartel casino con palos de cartas, fondo granate, borde dorado y texto dorado.
- `RoomGridView.java`: actualizar paleta de celdas, bordes, texto y posiblemente `NPC` si cabe. Mantener `CELL_SIZE = 82` inicialmente.
- `PlayerPanelView.java`: aplicar estilo de panel, titulos con iconografia y labels mas visuales. No cambiar datos leidos.
- `InventoryPanelView.java`: aplicar estilo casino a panel y lista. Mantener etiquetas actuales y seleccion actual.
- `RoutePanelView.java`: eliminar visualmente `Mapa textual:` y dejar panel limpio con sala, conexiones, ruta, siguiente sala y distancias.
- `EnemyInfoPanelView.java`: aplicar estilo de mesa de enemigo y simbolos simples. Mantener lectura de `Enemy`.
- `LogPanelView.java`: estilo oscuro en `TextArea`, titulo de casino y fuente monoespaciada.
- `ActionPanelView.java`: redisenar botones por prioridad visual y estados deshabilitados. Mantener callbacks.

Archivos que NO deben tocarse:

- `src/main/java/casinoescape/game/Game.java`
- `src/main/java/casinoescape/movement/MovementService.java`
- `src/main/java/casinoescape/movement/PathFinder.java`
- `src/main/java/casinoescape/movement/ReachableCellsCalculator.java`
- `src/main/java/casinoescape/movement/EnemyMovementService.java`
- `src/main/java/casinoescape/combat/*`
- `src/main/java/casinoescape/items/*`
- `src/main/java/casinoescape/model/*`
- `src/main/java/casinoescape/persistence/*`
- `src/main/java/casinoescape/structures/*`
- `src/test/java/*`
- `config/game_config.json`
- `saves/savegame.json`
- `pom.xml`

Sobre CSS:

- Opcion segura inicial: mantener estilos inline, porque el proyecto ya los usa.
- Opcion moderada: crear `src/main/resources/casinoescape/ui/casino.css` solo para estilos repetidos de paneles y botones.
- No mover `RoomGridView` a CSS de golpe porque sus estilos dependen de `CellType` y estado alcanzable.
- Si se crea CSS, `CasinoEscapeApp` tendria que cargarlo en la escena; eso es aceptable solo si se prueba bien y si hay fallback visual.

Recomendacion actual:

- Primera implementacion visual: inline.
- CSS solo en una segunda fase si hay tiempo.

## 10. Orden ideal de implementacion

No hacer todo de golpe. Implementar por bloques pequeños:

1. Bloque 1: cabecera y fondo general.
2. Bloque 2: eliminar `Mapa textual:` de `RoutePanelView` y limpiar `Plano del casino`.
3. Bloque 3: paleta del tablero en `RoomGridView`.
4. Bloque 4: paneles izquierdos, `PlayerPanelView` e `InventoryPanelView`.
5. Bloque 5: paneles derechos, `EnemyInfoPanelView` y `LogPanelView`.
6. Bloque 6: botones de `ActionPanelView` por prioridad visual.
7. Bloque 7: prueba manual completa de legibilidad y ajustes menores.
8. Bloque 8 opcional: valorar CSS pequeño si los estilos inline se vuelven demasiado repetidos.

Validacion despues de cada bloque:

- Abrir JavaFX.
- Confirmar que sigue arrancando desde `config/game_config.json`.
- Confirmar que no se han tocado reglas.
- Revisar tablero en sala 1.
- Revisar que los botones siguen respondiendo.

## 11. Riesgos visuales

Riesgos reales:

- Texto cortado en celdas de 82 px, especialmente `LOCK->3`, `RULETA` y `SALIDA`.
- Exceso de dorado y rojo puede hacer la UI menos legible.
- Unicode puede renderizar distinto en distintos equipos.
- Estilos inline largos pueden duplicarse y dificultar mantenimiento.
- Cambiar anchos de panel puede comprimir el tablero.
- Cambiar `TextArea` por `ListView` para log puede introducir codigo innecesario.
- Meter CSS sin probar rutas puede hacer que la UI pierda estilos al ejecutar desde Maven o IntelliJ.
- Intentar resaltar demasiados estados en tablero puede confundir alcanzable, puerta, enemigo y seleccion.
- Modificar `GameController` mas de la cuenta puede romper guardar, cargar, volver a jugar o dialogos.

Pruebas manuales obligatorias:

- Arrancar JavaFX desde la raiz del proyecto.
- Confirmar sala inicial y tablero 7x7.
- Confirmar que no aparece `Mapa textual:`.
- Confirmar que `Plano del casino` muestra ruta recomendada, siguiente sala y distancias.
- Confirmar que `P->n` y `LOCK->n` caben y se leen.
- Confirmar que `RULETA`, `SALIDA`, `BAR`, `TRAP`, `OBJ`, `E` y `J` son legibles.
- Confirmar que las casillas alcanzables destacan claramente.
- Confirmar que `Atacar` es rojo y no se confunde con otros botones.
- Confirmar que `Finalizar turno` es verde.
- Confirmar que `Guardar` y `Cargar` son dorados.
- Confirmar que botones deshabilitados se ven como deshabilitados.
- Confirmar que el panel de enemigo sigue actualizandose con click.
- Confirmar que la seleccion de inventario sigue funcionando.
- Confirmar que log sigue haciendo scroll al final.
- Confirmar que la ventana 1200x800 sigue siendo usable.

## 12. Que NO merece la pena tocar

No merece la pena:

- Rehacer toda la UI.
- Cambiar `BorderPane` por un layout nuevo.
- Cambiar `GridPane` por `Canvas`.
- Usar FXML.
- Crear sprites o imagenes para cada celda.
- Meter musica o sonidos.
- Crear animaciones de dano.
- Crear particulas.
- Usar emojis como lenguaje visual principal.
- Crear un minimapa grafico de nodos y aristas.
- Cambiar `GameLog` para categorizar mensajes.
- Cambiar persistencia para guardar preferencias visuales.
- Cambiar JSON para temas.
- Cambiar balance para que la UI parezca mas dinamica.
- Introducir dependencias externas.

Mejoras demasiado peligrosas ahora:

- Cambiar `LogPanelView` a `ListView` con celdas coloreadas.
- Pasar todos los estilos a CSS en una sola fase.
- Resaltar interactivos adyacentes calculando mucha logica en UI.
- Cambiar el flujo de click del tablero.
- Sustituir alertas por pantallas finales completas.

## 13. Recomendacion final realista

La ruta recomendada es una tematizacion fuerte pero incremental.

Prioridad real:

1. Eliminar `Mapa textual:` del panel `Plano del casino`.
2. Aplicar paleta casino global coherente.
3. Convertir la cabecera en cartel visual con `♠ ♥ ♦ ♣`.
4. Mejorar tablero sin cambiar `GridPane` ni simbolos base.
5. Redisenar botones por prioridad visual.
6. Pulir paneles laterales con fondos, bordes, titulos e iconografia simple.
7. Mantener log como `TextArea` estilizado.

Conclusion:

Casino Escape ya tiene una UI funcional y suficientemente organizada. El cambio definitivo no debe ser un rediseño tecnico, sino una capa visual coherente sobre lo existente. La mejor mejora es hacer que parezca una mesa de casino clara, no convertirlo en un juego grafico complejo.

Si una implementacion futura necesita tocar `Game`, BFS, movimiento, JSON, persistencia, combate, turnos, estructuras o tests para lograr una mejora visual, esa mejora debe rechazarse.
