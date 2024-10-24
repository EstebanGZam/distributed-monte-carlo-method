import MonteCarloPi.TaskCoordinator;
import MonteCarloPi.WorkerServicePrx;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Current;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class TaskCoordinatorI implements TaskCoordinator {
	private Communicator communicator;

	public TaskCoordinatorI(Communicator communicator) {
		this.communicator = communicator;
	}

	@Override
	public float estimatePi(int numPoints, int numWorkers, Current current) {
		System.out.println("Estimando el valor de π con " + numPoints + " puntos y " + numWorkers + " trabajadores...");
		int pointsPerWorker = numPoints / numWorkers;

		List<CompletableFuture<Integer>> futures = new ArrayList<>();

		for (int i = 0; i < numWorkers; i++) {
			WorkerServicePrx worker = WorkerServicePrx.checkedCast(
					communicator.stringToProxy("worker" + i + ":default -h localhost -p " + (10001 + i)));

			if (worker != null) {
				System.out.println("Solicitando al trabajador " + i + " calcular " + pointsPerWorker + " puntos...");
				CompletableFuture<Integer> future = worker.calculatePointsAsync(pointsPerWorker);
				futures.add(future);
			}
		}

		int totalPointsInCircle = 0;
		try {
			CompletableFuture<Void> allOf = CompletableFuture.allOf(
					futures.toArray(new CompletableFuture[0])
			);

			allOf.get();

			for (CompletableFuture<Integer> future : futures) {
				totalPointsInCircle += future.get();
			}
		} catch (InterruptedException | ExecutionException e) {
			System.err.println("Error al obtener resultados: " + e.getMessage());
			return 0.0f;
		}

		return 4.0f * totalPointsInCircle / numPoints;
	}
}