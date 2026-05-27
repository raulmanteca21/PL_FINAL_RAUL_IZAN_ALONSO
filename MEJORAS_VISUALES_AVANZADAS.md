# MEJORAS_VISUALES_AVANZADAS

## 1. Objetivo

Este documento estudia mejoras visuales avanzadas para Casino Escape sin autorizar todavia su implementacion.

El objetivo es que la demo sea mas clara, ambientada y profesional, manteniendo el proyecto dentro de un alcance universitario razonable. Las mejoras propuestas deben apoyarse en la interfaz JavaFX actual, conservar el `GridPane` de la sala, evitar refactors grandes y no trasladar reglas de juego a la UI.

La prioridad real no es convertir el juego en un videojuego grafico complejo, sino mejorar la presentacion sin poner en riesgo la estabilidad ya conseguida.

## 2. Restricciones tecnicas

No se puede tocar bajo ningun concepto en esta fase:

- BFS de movimiento.
- Movimiento normal ni movimiento especial.
- Reglas de turno.
- Orden movimiento antes de accion.
- Formula oficial de dano.
- Persistencia.
- Guardado y carga.
- `config/game_config.json` como fuente de arranque JavaFX.
- JSON base.
- Estructuras propias.
- `Game` como nucleo de reglas.
- Transitabilidad de celdas.
- Interaccion desde adyacencia ortogonal.
- Logica de enemigos.
- IA de enemigos.
- Grafo de habitaciones.
- Identidad de salas.
- Condicion de victoria.
- Arquitectura principal.
- Tests existentes.
- Dependencias del proyecto.

Reglas de trabajo para cualquier implementacion futura:

- JavaFX solo puede mostrar estado, pedir acciones al usuario y prefiltrar UX.
- La verdad de movimiento, combate, economia, victoria, derrota y turnos sigue estando en `Game` y servicios de dominio.
- No se deben usar `ArrayList`, `HashMap`, `LinkedList`, `Queue`, `Stack`, `Deque` ni equivalentes prohibidos para resolver logica central.
- Si se usa alguna coleccion JavaFX o API visual, debe quedar limitada a la capa `ui`.
- No se deben introducir librerias nuevas.
- No se deben introducir assets pesados.
- No se debe rehacer la UI completa.
- No se deben crear animaciones complejas.

## 3. Estado visual actual

Estado JavaFX actual detectado:

- `CasinoEscapeApp` arranca desde `config/game_config.json` mediante `GameConfigLoader`.
- `GameController` centraliza eventos, refresco de vistas, guardar, cargar, volver a jugar y dialogos simples.
- `RoomGridView` mantiene una matriz con `GridPane` y botones de 82 px.
- `RoomGridView` usa simbolos ASCII: `P`, `E`, `OBJ`, `TRAP`, `BAR`, `RULETA`, `SALIDA`, `P->n`, `LOCK->n`.
- `RoomGridView` ya aplica colores inline por tipo de celda.
- `PlayerPanelView` muestra sala, vida, ataque, defensa, movimiento, fichas, turnos y resultado final.
- `InventoryPanelView` muestra lista textual con nombre y tipo del objeto, arma equipada y armadura equipada.
- `ActionPanelView` usa botones contextuales, tooltips basicos y boton `Volver a jugar`.
- `RoutePanelView` muestra sala actual, conexiones, ruta recomendada y distancias.
- `LogPanelView` usa un `TextArea` de texto plano.
- Click sobre enemigo muestra estadisticas y no ataca automaticamente.
- Atacar se mantiene separado mediante boton `Atacar`.

Estado del repositorio al redactar:

- Hay cambios pendientes detectados en `config/game_config.json` y `src/main/java/casinoescape/game/Game.java`.
- Este documento no interpreta ni modifica esos cambios.
- Cualquier fase visual futura debe revisar de nuevo `git status`, porque puede haber trabajo concurrente.

Lectura tecnica del estado actual:

