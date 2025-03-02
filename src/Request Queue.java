public class PriorityQueue {
    private static class Request {
        int floor;          // Requested floor
        int priority;       // Priority (lower value means higher priority)
        long requestTime;   // Request timestamp (used to calculate waiting time)
        String direction;   // Request direction (higher priority if it matches the elevator's direction)

        public Request(int floor, int priority, String direction, long requestTime) {
            this.floor = floor;
            this.priority = priority;
            this.direction = direction;
            this.requestTime = requestTime;
        }
    }

    private final ArrayList<Request> heap;
    private final HashMap<Integer, Integer> floorIndexMap; // Mapping of floor to heap index (for quick updates)

    public PriorityQueue() {
        this.heap = new ArrayList<>();
        this.floorIndexMap = new HashMap<>();
    }

    // Core methods: insert, extract, dynamically update priority
}
