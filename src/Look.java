import java.util.List;

public class Look extends Algorithm {
    public Look(Building buildingState) {
        super(buildingState);
    }

    public Building NextStep() throws InvalidBuildingConfiguration {
        LiftState lift = building.getLift();
        FloorsState floors = building.getFloors();
        List<FloorState> floorList = floors.GetFloors();

        if (floorList.isEmpty()) {
            throw new InvalidBuildingConfiguration();
        }

        if (lift.getCapacity() < 1) {
            throw new InvalidBuildingConfiguration();
        }

        int currentFloor = lift.getCurrentFloor();
        boolean goingUp = lift.isGoingUp();

        //Show the current state
        System.out.println("Elevator at Floor: " + currentFloor + " | Direction: " + (goingUp ? "UP" : "DOWN"));

        //Find the highest and lowest requested floors
        Integer highestRequest = null;
        Integer lowestRequest = null;

        for (int i = 0; i < floorList.size(); i++) {
            if (!floorList.get(i).GetFloorRequests().isEmpty()) {
                if (highestRequest == null || i > highestRequest) highestRequest = i;
                if (lowestRequest == null || i < lowestRequest) lowestRequest = i;
            }
        }

        //Handles cases where no requests exist
        if (highestRequest == null) {
            System.out.println("No requests in the building. Elevator remains idle.");
            return building; // This return won't actually be reached
        }

        // Show highest & lowest requests
        System.out.println("Highest Request: " + highestRequest + " | Lowest Request: " + lowestRequest);

        //Check if there is a request at the current floor and remove ALL requests from this floor
        Queue currentFloorRequests = floorList.get(currentFloor).GetFloorRequests();
        if (!currentFloorRequests.isEmpty()) {
            while (!currentFloorRequests.isEmpty()) {
                currentFloorRequests.dequeue(); // Remove each request
            }
            System.out.println("Stopping at Floor " + currentFloor + " to pick up/drop off passengers.");

            // Re-check if there are still requests left after removing them
            highestRequest = null;
            lowestRequest = null;
            for (int i = 0; i < floorList.size(); i++) {
                if (!floorList.get(i).GetFloorRequests().isEmpty()) {
                    if (highestRequest == null || i > highestRequest) highestRequest = i;
                    if (lowestRequest == null || i < lowestRequest) lowestRequest = i;
                }
            }

            // If still no requests, go idle (and then exit)
            if (highestRequest == null || lowestRequest == null) {
                System.out.println("No requests in the building. Elevator remains idle.");
                System.exit(0); // Terminate the program immediately
                return building; // This return won't actually be reached
            }
        }

        //Move elevator in its direction, stopping at the last request in that direction
        if (goingUp) {
            if (currentFloor < highestRequest) {
                lift.setCurrentFloor(currentFloor + 1);
                System.out.println("Moving UP to Floor: " + (currentFloor + 1));
            } else {
                lift.setGoingUp(false); // Change direction when at highest request
                System.out.println("Reached highest request. Changing direction to DOWN.");
            }
        } else {
            if (currentFloor > lowestRequest) {
                lift.setCurrentFloor(currentFloor - 1);
                System.out.println("Moving DOWN to Floor: " + (currentFloor - 1));
            } else {
                lift.setGoingUp(true); // Change direction when at lowest request
                System.out.println("Reached lowest request. Changing direction to UP.");
            }
        }

        return building;
    }
}
