# Diario Y Prompts De IA

Este archivo servira como base del diario obligatorio de uso de IA.

Cada uso relevante de IA debe registrarse. No hace falta registrar consultas triviales, pero si cualquier uso que afecte al diseno, codigo, pruebas o documentacion.

## Formato De Registro

```text
Fecha:
Persona:
Herramienta o agente:
Modulo afectado:
Objetivo:
Prompt usado:
Resultado obtenido:
Cambios aplicados:
Cambios rechazados:
Revision humana realizada:
Problemas detectados:
Valoracion critica:
Acciones de mejora:
```

## Registro Inicial

```text
Fecha: 2026-05-15
Persona: equipo
Herramienta o agente: OpenCode / GPT-5.5
Modulo afectado: planificacion general
Objetivo: analizar enunciado, definir tematica, mapa, salas, mecanicas y plan modular.
Prompt usado: conversacion inicial con PDFs de la practica y documento base del companero.
Resultado obtenido: JUEGO.md, PLAN.md y documentacion auxiliar.
Cambios aplicados: se fija casino parodico, grafo de 8 salas, amigo como llave narrativa, bar como tienda, sala 3 bloqueada y ruleta rusa final optativa.
Cambios rechazados: minijuegos complejos como base inicial, salida secreta en base, IA avanzada, amigo como acompanante fisico.
Revision humana realizada: pendiente por el equipo.
Problemas detectados: necesidad de controlar que ningun agente invente mecanicas nuevas.
Valoracion critica: la IA ayudo a estructurar el alcance y reducir complejidad, pero el equipo debe revisar que todo sea implementable y defendible.
Acciones de mejora: mantener documentacion actualizada y registrar prompts tecnicos posteriores.
```

## Registro De Modulos Implementados

```text
Fecha: 2026-05-20
Persona: equipo
Herramienta o agente: OpenCode / GPT-5.5
Modulo afectado: modulo 1 - estructuras propias
Objetivo: crear estructuras propias genericas para sustituir estructuras prohibidas de java.util.
Prompt usado: peticion de iniciar el primer modulo de construccion siguiendo STRUCTURES.md y TESTING.md.
Resultado obtenido: MyLinkedList, MyQueue, MyStack, MyMatrix, MyGraph y tests asociados.
Cambios aplicados: se implementaron estructuras con nodos propios, matriz con array nativo y BFS de grafo usando cola propia.
Cambios rechazados: no se implemento logica de juego, JavaFX, JSON ni entidades de dominio.
Revision humana realizada: tests del modulo 1 ejecutados correctamente en IntelliJ por el equipo.
Problemas detectados: Maven no esta disponible en PATH en el entorno del agente; la verificacion JUnit se hizo desde IntelliJ.
Valoracion critica: el modulo satisface la restriccion academica principal y queda como base para inventario, mapa, movimiento y turnos.
Acciones de mejora: mantener busquedas de estructuras prohibidas tras cada modulo.
```

```text
Fecha: 2026-05-20
Persona: equipo
Herramienta o agente: OpenCode / GPT-5.5
Modulo afectado: modulo 2 - modelo minimo
Objetivo: crear las clases minimas del dominio sin interfaz grafica.
Prompt usado: peticion de implementar Position, CellType, Cell, Room, Player y GameState usando MyMatrix para Room.
Resultado obtenido: modelo minimo y tests asociados.
Cambios aplicados: se crearon posicion, celdas, salas con matriz propia, jugador basico y estado de juego.
Cambios rechazados: no se implementaron combate, inventario, JSON, JavaFX ni mapa completo.
Revision humana realizada: tests del modulo 2 ejecutados correctamente en IntelliJ por el equipo.
Problemas detectados: el alcance se mantuvo como modelo minimo; Enemy, Item, Inventory y Game quedan para modulos posteriores.
Valoracion critica: el modulo permite construir salas y jugador sin acoplarse a JavaFX.
Acciones de mejora: ampliar el modelo solo cuando el modulo correspondiente lo requiera.
```

