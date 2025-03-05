import java.util.List;

/**
 * Multi-elevator implementation of LOOK algorithm
 * Each elevator follows LOOK algorithm for its assigned floors
 */
public class MultiLiftLook extends MultiLiftAlgorithm {
    
    // Constructor
    public MultiLiftLook(MultiLiftBuilding building) {
        super(building);
    }
    
    @Override
    public MultiLiftBuilding NextStep() throws InvalidBuildingConfiguration {
        // Check that building is valid
        if (building.getFloors().GetFloors().isEmpty()) {
            throw new InvalidBuildingConfiguration();
        }
        
        for (int i = 0; i < building.getNumLifts(); i++) {
            if (building.getLift(i).getCapacity() < 1) {
                throw new InvalidBuildingConfiguration();
            }
        }
        
        // Dispatch requests
        dispatcher.dispatchRequests();
        
        // For each elevator, use LOOK algorithm
        for (int i = 0; i < building.getNumLifts(); i++) {
            moveElevator(i);
        }
        
        return building;
    }
    
    /**
     * Move one elevator using LOOK algorithm
     * @param liftIndex The elevator to move
     */
    private void moveElevator(int liftIndex) {
        // Get elevator info
        LiftState lift = building.getLift(liftIndex);
        List<FloorState> floorList = building.getFloors().GetFloors();
        int currentFloor = lift.getCurrentFloor();
        boolean goingUp = lift.isGoingUp();
        
        // Print current state
        System.out.println("Elevator " + liftIndex + " at Floor: " + currentFloor + 
                " | Direction: " + (goingUp ? "UP" : "DOWN"));
        
        // Check for requests at current floor
        Queue currentFloorRequests = building.GetCurrentFloorRequests(liftIndex);
        
        if (!currentFloorRequests.isEmpty()) {
            // Handle requests at current floor
            building.StopLift(liftIndex);
            System.out.println("Elevator " + liftIndex + " stopping at Floor " + 
                    currentFloor + " to pick up/drop off passengers.");
            
            // Clear assignment for this floor
            dispatcher.clearAssignment(currentFloor);
            
            // Done with this step - no movement for this elevator
            return;
        }
        
        // Get floors assigned to this elevator
        List<Integer> assignedFloors = dispatcher.getAssignedFloors(liftIndex);
        
        // Find highest and lowest assigned floors
        Integer highestAssigned = null;
        Integer lowestAssigned = null;
        
        for (Integer floor : assignedFloors) {
            if (highestAssigned == null || floor > highestAssigned) {
                highestAssigned = floor;
            }
            if (lowestAssigned == null || floor < lowestAssigned) {
                lowestAssigned = floor;
            }
        }
        
        // Also consider floors with passengers who want to get off
        for (int i = 0; i < floorList.size(); i++) {
            // Check if any passengers want to get off at this floor
            Object[] liftPassengers = lift.getCurrentlyHandlingObj();
            for (Object passenger : liftPassengers) {
                if (passenger instanceof Integer && (Integer)passenger == i) {
                    // Someone wants to get off at floor i
                    if (highestAssigned == null || i > highestAssigned) {
                        highestAssigned = i;
                    }
                    if (lowestAssigned == null || i < lowestAssigned) {
                        lowestAssigned = i;
                    }
                    break;
                }
            }
        }
        
        // If no assigned floors, stay put
        if (highestAssigned == null || lowestAssigned == null) {
            System.out.println("Elevator " + liftIndex + " has no assigned floors. Remaining idle.");
            return;
        }
        
        // LOOK algorithm logic
        if (goingUp) {
            if (currentFloor < highestAssigned) {
                // Continue going up
                building.MoveLiftUp(liftIndex);
                System.out.println("Elevator " + liftIndex + " moving UP to Floor: " + 
                        (currentFloor + 1));
            } else {
                // Change direction
                lift.setGoingUp(false);
                System.out.println("Elevator " + liftIndex + " reached highest request. " + 
                        "Changing direction to DOWN.");
                
                // Start moving down if there are requests below
                if (currentFloor > lowestAssigned) {
                    building.MoveLiftDown(liftIndex);
                    System.out.println("Elevator " + liftIndex + " moving DOWN to Floor: " + 
                            (currentFloor - 1));
                }
            }
        } else {
            if (currentFloor > lowestAssigned) {
                // Continue going down
                building.MoveLiftDown(liftIndex);
                System.out.println("Elevator " + liftIndex + " moving DOWN to Floor: " + 
                        (currentFloor - 1));
            } else {
                // Change direction
                lift.setGoingUp(true);
                System.out.println("Elevator " + liftIndex + " reached lowest request. " + 
                        "Changing direction to UP.");
                
                // Start moving up if there are requests above
                if (currentFloor < highestAssigned) {
                    building.MoveLiftUp(liftIndex);
                    System.out.println("Elevator " + liftIndex + " moving UP to Floor: " + 
                            (currentFloor + 1));
                }
            }
        }
    }
}
