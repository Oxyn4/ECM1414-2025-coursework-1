public class Scan extends Algorithm {
    public Scan(Building buildingState) {
        super(buildingState);
    }

    public Building NextStep() throws InvalidBuildingConfiguration {
        if (super.building.getLift().getCapacity() == 0) {
            throw new InvalidBuildingConfiguration();
        }

        if (super.building.getFloors().GetFloors().isEmpty()) {
            throw new InvalidBuildingConfiguration();
        }

        // copy of the building we will return
        Building building = super.building;

        // move the lift in the current direction of travel until we find a floor with requests
        while (building.GetCurrentFloorRequests().isEmpty()) {
            building.LiftContinue();
        }

        // we are now on a floor with requests

        return super.building;
    }
}
