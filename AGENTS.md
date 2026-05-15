# Instrucciones Para Agentes Y Subagentes

Este archivo define las reglas que debe seguir cualquier agente o subagente que trabaje en el proyecto.

## Lectura Obligatoria

Antes de analizar, disenar o implementar, el agente debe leer:

1. `JUEGO.md`
2. `PLAN.md`
3. `ARCHITECTURE.md`
4. El documento especifico del modulo que vaya a tocar, por ejemplo `STRUCTURES.md` o `TESTING.md`

Si el agente no ha leido esos documentos, no debe proponer cambios.

## Reglas Generales

- No inventar mecanicas nuevas.
- No cambiar el grafo definitivo.
- No cambiar las salas ni su identidad tematica.
- No cambiar la condicion de victoria.
- No introducir ampliaciones antes de cerrar la base funcional.
- No usar estructuras prohibidas para resolver requisitos evaluados.
- No mezclar logica de juego con JavaFX.
- No modificar documentacion maestra sin justificarlo.
- Si una decision no esta documentada, debe preguntar o marcarla como pendiente.

## Restricciones Academicas Criticas

No usar como estructura principal:

- `ArrayList`
- `HashMap`
- `LinkedList`
- `Queue`
- `Stack`
- `Deque`
- Estructuras equivalentes de `java.util` para listas, colas, pilas, grafos, matrices, inventario, log, turnos o BFS.

La logica central debe apoyarse en estructuras propias.

## Separacion De Responsabilidades

La arquitectura debe respetar:

- `structures`: estructuras propias genericas.
- `model`: entidades del dominio.
- `game`: coordinacion de partida y turnos.
- `movement`: movimiento y BFS en matriz.
- `combat`: combate y dano.
- `items`: objetos, inventario y tienda.
- `persistence`: JSON.
- `ui`: JavaFX.
- `exceptions`: excepciones personalizadas.
- `logging`: log del juego.

La interfaz JavaFX solo debe consultar el estado y solicitar acciones al modelo. No debe calcular reglas de movimiento, combate, victoria o economia.

## Tipos De Subagente Recomendados

### structure-reviewer

Uso:

- Revisar estructuras propias.
- Buscar incumplimientos academicos.
- Revisar costes y operaciones.

Debe leer:

- `JUEGO.md`
- `PLAN.md`
- `STRUCTURES.md`

No debe:

- Disenar mecanicas de juego.
- Tocar JavaFX.

### test-planner

Uso:

- Disenar pruebas JUnit.
- Detectar casos borde.

Debe leer:

- `JUEGO.md`
- `PLAN.md`
- `TESTING.md`

No debe:

- Cambiar reglas del juego.

### architecture-reviewer

Uso:

- Revisar paquetes, dependencias y separacion modelo/vista.

Debe leer:

- `ARCHITECTURE.md`
- `PLAN.md`

No debe:

- Meter funcionalidades fuera de fase.

### documentation-checker

Uso:

- Revisar entregables academicos.
- Revisar memoria, UML, diario IA y justificaciones.

Debe leer:

- `JUEGO.md`
- `PLAN.md`
- `PROMPTS_IA.md`

## Plantilla De Prompt Para Subagentes

```text
Lee JUEGO.md, PLAN.md y el documento especifico del modulo. Trabaja solo sobre el modulo indicado. No inventes mecanicas, no cambies el grafo, no uses estructuras prohibidas y no mezcles JavaFX con logica. Devuelve hallazgos, riesgos, propuesta concreta y pruebas necesarias. Si falta una decision, preguntala.
```

## Criterio De Calidad

Una propuesta de agente es aceptable si:

- Respeta el diseno cerrado.
- Es implementable en Java por un grupo de 3.
- Ayuda a cumplir el enunciado.
- Mantiene modularidad.
- Incluye pruebas o criterios de verificacion.
- No aumenta complejidad sin necesidad.
