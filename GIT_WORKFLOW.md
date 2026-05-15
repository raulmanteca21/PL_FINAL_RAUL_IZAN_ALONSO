# Flujo De Trabajo Con GitHub E IntelliJ

## Herramientas

- IDE: IntelliJ IDEA.
- Repositorio remoto: GitHub.
- Gestor recomendado: Maven.
- Java recomendado: Java 17.
- Tests: JUnit 5.
- Interfaz: JavaFX.

## Ramas Recomendadas

```text
main
develop
feature/structures
feature/domain
feature/movement
feature/combat
feature/items-shop
feature/persistence
feature/javafx
feature/docs
```

Uso:

- `main`: version estable entregable.
- `develop`: integracion del trabajo del equipo.
- `feature/*`: trabajo por modulo.

## Reglas De Commit

Commits pequenos y descriptivos.

Ejemplos:

```text
add linked list implementation
add queue unit tests
implement graph shortest path
document structure costs
add matrix bounds validation
```

Evitar commits tipo:

```text
cambios
cosas
arreglo
final
```

## Reglas De Pull Request

Cada PR debe indicar:

- Modulo afectado.
- Que se ha implementado.
- Que tests se han pasado.
- Riesgos o decisiones pendientes.

No mezclar en una PR:

- Estructuras y JavaFX.
- Combate y persistencia.
- Documentacion masiva y cambios de logica no relacionados.

## Configuracion Recomendada En IntelliJ

1. Abrir el proyecto desde la carpeta raiz.
2. Importar como Maven.
3. Configurar SDK Java 17.
4. Activar descarga automatica de dependencias Maven.
5. Ejecutar tests con JUnit desde IntelliJ.
6. Usar integracion Git de IntelliJ o terminal, pero revisar siempre el diff.

## Orden De Arranque

1. Crear proyecto Maven.
2. Crear `pom.xml` con Java, JUnit y JavaFX.
3. Crear paquetes base.
4. Implementar estructuras.
5. Ejecutar tests.
6. Subir rama `feature/structures`.
7. Revisar y fusionar a `develop`.

## Archivos Que No Deben Subirse

Depende de configuracion, pero normalmente evitar:

```text
target/
.idea/workspace.xml
*.iml si el equipo decide no versionarlos
out/
*.class
```

Debe crearse `.gitignore` al inicializar el proyecto.

## Reglas Para Evitar Conflictos

- Una persona no debe tocar todos los modulos a la vez.
- Avisar antes de modificar documentacion maestra.
- Sincronizar `develop` antes de empezar una nueva tarea.
- Revisar tests antes de fusionar.
