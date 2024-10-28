import MonteCarloPi.TaskCoordinatorPrx;
import com.zeroc.Ice.Communicator;
import java.util.concurrent.CompletableFuture;

import java.util.Scanner;

public class Client {

	public static void main(String[] args) {
		try (Communicator communicator = com.zeroc.Ice.Util.initialize(args, "client.cfg");
				Scanner scanner = new Scanner(System.in)) {
			int numWorkers = Integer.parseInt(communicator.getProperties().getProperty("num.workers"));

			System.out.println("Cliente iniciado...");
			System.out.println("Escriba 'exit' para terminar o ingrese el número de puntos a calcular.");

			boolean exit = false;

			while (!exit) {
				System.out.print("\nIngrese el número de puntos a calcular: ");
				String input = scanner.nextLine().trim();

				exit = input.equalsIgnoreCase("exit");
				if (exit) {
					System.out.println("Finalizando cliente...");
					communicator.shutdown();
					break;
				}

				try {
					int numPoints = Integer.parseInt(input);
					if (numPoints <= 0) {
						System.out.println("Por favor, ingrese un número positivo de puntos.");
						continue;
					}
					requestPiEstimation(communicator, numPoints, numWorkers);
				} catch (NumberFormatException e) {
					System.out.println("Por favor, ingrese un número válido o 'exit' para terminar.");
				} catch (Exception e) {
					System.err.println("Error en la ejecución: " + e.getMessage());
				}
			}
		}
	}

	public static CompletableFuture<Void> requestPiEstimation(Communicator communicator, int numPoints,
			int numWorkers) {
		// Obtener el proxy del archivo de configuración

		TaskCoordinatorPrx master = TaskCoordinatorPrx.checkedCast(
				communicator.propertyToProxy("master.proxy"));

		if (master != null) {
			System.out.println(
					"\nSolicitando estimación de pi con " + numPoints + " puntos y " + numWorkers + " trabajadores...");

			return master.calculatePiEstimationAsync(numPoints, numWorkers)
					.thenAccept(result -> {
						System.out.println("\n------------------------------------------------");
						System.out.println(
								"\nResultado con " + numPoints + " puntos y " + numWorkers + " trabajadores:");
						System.out.println("Estimación de pi: " + result.estimatedPiValue);
						System.out.println("Valor real de pi: " + Math.PI);
						System.out.println("Error absoluto: " + Math.abs(result.estimatedPiValue - Math.PI));
						System.out.println("Tiempo de procesado: " + result.processingTime + " ms");
						System.out.println("------------------------------------------------");

						System.out.print("\nIngrese el número de puntos a calcular: ");
					});
		} else {
			System.err.println("Error al obtener el proxy del maestro.");
		}
		return CompletableFuture.completedFuture(null);
	}
}
