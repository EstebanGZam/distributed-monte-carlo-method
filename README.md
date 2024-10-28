# Estimación de π con Sistema Distribuido

Este proyecto implementa un sistema distribuido para estimar el valor de π mediante el método de Monte Carlo. La estimación se logra lanzando puntos aleatorios y calculando la proporción de aquellos que caen dentro de un círculo inscrito en un cuadrado. Para esto, se distribuyen las tareas entre varios nodos en un modelo de cliente-maestro-trabajadores (master-workers), utilizando comunicación asíncrona a través de ICE (Internet Communications Engine).

## Objetivo

El objetivo de este proyecto es implementar un sistema distribuido donde:
1. El **cliente** envía una solicitud de estimación de π al maestro.
2. El **maestro** coordina el cálculo dividiendo la tarea entre varios **trabajadores**.
3. Los **trabajadores** calculan cuántos puntos aleatorios caen dentro de un círculo y devuelven el resultado al maestro.

## Método de Monte Carlo para Estimación de π

1. Se lanzan puntos aleatorios dentro de un cuadrado de lado 2 centrado en el origen.
2. Se cuentan cuántos puntos caen dentro del círculo inscrito en el cuadrado.
3. La proporción de puntos dentro del círculo respecto al total es igual a π/4, permitiendo estimar el valor de π.

La fórmula para calcular π es:

$$ π \approx 4 \times \frac{\text{número de puntos en el círculo}}{\text{total de puntos lanzados}} $$

## Ejecución

1. **Ejecución del Sistema**:
   - **Maestro**: Ejecuta el maestro para que escuche en el puerto configurado:
      ```bash
         java -jar \MasterPath.jar
      ```
   - **Trabajadores**: Ejecuta múltiples instancias del archivo `.jar` de los trabajadores:
     ```bash
     java -jar \WorkerPath.jar <worker_id>
     ```
   - **Cliente**: Ejecuta el cliente y solicita la estimación de π ingresando el número de puntos a calcular:
     ```bash
     java -jar \ClientPath.jar
     ```
