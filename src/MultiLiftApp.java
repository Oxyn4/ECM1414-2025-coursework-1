import java.io.IOException;

/**
 * Main application for running multi-elevator simulation
 */
public class MultiLiftApp {
    public static void main(String[] args) {
        try {
            System.out.println("=== Multi-Elevator Simulation ===");
            
            // Load building with multiple elevators
            System.out.println("Loading building from input.txt with 3 elevators...");
            MultiLiftBuilding building = MultiLiftBuilding.FromFile("input.txt", 3);
            
            // Print initial state
            System.out.println("\nInitial Building State:");
            System.out.println(building);
            
            // Create algorithm
            MultiLiftLook algorithm = new MultiLiftLook(building);
            
            // Run simulation steps
            System.out.println("\nRunning simulation...");
            for (int i = 0; i < 20; i++) {
                System.out.println("\n=== Step " + (i + 1) + " ===");
                building = algorithm.NextStep();
                
                // Check if all requests are handled
                boolean allDone = true;
                for (FloorState floor : building.getFloors().GetFloors()) {
                    if (!floor.GetFloorRequests().isEmpty()) {
                        allDone = false;
                        break;
                    }
                }
                
                // Exit loop if all requests are handled
                if (allDone) {
                    System.out.println("\nAll requests have been handled!");
                    break;
                }
            }
            
            // Print final state
            System.out.println("\nFinal Building State:");
            System.out.println(building);
            
        } catch (IOException e) {
            System.out.println("Error loading file: " + e.getMessage());
        } catch (InvalidBuildingConfiguration e) {
            System.out.println("Invalid building configuration: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
