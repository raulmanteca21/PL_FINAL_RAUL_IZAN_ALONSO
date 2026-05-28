# UML Casino Escape

Esta carpeta contiene los diagramas UML fuente en PlantUML para la entrega academica del proyecto Casino Escape. Los diagramas representan el codigo real de `src/main/java/casinoescape` y priorizan claridad frente a exhaustividad.

## Diagramas

- `casos_uso.puml`: casos de uso principales del jugador, incluyendo movimiento, interaccion, inventario, combate, guardado, carga, ruta minima y final de partida.
- `diagrama_clases_resumido.puml`: clases principales del sistema, separadas por paquetes, con relaciones relevantes y estructuras propias.
- `secuencia_movimiento.puml`: flujo de movimiento del jugador desde JavaFX hasta la logica de BFS y refresco de interfaz.
- `estados_juego.puml`: ciclo de vida de la partida, fases de turno, guardado/carga, victoria y derrota.
- `actividad_turno_jugador.puml`: actividad general de un turno: mostrar estado, mover, actuar, procesar enemigos y comprobar fin de partida.
- `secuencia_guardado.puml`: secuencia opcional de guardado JSON mediante `GameSaveWriter`.
- `secuencia_cambio_habitacion.puml`: secuencia opcional de uso de puerta y cambio de sala.

## Criterio de simplificacion

El diagrama de clases no incluye todas las clases auxiliares, DTOs internos, vistas JavaFX pequenas ni excepciones para evitar saturacion. Las clases omitidas estan representadas por notas o por relaciones con los servicios principales.

## Exportacion

Con PlantUML instalado, se pueden exportar todos los diagramas desde la raiz del proyecto:

```bash
plantuml -tpng docs/uml/*.puml
plantuml -tsvg docs/uml/*.puml
```

Las imagenes generadas pueden insertarse en la memoria final en PDF.
