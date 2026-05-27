# MEJORAS_VISUALES_Y_USABILIDAD

## 1. Objetivo

El objetivo de este documento es planificar mejoras visuales y de usabilidad para la interfaz JavaFX de Casino Escape sin modificar reglas core, estructuras, persistencia ni balance en esta fase.

Estas mejoras deben servir para que la demo sea mas clara, presentable y facil de explicar. La interfaz debe seguir siendo una capa externa: muestra estado, prefiltra acciones para evitar dialogos inutiles y llama a `Game` como fuente de verdad.

Este documento no autoriza implementar cambios todavia. Sirve como contrato tecnico para una fase posterior de implementacion controlada.

## 2. Reglas intocables

- Movimiento normal: sigue siendo BFS ortogonal.
- Movimiento normal: no permite diagonales.
- Movimiento normal: maximo 1 movimiento por turno.
- Acciones: maximo 1 accion por turno.
- Acciones: se mantiene el orden actual del sistema.
- Acciones: no implementar accion antes de movimiento.
- Transitabilidad: `ITEM`, `DOOR`, `SHOP`, `EXIT`, `MINIGAME`, `NPC` y `ENEMY` no son transitables.
- Transitabilidad: `TRAP` si es transitable, aplica dano y desaparece.
- Interacciones: se hacen desde adyacencia ortogonal.
- Estructuras: mantener estructuras propias en la logica central.
- Estructuras: no usar `ArrayList`, `HashMap`, `LinkedList`, `Queue`, `Stack`, `Deque` ni equivalentes prohibidos para resolver logica central.
- JSON: JavaFX debe seguir arrancando desde `config/game_config.json` mediante `GameConfigLoader`.
- JSON: no volver a `Game.createNewGame(...)` hardcodeado en el flujo principal JavaFX.
- JSON: no cambiar `config/game_config.json` salvo decision posterior estrictamente justificada.
- JavaFX: no mover reglas de juego a JavaFX.
- JavaFX: JavaFX puede mostrar, habilitar/deshabilitar botones y prefiltrar interacciones para usabilidad.
- JavaFX: `Game` y el modelo siguen siendo la fuente de verdad.
- Grafo: no cambiar salas ni conexiones definitivas.
- Matriz: no cambiar dimensiones ni identidad de salas.
- Combate: no cambiar la formula oficial de dano.
- Persistencia: no rehacer guardado/carga para estas mejoras.
- Tests: no cambiar tests como parte de la fase de planificacion.

## 3. Mejoras propuestas

### 3.1 Boton Volver a jugar

- Descripcion: mostrar un boton para reiniciar la partida cuando el estado sea victoria o derrota.
- Prioridad: imprescindible.
- Dificultad estimada: baja-media.
- Riesgo: medio.
- Archivos probables: `src/main/java/casinoescape/ui/ActionPanelView.java`, `src/main/java/casinoescape/ui/GameController.java`, `src/main/java/casinoescape/ui/CasinoEscapeApp.java` solo si se decide reutilizar constante de ruta.
- Comportamiento esperado: el boton no debe estar activo durante una partida en curso.
- Comportamiento esperado: al terminar por victoria o derrota, el boton debe aparecer o habilitarse claramente.
- Comportamiento esperado: al pulsarlo, se debe crear una nueva partida desde `config/game_config.json` usando `GameConfigLoader`.
- Comportamiento esperado: despues de reiniciar, deben refrescarse matriz, panel jugador, inventario, ruta, acciones y log.
- Comportamiento esperado: guardar y cargar deben seguir usando `GameSaveWriter` y `GameSaveLoader` como ahora.
- Comportamiento esperado: no debe usar `Game.createNewGame(...)`.
- Criterios de cierre: tras victoria, el boton permite iniciar nueva partida en sala 1 con turnos del JSON.
- Criterios de cierre: tras derrota por vida o turnos, el boton permite iniciar nueva partida.
- Criterios de cierre: despues de reiniciar, no quedan objetos, enemigos, inventario o log de la partida anterior salvo lo definido por config inicial.
- Criterios de cierre: guardar/cargar siguen funcionando antes y despues de reiniciar.
- Pruebas manuales: iniciar JavaFX, forzar o alcanzar derrota, comprobar que aparece o se habilita Volver a jugar.
- Pruebas manuales: pulsar Volver a jugar y comprobar sala 1, posicion inicial, vida inicial, inventario vacio y turnos de `config/game_config.json`.
- Pruebas manuales: guardar una partida, reiniciar con Volver a jugar, cargar la partida guardada y comprobar que se restaura el guardado.
- Riesgos concretos: olvidar reconectar handlers tras sustituir `game` en `GameController`.
- Riesgos concretos: reiniciar desde builder hardcodeado en lugar de JSON.

