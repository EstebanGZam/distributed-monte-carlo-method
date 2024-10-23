import MonteCarloPi.WorkerService;
import com.zeroc.Ice.Current;

import java.util.Random;

public class WorkerServiceI implements WorkerService {
	private final Random random = new Random();

	@Override
	public int calculatePoints(int numPoints, Current current) {
		int pointsInsideCircle = 0;

		for (int i = 0; i < numPoints; i++) {
			// Generar puntos aleatorios entre -1 y 1
			double x = random.nextDouble() * 2 - 1;
			double y = random.nextDouble() * 2 - 1;

			// Verificar si el punto está dentro del círculo
			if (x * x + y * y <= 1) {
				pointsInsideCircle++;
			}
		}

		return pointsInsideCircle;
	}
}
