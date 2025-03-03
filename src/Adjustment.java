import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Handles adjustments of request priorities in the elevator system.
 * Helps prevent requests from waiting too long.
 */
public class Adjustment {
    // References to priority queue components
    private ArrayList<Request> heap;
    private HashMap<Integer, List<Integer>> floorIndexMap;
    
    /**
     * Create a new adjustment utility
     */
    public Adjustment(ArrayList<Request> requestHeap, HashMap<Integer, List<Integer>> floorMap) {
        this.heap = requestHeap;
        this.floorIndexMap = floorMap;
    }
    
    /**
     * Updates the priority of requests from a specific floor.
     * This helps make sure requests don't wait too long.
     */
    public void updatePriority(int floor, String currentDirection) {
        // Get all requests from this floor
        List<Integer> indices = floorIndexMap.get(floor);
        if (indices == null) return;

        // Update each request's priority
        for (int index : indices) {
            Request req = heap.get(index);
            int originalPriority = req.priority;
            int newPriority = calculateNewPriority(req, currentDirection);
            req.priority = newPriority;

            // Maintain heap property after changing priority
            if (newPriority < originalPriority) {
                siftUp(index);  // Priority increased (lower value)
            } else {
                siftDown(index);  // Priority decreased (higher value)
            }
        }
    }

    /**
     * Calculates a new priority value for a request.
     * Considers both direction matching and waiting time.
     */
    private int calculateNewPriority(Request req, String currentDirection) {
        // Requests going in same direction as elevator get high priority (low value)
        int basePriority = req.direction.equals(currentDirection) ? 0 : 1000;
        
        // The longer a request waits, the higher its priority becomes
        long waitTime = System.currentTimeMillis() - req.requestTime;
        
        // Final priority formula (lower number = higher priority)
        return basePriority + (int) (waitTime / 1000);
    }
    
    /**
     * Updates priorities for all requests in the system
     */
    public void updateAllPriorities(String currentDirection) {
        // Loop through all requests
        for (int i = 0; i < heap.size(); i++) {
            Request req = heap.get(i);
            req.priority = calculateNewPriority(req, currentDirection);
        }
        
        // Rebuild the heap since many priorities changed
        rebuildHeap();
    }
    
    /**
     * Move a request up in the heap if its priority increased
     */
    private void siftUp(int index) {
        int current = index;
        
        // Keep moving up while priority is higher than parent
        while (current > 0) {
            // Find parent index
            int parent = (current - 1) / 2;
            
            // If parent has lower priority (higher number), swap them
            if (heap.get(parent).priority > heap.get(current).priority) {
                swap(parent, current);
                current = parent;
            } else {
                // Heap property is restored
                break;
            }
        }
    }
    
    /**
     * Move a request down in the heap if its priority decreased
     */
    private void siftDown(int index) {
        int current = index;
        int size = heap.size();
        
        while (true) {
            // Find left and right children
            int left = 2 * current + 1;
            int right = 2 * current + 2;
            int smallest = current;
            
            // Check if left child has higher priority
            if (left < size && heap.get(left).priority < heap.get(smallest).priority) {
                smallest = left;
            }
            
            // Check if right child has higher priority
            if (right < size && heap.get(right).priority < heap.get(smallest).priority) {
                smallest = right;
            }
            
            // If we found a child with higher priority, swap
            if (smallest != current) {
                swap(current, smallest);
                current = smallest;
            } else {
                // Heap property is restored
                break;
            }
        }
    }
    
    /**
     * Swap two elements in the heap
     */
    private void swap(int i, int j) {
        // Save one element
        Request temp = heap.get(i);
        
        // Do the swap
        heap.set(i, heap.get(j));
        heap.set(j, temp);
        
        // Update the floor index map
        updateFloorIndexMap(heap.get(i).floor, i, j);
        updateFloorIndexMap(heap.get(j).floor, j, i);
    }
    
    /**
     * Updates floor index mapping after a swap
     */
    private void updateFloorIndexMap(int floor, int newIndex, int oldIndex) {
        // Get all indices for this floor
        List<Integer> indices = floorIndexMap.get(floor);
        
        // Update the index that changed
        for (int i = 0; i < indices.size(); i++) {
            if (indices.get(i) == oldIndex) {
                indices.set(i, newIndex);
                break;
            }
        }
    }
    
    /**
     * Rebuild the entire heap from scratch
     */
    private void rebuildHeap() {
        // Start from last parent and work up to root
        for (int i = heap.size() / 2 - 1; i >= 0; i--) {
            siftDown(i);
        }
    }
}
