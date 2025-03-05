import java.io.*;
import java.util.*;

/**
 * Building class that supports multiple elevators
 * Based on the original Building class but extended for multiple lifts
 */
public class MultiLiftBuilding {
    // floors of the building
    private FloorsState floors;
    
    // array of elevators
    private LiftState[] lifts;
    
    // number of lifts in the building
    private int numLifts;
    
    // Constructor
    public MultiLiftBuilding(FloorsState floors, LiftState[] lifts) {
        this.floors = floors;
        this.lifts = lifts;
        this.numLifts = lifts.length;
    }
    
    // Get the state of a specific floor
    public FloorState GetFloor(int floorNum) {
        return floors.GetFloors().get(floorNum);
    }
    
    // Get the current floor of a specific lift
    public FloorState GetCurrentFloor(int liftIndex) {
        if(liftIndex < 0 || liftIndex >= numLifts) {
            System.out.println("Warning: Invalid lift index: " + liftIndex);
            return null;
        }
        return floors.GetFloors().get(lifts[liftIndex].getCurrentFloor());
    }
    
    // Get requests for the floor where a specific lift is
    public Queue GetCurrentFloorRequests(int liftIndex) {
        if(liftIndex < 0 || liftIndex >= numLifts) {
            System.out.println("Warning: Invalid lift index: " + liftIndex);
            return new Queue(); // return empty queue
        }
        return GetCurrentFloor(liftIndex).GetFloorRequests();
    }
    
    // Stop a lift at its current floor to handle requests
    public void StopLift(int liftIndex) {
        if(liftIndex < 0 || liftIndex >= numLifts) {
            System.out.println("Warning: Invalid lift index: " + liftIndex);
            return;
        }
        
        // Remove passengers going to this floor
        LiftState lift = lifts[liftIndex];
        lift.RemoveAllRequestsForFloor(lift.getCurrentFloor());
        
        // Add waiting passengers (up to capacity)
        Queue currentRequests = GetCurrentFloorRequests(liftIndex);
        int space = lift.getCapacity() - lift.Occupancy();
        if(space > 0 && !currentRequests.isEmpty()) {
            lift.AddRequest(currentRequests.dequeue(Math.min(space, currentRequests.size())));
        }
    }
    
    // Check if a lift is at the top floor
    public boolean IsLiftAtTop(int liftIndex) {
        if(liftIndex < 0 || liftIndex >= numLifts) {
            return false;
        }
        return floors.GetFloors().size() == (lifts[liftIndex].getCurrentFloor() + 1);
    }
    
    // Check if a lift is at the bottom floor
    public boolean IsLiftAtBottom(int liftIndex) {
        if(liftIndex < 0 || liftIndex >= numLifts) {
            return false;
        }
        return lifts[liftIndex].getCurrentFloor() == 0;
    }
    
    // Move a lift up one floor
    public void MoveLiftUp(int liftIndex) {
        if(liftIndex < 0 || liftIndex >= numLifts) {
            return;
        }
        
        if(!IsLiftAtTop(liftIndex)) {
            LiftState lift = lifts[liftIndex];
            lift.setCurrentFloor(lift.getCurrentFloor() + 1);
            lift.setGoingUp(true);
        }
    }
    
    // Move a lift down one floor
    public void MoveLiftDown(int liftIndex) {
        if(liftIndex < 0 || liftIndex >= numLifts) {
            return;
        }
        
        if(!IsLiftAtBottom(liftIndex)) {
            LiftState lift = lifts[liftIndex];
            lift.setCurrentFloor(lift.getCurrentFloor() - 1);
            lift.setGoingUp(false);
        }
    }
    
    // Move lift in current direction and handle direction changes
    public void LiftContinue(int liftIndex) {
        if(liftIndex < 0 || liftIndex >= numLifts) {
            return;
        }
        
        LiftState lift = lifts[liftIndex];
        
        // Change direction if at top or bottom
        if(IsLiftAtTop(liftIndex) || IsLiftAtBottom(liftIndex)) {
            lift.setGoingUp(!lift.isGoingUp());
        }
        
        // Move in current direction
        if(lift.isGoingUp()) {
            MoveLiftUp(liftIndex);
        } else {
            MoveLiftDown(liftIndex);
        }
    }
    
    // Getters
    public FloorsState getFloors() {
        return floors;
    }
    
    public LiftState getLift(int index) {
        if(index < 0 || index >= numLifts) {
            System.out.println("Warning: Invalid lift index: " + index);
            return null;
        }
        return lifts[index];
    }
    
    public int getNumLifts() {
        return numLifts;
    }
    
    // Get all lifts
    public LiftState[] getAllLifts() {
        return lifts;
    }
    
    // Load multi-elevator building from file
    public static MultiLiftBuilding FromFile(String filename, int numElevators) throws IOException {
        // First read the building configuration
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        
        String line;
        int numFloors = 0;
        int capacity = 0;
        HashMap<Integer, List<Integer>> requestsMap = new HashMap<>();
        
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue; // skip comments
            
            String[] parts = line.split(":");
            if (parts.length == 1 && parts[0].contains(",")) { 
                // parse floors and capacity
                String[] values = parts[0].split(",");
                numFloors = Integer.parseInt(values[0].trim());
                capacity = Integer.parseInt(values[1].trim());
            } else if (parts.length == 2) {
                // parse floor requests
                int floor = Integer.parseInt(parts[0].trim());
                String[] destinations = parts[1].split(",");
                List<Integer> destList = new ArrayList<>();
                for (String dest : destinations) {
                    if (!dest.trim().isEmpty()) {
                        destList.add(Integer.parseInt(dest.trim()));
                    }
                }
                requestsMap.put(floor, destList);
            }
        }
        reader.close();
        
        // Create multiple elevators
        LiftState[] lifts = new LiftState[numElevators];
        for (int i = 0; i < numElevators; i++) {
            lifts[i] = new LiftState(numFloors, capacity);
            
            // Position elevators at different floors
            lifts[i].setCurrentFloor(i % numFloors);
            
            // Alternate directions
            lifts[i].setGoingUp(i % 2 == 0);
        }
        
        // Create floors
        FloorsState floors = new FloorsState(numFloors, requestsMap);
        
        return new MultiLiftBuilding(floors, lifts);
    }
    
    // For debugging
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MultiLiftBuilding: ").append(numLifts).append(" lifts, ");
        sb.append(floors.GetFloors().size()).append(" floors\n");
        
        sb.append("Floors: ").append(floors).append("\n");
        
        sb.append("Lifts: [");
        for (int i = 0; i < numLifts; i++) {
            sb.append("\n  Lift ").append(i).append(": ");
            sb.append("Floor=").append(lifts[i].getCurrentFloor());
            sb.append(", Going").append(lifts[i].isGoingUp() ? "Up" : "Down");
            sb.append(", Capacity=").append(lifts[i].getCapacity());
            sb.append(", Occupancy=").append(lifts[i].Occupancy());
            if (i < numLifts - 1) sb.append(",");
        }
        sb.append("\n]");
        
        return sb.toString();
    }
}