- La base visual ya es funcional y suficientemente clara para demo academica.
- El mayor margen de mejora esta en presentacion, jerarquia visual, log, inventario y pantallas finales.
- No conviene aumentar complejidad del controlador si no se crean vistas pequenas y aisladas.
- El riesgo principal no es tecnico, sino degradar legibilidad o romper flujo estable con cambios esteticos demasiado amplios.

## 4. Mejoras propuestas

### 4.1 Ambientacion visual casino mas pulida

- Descripcion: consolidar una paleta visual consistente con verde oscuro, dorado, rojo casino, negro y paneles claros.
- Impacto visual: alto; hace que la demo parezca mas cohesionada sin cambiar reglas.
- Dificultad: baja-media.
- Riesgo: bajo si se limita a estilos, medio si se empiezan a tocar layouts.
- Archivos probables: `GameController.java`, `RoomGridView.java`, `PlayerPanelView.java`, `InventoryPanelView.java`, `RoutePanelView.java`, `LogPanelView.java`, `ActionPanelView.java`.
- Compatibilidad: muy buena si se mantienen estilos inline o CSS pequeno.
- Prioridad: alta.
- Recomendacion real: merece la pena, pero debe hacerse con una guia de colores unica y sin redisenar la pantalla.

Propuesta concreta:

- Fondo principal verde casino oscuro.
- Paneles laterales color crema o verde muy claro.
- Bordes dorados coherentes.
- Enemigos en rojo oscuro.
- Puertas en azul o dorado, no mezcladas con casillas alcanzables.
- Casillas alcanzables en verde claro muy distinguible.

No merece la pena:

- Crear degradados complejos.
- Meter sombras en todos los nodos.
- Cambiar `BorderPane` por otro layout.

### 4.2 Simbolos mas claros por tipo de celda

- Descripcion: estudiar si mantener ASCII puro o usar Unicode simple para mejorar reconocimiento visual.
- Impacto visual: medio-alto en la matriz.
- Dificultad: baja.
- Riesgo: bajo con ASCII, medio con Unicode, alto con emojis.
- Archivos probables: `RoomGridView.java`.
- Compatibilidad: ASCII es maxima; Unicode simple suele funcionar; emojis son variables por sistema.
- Prioridad: alta.
- Recomendacion real: mantener ASCII como base y valorar Unicode simple solo si se prueba en IntelliJ y Windows del equipo.

Opciones ASCII recomendadas:

- Jugador: `J` en vez de `P`, porque `P` puede confundirse con puerta.
- Enemigo: `E`.
- Objeto: `OBJ`.
- Trampa: `TRAP`.
- Tienda: `BAR`.
- Ruleta: `RULETA` o `RUL` si no cabe.
- Salida: `SALIDA` o `OUT` si no cabe.
- Puerta: `P->2`.
- Puerta bloqueada: `LOCK->3` o `L->3` si no cabe.
- Obstaculo: `#`.
- NPC: `NPC` en vez de `N` si cabe.

Regla:
- El jugador se representará como "J".
- Las puertas seguirán usando "P->n".

Regla:
- El click sobre enemigo actualiza EnemyInfoPanelView.
- El click NO abre Alert.
- El click NO ataca.
- El botón Atacar sigue siendo el único método de combate.

Regla:
- El minimapa textual debe construirse leyendo conexiones reales desde CasinoMap/MyGraph.
- No hardcodear rutas ni conexiones manualmente.

Regla:
- Si el CSS falla al cargar, la aplicación debe seguir siendo usable con estilos por defecto.

Unicode simple posible:

- Jugador: `@` o `J`; mejor no depender de simbolos raros.
- Corazon: `♥` para vida solo si se prueba bien.
- Llave: evitar `🔑`; es emoji y puede variar.
- Puerta: evitar emoji de puerta.

Emojis:

- No recomendados para la matriz principal.
- Pueden renderizar distinto en Windows, IntelliJ, JavaFX y equipos del profesor.
- Pueden cambiar tamanos de botones y romper alineacion.

### 4.3 Cabeceras de salas

