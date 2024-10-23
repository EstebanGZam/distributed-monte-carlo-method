import MonteCarloPi.TaskCoordinator;
import MonteCarloPi.WorkerServicePrx;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Current;

public class TaskCoordinatorI implements TaskCoordinator {
	private Communicator communicator;

	public TaskCoordinatorI(Communicator communicator) {
		this.communicator = communicator;
	}

	@Override
	public float estimatePi(int numPoints, int numWorkers, Current current) {
		System.out.println("Estimando el valor de π con " + numPoints + " puntos y " + numWorkers + " trabajadores...");
		int totalPointsInCircle = 0;
		int pointsPerWorker = numPoints / numWorkers;

		// Distribuir el trabajo entre los trabajadores
		for (int i = 0; i < numWorkers; i++) {
			WorkerServicePrx worker = WorkerServicePrx.checkedCast(
					communicator.stringToProxy("worker" + i + ":default -h localhost -p " + (10001 + i)));
			System.out.println(worker);

			if (worker != null) {
				System.out.println("Solicitando al trabajador " + i + " calcular " + pointsPerWorker + " puntos...");
				totalPointsInCircle += worker.calculatePoints(pointsPerWorker);
			}
		}

		// Estimar el valor de Pi
		return 4.0f * totalPointsInCircle / numPoints;
	}
}
