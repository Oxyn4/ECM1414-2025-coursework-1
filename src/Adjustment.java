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
