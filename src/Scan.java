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
        if (building.GetCurrentFloorRequests().isEmpty()) {
            building.LiftContinue();
            return building;
        }

        // we are now on a floor with requests
        // let people who want to get off and let people who want to get in
        building.Stop();

        // move to next floor
        building.LiftContinue();

        return super.building;
    }
}
