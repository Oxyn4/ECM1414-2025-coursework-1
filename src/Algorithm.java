// generic interface for algorithms
public abstract class Algorithm {
    Building building;

    public Algorithm(Building building) {
        this.building = building;
    }

    public abstract Building NextStep() throws InvalidBuildingConfiguration;
}





