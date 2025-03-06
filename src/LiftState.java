import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// Represents a Lift with different variables denoting properties
public class LiftState {
    // how many people can simultaneously use the lift
    int capacity;
    int currentFloor; //tracks current floor
    boolean goingUp; //tracks direction

    // people in the elevator
    private ArrayList<Integer> currentlyHandling = new ArrayList<>();

    public void AddRequest(int Request) {
        currentlyHandling.add(Request);
    }

    public void AddRequest(List<Integer> requests) {
        currentlyHandling.addAll(requests);
    }

    public void RemoveRequest(Integer Request) {
        currentlyHandling.remove(Request);
    }

    public void RemoveAllRequestsForFloor(Integer floor) {
        currentlyHandling.removeAll(Collections.singleton(floor));
    }

    public int RemoveLastRequest() {
        return currentlyHandling.remove(currentlyHandling.size() - 1);
    }

    public int Occupancy() {
        return this.currentlyHandling.size();
    }

    // private default constructor
    // stop mistakes by marking private bad things could happen if this object is default initialised
    private LiftState() {}

    public LiftState(int numFloors, int capacity) {
        this.capacity = capacity;
        this.currentFloor = 1; 
        this.goingUp = true;  
    }

    // Getter for capacity
    public int getCapacity() {
        return capacity;
    }

    // Setter for capacity
    // marked as private to prevent capacity being changed mid-algorithm or something
    private void setCapacity(int newCapacity) {
        capacity = newCapacity;
    }

    // getter for current floor
    public int getCurrentFloor() {
        return currentFloor;
    }

    //setter for current floor
    public void setCurrentFloor(int floor){
        this.currentFloor = floor;
    }

    //getter for directionn
    public boolean isGoingUp(){
        return goingUp;
    }

    //setter for direction
    public void setGoingUp(boolean goingUp){
        this.goingUp = goingUp;
    }

    // helper function to generate a random valid lift configuration
    // parameters are various bounds on lift properties
    public static LiftState FromRandom(int MaxCapacity) {
        LiftState ret = new LiftState();
        Random rand = new Random();

        ret.setCapacity(rand.nextInt(MaxCapacity) + 1);
        ret.currentFloor = rand.nextInt(10);
        ret.setGoingUp(rand.nextBoolean());

        return ret;
    }

    // debugging utility to print contents simply
    public String toString() {
        return "Lift[MaxCapacity = " + capacity + "]";
    }
}