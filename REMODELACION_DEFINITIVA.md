# Remodelacion Definitiva Del Proyecto

Este documento recoge los pasos recomendados para estabilizar y cerrar el proyecto Casino Escape de forma ordenada. La prioridad es corregir primero los errores que impiden compilar o jugar correctamente, y despues completar los requisitos academicos pendientes.

## 0. Reglas oficiales cerradas

Estas reglas NO deben ser reinterpretadas por OpenCode.

1. Movimiento normal:
  - Se calcula con BFS ortogonal.
  - No hay diagonales.
  - Cada paso cuesta 1 punto.
  - El jugador elige una celda alcanzable.
  - Solo hay 1 movimiento por turno.

2. Movimiento especial:
  - Solo la Pastilla de dudosa procedencia permite movimiento en línea recta hasta bloqueo.
- El movimiento especial se detiene antes de obstáculos, enemigos, NPCs, puertas, tienda, salida, ruleta, objetos y bordes de la matriz.
- Las trampas sí pueden pisarse durante el movimiento especial: se activan, aplican daño y desaparecen.

3. Objetos:
  - Las celdas ITEM no son transitables.
  - Los objetos se recogen desde celda adyacente.
  - Recoger consume acción.

4. Trampas:
  - Las celdas TRAP son transitables.
  - Al pisarlas, aplican daño y desaparecen.

5. Transitabilidad oficial de celdas:
- EMPTY: transitable.
- PLAYER: no transitable para otras entidades.
- WALL/OBSTACLE: no transitable.
- ITEM: no transitable; se recoge desde celda adyacente.
- SHOP: no transitable; se interactúa desde celda adyacente.
- EXIT: no transitable; se interactúa desde celda adyacente.
- MINIGAME: no transitable; se interactúa desde celda adyacente.
- DOOR: no transitable como movimiento normal; se usa desde celda adyacente.
- TRAP: transitable; al pisarla se activa y después pasa a EMPTY.
- NPC: no transitable.
- ENEMY: no transitable.
- NPC y ENEMY representan ocupación lógica de celda, no sustituyen completamente la entidad persistente del modelo.
- Las estadísticas, comportamiento y estado real deben almacenarse en objetos `NPC` y `Enemy` independientes.
- El `CellType` solo representa interacción/renderizado básico de la celda.

6. Puertas:
  - Se interactúa con puertas desde celda adyacente.
  - Al abrir/cruzar una puerta válida, el jugador cambia de sala y termina el turno.

7. Daño:
  - Usar la fórmula oficial del profesor:
    daño = max(0, ataque * (aleatorio * 2) - defensa)
  - En la interfaz, vida se representa como corazones.

8. Turnos:
  - La partida tiene 125 turnos iniciales.

9. Inventario:
  - Capacidad máxima 8.
  - Fichas no ocupan inventario.
  - Recoger objeto consume acción.
  - Usar objeto consume acción.
  - Cambiar equipamiento consume acción.

10. Regla completa de turno:
- Cada turno permite como máximo 1 movimiento y 1 acción.
- El jugador puede moverse y después recoger, usar, atacar, comprar, abrir puerta o interactuar.
- Recoger objeto consume la acción del turno.
- Usar objeto consume la acción del turno.
- Atacar consume la acción del turno.
- Comprar consume la acción del turno.
- Interactuar con NPC, tienda, salida o minijuego consume la acción del turno.
- Abrir o cruzar una puerta válida consume la acción y termina inmediatamente el turno por cambio de sala.

11. JavaFX:
  - No calcula reglas de juego.
  - Solo muestra estado y llama a métodos de Game/controladores.

12. JSON:
- La configuración inicial debe permitir reconstruir el mundo base sin depender de contenido dinámico hardcodeado.
- La configuración inicial debe incluir habitaciones, dimensiones, conexiones, puertas, objetos, enemigos, NPCs, tienda, celdas especiales, jugador inicial, turnos iniciales y objetivo de victoria.
- El guardado debe conservar el estado real de la partida: sala actual, posición del jugador, estadísticas, inventario, equipamiento, fichas, enemigos vivos o muertos, objetos recogidos, puertas abiertas o bloqueadas, amigo rescatado, turnos restantes, log y estado general.

