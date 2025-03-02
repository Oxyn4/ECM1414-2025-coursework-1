import java.io.IOException;

class ElevatorApp {
    public static void main(String[] args) {
        //Load from a text file (uncomment this to test with a file)
        
        try {
            System.out.println("Loading Building from File...");
            Building building = Building.FromFile("input.txt");

            System.out.println("Initial Building State:");
            System.out.println(building);

            // Run LOOK algorithm
            Algorithm algorithm = new Look(building);

            // Simulate elevator movement
            for (int i = 0; i < 10; i++) {
                System.out.println("\nStep " + (i + 1) + ":");
                building = algorithm.NextStep();
            }
        } catch (IOException e) {
            System.out.println("Error loading file: " + e.getMessage());
        } catch (InvalidBuildingConfiguration e) {
            throw new RuntimeException(e);
        }

        // generate a random building config (uncomment to test randomness)
        /*
        FloorsRandomParameters floorsRandomParameters = new FloorsRandomParameters();
        floorsRandomParameters.maxFloors = 10;
        floorsRandomParameters.maxPeoplePerFloor = 5;

        LiftRandomParameters liftRandomParameters = new LiftRandomParameters();
        liftRandomParameters.maxCapacity = 5;

        BuildingRandomParameters buildingRandomParameters = new BuildingRandomParameters();
        buildingRandomParameters.floorParameters = floorsRandomParameters;
        buildingRandomParameters.liftParameters = liftRandomParameters;

        System.out.println("Generating Random Building...");

        // Generate a random building configuration
        Building building = Building.FromRandom(buildingRandomParameters);

        System.out.println("Initial Random Building State:");
        System.out.println(building);

        // Run LOOK algorithm on the random building
        Algorithm algorithm = new Look(building);

        // Simulate elevator movement
        for (int i = 0; i < 10; i++) {
            System.out.println("\nStep " + (i + 1) + ":");
            building = algorithm.NextStep();
        }
        */
    }
}