```text
Fecha: 2026-05-20
Persona: equipo
Herramienta o agente: OpenCode / GPT-5.5
Modulo afectado: modulo 3 - mapa y habitaciones
Objetivo: implementar el mapa logico del casino segun JUEGO.md y PLAN.md.
Prompt usado: peticion de iniciar el modulo 3 definido como "Modulo De Mapa Y Habitaciones", respetando grafo definitivo, salas 7x7, puertas, obstaculos, salida exterior y puerta bloqueada entre sala 2 y sala 3.
Resultado obtenido: CasinoMap, Door, CasinoMapBuilder y tests asociados.
Cambios aplicados: se represento el grafo con MyGraph, las salas con Room y MyMatrix, las puertas como celdas DOOR, la salida como EXIT, la posicion inicial del jugador y la puerta bloqueada de tesoreria.
Cambios rechazados: no se implemento JavaFX, JSON, combate, inventario, economia ni movimiento BFS dentro de salas.
Revision humana realizada: tests del modulo 3 ejecutados correctamente en IntelliJ por el equipo.
Problemas detectados: Maven no esta disponible en PATH en el entorno del agente; la verificacion JUnit se hizo desde IntelliJ.
Valoracion critica: el modulo queda cerrado para la base logica del mapa, pero los contenidos avanzados de cada sala quedan para modulos posteriores.
Acciones de mejora: mantener los tests de mapa al modificar salas, puertas o transiciones.
```

```text
Fecha: 2026-05-20
Persona: equipo
Herramienta o agente: OpenCode / GPT-5.5
Modulo afectado: modulo 4 - movimiento
Objetivo: implementar movimiento del jugador dentro de salas y calculo de casillas alcanzables con BFS.
Prompt usado: peticion de iniciar el modulo 4 definido como "Modulo De Movimiento", usando Room, Position, Player, MyQueue y MyLinkedList, sin JavaFX, JSON, combate ni turnos completos.
Resultado obtenido: Direction, ReachableCellsCalculator, MovementService y tests asociados.
Cambios aplicados: se implemento BFS ortogonal con MyQueue, validacion de obstaculos y limites, movimiento normal del jugador y movimiento especial en linea recta.
Cambios rechazados: no se implemento sistema completo de turnos, IA enemiga, combate, inventario, JSON ni JavaFX.
Revision humana realizada: tests del modulo 4 ejecutados correctamente en IntelliJ por el equipo.
Problemas detectados: Maven no esta disponible en PATH en el entorno del agente; la verificacion JUnit se hizo desde IntelliJ.
Valoracion critica: el modulo cumple el movimiento base y deja preparada la integracion con turnos.
Acciones de mejora: mantener tests de movimiento al introducir enemigos, trampas, puertas y efectos temporales.
```

```text
Fecha: 2026-05-20
Persona: equipo
Herramienta o agente: OpenCode / GPT-5.5
Modulo afectado: modulo 5 - turnos
Objetivo: implementar el control basico de turnos del jugador.
Prompt usado: peticion de iniciar el modulo 5 definido como "Modulo De Turnos", con maximo un movimiento y una accion por turno.
Resultado obtenido: TurnPhase, TurnManager y tests asociados.
Cambios aplicados: se controlo movimiento usado, accion usada, fin de turno, contador de turnos, derrota por vida o por turnos y fin automatico tras cambio de sala.
Cambios rechazados: no se implemento IA enemiga, combate real, inventario, JSON ni JavaFX.
Revision humana realizada: tests del modulo 5 ejecutados correctamente en IntelliJ por el equipo.
Problemas detectados: la fase de enemigos queda preparada como placeholder sin IA ni combate real.
Valoracion critica: el modulo cubre el flujo minimo de turnos y queda preparado para integrarse con combate e IA posteriormente.
Acciones de mejora: conectar TurnManager con Game cuando exista el coordinador global.
```

```text
Fecha: 2026-05-20
Persona: equipo
Herramienta o agente: OpenCode / GPT-5.5 con subagentes explore usados como architecture-reviewer, test-planner y structure-reviewer
Modulo afectado: modulo 6 - combate, revision previa
Objetivo: revisar arquitectura, pruebas y restricciones antes de implementar combate.
Prompt usado: se pidio a subagentes revisar separacion model/combat/game, proponer tests JUnit de combate y confirmar ausencia de necesidad de estructuras prohibidas.
Resultado obtenido: recomendaciones para crear Enemy minimo, DamageCalculator, CombatService, tests deterministas con aleatorio controlado y evitar dependencias hacia ui, persistence, inventario o turnos avanzados.
Cambios aplicados: pendiente de implementacion del modulo 6.
Cambios rechazados: no se aceptan mecanicas nuevas, IA avanzada, inventario real ni drops complejos en combate base.
Revision humana realizada: pendiente tras implementar y probar el modulo 6.
Problemas detectados: decidir representacion entera del dano y representar drop del Crupier como texto hasta que exista inventario real.
Valoracion critica: el uso de subagentes ayuda a cumplir AGENTS.md y reduce riesgos de acoplamiento o incumplimiento academico.
Acciones de mejora: registrar el cierre del modulo 6 cuando sus tests pasen en IntelliJ.
```

