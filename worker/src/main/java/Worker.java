import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;

public class Worker {
    public static void main(String[] args) {
        try (Communicator communicator = com.zeroc.Ice.Util.initialize(args)) {
            // Crear un adaptador en el puerto 10001, 10002, etc.
            int workerId = Integer.parseInt(args[0]);
            // Verificar que el workerId sea mayor a 0
            if (workerId <= 0) {
                throw new IllegalArgumentException("El workerId debe ser mayor a 0");
            }
            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("WorkerAdapter",
                    "default -h localhost -p " + (10000 + workerId));

            // Crear instancia del trabajador
            WorkerServiceI worker = new WorkerServiceI();

            // Registrar el objeto de trabajador en el adaptador
            adapter.add(worker, com.zeroc.Ice.Util.stringToIdentity("worker" + workerId));

            // Activar el adaptador
            adapter.activate();

            System.out.println("El trabajador " + workerId + " está listo...");
            communicator.waitForShutdown();
        }
    }
}