13. Regla estructural de celdas:
- Una celda no puede perder su tipo estructural por una interacción incorrecta.
- El jugador no debe sustituir permanentemente celdas como SHOP, EXIT, DOOR, MINIGAME, ITEM, NPC o ENEMY.
- En la versión base, las celdas interactivas no transitables se gestionan desde adyacencia.
- La única celda especial transitable es TRAP, que se activa y después pasa a EMPTY.
- La representación visual del jugador debe respetar el tipo real de la celda.

## 1. Objetivo

Convertir el estado actual del proyecto en una version final:

- compilable,
- jugable,
- coherente con los requisitos,
- defendible academicamente,
- con estructuras propias,
- con JavaFX separado de la logica,
- con JSON funcional,
- con logs,
- con excepciones,
- con tests relevantes,
- y con documentacion final.

## 2. Prioridad Absoluta

Antes de anadir nuevas funcionalidades, hay que corregir los problemas que pueden romper compilacion, ejecucion o reglas centrales.

Orden recomendado:

1. Corregir compilacion.
2. Corregir reglas base inconsistentes.
3. Corregir movimiento y puertas.
4. Reejecutar tests.
5. Completar inventario y economia.
6. Reforzar JSON.
7. Pulir JavaFX.
8. Completar documentacion.

## 3. Bloque 1: Compilacion

### Problema 1.1: `PathFinder.java` no compila

Archivo:

```text
src/main/java/casinoescape/movement/PathFinder.java
```

Problema detectado:

```java
if (canVisit(room, visited, next, goal)) {
```

Pero solo existe:

```java
private boolean canVisit(Room room, MyMatrix<Boolean> visited, Position position)
```

Impacto:

- El proyecto no compila.
- No se pueden ejecutar los tests.
- JavaFX no puede arrancar.
- `PathFinderTest` no puede pasar.
- La informacion de ruta minima queda bloqueada.

Accion necesaria:

- Corregir la llamada o ampliar el metodo `canVisit`.
- Verificar que el BFS permita calcular rutas hasta celdas adyacentes a puertas y salida.
- Mantener bloqueados NPCs, enemigos y obstaculos.

Criterio de cierre:

- El proyecto compila.
- `PathFinderTest` pasa.
- `Game.getShortestPathInfo()` funciona sin excepciones.

## 4. Bloque 2: Decisiones De Reglas Oficiales

Antes de tocar balance o tests, hay que cerrar que reglas mandan.

### Problema 2.1: Estadisticas iniciales inconsistentes

Requisito indicado:

```text
corazones: 20
escudo: 0
ataque: 4
movimiento: 3
turnos: 125
```

Codigo actual:

```java
new Player(100, 10, 5, 3, ...)
```

Y JavaFX/config usan:

```text
30 turnos
```

Accion necesaria:

- El balance oficial queda fijado según las reglas cerradas del apartado 0.
- El código actual debe adaptarse a dichos valores.
- Actualizar:
  - `Game.createNewGame`,
  - `GameConfigLoader`,
  - `CasinoEscapeApp.DEFAULT_TURNS`,
  - `config/game_config.json`,
  - tests afectados.

Criterio de cierre:

- Todos los puntos de entrada crean el mismo jugador inicial.
- El README, tests y JSON coinciden.

### Problema 2.2: Formula de dano inconsistente

Requisito indicado:

```text
danoFinal = max(0, ataqueAtacante - escudoDefensor)
```

Documento `JUEGO.md` y codigo actual:

```text
dano = max(0, ataque * (aleatorio * 2) - defensa)
```

Archivo actual:

```text
src/main/java/casinoescape/combat/DamageCalculator.java
```

Accion necesaria:

- La fórmula oficial queda fijada según el apartado 0.
- Todos los módulos, tests y documentación deben adaptarse a esta fórmula.
- Ajustar `CombatService`, tests y ruleta/enemigos si procede.

Criterio de cierre:

- La formula usada en codigo, tests y documentacion es unica.

## 5. Bloque 3: Movimiento Y Celdas Interactivas

### Problema 3.1: Movimiento puede sobrescribir objetos, tienda, salida, ruleta o trampa

Archivos implicados:

```text
src/main/java/casinoescape/model/Cell.java
src/main/java/casinoescape/movement/MovementService.java
src/main/java/casinoescape/ui/GameController.java
```

Actualmente `Cell.isWalkable()` permite moverse sobre:

```text
ITEM
DOOR
TRAP
SHOP
EXIT
MINIGAME
```
- `TRAP` sí es transitable según las reglas oficiales.
- El problema real afecta a `ITEM`, `DOOR`, `SHOP`, `EXIT` y `MINIGAME`, que no deben ser transitables.
3
Y `MovementService.movePlayer` hace:

```java
room.setCellType(origin, CellType.EMPTY);
player.setPosition(destination);
room.setCellType(destination, CellType.PLAYER);
```

Riesgo:

- Una celda `ITEM` puede convertirse en `PLAYER`.
- Al salir, la celda puede quedar `EMPTY`.
- La tienda puede desaparecer.
- La ruleta puede desaparecer.
- La salida puede desaparecer.
- La trampa puede desaparecer.
- La matriz y las listas internas pueden quedar desincronizadas.

Accion recomendada:

- Implementar la regla oficial de transitabilidad:
  - ITEM, SHOP, EXIT, MINIGAME, DOOR, NPC y ENEMY no son transitables.
  - TRAP sí es transitable, se activa al pisarla y después pasa a EMPTY.
  - EMPTY es transitable.
- El jugador debe interactuar con objetos, puertas, tienda, salida, ruleta y NPCs desde una celda adyacente.

Criterio de cierre:

- El jugador no puede destruir celdas interactivas moviendose.
- Click en celda interactiva desde JavaFX no llama a `movePlayer` por defecto.
- Hay tests para `ITEM`, `SHOP`, `EXIT`, `MINIGAME`, `DOOR`, `NPC`, `ENEMY` y `TRAP`.
- Los tests de `TRAP` deben comprobar que sí es transitable, aplica daño y desaparece.

### Problema 3.2: Movimiento especial en linea tambien puede sobrescribir contenido

Archivo:

```text
src/main/java/casinoescape/movement/MovementService.java
```

Accion necesaria:

- Aplicar la misma regla que en movimiento normal.
- El movimiento en línea debe detenerse antes de puertas, enemigos, NPCs, tienda, salida, ruleta, objetos, obstáculos y bordes de la matriz.
- Las trampas no detienen el movimiento especial: se pisan, aplican daño, desaparecen y el movimiento continúa si no hay otro bloqueo.

Criterio de cierre:

- La Pastilla de dudosa procedencia no permite borrar contenido.

## 6. Bloque 4: Puertas Y Cambio De Sala

### Problema 4.1: `useDoorTo` permite cambiar de sala sin estar junto a la puerta

Archivo:

```text
src/main/java/casinoescape/game/Game.java
```

Metodo problematico:

```java
public void useDoorTo(int destinationRoomId)
```

Problema:

- Valida conexion y llave.
- No valida posicion fisica del jugador respecto a una puerta concreta.

Accion recomendada:

- Hacer que el flujo publico principal sea `useDoorAt(Position doorPosition)`.
- Convertir `useDoorTo` en privado o hacer que busque y valide puerta adyacente.
- Actualizar tests que usan `useDoorTo` directamente.

Criterio de cierre:

- No se puede cambiar de sala desde una posicion arbitraria.
- Cambiar de sala requiere puerta existente y adyacente.
- La puerta bloqueada de sala 2 a sala 3 exige llave real.

## 7. Bloque 5: Inventario Y Objetos

