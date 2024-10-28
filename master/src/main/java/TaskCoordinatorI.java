import MonteCarloPi.PiEstimationResult;
import MonteCarloPi.TaskCoordinator;
import MonteCarloPi.WorkerServicePrx;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Current;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class TaskCoordinatorI implements TaskCoordinator {
	private final Communicator communicator;

	public TaskCoordinatorI(Communicator communicator) {
		this.communicator = communicator;
	}

	@Override
	public PiEstimationResult calculatePiEstimation(long numPoints, int numWorkers, Current current) {
		System.out.println("------------------------------------------------");
		System.out
				.println("Estimando el valor de pi con " + numPoints + " puntos y " + numWorkers + " trabajadores...");
		long pointsPerWorker = numPoints / numWorkers;

		List<CompletableFuture<Long>> futures = new ArrayList<>();

		long startTime = System.currentTimeMillis(); // Inicio del tiempo de procesamiento

		for (int i = 1; i <= numWorkers; i++) {
			// Obtiene el proxy desde la configuración
			String proxyProperty = "Worker" + i + ".Proxy";
			String proxyString = communicator.getProperties().getProperty(proxyProperty);

			if (proxyString != null && !proxyString.isEmpty()) {
				try {
					WorkerServicePrx worker = WorkerServicePrx.checkedCast(
							communicator.stringToProxy(proxyString));

					if (worker != null) {
						System.out.println(
								"Solicitando al trabajador " + i + " calcular " + pointsPerWorker + " puntos...");
						CompletableFuture<Long> future = worker.countPointsInsideCircleAsync(pointsPerWorker);
						futures.add(future);
					} else {
						System.err.println("No se pudo conectar al worker " + i);
					}
				} catch (Exception e) {
					System.err.println("Error al crear proxy para el worker " + i + ": " + e.getMessage());
				}
			} else {
				System.err.println("No se encontró configuración para worker " + i);
			}
		}

		long totalPointsInCircle = 0;

		try {
			CompletableFuture<Void> allOf = CompletableFuture.allOf(
					futures.toArray(new CompletableFuture[0]));

			allOf.get();

			for (CompletableFuture<Long> future : futures) {
				totalPointsInCircle += future.get();
			}
		} catch (InterruptedException | ExecutionException e) {
			System.err.println("Error al obtener resultados: " + e.getMessage());
			return new PiEstimationResult(0, 0.0); // Retorna 0 en caso de error
		}

		long endTime = System.currentTimeMillis(); // Fin del tiempo de procesamiento
		long processingTime = endTime - startTime; // Tiempo de procesamiento en milisegundos

		return new PiEstimationResult(processingTime, (4.0 * totalPointsInCircle) / numPoints);
	}
}