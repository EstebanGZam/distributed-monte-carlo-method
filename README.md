# Estimación de π con Sistema Distribuido

## Descripción del Proyecto

Este proyecto implementa un sistema distribuido para estimar el valor de π mediante el método de Monte Carlo. La estimación se logra lanzando puntos aleatorios y calculando la proporción de aquellos que caen dentro de un círculo inscrito en un cuadrado. Para esto, se distribuyen las tareas entre varios nodos en un modelo de cliente-maestro-trabajadores (master-workers), utilizando comunicación asíncrona a través de ICE (Internet Communications Engine).

## Equipo

- Esteban Gaviria Zambrano - A00396019
- Jose Manuel Cardona - A00399980
- Juan Manuel Díaz - A00394477
- Juan Camilo Muñoz Barco - A00399199

## Objetivo

Implementar un sistema distribuido donde:

1. El **cliente** envía una solicitud de estimación de π al maestro.
2. El **maestro** coordina el cálculo dividiendo la tarea entre varios **trabajadores**.
3. Los **trabajadores** calculan cuántos puntos aleatorios caen dentro de un círculo y devuelven el resultado al maestro.

## Método de Monte Carlo para Estimación de π

1. Se lanzan puntos aleatorios dentro de un cuadrado de lado 2 centrado en el origen.
2. Se cuentan cuántos puntos caen dentro del círculo inscrito en el cuadrado.
3. La proporción de puntos dentro del círculo respecto al total es igual a π/4, permitiendo estimar el valor de π.

La fórmula para calcular π es:

$$ π \approx 4 \times \frac{\text{número de puntos en el círculo}}{\text{total de puntos lanzados}} $$

## Instrucciones para la Ejecución

### **Configuración Inicial**

1. **Configuración del entorno**:

   - Asegúrate de tener **Java** y **Gradle** instalados en tu sistema.
   - Clona el repositorio del proyecto.

---

⚠️ **Importante (Ajustar antes de compilar el proyecto)**:

El número de trabajadores que participarán en la estimación de π se modifica desde el archivo de configuración del cliente. En este archivo, la línea:

```properties
num.workers=4
```

define cuántos trabajadores utilizará el maestro para distribuir la carga de trabajo. Si deseas aumentar el número de trabajadores, simplemente cambia este valor.

Sin embargo, si deseas utilizar más de 4 trabajadores, es necesario también añadir las configuraciones correspondientes en el archivo de configuración del maestro. Actualmente, el máximo número de trabajadores está limitado a 4, como se indica en la configuración del maestro:

```properties
Worker1.Proxy=worker1:default -h localhost -p 10001
Worker2.Proxy=worker2:default -h localhost -p 10002
Worker3.Proxy=worker3:default -h localhost -p 10003
Worker4.Proxy=worker4:default -h localhost -p 10004
```

Cada nuevo trabajador adicional deberá tener su propio proxy definido en este archivo, con una dirección IP y puerto únicos.

---

2. **Compilar el proyecto**:

   - Ejecuta una vez `./gradlew build` para compilar las clases Java y generar los stubs y proxies a partir del archivo `MonteCarloPi.ice`.
   - Ejecuta una vez `.\gradlew :client:build` para compilar el cliente
   - Ejecuta una vez `.\gradlew :master:build` para compilar el servidor maestro
   - Ejecuta una vez `.\gradlew :worker:build` para compilar los trabajadores

3. **Iniciar el servidor maestro**:

   - Navega a la carpeta del maestro y ejecuta en una consola separada:
     ```
     java -jar master/build/libs/master.jar
     ```

4. **Iniciar los trabajadores**:

   - Para cada trabajador, ejecuta en una consola separada:
     ```
     java -jar worker/build/libs/worker.jar <workerId>
     ```
     Donde `<workerId>` es el identificador del trabajador (1, 2, ...).

5. **Iniciar el cliente**:
   - Ejecuta en una consola separada:
     ```
     java -jar client/build/libs/client.jar
     ```

### **Probar el sistema**:

Una vez que el **servidor maestro** se ha iniciado, en la consola del maestro se verá un mensaje que indica que el servidor está listo y esperando las solicitudes de los clientes:

```
El servidor maestro está listo...
```