### Problema 5.1: Falta capacidad maxima de inventario

Requisito:

```text
capacidad maxima: 8 objetos
```

Archivo:

```text
src/main/java/casinoescape/items/Inventory.java
```

Accion necesaria:

- Anadir constante `MAX_ITEMS = 8`.
- Impedir `addItem` si el inventario esta lleno.
- Definir excepcion o error claro.

Criterio de cierre:

- Test de inventario lleno.
- Fichas no ocupan inventario.

### Problema 5.2: Primera arma no se equipa automaticamente

Requisito:

```text
Primera arma se equipa automaticamente si no hay arma equipada.
```

Accion necesaria:

- Al recoger o anadir un arma, si no hay arma equipada, equiparla automaticamente.
- Cuidar que esto no rompa restauracion desde JSON.

Criterio de cierre:

- Recoger Botella rota la equipa si no hay arma.
- Si ya hay arma equipada, no se cambia automaticamente.

### Problema 5.3: Quitar objeto equipado no revierte bonus

Riesgo:

- Si se elimina un arma/armadura equipada, el bonus puede permanecer.

Accion necesaria:

- Revisar `removeItem` y `removeAt`.
- Si el objeto eliminado esta equipado, desequipar y revertir bonus.

Criterio de cierre:

- Tests de eliminar arma/armadura equipada.

## 8. Bloque 6: Economia Y Tienda

### Problema 6.1: Tienda incompleta

Archivo:

```text
src/main/java/casinoescape/items/Shop.java
```

Actualmente contiene:

- Llave de Tesoreria.
- Coctel curativo.

Requisitos previstos:

- Llave de Tesoreria.
- Vodka Redbull.
- Coctel curativo.
- Chaleco de portero.

Accion necesaria:

- Completar la tienda con todos los objetos definidos como oficiales.
- Añadir tests de compra para cada objeto de tienda.

Criterio de cierre:

- La tienda contiene obligatoriamente Llave de Tesorería, Vodka Redbull, Cóctel curativo y Chaleco de portero.

### Problema 6.2: Precios no coinciden con requisitos indicados

Requisito indicado:

```text
Vodka Redbull: 10 fichas
Coctel curativo: 18 fichas
Chaleco de portero: 25 fichas
Llave de Tesoreria: 30 fichas
```

Codigo actual:

```text
Llave: 6
Coctel: 3
```

Accion necesaria:

- Aplicar los precios oficiales definidos en este documento.
- Ajustar drops de enemigos solo si algún flujo de partida queda matemáticamente imposible.

Criterio de cierre:

- El jugador puede conseguir suficientes fichas para comprar llave.
- Los precios coinciden con documentacion y tests.

## 9. Bloque 7: Combate Y Enemigos

### Problema 7.1: Valores de jugador y enemigos no coinciden con requisitos indicados

Codigo actual usa valores mas altos:

```text
Jugador: 100 vida, 10 ataque, 5 defensa
Enemigos: vida y ataques superiores a los valores del enunciado simplificado
```

Accion necesaria:

- Aplicar el balance oficial definido en las reglas cerradas y documentación del proyecto.
- Ajustar `CasinoMapBuilder` y tests.

Criterio de cierre:

- Enemigos, jugador, drops y precios forman un sistema jugable y coherente.

### Problema 7.2: Fase enemiga real poco probada

Archivos:

```text
src/main/java/casinoescape/game/Game.java
src/main/java/casinoescape/movement/EnemyMovementService.java
```

Accion necesaria:

- Anadir tests de `Game.endTurn()` con enemigo adyacente.
- Anadir tests de movimiento enemigo con BFS.
- Verificar derrota por ataque enemigo.

Criterio de cierre:

- Los enemigos se mueven o atacan tras el turno del jugador.
- La derrota por combate enemigo queda probada.

## 10. Bloque 8: JSON Y Persistencia

### Problema 8.1: Configuracion inicial JSON incompleta

Archivo:

```text
config/game_config.json
```

