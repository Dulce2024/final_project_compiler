# Final Project Compiler — Base Project

Proyecto base para el examen final de Compiladores.

## Requisitos

- Java 8 o superior.
- `javac` y `java` disponibles en consola.
- No requiere Maven, Gradle ni dependencias externas.

## Objetivo del proyecto

El proyecto ya incluye la base mínima de un front-end de compilador SQL:

- Lexer / tokenización.
- Parser básico para `SELECT <columnas> FROM <tabla>`.
- AST mínimo.
- Validación semántica contra un schema fijo.
- Diagnósticos.
- Tests manuales.

La tarea del examen es completar el soporte para cláusulas `WHERE`.

## Validar que el proyecto base está listo

Desde la raíz del proyecto:

```bash
./run-tests.sh
```

Resultado final con la implementación de WHERE completa:

```text
PASS valid SELECT without WHERE
PASS unknown projection column
PASS TODO WHERE AST and trace
PASS TODO WHERE unknown column diagnostic
PASS TODO WHERE type mismatch diagnostic
PASS TODO WHERE missing operand diagnostic
Passed: 6 Failed: 0
```

![Resultado de las pruebas](evidencia/resultado_tests.png)

## Restricciones

- Mantener Java 8.
- No agregar librerías externas.
- No modificar los tests para ocultar fallos.
- Implementar la solución dentro de `src/`.