- Descripcion: anadir una cabecera visible sobre el tablero con formato `Sala 5 - Bar` o `Sala 8 - Ruleta / Final`.
- Impacto visual: alto para orientacion y demo.
- Dificultad: baja-media.
- Riesgo: bajo.
- Archivos probables: `GameController.java` y posible nueva vista pequena `RoomHeaderView.java`.
- Compatibilidad: muy buena.
- Prioridad: alta.
- Recomendacion real: recomendable si se hace como componente pequeno encima del `RoomGridView`.

Opciones de implementacion futura:

- Crear `RoomHeaderView` con `Label` para sala actual y estado breve.
- Envolver centro en `VBox(header, grid)` sin cambiar el `GridPane`.
- Mostrar texto de sala, no reglas.

Riesgos reales:

- Si se mete en `RoomGridView`, la clase mezcla matriz y cabecera.
- Si se duplica informacion de `PlayerPanelView` y `RoutePanelView`, puede saturar pantalla.

Recomendacion concreta:

- Usar una cabecera simple: `Sala 5 - Bar`.
- No mostrar lore largo en cabecera.
- No cargar descripcion desde JSON en esta fase.

### 4.4 Panel visual de enemigos

- Descripcion: sustituir o complementar el `Alert` de estadisticas con un panel lateral fijo o contextual.
- Impacto visual: alto en combates.
- Dificultad: media.
- Riesgo: medio.
- Archivos probables: nuevo `EnemyInfoPanelView.java`, `GameController.java`, posiblemente `RoomGridView.java` si se quiere marcar seleccion.
- Compatibilidad: buena si solo lee `Enemy` y no modifica reglas.
- Prioridad: media-alta.
- Recomendacion real: merece la pena si se evita hover y se usa seleccion por click.

Opcion recomendada:

- Crear `EnemyInfoPanelView` como panel pequeno debajo de ruta o encima del log.
- Click en enemigo actualiza el panel.
- El panel muestra nombre, vida, ataque, escudo, recompensa y nota `Atacar solo con boton Atacar`.
- No consume accion.
- No llama a `Game.attackEnemyAt`.

Opcion moderada:

- Resaltar visualmente la casilla del enemigo seleccionado.
- Requiere que `RoomGridView.refresh` reciba una `Position selectedEnemyPosition`.
- Riesgo: pequeno cambio de firma, pero controlado.

Opcion no recomendada:

- Hover para estadisticas.
- En JavaFX puede ser comodo, pero obliga a manejar eventos adicionales por celda y puede ser menos claro durante la demo.
- No aporta mucho frente a click + panel.

### 4.5 Resaltado visual avanzado

- Descripcion: reforzar visualmente jugador, enemigos, puertas, interactivos y casillas alcanzables.
- Impacto visual: alto.
- Dificultad: baja-media.
- Riesgo: bajo si solo cambia estilos, medio si se anaden estados visuales nuevos.
- Archivos probables: `RoomGridView.java`, `GameController.java` si se pasa seleccion.
- Compatibilidad: muy buena.
- Prioridad: alta.
- Recomendacion real: recomendable, con maximo 2 o 3 estados extra.

Estados visuales utiles:

- Casilla alcanzable: verde claro.
- Jugador: borde dorado grueso.
- Enemigo: rojo con borde oscuro.
- Interactivo adyacente disponible: borde dorado o amarillo.
- Puerta bloqueada: azul/gris con texto `LOCK`.

Riesgo tecnico importante:

- Para resaltar interactivos adyacentes correctamente, la UI necesita saber que celdas son adyacentes al jugador.
- Calcular adyacencia simple en UI es aceptable como prefiltrado visual si no decide reglas, pero puede duplicar criterio.
- Mejor opcion: usar consultas existentes de `Game` o pasar posiciones ya calculadas por `Game` si existen.

Recomendacion concreta:

- Primero mejorar solo estilos por `CellType`.
- Despues valorar resaltado de seleccion de enemigo.
- No implementar resaltado contextual complejo para todos los interactivos hasta tener pruebas manuales.

### 4.6 Minimapa ligero textual

