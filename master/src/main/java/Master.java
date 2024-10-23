import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;

public class Master {
    public static void main(String[] args) {
        try (Communicator communicator = com.zeroc.Ice.Util.initialize(args)) {
            // Crear un adaptador en el puerto 10000
            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("MasterAdapter",
                    "default -h localhost -p 10000");

            // Crear instancia del maestro
            TaskCoordinatorI taskCoordinator = new TaskCoordinatorI(communicator);

            // Registrar el objeto de maestro en el adaptador
            adapter.add(taskCoordinator, com.zeroc.Ice.Util.stringToIdentity("master"));

            // Activar el adaptador
            adapter.activate();

            System.out.println("El servidor maestro está listo...");
            communicator.waitForShutdown();
        }
    }
}
