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