- Descripcion: mejorar `RoutePanelView` con una representacion textual compacta del grafo y sala actual.
- Impacto visual: medio-alto para explicar el grafo.
- Dificultad: media.
- Riesgo: bajo-medio.
- Archivos probables: `RoutePanelView.java`, `GameController.java`.
- Compatibilidad: buena si se usa texto fijo y datos del grafo actual.
- Prioridad: media.
- Recomendacion real: recomendable solo como texto, no como grafo grafico.

Propuesta textual viable:

```text
Mapa:
1 -- 2 -- 5 -- 7 -- 8
|    |    |         SALIDA
4 -- 6
Actual: [5]
Ruta: 5 -> 7 -> 8
```

Limitacion:

- Esta representacion conceptual no debe convertirse en fuente de verdad.
- La autoridad sigue siendo `CasinoMap` y `MyGraph`.

Riesgo real:

- Si se hardcodea demasiado, puede parecer duplicacion del grafo.
- Para este proyecto el grafo es cerrado, asi que se puede justificar como vista conceptual, pero debe documentarse como renderizado.

No recomendado:

- Dibujar nodos y aristas con `Canvas`.
- Usar `Line`, `Circle` y layout manual para grafo interactivo.
- Crear zoom, pan o minimapa grafico.

### 4.7 Mejoras de log

- Descripcion: mejorar claridad del log separando visualmente combate, objetos, movimiento y eventos importantes.
- Impacto visual: medio-alto.
- Dificultad: media.
- Riesgo: medio.
- Archivos probables: `LogPanelView.java`, opcionalmente `GameLog` solo si se decide tipar eventos, pero eso no se recomienda en esta fase.
- Compatibilidad: buena si se mantiene texto plano.
- Prioridad: media.
- Recomendacion real: hacer mejoras textuales simples; evitar log rico si obliga a tocar modelo de logs.

Opcion segura:

- Mantener `TextArea`.
- Prefijar visualmente en UI segun texto existente: `[MOV]`, `[COMBATE]`, `[OBJ]`, `[SISTEMA]` solo si ya viene en mensaje o si se transforma superficialmente.
- Mejorar scroll y altura.

Opcion moderada:

- Cambiar a `ListView<String>` para eventos separados.
- Permite seleccionar eventos y aplicar estilos simples por celda.
- Riesgo: mas codigo JavaFX, pero sin tocar logica.

Opcion peligrosa:

- Cambiar `GameLog` y `LogEntry` para incluir categorias.
- Puede afectar tests y persistencia si el log se guarda.
- No recomendable antes de entrega.

Recomendacion concreta:

- Mantener `TextArea` para estabilidad.
- Mejorar mensajes solo desde UI si no se toca `GameLog`.
- No introducir colores por linea salvo que se cambie a `ListView`, y eso deberia ser fase separada.

### 4.8 Inventario mas claro

- Descripcion: distinguir equipado, consumible, arma, armadura y llave de forma visual.
- Impacto visual: alto para usabilidad.
- Dificultad: baja-media.
- Riesgo: bajo.
- Archivos probables: `InventoryPanelView.java`.
- Compatibilidad: muy buena.
- Prioridad: alta.
- Recomendacion real: una de las mejoras mas rentables.

Propuesta segura:

- Mantener `ListView<String>`.
- Formatear entradas como `[ARMA] Botella rota`, `[ARMADURA] Chaleco`, `[CONSUMIBLE] Coctel`, `[LLAVE] Llave de Tesoreria`.
- Marcar equipados con `[EQUIPADO]`.
- Mantener arma y armadura equipada como labels separados.

Riesgo tecnico:

- Para saber si un objeto de la lista es el equipado, se compara con `inventory.getEquippedWeapon()` o `inventory.getEquippedArmor()`.
- Esto es lectura de estado, no logica nueva.

No recomendado:

- Crear tarjetas visuales complejas por objeto.
- Implementar drag and drop.
- Cambiar la forma de seleccionar objetos.

### 4.9 Pantallas de victoria y derrota

