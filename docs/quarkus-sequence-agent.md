# Agentes y `outputKey` en Quarkus LangChain4j

Síntesis de los dos conceptos que sostienen el trabajo agentic: la anotación
`@Agent` (qué es un agente) y `outputKey` (cómo se pasan datos entre agentes).

## 1. ¿Qué es un agente?

En `quarkus-langchain4j-agentic`, un **agente** es un método de una interfaz que el
modelo (LLM) ejecuta. No se implementa a mano: la extensión detecta la interfaz en
build-time, la valida y la registra como **bean CDI `@ApplicationScoped`** (no lleva
`@RegisterAiService`). Se inyecta y se invoca como cualquier bean.

## 2. `@Agent`

- Import: `dev.langchain4j.agentic.Agent`.
- Se coloca en un **método** (no en la clase).
- El prompt se define con `@UserMessage` (y opcionalmente `@SystemMessage`).
- El tipo de retorno es la salida del agente (`String` se envía tal cual; los
  records/objetos se serializan a JSON).

Atributos principales:

| Atributo | Para qué sirve | Por defecto |
| --- | --- | --- |
| `value` / `description` | Describe qué hace el agente. Imprescindible en patrones donde el LLM elige agente (p. ej. `@SupervisorAgent`). | vacío |
| `outputKey` | Clave bajo la que se guarda el resultado en el `AgenticScope`. | vacío |
| `name` | Identificador del agente dentro del workflow (se usa en logs/observabilidad). | nombre del método |
| `async` | Ejecuta el agente de forma asíncrona. | `false` |
| `optional` | El agente puede no ejecutarse. | `false` |
| `compensateOnError` | Permite compensación si un paso posterior falla. | `false` |

> Regla práctica: **declara siempre `outputKey`**. Si se omite, su valor por defecto
> es vacío y el resultado no queda publicado con una clave legible en el scope.

Ejemplo:

```java
public interface WeatherExpertAgent {

    @UserMessage("Responde el clima de '{{request}}' usando las herramientas.")
    @Agent(description = "Experto en clima", outputKey = "response")
    String answer(String request);
}
```

## 3. `outputKey`

- Es la **clave** bajo la que el resultado del agente se escribe en el
  `AgenticScope`.
- Permite que **el siguiente agente** (o el código que invoca el workflow) lea ese
  valor sin acoplamiento directo entre agentes.
- Es el "contrato" entre pasos: si un agente escribe bajo `location`, el siguiente
  declara un parámetro llamado `location` y lo recibe.

## 4. El `AgenticScope`: el tablero compartido

`@SequenceAgent` / `@ConditionalAgent` **no pasan valores por parámetros Java** entre
subagentes. Usan un estado compartido:

- **Entradas:** los parámetros del método del workflow se escriben en el scope por
  nombre (p. ej. `destination`, `days`, `preferences`).
- **Salidas:** cada agente escribe su resultado bajo su `outputKey`.
- **Lectura:** cada subagente declara parámetros cuyo **nombre coincide** con una
  clave del scope y los recibe automáticamente.
- `@MemoryId` es especial: identifica la conversación (memoria de chat) y **no** se
  escribe en el scope.
- Para leer el scope completo, el workflow devuelve `ResultWithAgenticScope<T>`
  (`result.result()` + `result.agenticScope()`).

## 5. Ejemplo real (E1 – Trip Planner)

`TripPlannerWorkflow` encadena cuatro agentes; el scope evoluciona así:

| Paso | Agente | Lee del scope | Escribe (`outputKey`) |
| --- | --- | --- | --- |
| 1 | `DestinationResolverAgent.resolve` | `destination` | `location` |
| 2 | `ForecastAgent.forecast` | `location`, `days` | `weather` |
| 3 | `ActivityPlannerAgent.activities` | `location`, `weather`, `preferences` | `activities` |
| 4 | `ItineraryComposerAgent.compose` | `location`, `weather`, `activities`, `days`, `preferences` | `itinerary` |

```java
public interface TripPlannerWorkflow {

    @SequenceAgent(outputKey = "itinerary",
            subAgents = { DestinationResolverAgent.class, ForecastAgent.class,
                    ActivityPlannerAgent.class, ItineraryComposerAgent.class })
    ResultWithAgenticScope<String> plan(@MemoryId String sessionId,
            String destination, int days, String preferences);
}
```

## 6. Reglas y errores frecuentes

- **Coincidencia de nombres:** el parámetro que lee un agente debe llamarse igual que
  el `outputKey` que produjo el anterior. Un desajuste provoca
  `MissingArgumentException`.
- **`outputKey` único y semántico:** usa nombres claros (`location`, `weather`,
  `itinerary`) para que el flujo se entienda sin mirar el código.
- **`@MemoryId` fuera del scope:** no lo declares como dependencia de otro agente.
- **Atributos de agente estáticos:** `@ActivationCondition`, `@ExitCondition`,
  `@Output`, `@ErrorHandler` deben ser métodos `static` y sus parámetros se resuelven
  del scope.
- **Tools:** se aportan con un método `@ToolsSupplier` `static` en la propia interfaz
  del agente (los beans CDI se inyectan con `@CdiBean`).
- **Coste:** cada agente es una llamada al LLM; en secuencia, N agentes = N llamadas
  (más rondas si usan tools).
