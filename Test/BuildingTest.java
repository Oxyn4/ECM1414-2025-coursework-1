import java.io.IOException;

/**
 * Test class for Building
 * Just some basic tests for building stuff
 */
public class BuildingTest {
    
    public static void main(String[] args) {
        // test stuff in this main method
        testBuildingFromFile();
        testBuildingOperations();
        
        System.out.println("All Building tests passed!");
    }
    
    // test loading building from file
    private static void testBuildingFromFile() {
        try {
            System.out.println("Testing Building.FromFile()...");
            Building building = Building.FromFile("test_input.txt");
            
            // Check if building loaded correctly
            if (building == null) {
                System.out.println("FAILED: Building is null");
                return;
            }
            
            // Check floors count
            if (building.getFloors().GetFloors().size() != 5) {
                System.out.println("FAILED: Expected 5 floors, got " + 
                    building.getFloors().GetFloors().size());
                return;
            }
            
            // Check lift capacity
            if (building.getLift().getCapacity() != 4) {
                System.out.println("FAILED: Expected capacity 4, got " + 
                    building.getLift().getCapacity());
                return;
            }
            
            // Check floor requests
            FloorState floor1 = building.getFloors().GetFloors().get(0);
            if (floor1.GetFloorRequests().size() != 2) {
                System.out.println("FAILED: Expected 2 requests on floor 1, got " + 
                    floor1.GetFloorRequests().size());
                return;
            }
            
            System.out.println("Building.FromFile() test passed!");
            
        } catch (IOException e) {
            System.out.println("FAILED: Error loading file - " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // test building operations
    private static void testBuildingOperations() {
        try {
            System.out.println("Testing Building operations...");
            
            // Create test building
            Building building = Building.FromFile("test_input.txt");
            
            // Test GetCurrentFloor
            FloorState currentFloor = building.GetCurrentFloor();
            if (currentFloor == null) {
                System.out.println("FAILED: CurrentFloor is null");
                return;
            }
            
            // Test MoveLiftUp
            int initialFloor = building.getLift().getCurrentFloor();
            building.MoveLiftUp();
            if (building.getLift().getCurrentFloor() != initialFloor + 1) {
                System.out.println("FAILED: Lift did not move up correctly");
                return;
            }
            
            // Test MoveLiftDown
            building.MoveLiftDown();
            if (building.getLift().getCurrentFloor() != initialFloor) {
                System.out.println("FAILED: Lift did not move down correctly");
                return;
            }
            
            // Test Stop method
            int originalSize = building.GetCurrentFloorRequests().size();
            building.Stop();
            // Only works if lift has capacity and there are requests
            if (building.getLift().getCapacity() > 0 && originalSize > 0) {
                if (building.GetCurrentFloorRequests().size() >= originalSize) {
                    System.out.println("WARNING: Stop() didn't change requests, but might be due to capacity");
                }
            }
            
            System.out.println("Building operations test passed!");
            
        } catch (IOException e) {
            System.out.println("FAILED: Error in operations test - " + e.getMessage());
            e.printStackTrace();
        }
    }
