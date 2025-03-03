/**
 * Represents an elevator request with priority information.
 * Used with the priority queue and adjustment system.
 */
public class Request {
    // The floor number of this request
    int floor;
    
    // Priority value (lower value means higher priority)
    int priority;
    
    // Direction of travel (UP or DOWN)
    String direction;
    
    // When the request was created (used for waiting time calculation)
    long requestTime;
    
    /**
     * Create a new elevator request
     * 
     * @param floor Floor number
     * @param priority Initial priority value
     * @param direction Direction of travel
     */
    public Request(int floor, int priority, String direction) {
        this.floor = floor;
        this.priority = priority;
        this.direction = direction;
        this.requestTime = System.currentTimeMillis();
    }
    
    /**
     * Create a new elevator request with a specific timestamp
     * (useful for testing)
     */
    public Request(int floor, int priority, String direction, long timestamp) {
        this.floor = floor;
        this.priority = priority;
        this.direction = direction;
        this.requestTime = timestamp;
    }
    
    /**
     * Get a string representation of this request
     */
    @Override
    public String toString() {
        return "Request{floor=" + floor + 
               ", direction=" + direction + 
               ", priority=" + priority + 
               ", waitTime=" + getWaitTime() + "s}";
    }
    
    /**
     * Calculate how long this request has been waiting (in seconds)
     */
    public long getWaitTime() {
        return (System.currentTimeMillis() - requestTime) / 1000;
    }
}
