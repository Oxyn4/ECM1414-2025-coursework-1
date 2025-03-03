import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Handles dynamic priority adjustments for elevator requests.
 * Helps prevent starvation by increasing priority of long-waiting requests.
 */
public class Adjustment {
    // Reference to the request heap
    private ArrayList<Request> heap;
    
    // Maps floor numbers to their positions in the heap for quick lookups
    private HashMap<Integer, List<Integer>> floorIndexMap;
    
    // Weight factors for priority calculation
    private final int DIRECTION_MATCH_WEIGHT = 0;  // Lower value = higher priority
    private final int DIRECTION_MISMATCH_WEIGHT = 1000;
    private final int WAIT_TIME_MULTIPLIER = 1;  // How much wait time affects priority
    
    /**
     * Constructor for the Adjustment class
     * 
     * @param requestHeap The heap of elevator requests
     * @param floorMap The mapping of floors to heap indices
     */
    public Adjustment(ArrayList<Request> requestHeap, HashMap<Integer, List<Integer>> floorMap) {
        this.heap = requestHeap;
        this.floorIndexMap = floorMap;
    }
    
    /**
     * Updates priority for all requests from a specific floor
     * 
     * @param floor The floor number to update
     * @param currentDirection The current direction of the elevator ("UP" or "DOWN")
     */
    public void updatePriority(int floor, String currentDirection) {
        // Get the indices of all requests from this floor
        List<Integer> indices = floorIndexMap.get(floor);
        
        // If no requests from this floor, do nothing
        if (indices == null || indices.isEmpty()) {
            return;
        }
        
        // Update each request from this floor
        for (int index : indices) {
            // Get the request
            Request req = heap.get(index);
            
            // Save original priority for comparison
            int originalPriority = req.priority;
            
            // Calculate and set new priority
            int newPriority = calculateNewPriority(req, currentDirection);
            req.priority = newPriority;
            
            // If priority increased (lower value), need to move up in heap
            if (newPriority < originalPriority) {
                siftUp(index);
            } 
            // If priority decreased (higher value), need to move down in heap
            else if (newPriority > originalPriority) {
                siftDown(index);
            }
            // If priority unchanged, do nothing
        }
    }
    
    /**
     * Calculate a new priority for a request based on wait time and direction
     * 
     * @param req The request to calculate priority for
     * @param currentDirection The current direction of the elevator
     * @return The calculated priority value (lower = higher priority)
     */
    private int calculateNewPriority(Request req, String currentDirection) {
        // Base priority depends on direction match
        int basePriority = req.direction.equals(currentDirection) 
                        ? DIRECTION_MATCH_WEIGHT 
                        : DIRECTION_MISMATCH_WEIGHT;
        
        // Calculate how long this request has been waiting (in seconds)
        long waitTime = (System.currentTimeMillis() - req.requestTime) / 1000;
        
        // Final priority: base priority adjusted by wait time
        // The longer it waits, the higher its priority becomes
        return basePriority - (int)(waitTime * WAIT_TIME_MULTIPLIER);
    }
    
    /**
     * Helper method to move an element up in the heap if its priority increased
     */
    private void siftUp(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            
            // If parent has lower priority (higher value), swap them
            if (heap.get(parentIndex).priority > heap.get(index).priority) {
                swapHeapElements(parentIndex, index);
                index = parentIndex;
            } else {
                break; // Heap property restored
            }
        }
    }
    
    /**
     * Helper method to move an element down in the heap if its priority decreased
     */
    private void siftDown(int index) {
        int size = heap.size();
        
        while (true) {
            int leftChild = 2 * index + 1;
            int rightChild = 2 * index + 2;
            int smallest = index;
            
            // Find the smallest of parent and children
            if (leftChild < size && heap.get(leftChild).priority < heap.get(smallest).priority) {
                smallest = leftChild;
            }
            
            if (rightChild < size && heap.get(rightChild).priority < heap.get(smallest).priority) {
                smallest = rightChild;
            }
            
            // If smallest is not the parent, swap and continue
            if (smallest != index) {
                swapHeapElements(index, smallest);
                index = smallest;
            } else {
                break; // Heap property restored
            }
        }
    }
    
    /**
     * Helper method to swap two elements in the heap
     */
    private void swapHeapElements(int i, int j) {
        Request temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
        
        // Update the floor index map after swapping
        updateFloorIndexMap(heap.get(i).floor, i, j);
        updateFloorIndexMap(heap.get(j).floor, j, i);
    }
    
    /**
     * Updates floor index mapping after swapping elements
     */
    private void updateFloorIndexMap(int floor, int newIndex, int oldIndex) {
        List<Integer> indices = floorIndexMap.get(floor);
        
        // Find and update the old index
        for (int i = 0; i < indices.size(); i++) {
            if (indices.get(i) == oldIndex) {
                indices.set(i, newIndex);
                break;
            }
        }
    }
    
    /**
     * Handle priority adjustments for all requests when elevator changes direction
     */
    public void handleDirectionChange(String newDirection) {
        // Update all requests with the new direction
        for (int i = 0; i < heap.size(); i++) {
            Request req = heap.get(i);
            req.priority = calculateNewPriority(req, newDirection);
        }
        
        // Rebuild the entire heap
        rebuildHeap();
    }
    
    /**
     * Rebuild the entire heap from scratch
     */
    private void rebuildHeap() {
        // Heapify from the bottom up
        for (int i = heap.size() / 2 - 1; i >= 0; i--) {
            siftDown(i);
        }
    }
    
    /**
     * Utility method for regular priority updates (call periodically)
     */
    public void updateAllPriorities(String currentDirection) {
        for (int i = 0; i < heap.size(); i++) {
            Request req = heap.get(i);
            req.priority = calculateNewPriority(req, currentDirection);
        }
        
        rebuildHeap();
    }
}
