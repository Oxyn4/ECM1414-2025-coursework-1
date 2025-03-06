import java.io.IOException;

/**
 * Test for the elevator algorithms
 * Compares the performance of SCAN, LOOK and MYLIFT algorithms
 */
public class AlgorithmTest {
    
    // Record statistics for each algorithm
    private static int scanSteps = 0;
    private static int lookSteps = 0;
    private static int myLiftSteps = 0;
    
    private static int scanMoves = 0;
    private static int lookMoves = 0;
    private static int myLiftMoves = 0;
    
    public static void main(String[] args) {
        System.out.println("Starting elevator algorithm tests...");
        
        // Run comparative test
        testAlgorithmPerformance();
        
        // Print results
        System.out.println("\n===== Algorithm Performance Results =====");
        System.out.println("Total steps taken:");
        System.out.println("  SCAN:   " + scanSteps);
        System.out.println("  LOOK:   " + lookSteps);
        System.out.println("  MYLIFT: " + myLiftSteps);
        
        System.out.println("\nTotal elevator movements:");
        System.out.println("  SCAN:   " + scanMoves);
        System.out.println("  LOOK:   " + lookMoves);
        System.out.println("  MYLIFT: " + myLiftMoves);
        
        // Determine winner
        String stepWinner = (scanSteps <= lookSteps && scanSteps <= myLiftSteps) ? "SCAN" :
                           (lookSteps <= scanSteps && lookSteps <= myLiftSteps) ? "LOOK" : "MYLIFT";
        
        String moveWinner = (scanMoves <= lookMoves && scanMoves <= myLiftMoves) ? "SCAN" :
                           (lookMoves <= scanMoves && lookMoves <= myLiftMoves) ? "LOOK" : "MYLIFT";
        
        System.out.println("\nMost efficient algorithm (by steps): " + stepWinner);
        System.out.println("Most efficient algorithm (by moves): " + moveWinner);
        
        System.out.println("\nAll algorithm tests completed!");
    }
    
    /**
     * Test and compare the performance of all three algorithms
     */
    private static void testAlgorithmPerformance() {
        try {
            System.out.println("Running comparative performance test...");
            
            // Create three identical buildings for fair comparison
            Building buildingForScan = loadTestBuilding();
            Building buildingForLook = loadTestBuilding();
            Building buildingForMylift = loadTestBuilding();
            
            // Set the same starting position for all
            int startFloor = 0;  // start at ground floor
            
            buildingForScan.getLift().setCurrentFloor(startFloor);
            buildingForScan.getLift().setGoingUp(true);
            
            buildingForLook.getLift().setCurrentFloor(startFloor);
            buildingForLook.getLift().setGoingUp(true);
            
            buildingForMylift.getLift().setCurrentFloor(startFloor);
            buildingForMylift.getLift().setGoingUp(true);
            
            // Create the algorithms
            Scan scan = new Scan(buildingForScan);
            Look look = new Look(buildingForLook);
            MyLift myLift = new MyLift(buildingForMylift);
            
            // Run each algorithm until all requests are processed
            boolean scanDone = false;
            boolean lookDone = false;
            boolean myLiftDone = false;
            
            // Set maximum steps to prevent infinite loops
            int maxSteps = 100;
            
            for (int step = 1; step <= maxSteps; step++) {
                // Run the SCAN algorithm if not done
                if (!scanDone) {
                    int floorBefore = buildingForScan.getLift().getCurrentFloor();
                    buildingForScan = scan.NextStep();
                    int floorAfter = buildingForScan.getLift().getCurrentFloor();
                    
                    // Count steps and movements
                    scanSteps++;
                    if (floorBefore != floorAfter) {
                        scanMoves++;
                    }
                    
                    // Check if all requests are processed
                    scanDone = areAllRequestsProcessed(buildingForScan);
                    if (scanDone) {
                        System.out.println("SCAN completed in " + scanSteps + " steps");
                    }
                }
                
                // Run the LOOK algorithm if not done
                if (!lookDone) {
                    int floorBefore = buildingForLook.getLift().getCurrentFloor();
                    buildingForLook = look.NextStep();
                    int floorAfter = buildingForLook.getLift().getCurrentFloor();
                    
                    // Count steps and movements
                    lookSteps++;
                    if (floorBefore != floorAfter) {
                        lookMoves++;
                    }
                    
                    // Check if all requests are processed
                    lookDone = areAllRequestsProcessed(buildingForLook);
                    if (lookDone) {
                        System.out.println("LOOK completed in " + lookSteps + " steps");
                    }
                }
                
                // Run the MYLIFT algorithm if not done
                if (!myLiftDone) {
                    int floorBefore = buildingForMylift.getLift().getCurrentFloor();
                    buildingForMylift = myLift.NextStep();
                    int floorAfter = buildingForMylift.getLift().getCurrentFloor();
                    
                    // Count steps and movements
                    myLiftSteps++;
                    if (floorBefore != floorAfter) {
                        myLiftMoves++;
                    }
                    
                    // Check if all requests are processed
                    myLiftDone = areAllRequestsProcessed(buildingForMylift);
                    if (myLiftDone) {
                        System.out.println("MYLIFT completed in " + myLiftSteps + " steps");
                    }
                }
                
                // Break if all algorithms are done
                if (scanDone && lookDone && myLiftDone) {
                    break;
                }
                
                // Bail out if we reach max steps
                if (step == maxSteps) {
                    System.out.println("WARNING: Reached maximum steps (" + maxSteps + ")");
                    if (!scanDone) System.out.println("SCAN did not complete");
                    if (!lookDone) System.out.println("LOOK did not complete");
                    if (!myLiftDone) System.out.println("MYLIFT did not complete");
                    break;
                }
            }
            
        } catch (Exception e) {
            System.out.println("ERROR in algorithm test: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Check if all requests have been processed in a building
     */
    private static boolean areAllRequestsProcessed(Building building) {
        for (FloorState floor : building.getFloors().GetFloors()) {
            if (!floor.GetFloorRequests().isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Load a test building from file
     */
    private static Building loadTestBuilding() throws IOException {
        try {
            return Building.FromFile("test_input.txt");
        } catch (IOException e) {
            System.out.println("Warning: test_input.txt not found, using input.txt instead");
            return Building.FromFile("input.txt");
        }
    }
}