### 3.2 Mostrar estadisticas de enemigos

- Descripcion: permitir ver nombre, vida/corazones, ataque, defensa/escudo y movimiento del enemigo seleccionado o clicado.
- Prioridad: recomendable.
- Dificultad estimada: baja-media.
- Riesgo: bajo.
- Archivos probables: `src/main/java/casinoescape/ui/GameController.java`, `src/main/java/casinoescape/ui/RoomGridView.java`; opcionalmente nuevo panel pequeno `EnemyInfoPanelView.java` si no se quiere usar alertas.
- Comportamiento esperado: al hacer click en un enemigo no adyacente, mostrar sus estadisticas sin atacar.
- Comportamiento esperado: al hacer click en un enemigo adyacente, definir comportamiento antes de implementar: opcion segura recomendada, mostrar estadisticas y usar el boton Atacar para combatir.
- Comportamiento esperado: no debe atacar automaticamente si el objetivo de la mejora es inspeccionar enemigos.
- Comportamiento esperado: si se mantiene ataque por click en enemigo adyacente, debe haber una forma clara alternativa de ver estadisticas, por ejemplo un mensaje previo o panel lateral.
- Comportamiento esperado: el texto debe ser presentable: `Enemigo: Crupier de Blackjack`, `Vida: 45/45`, `Ataque: 9`, `Escudo: 4`, `Movimiento: aproximacion por BFS` o `Movimiento: 1 paso` si se documenta asi.
- Criterios de cierre: todos los enemigos de salas 2, 4, 5 y 7 muestran datos correctos.
- Criterios de cierre: consultar estadisticas no consume accion ni turno.
- Criterios de cierre: consultar estadisticas no cambia vida, posicion ni log de combate.
- Pruebas manuales: en sala 2, hacer click sobre Maquina Tragaperras Averiada y comprobar nombre/vida/ataque/defensa.
- Pruebas manuales: en sala 7, comprobar Mafioso Ruso y Maton VIP.
- Pruebas manuales: comprobar que despues de ver estadisticas el jugador puede seguir moviendose o actuar segun el estado previo del turno.
- Riesgos concretos: cambiar accidentalmente el flujo de click que ahora ataca enemigos adyacentes.
- Riesgos concretos: duplicar reglas de combate en UI. La UI solo debe leer datos del `Enemy`.
- Regla oficial:
- Click izquierdo sobre enemigo: muestra estadísticas, nunca ataca.
- Atacar enemigos se realiza solo mediante botón Atacar.
- Ver estadísticas no consume acción ni turno.

### 3.3 Ocultar o condicionar Movimiento en linea

