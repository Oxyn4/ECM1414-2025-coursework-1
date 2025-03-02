public void updatePriority(int floor, String currentDirection) {
    List<Integer> indices = floorIndexMap.get(floor);
    if (indices == null) return;

    for (int index : indices) {
        Request req = heap.get(index);
        int originalPriority = req.priority;
        int newPriority = calculateNewPriority(req, currentDirection);
        req.priority = newPriority;

        if (newPriority < originalPriority) {
            siftUp(index);
        } else {
            siftDown(index);
        }
    }
}

private int calculateNewPriority(Request req, String currentDirection) {
    int basePriority = req.direction.equals(currentDirection) ? 0 : 1000;
    long waitTime = System.currentTimeMillis() - req.requestTime;
    return basePriority + (int) (waitTime / 1000);
}