El maestro ahora se encuentra en espera de conexiones de los trabajadores y de las solicitudes de los clientes para coordinar las tareas de cálculo de $\pi$.

Paralelamente, se inician los **trabajadores**. Para cada trabajador que se lanza, la consola muestra un mensaje indicando que está escuchando en un puerto específico, por ejemplo:

```
El trabajador 0 está listo...
El trabajador 1 está listo...
```

Este mensaje se repetirá para cada trabajador adicional que se inicie. Cada trabajador queda en espera de recibir tareas de cálculo desde el maestro.

Una vez que el maestro y los trabajadores están en ejecución, se procede a ejecutar el **cliente**. En la consola del cliente, se mostrará un mensaje inicial solicitando al usuario que ingrese el número de puntos que desea utilizar para estimar $\pi$. La consola del cliente se ve así:

```
Cliente iniciado...
Escriba 'exit' para terminar o ingrese el número de puntos a calcular.

Ingrese el número de puntos a calcular:
```

El usuario entonces ingresa el número de puntos, por ejemplo, `20. En la consola del cliente, aparecerá un mensaje indicando que se ha enviado la solicitud de estimación al maestro:

```
Ingrese el número de puntos a calcular: 20
Solicitando estimación de π con 20 puntos y 2 trabajadores...
```

En este momento, el cliente envía la solicitud de estimación de $\pi$ al maestro, junto con la cantidad de puntos y el número de trabajadores que se utilizarán para la tarea. El maestro, al recibir esta solicitud, muestra en su propia consola un mensaje que indica el inicio del proceso de estimación y la distribución de la carga de trabajo entre los trabajadores:

```
Estimando el valor de π con 20 puntos y 2 trabajadores...
Solicitando al trabajador 0 calcular 10 puntos...
Solicitando al trabajador 1 calcular 10 puntos...
```

Este mensaje refleja que el maestro ha dividido el total de puntos (20) entre los dos trabajadores, asignando 10 puntos a cada uno. Cada trabajador, a su vez, comienza a procesar los puntos de manera independiente. Mientras los trabajadores realizan el cálculo, el maestro espera a que estos finalicen sus tareas. Dado que el proceso es asíncrono, el maestro puede seguir realizando otras actividades mientras se espera la respuesta. Una vez que un trabajador termina su tarea, muestra en su consola el número de puntos que cayeron dentro del círculo y envía el resultado al maestro. Al recibir las respuestas de los trabajadores, el maestro suma los resultados y calcula la estimación de $\pi$ usando la proporción de puntos dentro del círculo. La consola del maestro muestra que ha completado la estimación:

```
Resultados recibidos de todos los trabajadores.
Estimación de π: 3.940782
```

La estimación de $\pi$ se envía de vuelta al cliente, donde se imprime el resultado, junto con una comparación con el valor real de $\pi$, el error absoluto y el tiempo de procesamiento:

```
------------------------------------------------
Resultado con 1000000 puntos y 4 trabajadores:
Estimación de pi: 3.14528
Valor real de pi: 3.141592653589793
Error absoluto: 0.0036873464102069597
Tiempo de procesado: 21 ms
------------------------------------------------
```

El usuario puede observar directamente en la consola del cliente la diferencia entre la estimación obtenida y el valor real de $\pi$, así como la magnitud del error y el tiempo de procesado. Esto permite evaluar no solo la precisión del método de Monte Carlo para el número de puntos ingresados, sino también la eficiencia temporal del cálculo realizado.

En este punto, el cliente puede continuar ingresando diferentes números de puntos para realizar más estimaciones, o escribir `exit` para cerrar la conexión:

```
Ingrese el número de puntos a calcular: exit
Finalizando cliente...
```

Este comando termina la ejecución del cliente, mientras que los trabajadores y el maestro continúan en espera de nuevas conexiones y solicitudes. Para detener completamente el sistema, se debe cerrar manualmente el maestro y los trabajadores, lo que puede hacerse usando un comando de interrupción como `Ctrl+C` en cada terminal correspondiente.

## Informe de Resultados

Para acceder al informe detallado, que incluye las pruebas del proyecto desplegado, consulta el siguiente enlace:

[**Ver informe completo**](doc/Informe.ipynb)