- Descripcion: evitar que el boton `Movimiento linea` confunda cuando el jugador no tiene activa la Pastilla de dudosa procedencia.
- Prioridad: imprescindible.
- Dificultad estimada: baja.
- Riesgo: bajo.
- Archivos probables: `src/main/java/casinoescape/ui/ActionPanelView.java`.
- Comportamiento esperado: opcion preferida, el boton aparece deshabilitado si no existe efecto activo `LINE_MOVEMENT`.
- Comportamiento esperado: el boton se habilita solo si la partida esta en curso, el jugador puede moverse y el inventario tiene efecto activo `LINE_MOVEMENT`.
- Comportamiento esperado: no se elimina la funcionalidad de `Game.movePlayerInLine(...)`.
- Comportamiento esperado: no se cambia la duracion de la Pastilla ni sus reglas.
- Criterios de cierre: al iniciar partida, el boton no esta disponible.
- Criterios de cierre: tras usar Pastilla de dudosa procedencia, el boton se habilita.
- Criterios de cierre: cuando el efecto termina, el boton vuelve a deshabilitarse.
- Pruebas manuales: iniciar partida y comprobar que Movimiento linea esta gris o no visible.
- Pruebas manuales: recibir Pastilla en sala 5, usarla y comprobar que Movimiento linea pasa a estar activo.
- Pruebas manuales: usar movimiento en linea hasta bloqueo y comprobar que respeta obstaculos, puertas, objetos, enemigos y trampas.
- Riesgos concretos: si se oculta totalmente, el jugador puede no saber que la mecanica existe cuando obtiene la Pastilla.
- Riesgos concretos: si solo se deshabilita, conviene que el texto o tooltip explique por que.
- Regla oficial:
- El botón Movimiento en línea permanece visible pero deshabilitado si no hay efecto LINE_MOVEMENT activo.
- Cuando esté deshabilitado debe mostrar texto claro: “Requiere Pastilla”.
- No eliminar el botón.

### 3.4 Limpiar panel de jugador

- Descripcion: hacer el panel del jugador mas presentable y menos tecnico.
- Prioridad: imprescindible.
- Dificultad estimada: baja.
- Riesgo: bajo.
- Archivos probables: `src/main/java/casinoescape/ui/PlayerPanelView.java`.
- Comportamiento esperado: mostrar sala actual con id y nombre.
- Comportamiento esperado: mostrar vida como corazones o texto claro `Vida: actual/max` si no se implementan simbolos.
- Comportamiento esperado: mostrar ataque, defensa/escudo, movimiento, fichas y turnos.
- Comportamiento esperado: no mostrar `Estado: IN_PROGRESS` durante partida normal.
- Comportamiento esperado: no mostrar `Amigo: pendiente`.
- Comportamiento esperado: si se muestra amigo, usar `Amigo rescatado: Si` o `Amigo rescatado: No`.
- Comportamiento esperado: si la partida termina, mostrar un texto presentable como `Resultado: Victoria` o `Resultado: Derrota`, no el enum crudo.
- Criterios de cierre: en partida normal no aparece `IN_PROGRESS`.
- Criterios de cierre: no aparece `amigo: pendiente` ni otro texto poco presentable.
- Criterios de cierre: todos los valores se actualizan tras movimiento, compra, combate, uso de objeto, rescate y carga.
- Pruebas manuales: iniciar JavaFX y revisar panel izquierdo.
- Pruebas manuales: comprar en tienda y comprobar fichas.
- Pruebas manuales: recibir dano y comprobar vida.
- Pruebas manuales: rescatar amigo y comprobar que el panel no ensucia la UI o muestra `Si` de forma natural.
- Riesgos concretos: ocultar demasiada informacion util para explicar victoria. Si se oculta amigo, la ruta de victoria debe seguir clara por mensajes/log.

### 3.5 Botones Tienda y Ruleta contextuales

