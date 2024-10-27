import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;

public class Master {
    public static void main(String[] args) {
        try (Communicator communicator = com.zeroc.Ice.Util.initialize(args, "master.cfg")) {
            // Crear un adaptador en el puerto 10000
            ObjectAdapter adapter = communicator.createObjectAdapter("MasterAdapter");

            // Crear instancia del maestro
            TaskCoordinatorI taskCoordinator = new TaskCoordinatorI(communicator);

            // Registrar el objeto de maestro en el adaptador
            adapter.add(taskCoordinator, com.zeroc.Ice.Util.stringToIdentity("master"));

            // Activar el adaptador
            adapter.activate();

            System.out.println("El maestro está listo...");
            communicator.waitForShutdown();
        }
    }
}