- Descripcion: reemplazar o complementar alertas finales con una vista/panel final mas presentable.
- Impacto visual: alto para cierre de demo.
- Dificultad: media.
- Riesgo: medio.
- Archivos probables: `GameController.java`, `ActionPanelView.java`, posible `EndGamePanelView.java`.
- Compatibilidad: buena si se mantiene `Volver a jugar` y no se cambia guardado/carga.
- Prioridad: media-alta.
- Recomendacion real: recomendable si se hace como panel simple, no como pantalla nueva completa.

Opcion segura:

- Mantener alertas actuales.
- Mejorar texto de victoria/derrota.
- Hacer mas visible `Volver a jugar`.

Opcion moderada:

- Crear `EndGamePanelView` en el panel derecho o inferior.
- Mostrar resultado, sala final, turnos restantes, vida, fichas y amigo rescatado.
- No cambiar `GameState`.

Opcion no recomendada:

- Cambiar toda la escena por una pantalla nueva.
- Riesgo de romper handlers, carga, guardado y reinicio.

### 4.10 Tooltips y mensajes de ayuda

- Descripcion: ampliar ayudas contextuales en botones y celdas.
- Impacto visual: medio.
- Dificultad: baja.
- Riesgo: bajo.
- Archivos probables: `ActionPanelView.java`, `RoomGridView.java`.
- Compatibilidad: muy buena.
- Prioridad: media.
- Recomendacion real: util, pero no debe sustituir claridad visual principal.

Tooltips recomendados:

- `Atacar`: `Requiere enemigo adyacente`.
- `Recoger`: `Requiere objeto adyacente`.
- `Usar puerta`: `Requiere puerta adyacente`.
- `Tienda`: `Requiere estar junto al BAR`.
- `Ruleta`: `Requiere estar junto a RULETA`.
- `Requiere Pastilla`: `Activa la Pastilla de dudosa procedencia`.

Tooltips en celdas:

- Pueden ayudar, pero aumentan codigo en `RoomGridView`.
- Si se implementan, que sean estaticos por tipo de celda.
- No calcular reglas de disponibilidad en tooltip.

### 4.11 Feedback visual de combate y dano

- Descripcion: mejorar la claridad del resultado de ataque sin cambiar formula ni balance.
- Impacto visual: medio-alto.
- Dificultad: media.
- Riesgo: medio.
- Archivos probables: `GameController.java`, `LogPanelView.java`, posible `EnemyInfoPanelView.java`.
- Compatibilidad: buena si se limita a mensajes.
- Prioridad: media.
- Recomendacion real: mejorar texto y panel de enemigo; evitar animaciones.

Opciones seguras:

- Tras atacar, mostrar `Dano causado: X` y vida restante del enemigo si sigue vivo.
- Tras recibir dano, el log ya informa; podria resaltarse con texto mas claro.
- Actualizar panel de enemigo seleccionado tras ataque.

Opciones no recomendadas:

- Animar parpadeo de casillas.
- Floating numbers.
- Barras de vida animadas.
- Cambiar formula o balance para que el feedback parezca mejor.

Riesgo real:

- Para mostrar vida restante tras ataque, el controlador debe saber que enemigo fue atacado.
- `attackAdjacentEnemy()` elige el primer enemigo adyacente; si se quiere asociar con seleccion visual, habria que coordinar seleccion y ataque.
- No conviene tocar esto salvo que haya tiempo.

### 4.12 Imagenes pequenas e iconos

- Descripcion: valorar iconos o logos pequenos para ambientacion.
- Impacto visual: medio si se hace bien.
- Dificultad: media.
- Riesgo: medio-alto.
- Archivos probables: `src/main/resources`, `CasinoEscapeApp.java`, vistas UI que carguen recursos.
- Compatibilidad: variable segun rutas y empaquetado.
- Prioridad: baja.
- Recomendacion real: no implementarlo salvo que la UI textual ya este cerrada y probada.

Usos razonables:

- Logo pequeno en cabecera.
- Icono pequeno para victoria/derrota.
- Fondo muy simple, no imagen grande.

Riesgos reales:

