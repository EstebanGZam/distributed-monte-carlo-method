module MonteCarloPi {
    // Interface for the workers, who will perform the calculation
    interface WorkerService {
        // Method to receive the number of points to generate and return the points within the circle
        ["async"] int calculatePoints(int numPoints);
    };

    // Interface for the master, who will coordinate the task among the workers
    interface TaskCoordinator {
        // Method to distribute tasks among the workers and calculate pi
        ["async"] float estimatePi(int numPoints, int numWorkers);
    };

};
