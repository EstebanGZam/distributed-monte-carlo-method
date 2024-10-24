import MonteCarloPi.TaskCoordinatorPrx;
import com.zeroc.Ice.Communicator;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class Client {
	private static final int NUM_WORKERS = 2;

	public static void main(String[] args) {
		try (Communicator communicator = com.zeroc.Ice.Util.initialize(args);
			 Scanner scanner = new Scanner(System.in)) {

			System.out.println("Cliente iniciado...");
			System.out.println("Escriba 'exit' para terminar o ingrese el número de puntos a calcular.");

			while (true) {
				System.out.print("\nIngrese el número de puntos a calcular: ");
				String input = scanner.nextLine().trim();

				if (input.equalsIgnoreCase("exit")) {
					System.out.println("Finalizando cliente...");
					break;
				}

				try {
					int numPoints = Integer.parseInt(input);
					if (numPoints <= 0) {
						System.out.println("Por favor, ingrese un número positivo de puntos.");
						continue;
					}
					// Esperar a que termine la estimación antes de pedir el siguiente número
					requestPiEstimation(communicator, numPoints, NUM_WORKERS).get();
				} catch (NumberFormatException e) {
					System.out.println("Por favor, ingrese un número válido o 'exit' para terminar.");
				} catch (Exception e) {
					System.err.println("Error en la ejecución: " + e.getMessage());
				}
			}
		}
	}

	public static CompletableFuture<Void> requestPiEstimation(Communicator communicator, int numPoints, int numWorkers) {
		TaskCoordinatorPrx master = TaskCoordinatorPrx.checkedCast(
				communicator.stringToProxy("master:default -h localhost -p 10000"));

		if (master != null) {
			System.out.println("Solicitando estimación de π con " + numPoints + " puntos y " + numWorkers + " trabajadores...");

			return master.estimatePiAsync(numPoints, numWorkers)
					.thenAccept(result -> {
						System.out.println("\nResultados:");
						System.out.println("------------");
						System.out.println("Estimación de π: " + result);
						System.out.println("Valor real de π: " + Math.PI);
						System.out.println("Error absoluto: " + Math.abs(result - Math.PI));
						System.out.println("------------");
					});
		}
		return CompletableFuture.completedFuture(null);
	}
}