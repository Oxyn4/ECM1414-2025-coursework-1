/**
 * Main test runner class
 * Runs all the tests for the elevator system
 */
public class TestRunner {

    // track pass/fail counts
    static int totalTests = 0;
    static int passedTests = 0;
    
    public static void main(String[] args) {
        System.out.println("=== Starting Elevator System Tests ===\n");
        
        // record start time
        long startTime = System.currentTimeMillis();
        
        // run building tests
        runTest("Building Tests", () -> {
            BuildingTest.main(null);
        });
        
        // run queue tests
        runTest("Queue Tests", () -> {
            QueueTest.main(null);
        });
        
        // run algorithm tests
        runTest("Algorithm Tests", () -> {
            AlgorithmTest.main(null);
        });
        
        // Print overall test results
        long endTime = System.currentTimeMillis();
        double totalTime = (endTime - startTime) / 1000.0;
        
        System.out.println("\n=== Test Results ===");
        System.out.println("Tests run: " + totalTests);
        System.out.println("Tests passed: " + passedTests);
        System.out.println("Tests failed: " + (totalTests - passedTests));
        System.out.println("Time taken: " + totalTime + " seconds");
        
        if (passedTests == totalTests) {
            System.out.println("\nALL TESTS PASSED!");
        } else {
            System.out.println("\nSOME TESTS FAILED!");
            // Non-zero exit code indicates test failure
            System.exit(1);
        }
    }
    
    // Helper to run a test and track results
    static void runTest(String testName, Runnable test) {
        System.out.println("Running " + testName + "...");
        totalTests++;
        
        try {
            test.run();
            passedTests++;
            System.out.println(testName + " PASSED\n");
        } catch (Exception e) {
            System.out.println(testName + " FAILED: " + e.getMessage());
            e.printStackTrace();
            System.out.println();
        }
    }
}