Actualmente define celdas y conexiones, pero no toda la configuracion dinamica.

Falta representar en JSON:

- enemigos con estadisticas,
- objetos con tipo y efectos,
- drops,
- tienda,
- NPCs con ids y comportamiento,
- trampa con dano,
- estado inicial completo del jugador.

Accion recomendada:

- Ampliar esquema JSON.
- Actualizar `PersistenceData`.
- Actualizar `JsonValidator`.
- Actualizar `GameConfigLoader`.
- Reducir hardcodeo de `CasinoMapBuilder`.

Criterio de cierre:

- La configuracion inicial permite reconstruir el mundo base sin inyectar contenido dinamico hardcodeado.

### Problema 8.2: Guardado/carga acoplado a IDs base

Archivos:

```text
src/main/java/casinoescape/persistence/GameSaveWriter.java
src/main/java/casinoescape/persistence/GameSaveLoader.java
```

Riesgo:

- Si se anade contenido, puede no guardarse.
- Solo se guarda explicitamente la puerta de Tesoreria.
- Enemigos y objetos se persisten mediante listas fijas.

Accion necesaria:

- Hacer guardado mas generico o documentar claramente el modelo cerrado.
- Guardar enemigos existentes recorriendo habitaciones.
- Guardar objetos de sala recorriendo habitaciones.

Criterio de cierre:

- Guardar/cargar conserva estado real de mapa, enemigos, objetos, inventario y jugador.

## 11. Bloque 9: Logs Y Excepciones

### Problema 9.1: Logs sin turno en la mayoria de eventos

Archivo:

```text
src/main/java/casinoescape/logging/GameLog.java
```

Accion recomendada:

- Registrar eventos con numero de turno cuando sea posible.
- Mantener `add(String)` solo para eventos de sistema si se justifica.

Criterio de cierre:

- Movimiento, ataque, compras, cambios de sala, victoria y derrota incluyen turno.

### Problema 9.2: Uso inconsistente de excepciones personalizadas

Ejemplos:

- Algunas acciones lanzan `IllegalStateException`.
- Otras lanzan `InvalidActionException`.
- Movimiento usa `InvalidMoveException` en algunos puntos, pero otros servicios usan `IllegalArgumentException`.

Accion necesaria:

- Homogeneizar excepciones en capa `game`.
- Reservar excepciones estandar para validaciones internas simples.

Criterio de cierre:

- Movimiento invalido -> `InvalidMoveException`.
- Accion invalida -> `InvalidActionException`.
- Puerta bloqueada -> `LockedDoorException`.
- Compra sin fichas -> `NotEnoughChipsException`.
- JSON invalido -> `InvalidConfigurationException` o `PersistenceException`.

## 12. Bloque 10: JavaFX

### Problema 10.1: Click en celda no distingue bien movimiento e interaccion

Archivo:

```text
src/main/java/casinoescape/ui/GameController.java
```

Actualmente:

```java
if (cell.getType() == CellType.DOOR) {
    game.useDoorAt(position);
} else {
    game.movePlayer(position);
}
```

Riesgo:

- Click en tienda, salida, objeto, ruleta o trampa intenta mover.

Accion recomendada:

- Si la celda es interactiva, llamar a una accion de interaccion o mostrar mensaje.
- Solo mover si la celda es realmente destino de movimiento permitido.

Criterio de cierre:

- La UI no puede destruir celdas especiales.
- Las acciones visibles coinciden con el estado del juego.

### Problema 10.2: Pruebas manuales JavaFX pendientes

Accion necesaria:

- Probar manualmente:
  - movimiento,
  - recoger objeto,
  - atacar,
  - tienda,
  - puerta bloqueada,
  - rescate amigo,
  - ruleta,
  - salida,
  - guardar/cargar,
  - victoria/derrota.

Criterio de cierre:

- Registrar resultado de pruebas manuales en documentacion.

## 13. Bloque 11: Tests

### Tests imprescindibles nuevos

Anadir tests para:

1. Proyecto compila y `PathFinderTest` pasa.
2. No se puede mover a `ITEM` sin recogerlo.
3. No se puede mover a `SHOP` y borrarla.
4. No se puede mover a `EXIT` y borrarla.
5. No se puede mover a `MINIGAME` y borrarla.
6. No se puede usar puerta sin estar adyacente.
7. `useDoorTo` no salta reglas fisicas.
8. Inventario maximo 8.
9. Primera arma se equipa automaticamente.
10. Fase enemiga ataca si esta adyacente.
11. Fase enemiga se mueve si no esta adyacente.
12. Derrota por ataque enemigo.
13. Flujo completo de victoria.
14. Guardar/cargar conserva estado tras recoger objeto y matar enemigo.

## 14. Bloque 12: Documentacion Final

### Pendiente

Crear carpeta:

```text
docs/
```

Con al menos:

- UML de clases.
- Diagrama de casos de uso.
- Bocetos JavaFX.
- Memoria tecnica.
- Justificacion de estructuras propias.
- Explicacion de BFS.
- Explicacion de JSON.
- Diario IA consolidado.
- Guia de ejecucion.

Criterio de cierre:

- La documentacion permite defender el proyecto sin leer todo el codigo.

## 15. Orden De Trabajo Recomendado

### Fase 1: Estabilizacion tecnica

1. Corregir `PathFinder`.
2. Ejecutar tests.
3. Corregir errores de compilacion restantes si aparecen.

### Fase 2: Reglas base

1. Aplicar estadísticas iniciales oficiales.
2. Aplicar fórmula de daño oficial.
3. Aplicar 125 turnos iniciales.
4. Aplicar precios y drops oficiales.

### Fase 3: Bugs funcionales graves

1. Corregir movimiento sobre celdas interactivas.
2. Corregir puertas.
3. Anadir tests de regresion.

### Fase 4: Inventario y economia

1. Capacidad maxima 8.
2. Autoequipar primera arma.
3. Completar tienda.
4. Ajustar balance.

### Fase 5: Enemigos y partida completa

1. Probar fase enemiga real.
2. Probar derrota por enemigo.
3. Crear test de victoria completa.

### Fase 6: JSON y persistencia

1. Ampliar configuracion inicial.
2. Hacer guardado/carga mas completo.
3. Probar restauracion de partida avanzada.

### Fase 7: JavaFX y documentacion

1. Ajustar click/interaccion.
2. Ejecutar pruebas manuales.
3. Crear UML, bocetos y memoria.

## 16. Reparto Recomendado

### Persona A: Estructuras Y Algoritmos

- Corregir `PathFinder`.
- Revisar BFS de matriz y grafo.
- Revisar `EnemyMovementService`.
- Documentar costes.
- Comprobar que no se usan estructuras prohibidas.

### Persona B: Logica Del Juego

- Corregir movimiento.
- Corregir puertas.
- Aplicar estadísticas oficiales, fórmula oficial y turnos oficiales.
- Completar inventario.
- Completar economia.
- Anadir tests de partida completa.

### Persona C: JavaFX, JSON Y Documentacion

- Ajustar UI para no destruir celdas.
- Ampliar JSON.
- Revisar guardado/carga.
- Crear documentacion final.
- Ejecutar pruebas manuales JavaFX.

## 17. Definicion De Proyecto Estable

El proyecto puede considerarse estable cuando:

- Compila sin errores.
- Todos los tests pasan.
- El jugador no puede borrar celdas especiales moviendose.
- Las puertas solo se usan desde posicion valida.
- El inventario respeta capacidad y equipamiento.
- La tienda permite comprar la llave y objetos previstos.
- Los enemigos actuan tras el jugador.
- Se puede ganar rescatando al amigo y saliendo por sala 8.
- Se puede perder por vida o turnos.
- JSON carga configuracion y partida guardada.
- JavaFX muestra sala, jugador, inventario, acciones, ruta y log.
- La documentacion explica decisiones, estructuras, BFS, JSON y arquitectura.