- Descripcion: evitar que Tienda y Ruleta abran dialogos inutiles cuando el jugador no esta en el contexto correcto.
- Prioridad: imprescindible.
- Dificultad estimada: baja-media.
- Riesgo: bajo.
- Archivos probables: `src/main/java/casinoescape/ui/ActionPanelView.java`, posiblemente `src/main/java/casinoescape/game/Game.java` si se necesitan consultas de disponibilidad limpias.
- Comportamiento esperado: opcion preferida, botones visibles pero deshabilitados cuando no procedan.
- Comportamiento esperado: Tienda se habilita solo si la partida esta en curso y hay una celda `SHOP` adyacente.
- Comportamiento esperado: Ruleta se habilita solo si la partida esta en curso y hay una celda `MINIGAME` adyacente.
- Comportamiento esperado: si no estan habilitados, no se abre ningun dialogo.
- Comportamiento esperado: la validacion real sigue en `Game.buyFromAdjacentBar(...)` y `Game.playRussianRoulette(...)`.
- Criterios de cierre: en sala 1, Tienda y Ruleta estan deshabilitados.
- Criterios de cierre: en sala 5 junto al bar, Tienda esta habilitado.
- Criterios de cierre: en sala 8 junto a la ruleta, Ruleta esta habilitado.
- Criterios de cierre: alejarse del bar o ruleta vuelve a deshabilitar el boton.
- Pruebas manuales: iniciar partida y comprobar botones.
- Pruebas manuales: colocarse adyacente al bar en sala 5 y comprobar Tienda.
- Pruebas manuales: colocarse adyacente a ruleta en sala 8 y comprobar Ruleta.
- Riesgos concretos: calcular contexto solo en JavaFX y olvidarse de refrescar tras movimiento/carga.
- Riesgos concretos: duplicar demasiada logica en UI. Mejor usar consultas ya existentes como `game.findCurrentOrAdjacentCellOfType(...)`.
  Regla oficial:
- Los botones Tienda y Ruleta permanecen visibles pero deshabilitados si no están disponibles.
- Tienda solo se habilita si hay SHOP adyacente.
- Ruleta solo se habilita si hay MINIGAME adyacente.

### 3.6 Puerta bloqueada visualmente distinta

- Descripcion: diferenciar visualmente puertas normales y puertas bloqueadas.
- Prioridad: recomendable.
- Dificultad estimada: baja.
- Riesgo: bajo.
- Archivos probables: `src/main/java/casinoescape/ui/RoomGridView.java`.
- Comportamiento esperado: puerta normal debe tener simbolo distinto de puerta bloqueada.
- Comportamiento esperado: ejemplo ASCII aceptable: `P->2` para puerta normal y `LOCK->3` para puerta bloqueada.
- Comportamiento esperado: salida exterior debe verse como `SALIDA` u `OUT` de forma clara.
- Comportamiento esperado: no se modifica `Door.canPass(...)`, `CasinoMap.canTransition(...)` ni `Game.useDoorAt(...)`.
- Criterios de cierre: puerta de sala 2 a sala 3 se ve bloqueada antes de tener llave.
- Criterios de cierre: las puertas no bloqueadas siguen viendose como puertas normales.
- Criterios de cierre: usar la llave permite pasar igual que antes; la mejora visual no cambia reglas.
- Pruebas manuales: ir a sala 2 y comprobar que la puerta a sala 3 se distingue.
- Pruebas manuales: comprar llave y comprobar que la interaccion funciona igual.
- Riesgos concretos: si el simbolo depende solo de `Door.isLocked()`, podria seguir mostrando bloqueada aunque el jugador tenga llave. Eso es aceptable si significa `puerta con cerradura`; si se quiere mostrar `abierta`, hace falta considerar inventario, lo que requeriria pasar mas contexto a `RoomGridView`.

### 3.7 Orientacion / minimapa simple