```text
Fecha: 2026-05-20
Persona: equipo
Herramienta o agente: OpenCode / GPT-5.5
Modulo afectado: modulo 6 - combate, cierre
Objetivo: implementar sistema basico de combate con formula oficial, adyacencia, muerte, fichas y drop textual del Crupier.
Prompt usado: peticion de implementar el modulo 6 tras revision con subagentes, sin JavaFX, JSON, inventario real ni IA avanzada.
Resultado obtenido: Enemy, DamageCalculator, CombatResult, CombatService y tests asociados.
Cambios aplicados: se implemento dano determinista en tests, ataque jugador-enemigo, ataque enemigo-jugador, vida minima 0, fichas al matar y drop textual "Traje con escudo".
Cambios rechazados: no se implemento inventario real, tienda, IA enemiga, JSON ni JavaFX.
Revision humana realizada: tests del modulo 6 ejecutados correctamente en IntelliJ por el equipo.
Problemas detectados: el drop del Crupier queda como texto hasta el modulo de inventario y objetos.
Valoracion critica: el modulo cumple combate base y deja pendiente integrar recompensas reales con inventario.
Acciones de mejora: convertir drops textuales a objetos cuando exista Inventory.
```

```text
Fecha: 2026-05-20
Persona: equipo
Herramienta o agente: OpenCode / GPT-5.5 con subagentes explore usados como architecture-reviewer, test-planner y structure-reviewer
Modulo afectado: modulo 7 - inventario y objetos, revision previa
Objetivo: revisar arquitectura, pruebas y restricciones antes de implementar inventario y objetos.
Prompt usado: se pidio a subagentes revisar separacion items/model/combat/game, proponer tests JUnit y confirmar uso de MyLinkedList sin estructuras prohibidas.
Resultado obtenido: recomendaciones para implementar Item, Weapon, Armor, Consumable, KeyItem, Effect e Inventory con MyLinkedList, usando IDs estables y sin tienda ni JavaFX.
Cambios aplicados: pendiente de implementacion del modulo 7.
Cambios rechazados: no se aceptan tienda, compra con fichas, JSON, JavaFX, minijuegos ni economia avanzada en este modulo.
Revision humana realizada: pendiente tras implementar y probar el modulo 7.
Problemas detectados: CombatService entrega fichas y drop textual; la integracion con inventario real debe hacerse mas adelante desde game.
Valoracion critica: el uso de subagentes ayuda a proteger la separacion entre items, combat y game.
Acciones de mejora: registrar el cierre del modulo 7 cuando sus tests pasen en IntelliJ.
```

## Prompts Reutilizables

### Revisar Estructuras

```text
Lee JUEGO.md, PLAN.md, AGENTS.md y STRUCTURES.md. Revisa el modulo de estructuras propias para un proyecto Java universitario donde no se pueden usar ArrayList, HashMap, LinkedList ni estructuras equivalentes. Devuelve riesgos, operaciones necesarias, casos borde y pruebas minimas. No propongas mecanicas nuevas.
```

### Disenar Tests

```text
Lee JUEGO.md, PLAN.md y TESTING.md. Disena pruebas JUnit 5 para el modulo indicado. Devuelve nombre del test, preparacion, accion, resultado esperado y casos borde. No escribas codigo si no se te pide.
```

### Revisar Arquitectura

```text
Lee JUEGO.md, PLAN.md, AGENTS.md y ARCHITECTURE.md. Revisa si la arquitectura respeta separacion entre logica e interfaz, restricciones de estructuras propias y requisitos academicos. Devuelve hallazgos ordenados por severidad.
```

### Revisar Codigo

```text
Lee JUEGO.md, PLAN.md, AGENTS.md y los archivos del modulo indicado. Haz una revision de codigo centrada en bugs, restricciones academicas, complejidad innecesaria y pruebas faltantes. No cambies archivos salvo que se te pida explicitamente.
```

### Crear Documentacion Academica

```text
Lee JUEGO.md, PLAN.md, ARCHITECTURE.md y el codigo existente. Redacta la seccion indicada de la memoria con lenguaje academico claro, incluyendo decisiones, justificacion, costes si aplica y limitaciones.
```

## Criterios De Uso Responsable

- Todo codigo generado debe ser revisado por el equipo.
- Todo diseno generado debe poder explicarse oralmente.
- No aceptar propuestas que incumplan el enunciado.
- No aceptar codigo que use estructuras prohibidas en partes evaluadas.
- Registrar prompts importantes.
- Documentar modificaciones humanas posteriores.
