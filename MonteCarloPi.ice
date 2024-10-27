module MonteCarloPi {

    // Interface for workers who will perform the point calculations
    interface WorkerService {
        // Method to receive the number of points to generate and return the points inside the circle
        ["async"] long countPointsInsideCircle(long totalPoints);
    };

    // Interface for the task coordinator who will coordinate tasks among workers
    interface TaskCoordinator {
        // Method to distribute tasks among workers and estimate the value of π
        ["async"] double calculatePiEstimation(long totalPoints, int numWorkers);
    };

};