- Descripcion: mejorar la orientacion del jugador sin crear un minimapa grafico complejo.
- Prioridad: recomendable.
- Dificultad estimada: baja-media.
- Riesgo: bajo-medio.
- Archivos probables: `src/main/java/casinoescape/ui/RoutePanelView.java`, `src/main/java/casinoescape/ui/GameController.java`, posiblemente `src/main/java/casinoescape/model/CasinoMap.java` si se necesita exponer conexiones de forma limpia.
- Comportamiento esperado: aprovechar `RoutePanelView` como panel de orientacion.
- Comportamiento esperado: mostrar `Sala actual: 5 - Bar`.
- Comportamiento esperado: mostrar conexiones disponibles de la sala actual, por ejemplo `Conexiones: 2, 4, 6, 7`.
- Comportamiento esperado: mostrar ruta recomendada a salida usando `ShortestPathInfo`, por ejemplo `Ruta recomendada: 5 -> 7 -> 8`.
- Comportamiento esperado: mostrar siguiente sala recomendada y distancia a puerta/salida.
- Comportamiento esperado: no cambiar BFS ni calculo de ruta.
- Criterios de cierre: cada sala muestra id, nombre y conexiones correctas.
- Criterios de cierre: ruta recomendada cambia al cambiar de sala.
- Criterios de cierre: sala 8 muestra salida/actual de forma clara.
- Pruebas manuales: iniciar en sala 1 y comprobar ruta.
- Pruebas manuales: moverse a sala 5 y comprobar conexiones `2, 4, 6, 7`.
- Pruebas manuales: moverse a sala 7 y comprobar ruta hacia 8.
- Riesgos concretos: duplicar grafo en JavaFX. Debe leerse desde `game.getMap().getConnectedRooms(...)` o datos ya expuestos.

### 3.8 Identificar puertas con sala destino

- Descripcion: mostrar en cada puerta el destino de sala para mejorar orientacion.
- Prioridad: recomendable.
- Dificultad estimada: baja.
- Riesgo: bajo.
- Archivos probables: `src/main/java/casinoescape/ui/RoomGridView.java`.
- Comportamiento esperado: puerta a sala 2 puede mostrarse como `P->2`.
- Comportamiento esperado: puerta bloqueada a sala 3 puede mostrarse como `LOCK->3`.
- Comportamiento esperado: salida exterior debe mostrarse como `SALIDA` u `OUT`.
- Comportamiento esperado: si una celda no tiene `Door`, no debe intentar leer destino.
- Criterios de cierre: todas las puertas de las 8 salas muestran destino correcto.
- Criterios de cierre: la puerta bloqueada de sala 2 a sala 3 muestra destino 3.
- Criterios de cierre: no cambia la interaccion por click ni por boton.
- Pruebas manuales: revisar visualmente puertas en salas 1, 2, 5, 7 y 8.
- Pruebas manuales: cruzar cada tipo de puerta y comprobar que destino real coincide con etiqueta.
- Riesgos concretos: texto largo puede no caber en botones de 82px. Usar formato corto.

### 3.9 Mejora visual tematica casino

- Descripcion: mejorar aspecto general con simbolos, colores y titulos sin introducir assets externos ni CSS grande.
- Prioridad: opcional-recomendable.
- Dificultad estimada: baja-media.
- Riesgo: bajo si se limita a estilos y textos.
- Archivos probables: `src/main/java/casinoescape/ui/RoomGridView.java`, `PlayerPanelView.java`, `InventoryPanelView.java`, `RoutePanelView.java`, `LogPanelView.java`, `ActionPanelView.java`.
- Comportamiento esperado: usar simbolos ASCII claros y consistentes si se evita Unicode.
- Comportamiento esperado: jugador `J`, enemigo `E`, objeto `OBJ`, puerta `P->n`, puerta bloqueada `LOCK->n`, tienda `BAR`, ruleta `RULETA`, salida `SALIDA`, trampa `TRAP`.
- Comportamiento esperado: colores coherentes con casino: verdes oscuros, dorados, rojos, negros, sin perder legibilidad.
- Comportamiento esperado: mantener `GridPane` y layout actual.
- Comportamiento esperado: no usar imagenes externas.
- Comportamiento esperado: no crear animaciones.
- Criterios de cierre: la matriz sigue siendo legible en 7x7.
- Criterios de cierre: los textos caben en botones.
- Criterios de cierre: la UI sigue siendo usable en ventana 1200x800.
- Pruebas manuales: abrir JavaFX y revisar legibilidad de cada tipo de celda.
- Pruebas manuales: cambiar entre salas y comprobar que no hay textos cortados criticamente.
- Riesgos concretos: exceso de colores o simbolos reduce claridad.
- Riesgos concretos: usar emojis puede verse distinto segun sistema operativo. Preferir ASCII si se busca estabilidad academica.

