/**
 * A simplified queue system for elevator requests that separates up and down requests.
 */
public class DirectionalQueue {
    
    // Simple class to store request info
    public class Request {
        int start;
        int end;
        
        public Request(int start, int end) {
            this.start = start;
            this.end = end;
        }
        
        public int getStart() {
            return start;
        }
        
        public int getEnd() {
            return end;
        }
        
        // Simple way to show the request
        public String toString() {
            return start + "->" + end;
        }
    }
    
    // Using two simple queues
    private Queue upQueue;
    private Queue downQueue;
    private int maxFloor;
    
    public DirectionalQueue(int floors) {
        maxFloor = floors;
        upQueue = new Queue();
        downQueue = new Queue();
    }
    
    // Add a new request and put it in the right queue
    public void addRequest(int start, int end) {
        // Basic error checking
        if (start < 1 || start > maxFloor || end < 1 || end > maxFloor) {
            System.out.println("Error: Invalid floor number");
            return;
        }
        
        if (start == end) {
            System.out.println("Error: Start and end floor are the same");
            return;
        }
        
        Request req = new Request(start, end);
        
        if (end > start) {
            // Going up
            upQueue.add(req);
        } else {
            // Going down
            downQueue.add(req);
        }
    }
    
    // Get the next up request
    public Request getNextUpRequest() {
        if (upQueue.isEmpty()) {
            return null;
        }
        return upQueue.remove();
    }
    
    // Get the next down request
    public Request getNextDownRequest() {
        if (downQueue.isEmpty()) {
            return null;
        }
        return downQueue.remove();
    }
    
    // Check if there are any up requests
    public boolean hasUpRequests() {
        return !upQueue.isEmpty();
    }
    
    // Check if there are any down requests
    public boolean hasDownRequests() {
        return !downQueue.isEmpty();
    }
    
    // Get total request count
    public int totalRequests() {
        return upQueue.size() + downQueue.size();
    }
    
    // Simple queue using a linked list
    private class Queue {
        // Node for linked list
        private class Node {
            Request data;
            Node next;
            
            Node(Request data) {
                this.data = data;
                next = null;
            }
        }
        
        private Node front;
        private Node back;
        private int count;
        
        public Queue() {
            front = null;
            back = null;
            count = 0;
        }
        
        // Check if empty
        public boolean isEmpty() {
            return count == 0;
        }
        
        // Add to the back
        public void add(Request data) {
            Node newNode = new Node(data);
            
            if (isEmpty()) {
                front = newNode;
                back = newNode;
            } else {
                back.next = newNode;
                back = newNode;
            }
            
            count++;
        }
        
        // Remove from the front
        public Request remove() {
            if (isEmpty()) {
                return null; // Simple error handling
            }
            
            Request value = front.data;
            front = front.next;
            
            if (front == null) {
                back = null;
            }
            
            count--;
            return value;
        }
        
        // Peek at the front
        public Request peek() {
            if (isEmpty()) {
                return null; // Simple error handling
            }
            
            return front.data;
        }
        
        // Get size
        public int size() {
            return count;
        }
    }
}
