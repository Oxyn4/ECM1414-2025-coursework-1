import java.util.ArrayList;
import java.util.List;

/**
 * This class decides which elevator should handle each request
 */
public class RequestDispatcher {
    // The building with multiple elevators
    private MultiLiftBuilding building;
    
    // Keep track of which elevator is assigned to each floor
    private int[] floorToElevator;
    
    // Constructor
    public RequestDispatcher(MultiLiftBuilding building) {
        this.building = building;
        
        // Initialize array to track elevator assignments
        int floors = building.getFloors().GetFloors().size();
        floorToElevator = new int[floors];
        
        // Default: no assignment (-1)
        for (int i = 0; i < floors; i++) {
            floorToElevator[i] = -1;
        }
    }
    
    /**
     * Find the best elevator to handle a request
     * @param requestFloor Floor where the request is
     * @param destinationFloor Where they want to go
     * @return Index of the best elevator to handle this request
     */
    public int getBestElevator(int requestFloor, int destinationFloor) {
        // If a request is already assigned to an elevator, use that one
        if (floorToElevator[requestFloor] != -1) {
            return floorToElevator[requestFloor];
        }
        
        int numLifts = building.getNumLifts();
        int bestLift = 0;  // default to first elevator
        int shortestDistance = Integer.MAX_VALUE;
        
        // Find the closest elevator
        for (int i = 0; i < numLifts; i++) {
            LiftState lift = building.getLift(i);
            int currentFloor = lift.getCurrentFloor();
            int distance = Math.abs(currentFloor - requestFloor);
            
            // Additional logic for direction
            boolean goingUp = destinationFloor > requestFloor;
            boolean liftGoingUp = lift.isGoingUp();
            
            // Prefer elevators going in the same direction
            if (goingUp == liftGoingUp) {
                distance -= 2;  // make it more attractive
            }
            
            // Prefer elevators with less occupancy
            if (lift.Occupancy() < lift.getCapacity() / 2) {
                distance -= 1;
            }
            
            // Update if this is better
            if (distance < shortestDistance) {
                shortestDistance = distance;
                bestLift = i;
            }
        }
        
        // Remember this assignment
        floorToElevator[requestFloor] = bestLift;
        
        return bestLift;
    }
    
    /**
     * Find all floors that have requests
     * @return List of floor numbers with pending requests
     */
    public List<Integer> getFloorsWithRequests() {
        List<Integer> result = new ArrayList<>();
        List<FloorState> floors = building.getFloors().GetFloors();
        
        for (int i = 0; i < floors.size(); i++) {
            if (!floors.get(i).GetFloorRequests().isEmpty()) {
                result.add(i);
            }
        }
        
        return result;
    }
    
    /**
     * Assign available requests to elevators
     * Should be called periodically to update assignments
     */
    public void dispatchRequests() {
        // Get floors that have requests
        List<Integer> requestFloors = getFloorsWithRequests();
        
        // For each floor with requests
        for (int floor : requestFloors) {
            // Skip if already assigned
            if (floorToElevator[floor] != -1) {
                continue;
            }
            
            // Get floor's queue of requests
            Queue requests = building.GetFloor(floor).GetFloorRequests();
            
            // Can't see destination without removing from queue, so use default
            int defaultDestination = floor < building.getFloors().GetFloors().size() / 2 ? 
                    building.getFloors().GetFloors().size() - 1 : 0;
            
            // Assign to best elevator
            int bestLift = getBestElevator(floor, defaultDestination);
            floorToElevator[floor] = bestLift;
            
            // Print assignment for debugging
            System.out.println("Assigned floor " + floor + " to elevator " + bestLift);
        }
    }
    
    /**
     * Clear assignment when an elevator handles a floor
     * @param floor The floor that was handled
     */
    public void clearAssignment(int floor) {
        floorToElevator[floor] = -1;
    }
    
    /**
     * Get all floors assigned to a specific elevator
     * @param liftIndex The elevator index
     * @return List of floor numbers assigned to this elevator
     */
    public List<Integer> getAssignedFloors(int liftIndex) {
        List<Integer> result = new ArrayList<>();
        
        for (int i = 0; i < floorToElevator.length; i++) {
            if (floorToElevator[i] == liftIndex) {
                result.add(i);
            }
        }
        
        return result;
    }
}