### 3.10 Balance minimo

- Descripcion: estudiar si el balance actual provoca demasiados ataques de 0 dano, dano enemigo excesivo o dificultad demasiado alta.
- Prioridad: opcional, solo analisis.
- Dificultad estimada: media.
- Riesgo: alto si se implementa sin pruebas.
- Archivos probables si se analizara: `config/game_config.json`, `src/main/java/casinoescape/game/CasinoMapBuilder.java` por duplicidad actual, tests de combate/economia si se cambiara balance.
- Comportamiento esperado en esta fase: no cambiar nada.
- Comportamiento esperado futuro: si se ajusta balance, preferir modificar estadisticas en JSON antes que tocar formula.
- Comportamiento esperado futuro: no cambiar la formula oficial `dano = max(0, ataque * (aleatorio * 2) - defensa)`.
- Comportamiento esperado futuro: si se toca JSON, mantener coherencia con builder o decidir formalmente fuente unica.
- Criterios de cierre de analisis: registrar casos de combate donde haya ataques repetidos de 0 dano.
- Criterios de cierre de analisis: probar enemigo basico, Crupier, Borracho, Mafioso y Maton.
- Criterios de cierre de analisis: identificar si la llave de Tesoreria es comprable sin farm imposible.
- Pruebas manuales: jugar varios combates con log visible y anotar dano causado/recibido.
- Pruebas manuales: comprobar si el jugador puede obtener fichas suficientes para llave.
- Riesgos concretos: tocar balance puede invalidar tests existentes y romper una partida ya demostrable.
- Riesgos concretos: cambiar formula incumple requisito del profesor.

## 4. Clasificacion por prioridad

### Imprescindibles

- Boton Volver a jugar.
- Ocultar o condicionar Movimiento en linea.
- Limpiar panel de jugador.
- Botones Tienda y Ruleta contextuales.

### Recomendables

- Mostrar estadisticas de enemigos.
- Puerta bloqueada visualmente distinta.
- Orientacion / minimapa simple aprovechando `RoutePanelView`.
- Identificar puertas con sala destino.

### Opcionales

- Mejora visual tematica casino limitada a simbolos, colores y textos.
- Analisis de balance minimo sin cambiar valores.

### No recomendadas

- Rehacer toda la UI.
- Crear minimapa grafico complejo.
- Introducir imagenes externas.
- Introducir CSS grande.
- Introducir animaciones.
- Cambiar balance antes de tener partida manual completa documentada.
- Cambiar formula de dano.

## 5. Orden recomendado de implementacion

1. Bloque 1: seguridad de flujo JavaFX.

- Implementar Volver a jugar cargando desde `config/game_config.json`.
- Verificar que guardar/cargar sigue funcionando.

2. Bloque 2: reducir confusion de acciones.

- Deshabilitar u ocultar Movimiento en linea cuando no haya efecto activo.
- Deshabilitar Tienda y Ruleta cuando no haya contexto adyacente valido.

3. Bloque 3: limpieza de paneles.

- Limpiar `PlayerPanelView`.
- Ajustar textos presentables y evitar enums crudos.

4. Bloque 4: orientacion basica.

- Mejorar `RoutePanelView` con sala actual, conexiones y ruta recomendada.
- Identificar puertas con destino en `RoomGridView`.
- Diferenciar puerta bloqueada.

5. Bloque 5: informacion de enemigos.

- Decidir si se usa alerta simple o panel minimo.
- Implementar consulta de estadisticas sin consumir turno.

6. Bloque 6: tema visual ligero.

