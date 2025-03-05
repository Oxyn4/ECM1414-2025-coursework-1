import java.util.List;

/**
 * Base class for multi-elevator algorithms
 */
public abstract class MultiLiftAlgorithm {
    protected MultiLiftBuilding building;
    protected RequestDispatcher dispatcher;
    
    public MultiLiftAlgorithm(MultiLiftBuilding building) {
        this.building = building;
        this.dispatcher = new RequestDispatcher(building);
    }
    
    /**
     * Run one step of the algorithm
     * @return Updated building state
     */
    public abstract MultiLiftBuilding NextStep() throws InvalidBuildingConfiguration;
}
