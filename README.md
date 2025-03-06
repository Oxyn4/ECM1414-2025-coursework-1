## ECM 1414 Coursework - Elevator Control System Using Queues and Scheduling Algorithms

## Project Overview

This project implements an elevator control system that simulates elevator operations in multi-story buildings, handles passenger requests, and uses different scheduling algorithms to optimize elevator operation. The system supports both single and multiple elevator modes and provides a graphical interface for visualization.

## Features

- **Multiple Scheduling Algorithms**:
  - SCAN (Elevator Scan Algorithm) - Elevator moves in one direction until no more requests
  - LOOK (Improved Scan Algorithm) - Similar to SCAN but changes direction at furthest request
  - MYLIFT (Custom Algorithm) - Optimized algorithm considering wait times and priorities

- **Data Structures**:
  - Queue - Manages floor requests
  - Priority Queue - Optimizes request handling based on wait time

- **Multi-Elevator System**:
  - Supports multiple elevators operating simultaneously
  - Request allocation mechanism selects optimal elevator
  - Coordination between elevators to avoid redundant movements

- **Graphical User Interface**:
  - Visualizes elevator positions and directions
  - Displays floor request status
  - Simulation controls (start/pause/step/reset)
  - Supports single and multiple elevator display

- **Performance Testing Framework**:
  - Generates various test scenarios
  - Collects performance metrics (wait time, energy usage, etc.)
  - Creates charts comparing different algorithms

#### Single Elevator Console Mode:java ElevatorApp

#### Single Elevator GUI Mode:java StudentElevatorGUI

#### Multi-Elevator Mode:java MultiLiftApp

#### Multi-Elevator GUI Mode:java MultiElevatorGUI

#### Running the Test Suite:java ElevatorTestMain

### Core Components:
- `Building.java` - Defines building and single elevator system
- `Algorithm.java` - Base class for algorithms
- `Scan.java` - SCAN algorithm implementation
- `Look.java` - LOOK algorithm implementation
- `MyLift.java` - Custom optimized algorithm
- `Queue.java` - Queue implementation
- `Request.java` - Elevator request model
- `PriorityQueue.java` - Priority queue implementation

### Graphical Interface:
- `ElevatorGUI.java` - Single elevator graphical interface
- `MultiElevatorGUI.java` - Multi-elevator graphical interface

### Multi-Elevator System:
- `MultiLiftBuilding.java` - Multi-elevator building model
- `RequestDispatcher.java` - Request allocation system
- `MultiLiftAlgorithm.java` - Base class for multi-elevator algorithms
- `MultiLiftLook.java` - Multi-elevator LOOK algorithm implementation
- `MultiLiftApp.java` - Multi-elevator application entry point

### Testing Framework:
- `BuildingTest.java` - Tests for building operations
- `QueueTest.java` - Tests for queue data structure
- `AlgorithmTest.java` - Tests for scheduling algorithms
- `TestRunner.java` - Test execution framework
- `test_input.txt` - Test building configuration

### SCAN Algorithm
The elevator moves in one direction and only changes direction when it reaches the top or bottom floors. While moving up, it sequentially services each floor's up requests; while moving down, it sequentially services each floor's down requests.

### LOOK Algorithm
The LOOK algorithm is an improved version of SCAN. The elevator moves in the current direction to the furthest request and then changes direction, rather than having to reach the top or bottom of the building. This reduces unnecessary movements.

### MYLIFT Algorithm
The custom algorithm optimizes elevator scheduling by considering the following factors:
- Request wait time (to avoid request starvation)
- Match between request direction and elevator direction
- Current elevator occupancy
- Priorities for special floors (such as entrance lobbies)

## Multi-Elevator System

The multi-elevator system coordinates multiple elevators through the `RequestDispatcher`:
1. Evaluates the distance and direction of each request relative to each elevator
2. Assigns the optimal elevator to each request
3. Each elevator uses a separate LOOK algorithm to handle assigned requests
4. Periodically reassesses assignments to adapt to system changes

## Team Members:YanBin Huang,Sam Harries,Logan Westwood,Jacob Evans,Xuanjin Qu.

*This project is submitted as part of ECM 1414 coursework, University of Exeter, 2025*