- Ajustar colores y simbolos sin cambiar layout.

7. Bloque 7: analisis de balance.

- Solo jugar, anotar y decidir si merece la pena tocar JSON en otra fase.

## 6. Cambios prohibidos

- No cambiar BFS de movimiento.
- No permitir diagonales.
- No permitir mas de 1 movimiento por turno.
- No permitir mas de 1 accion por turno.
- No implementar accion antes de movimiento.
- No hacer transitables `ITEM`, `DOOR`, `SHOP`, `EXIT`, `MINIGAME`, `NPC` ni `ENEMY`.
- No impedir que `TRAP` sea transitable.
- No cambiar interacciones desde adyacencia.
- No modificar estructuras propias para estas mejoras.
- No usar estructuras prohibidas en logica central.
- No cambiar el grafo de habitaciones.
- No cambiar salas ni identidad tematica.
- No cambiar condicion de victoria.
- No cambiar formula de dano.
- No volver a iniciar JavaFX con `Game.createNewGame(...)`.
- No cambiar persistencia de guardado/carga.
- No cambiar tests como parte de estas mejoras visuales.
- No introducir librerias nuevas.
- No introducir assets externos.
- No hacer refactors grandes.
- No mover reglas de juego a JavaFX.

## 7. Pruebas manuales finales

- Arrancar JavaFX desde la raiz del proyecto.
- Confirmar que la partida inicial usa `config/game_config.json`.
- Confirmar que la sala inicial es `1 - Hall / Entrada`.
- Confirmar que turnos iniciales coinciden con JSON.
- Confirmar que el panel de jugador no muestra `IN_PROGRESS` ni `Amigo: pendiente`.
- Confirmar que Movimiento en linea esta deshabilitado al inicio.
- Confirmar que Tienda esta deshabilitada fuera de sala 5 o lejos del bar.
- Confirmar que Ruleta esta deshabilitada fuera de sala 8 o lejos de la ruleta.
- Moverse por celdas resaltadas y comprobar que botones se refrescan.
- Recoger Botella rota y comprobar inventario y estadisticas.
- Llegar a sala 5, ponerse junto al bar y comprobar Tienda habilitada.
- Comprar o intentar comprar un objeto y comprobar que no se rompe el flujo.
- Obtener o usar Pastilla de dudosa procedencia y comprobar Movimiento en linea habilitado.
- Llegar a sala 2 y comprobar puerta bloqueada visualmente distinta.
- Comprobar que puertas muestran destino correcto.
- Consultar estadisticas de enemigo sin consumir turno.
- Atacar enemigo mediante accion prevista y comprobar log.
- Guardar partida.
- Cargar partida.
- Terminar partida en victoria o derrota.
- Comprobar que aparece o se habilita Volver a jugar.
- Pulsar Volver a jugar y comprobar nueva partida limpia desde JSON.

## 8. Recomendacion final

Merece la pena implementar primero las mejoras que reducen confusion durante la demo: Volver a jugar, Movimiento en linea contextual, limpieza del panel de jugador y botones Tienda/Ruleta contextuales.

Despues conviene implementar orientacion visual: puertas con destino, puerta bloqueada distinguible y ampliacion simple de `RoutePanelView`. Estas mejoras ayudan mucho a explicar grafo, salas y ruta minima sin tocar reglas.

Mostrar estadisticas de enemigos es util, pero debe hacerse con mucho cuidado para no cambiar el flujo de ataque por click. La opcion mas segura es mostrar datos en un mensaje o panel minimo y dejar el combate en el boton Atacar.

La mejora tematica de casino debe ser ligera. No merece la pena introducir imagenes, CSS grande, animaciones ni redisenar la interfaz.

El balance no debe tocarse en esta fase. Primero hay que jugar una partida manual completa y anotar problemas concretos. Si se decide ajustar, debe preferirse cambiar estadisticas en JSON y no la formula oficial de dano.
