import java.io.IOException;

/**
 * ScenarioTestRunner:
 * Runs SCAN, LOOK, and MYLIFT on multiple .txt scenario files.
 * Shows pass/fail style results similar to the existing TestRunner pattern.
 */
public class ScenarioTestRunner {

    // Track how many scenario tests were run overall
    private static int totalTests = 0;
    private static int passedTests = 0;

    public static void main(String[] args) {
        System.out.println("=== Starting Scenario Tests ===\n");

        long startTime = System.currentTimeMillis();

        // List out the files you want to test
        String[] scenarioFiles = {
            "small_scenario.txt",
            "medium_scenario.txt",
            "large_scenario.txt"
        };

        // Run each scenario
        for (String fileName : scenarioFiles) {
            runScenarioTest(fileName);
        }

        // All scenarios done, print summary
        long endTime = System.currentTimeMillis();
        double totalTime = (endTime - startTime) / 1000.0;

        System.out.println("\n=== Scenario Test Results ===");
        System.out.println("Tests run: " + totalTests);
        System.out.println("Tests passed: " + passedTests);
        System.out.println("Tests failed: " + (totalTests - passedTests));
        System.out.println("Time taken: " + totalTime + " seconds");

        if (passedTests == totalTests) {
            System.out.println("\nALL SCENARIO TESTS PASSED!");
        } else {
            System.out.println("\nSOME SCENARIO TESTS FAILED!");
            // Non-zero exit code indicates test failure in many CI systems
            System.exit(1);
        }
    }

    /**
     * Runs SCAN, LOOK, and MYLIFT on one file, tracking pass/fail for each.
     */
    private static void runScenarioTest(String fileName) {
        System.out.println("=== Running tests for file: " + fileName + " ===");

        // SCAN
        runTest("SCAN on " + fileName, () -> {
            Building building = Building.FromFile(fileName);
            runAlgorithm("SCAN", new Scan(building));
        });

        // LOOK
        runTest("LOOK on " + fileName, () -> {
            Building building = Building.FromFile(fileName);
            runAlgorithm("LOOK", new Look(building));
        });

        // MYLIFT
        runTest("MYLIFT on " + fileName, () -> {
            Building building = Building.FromFile(fileName);
            runAlgorithm("MYLIFT", new MyLift(building));
        });
    }

    /**
     * Helper to run a single test scenario (algorithm) with pass/fail tracking.
     */
    private static void runTest(String testName, ThrowingRunnable test) {
        System.out.println("Running " + testName + "...");
        totalTests++;

        try {
            test.run();  // run the scenario
            passedTests++;
            System.out.println(testName + " PASSED\n");
        } catch (Exception e) {
            System.out.println(testName + " FAILED: " + e.getMessage());
            e.printStackTrace();
            System.out.println();
        }
    }

    /**
     * Simulate an algorithm by repeatedly calling NextStep().
     */
    private static void runAlgorithm(String algoName, Algorithm algo) throws InvalidBuildingConfiguration {
        int steps = 0;
        int moves = 0;
        boolean isDone = false;
        int maxSteps = 1000; // safeguard against infinite loops

        // Step until all requests are cleared or we hit maxSteps
        while (!isDone && steps < maxSteps) {
            int floorBefore = algo.building.getLift().getCurrentFloor();
            algo.NextStep();  // modifies building state
            int floorAfter = algo.building.getLift().getCurrentFloor();

            steps++;
            if (floorBefore != floorAfter) {
                moves++;
            }

            // Check if requests are done
            isDone = allRequestsProcessed(algo.building);
        }

        if (!isDone) {
            // If we never finished, throw an exception to mark test as failed
            throw new RuntimeException(
                algoName + " did not finish within " + maxSteps + " steps"
            );
        } else {
            System.out.println(algoName + " completed in " + steps
                + " steps (" + moves + " moves).");
        }
    }

    /**
     * Return true if all floors have no more pending requests.
     */
    private static boolean allRequestsProcessed(Building building) {
        for (FloorState floor : building.getFloors().GetFloors()) {
            if (!floor.GetFloorRequests().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * A functional interface to allow lambdas that throw exceptions.
     */
    @FunctionalInterface
    interface ThrowingRunnable {
        void run() throws Exception;
    }
}