- Rutas relativas fallan al ejecutar desde IntelliJ, Maven o JAR si no se usa `getResource` correctamente.
- Hay que crear `src/main/resources` y asegurar empaquetado.
- Imagenes externas pueden complicar entrega ZIP.
- Iconos pueden no cargar y dejar la UI rota si no se gestiona fallback.

Recomendacion concreta:

- Si se usan imagenes, maximo 2 o 3 PNG pequenos.
- Cargar siempre con `getResource`.
- Mantener fallback textual.
- No usar imagenes para celdas del tablero en esta fase.

### 4.13 CSS ligero

- Descripcion: mover estilos repetidos a un CSS pequeno para profesionalizar la UI.
- Impacto visual: medio.
- Dificultad: media.
- Riesgo: medio.
- Archivos probables: `src/main/resources/casinoescape/ui/casino.css`, `CasinoEscapeApp.java`, vistas UI.
- Compatibilidad: buena si se carga correctamente; riesgo de rutas.
- Prioridad: media-baja.
- Recomendacion real: util si se quiere limpiar estilos, pero no imprescindible.

Partes que si tendrian sentido en CSS:

- Fondo principal.
- Estilo de paneles.
- Titulos.
- Botones de accion.
- Celdas por tipo si se asignan style classes.

Partes que no merece la pena tocar:

- Layout general.
- Tamano de matriz si ya funciona.
- Dialogos JavaFX estandar.
- Estilos dinamicos complejos para cada estado.

Riesgos reales:

- Hay que anadir `getStyleClass()` en muchas vistas.
- Si se mezcla CSS con estilos inline, puede quedar inconsistente.
- Si el CSS no carga, la UI vuelve a aspecto por defecto.

Recomendacion concreta:

- No mover todo a CSS de golpe.
- Si se hace, empezar por paneles y botones, no por celdas.
- Mantener estilos dinamicos de `RoomGridView` en Java hasta estabilizar.

## 5. Mejoras recomendadas realmente

### Seguras

- Cabecera simple de sala sobre el tablero.
- Mejorar inventario textual con etiquetas `[ARMA]`, `[CONSUMIBLE]`, `[LLAVE]`, `[EQUIPADO]`.
- Tooltips adicionales en botones.
- Ajuste fino de paleta y bordes existentes.
- Mantener simbolos ASCII y corregir `P` del jugador a `J` si se quiere evitar confusion con puertas.
- Mejorar mensajes finales manteniendo `Alert` y boton `Volver a jugar`.

### Moderadas

- `EnemyInfoPanelView` lateral en vez de `Alert`.
- Resaltado de enemigo seleccionado.
- Minimapa textual fijo en `RoutePanelView`.
- Cambiar `LogPanelView` de `TextArea` a `ListView<String>` para separar eventos visualmente.
- Crear CSS pequeno para paneles y botones.

### Peligrosas o no recomendadas

- Rehacer la pantalla completa.
- Cambiar `BorderPane` por una composicion nueva grande.
- Usar emojis como base de la matriz.
- Usar imagenes para cada celda.
- Crear animaciones de combate.
- Dibujar un grafo interactivo con nodos y aristas.
- Meter musica, sonidos, particulas o efectos visuales complejos.
- Cambiar `GameLog` para categorias antes de cerrar pruebas de persistencia.
- Cambiar JSON para guardar preferencias visuales.
- Cambiar reglas para que la UI sea mas facil de explicar.

## 6. Orden ideal de implementacion

### Bloque 1: claridad sin riesgo

1. Anadir cabecera de sala simple.
2. Ajustar simbolo de jugador si se decide pasar de `P` a `J`.
3. Revisar que `RULETA`, `SALIDA` y `LOCK->3` caben en la matriz.
4. Anadir tooltips basicos a botones que aun no los tengan.

### Bloque 2: inventario y paneles

1. Mejorar textos del inventario con etiquetas por tipo.
2. Marcar equipados en la lista.
3. Afinar titulos de paneles sin cambiar estructura.
4. Validar uso de objeto, equipar arma y equipar armadura.

