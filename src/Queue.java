import java.util.ArrayList;
import java.util.List;

/**
 * Basic queue implementation for the elevator control system.
 * Used to manage floor requests in the simulation.
 */
public class Queue {
    // store all the requests (floor numbers)
    private ArrayList<Integer> data;
    
    // keep track of how many items we've processed
    private int totalProcessed = 0;
    
    // constructor - create a new empty queue
    public Queue() {
        data = new ArrayList<Integer>();
    }
    
    // add a single item to the queue
    public void enqueue(int floorNumber) {
        // just add it to the end of our list
        data.add(floorNumber);
    }
    
    // add multiple floor requests at once
    public void enqueue(List<Integer> newRequests) {
        // could do a for loop here but this is easier
        if (newRequests != null && newRequests.size() > 0) {
            data.addAll(newRequests);
        }
    }
    
    // remove and return the first item
    public int dequeue() {
        // make sure we have something to return
        if (isEmpty()) {
            // probably shouldn't happen but just in case
            System.out.println("Warning: tried to dequeue from empty queue");
            return -1; // error code
        }
        
        // get the item at the front
        int result = data.get(0);
        
        // remove it from our list
        data.remove(0);
        
        // count that we processed another request
        totalProcessed++;
        
        return result;
    }
    
    // remove and return multiple items
    public List<Integer> dequeue(int howMany) {
        // create a list to hold the results
        List<Integer> result = new ArrayList<Integer>();
        
        // don't try to remove more than we have
        int actualCount = Math.min(howMany, data.size());
        
        // get the requested number of items
        for (int i = 0; i < actualCount; i++) {
            result.add(dequeue());
        }
        
        return result;
    }
    
    // look at the first item without removing it
    public int peek() {
        if (isEmpty()) {
            // probably shouldn't happen but just in case
            System.out.println("Warning: tried to peek at empty queue");
            return -1; // error code
        }
        return data.get(0);
    }
    
    // look at any item by position
    public int peek(int position) {
        if (position < 0 || position >= data.size()) {
            // out of bounds
            System.out.println("Warning: invalid position in peek: " + position);
            return -1; // error code
        }
        return data.get(position);
    }
    
    // check if the queue is empty
    public boolean isEmpty() {
        return data.size() == 0;
    }
    
    // get the number of items in the queue
    public int size() {
        return data.size();
    }
    
    // for debugging - print out the queue
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }
        
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < data.size(); i++) {
            sb.append(data.get(i));
            if (i < data.size() - 1) {
                sb.append(", ");
            }
        }
        
        return sb.toString();
    }
    
    // get total number of requests we've processed
    // (not used right now but might be useful for stats)
    public int getTotalProcessed() {
        return totalProcessed;
    }
}
