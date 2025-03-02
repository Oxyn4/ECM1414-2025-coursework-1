import java.io.*;
import java.util.*;

public final class Building {
    private FloorsState floors;
    private LiftState lift;

    private Building() {}

    public FloorState GetCurrentFloor() {
        return floors.GetFloors().get(lift.getCurrentFloor());
    }

    public Queue GetCurrentFloorRequests() {
        return GetCurrentFloor().GetFloorRequests();
    }

    public Queue Admit(int NumberOfPeople) {
        if (NumberOfPeople <= 0) {
            throw new IllegalArgumentException("Number of people must be greater than 0");
        }
        if (NumberOfPeople > lift.getCapacity()) {
            throw new IllegalArgumentException("Number of people must be less than or equal capacity");
        }

        Queue Requests = GetCurrentFloorRequests();

        lift.enqueueRequest(Requests.dequeue(NumberOfPeople));

        return Requests;
    }

    // checks if the lift is at the highest
    public boolean IsLiftAtTop() {
        return floors.GetFloors().size() == (lift.currentFloor - 1);
    }

    // checks if the lift is at the bottom
    public boolean IsLiftAtBottom() {
        return lift.currentFloor == 0;
    }

    // checks if the lift is at top or bottom
    public boolean CanLiftContinue() {
        return IsLiftAtTop() || IsLiftAtBottom();
    }

    // moves the elevator up one space
    public void MoveLiftUp() {
        if (IsLiftAtTop()) {
            return;
        }
        lift.currentFloor++;
    }

    public void MoveLiftDown() {
        if (IsLiftAtBottom()) {
            return;
        }
        lift.currentFloor--;
    }

    // move lift one space in current direction of travel
    public void LiftContinue() {
        if (CanLiftContinue()) {
            lift.goingUp = !lift.goingUp;
        }
        if (lift.goingUp) {
            lift.currentFloor = lift.currentFloor + 1;
        } else {
            lift.currentFloor = lift.currentFloor - 1;
        }
    }

    //Getter methods to access lift and floors
    public LiftState getLift() {
        return lift;
    }

    public FloorsState getFloors() {
        return floors;
    }

    // Load Building from a Text File
    public static Building FromFile(String filename) throws IOException {
        Building building = new Building();
        BufferedReader reader = new BufferedReader(new FileReader(filename));

        String line;
        int numFloors = 0;
        int capacity = 0;
        HashMap<Integer, List<Integer>> requestsMap = new HashMap<>();

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue; // skips comments and empty lines

            String[] parts = line.split(":");
            if (parts.length == 1 && parts[0].contains(",")) { 
                //parses number of floors and lift capacity
                String[] values = parts[0].split(",");
                numFloors = Integer.parseInt(values[0].trim());
                capacity = Integer.parseInt(values[1].trim());
            } else if (parts.length == 2) {
                // parses floor requests
                int floor = Integer.parseInt(parts[0].trim());
                String[] destinations = parts[1].split(",");
                List<Integer> destList = new ArrayList<>();
                for (String dest : destinations) {
                    destList.add(Integer.parseInt(dest.trim()));
                }
                requestsMap.put(floor, destList);
            }
        }
        reader.close();

        //Uses the constructor for LiftState
        building.lift = new LiftState(numFloors, capacity); 
        building.floors = new FloorsState(numFloors, requestsMap);

        return building;
    }

    //for debugging
    @Override
    public String toString() {
        return "Building[ Floors: " + floors + ", Lift: " + lift + " ]";
    }
}
