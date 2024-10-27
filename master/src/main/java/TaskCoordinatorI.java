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
	public double calculatePiEstimation(long numPoints, int numWorkers, Current current) {
		System.out.println("Estimando el valor de π con " + numPoints + " puntos y " + numWorkers + " trabajadores...");
		long pointsPerWorker = numPoints / numWorkers + 1;

		List<CompletableFuture<Long>> futures = new ArrayList<>();

		for (int i = 0; i < numWorkers; i++) {
			WorkerServicePrx worker = WorkerServicePrx.checkedCast(
					communicator.stringToProxy("worker" + i + ":default -h localhost -p " + (10001 + i)));

			if (worker != null) {
				System.out.println("Solicitando al trabajador " + i + " calcular " + pointsPerWorker + " puntos...");
				CompletableFuture<Long> future = worker.countPointsInsideCircleAsync(pointsPerWorker);
				futures.add(future);
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
			return 0.0;
		}

		return (4.0 * totalPointsInCircle) / numPoints;
	}
}