### Bloque 3: enemigos

1. Crear `EnemyInfoPanelView` minimo.
2. Click en enemigo actualiza panel en vez de abrir solo alerta.
3. Mantener boton `Atacar` como unica accion de combate.
4. Opcionalmente resaltar enemigo seleccionado.

### Bloque 4: orientacion

1. Mejorar `RoutePanelView` con minimapa textual simple.
2. Mantener ruta real desde `ShortestPathInfo`.
3. No dibujar grafo grafico.

### Bloque 5: cierre de partida

1. Mejorar texto de victoria y derrota.
2. Hacer `Volver a jugar` mas visible.
3. Valorar resumen simple de partida sin tocar persistencia.

### Bloque 6: CSS o imagenes solo si queda tiempo

1. CSS pequeno para paneles y botones.
2. Probar ejecucion desde IntelliJ y Maven.
3. Imagenes pequenas solo si hay fallback textual y no afectan tablero.

## 7. Riesgos generales

- Saturacion visual: demasiados colores, bordes o simbolos pueden hacer la matriz menos clara.
- Texto largo en botones: `LOCK->3`, `RULETA` y `SALIDA` pueden cortarse en 82 px.
- Duplicacion de reglas: resaltar disponibilidad desde UI puede copiar adyacencia o estados de accion.
- Acoplamiento: pasar demasiada informacion a `RoomGridView` puede convertirlo en controlador secundario.
- Recursos externos: imagenes y CSS pueden fallar por rutas al ejecutar desde IntelliJ o JAR.
- Regresion de flujo: cambiar alertas por paneles puede dejar sin feedback algunas acciones.
- Perdida de estabilidad: refactors visuales grandes pueden romper guardar, cargar o volver a jugar.
- Tests: aunque la UI no tenga tests automatizados, cambios en firmas publicas pueden romper compilacion.
- Trabajo concurrente: el repositorio puede estar sucio; cualquier implementacion debe revisar cambios antes de editar.

## 8. Que NO merece la pena tocar

- No merece la pena rehacer toda la UI.
- No merece la pena crear sprites para todas las celdas.
- No merece la pena meter musica o sonido.
- No merece la pena usar `Canvas` para tablero o minimapa.
- No merece la pena crear animaciones de dano.
- No merece la pena introducir CSS grande.
- No merece la pena cambiar a FXML a estas alturas.
- No merece la pena cambiar `GridPane`.
- No merece la pena cambiar `GameController` en profundidad.
- No merece la pena modificar JSON para temas visuales.
- No merece la pena categorizar logs en modelo si puede afectar persistencia o tests.
- No merece la pena tocar balance para mejorar feedback visual.
- No merece la pena usar emojis como lenguaje visual principal.

## 9. Recomendacion final realista

La mejor ruta es una mejora visual incremental, no un rediseño.

Recomendacion prioritaria:

1. Cabecera de sala.
2. Inventario mas claro.
3. Tooltips adicionales.
4. Panel lateral de enemigo si queda tiempo.
5. Minimapa textual simple.
6. Mejor cierre de victoria/derrota.

Recomendacion sobre tecnologia:

- Mantener JavaFX actual con `BorderPane`, `VBox`, `GridPane`, `Button`, `Label`, `TextArea` y `ListView`.
- Mantener simbolos ASCII como base.
- Usar CSS solo si se limita a un archivo pequeno y se prueba bien.
- Evitar imagenes salvo logo o icono pequeno con fallback textual.

Conclusion:

Casino Escape no necesita una UI compleja para parecer mas profesional. Necesita jerarquia visual, textos mas claros, feedback estable y una presentacion coherente. Las mejoras mas rentables son las que hacen la demo mas explicable sin tocar reglas: cabecera de sala, inventario claro, panel de enemigo, minimapa textual y cierre de partida mas presentable.

Este documento no autoriza implementacion automatica. Cualquier cambio futuro debe hacerse por bloques pequenos, revisando compilacion y pruebas manuales JavaFX despues de cada bloque.
