import MonteCarloPi.TaskCoordinatorPrx;
import com.zeroc.Ice.Communicator;

public class Client {

	public static void main(String[] args) {
		try (Communicator communicator = com.zeroc.Ice.Util.initialize(args)) {
			System.out.println("Cliente iniciado...");
			// Solicitar la estimación de Pi (ejemplo con 100000 puntos y 4 trabajadores)
			requestPiEstimation(communicator, 1000, 2);
		}

	}

	public static void requestPiEstimation(Communicator communicator, int numPoints, int numWorkers) {
		TaskCoordinatorPrx master = TaskCoordinatorPrx.checkedCast(
				communicator.stringToProxy("master:default -h localhost -p 10000"));
		System.out.println("Solicitando estimación de π con " + numPoints + " puntos y " + numWorkers + " trabajadores...");
		System.out.println(master);
		if (master != null) {
			System.out.println("Hola?");
			float piEstimation = master.estimatePi(numPoints, numWorkers);
			System.out.println("Estimación de π: " + piEstimation);
		}
	}
}
