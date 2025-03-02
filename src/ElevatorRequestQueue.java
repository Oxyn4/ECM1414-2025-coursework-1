import java.util.ArrayList;

/**
 * Priority queue implementation for managing elevator requests
 * using a min-heap structure.
 */
public class ElevatorRequestQueue {
    private ArrayList<ElevatorRequest> heap = new ArrayList<>();
    private String currentDirection;  // Current movement direction of the elevator

    public ElevatorRequestQueue(String initialDirection) {
        currentDirection = initialDirection;
    }

    /**
     * Internal class encapsulating elevator request data
     */
    private static class ElevatorRequest {
        int floor;                  // Target floor number
        int priority;               // Calculated priority value
        String direction;           // Requested movement direction
        long createTime;            // Timestamp for tracking request age (milliseconds since epoch)

        ElevatorRequest(int floor, int priority, String direction, long createTime) {
            this.floor = floor;
            this.priority = priority;
            this.direction = direction;
            this.createTime = createTime;
        }
    }

    /**
     * Add new request to the queue and adjust heap structure
     * @param floor Target floor number
     * @param direction Request direction (UP/DOWN)
     */
    public void addRequest(int floor, String direction) {
        long timestamp = System.currentTimeMillis();
        int basePriority = direction.equals(currentDirection) ? 0 : 1000;
        int initialWaitCompensation = 0;  // Initial wait time compensation (can be modified for different strategies)

        ElevatorRequest request = new ElevatorRequest(
            floor, 
            basePriority + initialWaitCompensation, 
            direction, 
            timestamp
        );
        heap.add(request);

        // Heapify-up operation to maintain heap property
        int currentIndex = heap.size() - 1;
        while (currentIndex > 0) {
            int parentIndex = (currentIndex - 1) / 2;
            if (heap.get(currentIndex).priority < heap.get(parentIndex).priority) {
                swapElements(currentIndex, parentIndex);
                currentIndex = parentIndex;
            } else {
                break;
            }
        }
    }

    /**
     * Remove and return the highest priority request
     * @return Floor number of processed request, or -1 if queue is empty
     */
    public int processNextRequest() {
        if (heap.isEmpty()) return -1;

        ElevatorRequest highestPriority = heap.get(0);
        currentDirection = highestPriority.direction;

        // Heap reconstruction logic for last element
        if (heap.size() == 1) {
            heap.remove(0);
        } else {
            ElevatorRequest lastElement = heap.remove(heap.size() - 1);
            heap.set(0, lastElement);
            heapifyDown(0);
        }

        return highestPriority.floor;
    }

    /**
     * Update request priorities based on current system state and wait times
     */
    public void refreshPriorities() {
        final long currentTime = System.currentTimeMillis();
        
        for (int i = 0; i < heap.size(); i++) {
            ElevatorRequest request = heap.get(i);
            long elapsedSeconds = (currentTime - request.createTime) / 1000;
            int adjustedPriority = (request.direction.equals(currentDirection) ? 0 : 1000) 
                                 + (int) elapsedSeconds;
            request.priority = adjustedPriority;
        }

        rebuildHeap();
    }

    /**
     * Full heap reconstruction from scratch
     */
    private void rebuildHeap() {
        for (int i = (heap.size() - 1) / 2; i >= 0; i--) {
            heapifyDown(i);
        }
    }

    /**
     * Maintain heap property by moving elements down
     * @param index Starting index for heapify operation
     */
    private void heapifyDown(int index) {
        int current = index;
        while (true) {
            int leftChild = 2 * current + 1;
            int rightChild = 2 * current + 2;
            int smallestIndex = current;

            if (leftChild < heap.size() 
                && heap.get(leftChild).priority < heap.get(smallestIndex).priority) {
                smallestIndex = leftChild;
            }
            if (rightChild < heap.size() 
                && heap.get(rightChild).priority < heap.get(smallestIndex).priority) {
                smallestIndex = rightChild;
            }

            if (smallestIndex == current) break;

            swapElements(current, smallestIndex);
            current = smallestIndex;
        }
    }

    /**
     * Utility method for swapping elements in the heap
     */
    private void swapElements(int indexA, int indexB) {
        ElevatorRequest temp = heap.get(indexA);
        heap.set(indexA, heap.get(indexB));
        heap.set(indexB, temp);
    }

    /**
     * Debug utility for displaying queue state
     */
    public void displayQueueState() {
        System.out.println("[DEBUG] Current Queue State:");
        for (ElevatorRequest req : heap) {
            System.out.printf(" Floor: %02d | Priority: %04d | Direction: %-4s | Age: %ds%n",
                req.floor,
                req.priority,
                req.direction,
                (System.currentTimeMillis() - req.createTime)/1000);
        }
        System.out.println("----------------------------");
    }
}
