import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * My custom elevator algorithm that tries to be smarter than SCAN and LOOK
 */
public class MyLift extends Algorithm {
    // Keep track of waiting times
    private Map<Integer, Long> floorWaitTimes = new HashMap<>();
    private Map<Integer, Long> lastVisitTime = new HashMap<>();
    private int skipCounter = 0; // count floors we pass without stopping
    private final int MAX_SKIPS = 3;
    private final long LONG_WAIT_TIME = 10000; // 10 seconds
    
    // Constructor
    public MyLift(Building buildingState) {
        super(buildingState);
        
        // Set up initial values for wait times
        FloorsState floors = building.getFloors();
        List<FloorState> floorList = floors.GetFloors();
        for (int i = 0; i < floorList.size(); i++) {
            floorWaitTimes.put(i, 0L);
            lastVisitTime.put(i, System.currentTimeMillis());
        }
    }

    @Override
    public Building NextStep() throws InvalidBuildingConfiguration {
        // Get the current elevator and building state
        LiftState lift = building.getLift();
        FloorsState floors = building.getFloors();
        List<FloorState> floorList = floors.GetFloors();
        
        // Make sure we have a valid configuration
        if (floorList.isEmpty() || lift.getCapacity() < 1) {
            throw new InvalidBuildingConfiguration();
        }

        int currentFloor = lift.getCurrentFloor();
        boolean goingUp = lift.isGoingUp();
        long currentTime = System.currentTimeMillis();
        
        // Update how long each floor has been waiting
        for (int i = 0; i < floorList.size(); i++) {
            if (!floorList.get(i).GetFloorRequests().isEmpty()) {
                // Calculate wait time
                long waitTime = currentTime - lastVisitTime.get(i);
                floorWaitTimes.put(i, waitTime);
            }
        }

        // Print current state
        System.out.println("MyLift at Floor: " + currentFloor + " | Going: " + (goingUp ? "UP" : "DOWN"));
        
        // Find which floors have requests
        int highest = -1;
        int lowest = -1;
        int requestsAbove = 0;
        int requestsBelow = 0;
        int priorityFloor = -1;
        long maxWait = 0;
        
        // Loop through all floors to gather info
        for (int i = 0; i < floorList.size(); i++) {
            if (!floorList.get(i).GetFloorRequests().isEmpty()) {
                // Track max/min floors with requests
                if (highest == -1 || i > highest) {
                    highest = i;
                }
                if (lowest == -1 || i < lowest) {
                    lowest = i;
                }
                
                // Count requests above and below
                if (i > currentFloor)
                    requestsAbove++;
                else if (i < currentFloor)
                    requestsBelow++;
                
                // Check for long wait times
                long waitTime = floorWaitTimes.get(i);
                if (waitTime > maxWait) {
                    maxWait = waitTime;
                    priorityFloor = i;
                }
            }
        }
        
        // No requests? Stay put
        if (highest == -1 || lowest == -1) {
            System.out.println("No requests in building. Elevator stays put.");
            return building;
        }
        
        // Debug info
        System.out.println("Highest: " + highest + ", Lowest: " + lowest);
        System.out.println("Above: " + requestsAbove + ", Below: " + requestsBelow);
        
        // Check current floor for requests
        Queue currentFloorRequests = floorList.get(currentFloor).GetFloorRequests();
        if (!currentFloorRequests.isEmpty()) {
            // Reset skip counter
            skipCounter = 0;
            
            // Handle requests at this floor
            while (!currentFloorRequests.isEmpty()) {
                currentFloorRequests.dequeue();
            }
            
            System.out.println("Stopping at Floor " + currentFloor + " to load/unload.");
            
            // Reset wait time
            floorWaitTimes.put(currentFloor, 0L);
            lastVisitTime.put(currentFloor, currentTime);
            
            // Recheck for any remaining requests
            boolean stillHasRequests = false;
            for (int i = 0; i < floorList.size(); i++) {
                if (!floorList.get(i).GetFloorRequests().isEmpty()) {
                    stillHasRequests = true;
                    break;
                }
            }
            
            if (!stillHasRequests) {
                System.out.println("All requests handled. Elevator idle.");
                return building;
            }
        } else {
            // We passed a floor without stopping
            skipCounter++;
        }
        
        // Handle long wait times - prioritize floors waiting too long
        if (maxWait > LONG_WAIT_TIME && priorityFloor != -1 && priorityFloor != currentFloor) {
            System.out.println("Floor " + priorityFloor + " waiting too long: " + (maxWait/1000) + "s");
            
            // Do we need to change direction?
            if ((goingUp && priorityFloor < currentFloor) || (!goingUp && priorityFloor > currentFloor)) {
                goingUp = !goingUp;
                lift.setGoingUp(goingUp);
                System.out.println("Changing direction to reach priority floor");
            }
            
            // Move toward priority floor
            if (priorityFloor > currentFloor) {
                lift.setCurrentFloor(currentFloor + 1);
                System.out.println("Moving UP to Floor: " + (currentFloor + 1));
            } else {
                lift.setCurrentFloor(currentFloor - 1);
                System.out.println("Moving DOWN to Floor: " + (currentFloor - 1));
            }
            
            return building;
        }
        
        // Check if we've skipped too many floors
        if (skipCounter >= MAX_SKIPS) {
            System.out.println("Skipped " + skipCounter + " floors, reconsidering direction");
            
            // Change direction if there are more requests the other way
            if ((goingUp && requestsBelow > requestsAbove) || 
                (!goingUp && requestsAbove > requestsBelow)) {
                goingUp = !goingUp;
                lift.setGoingUp(goingUp);
                skipCounter = 0;
                System.out.println("Changing direction to " + (goingUp ? "UP" : "DOWN"));
            }
        }
        
        // Move elevator using LOOK-style logic with our improvements
        if (goingUp) {
            if (currentFloor < highest) {
                lift.setCurrentFloor(currentFloor + 1);
                System.out.println("Moving UP to Floor: " + (currentFloor + 1));
            } else {
                lift.setGoingUp(false);
                System.out.println("Reached top. Changing direction to DOWN.");
                
                // Only move down if there are floors below with requests
                if (currentFloor > lowest) {
                    lift.setCurrentFloor(currentFloor - 1);
                    System.out.println("Moving DOWN to Floor: " + (currentFloor - 1));
                }
            }
        } else {
            if (currentFloor > lowest) {
                lift.setCurrentFloor(currentFloor - 1);
                System.out.println("Moving DOWN to Floor: " + (currentFloor - 1));
            } else {
                lift.setGoingUp(true);
                System.out.println("Reached bottom. Changing direction to UP.");
                
                // Only move up if there are floors above with requests
                if (currentFloor < highest) {
                    lift.setCurrentFloor(currentFloor + 1);
                    System.out.println("Moving UP to Floor: " + (currentFloor + 1));
                }
            }
        }
        
        return building;
    }
}
