import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Elevator GUI
 * @author Student Team
 */
public class ElevatorGUI extends JFrame {
    // variables for the GUI components
    JPanel mainPanel;
    JPanel buildingView;
    JButton startBtn;
    JButton stepBtn;
    JButton resetBtn;
    JComboBox algoBox;
    JLabel statusLbl;
    
    // variables for the simulation
    Building myBuilding;
    Algorithm currentAlgo;
    boolean isRunning = false;
    Timer timer;
    int steps = 0;
    
    // some constants
    int FLOOR_HEIGHT = 60;  // pixels per floor
    int ELEVATOR_WIDTH = 70;
    
    // constructor
    public ElevatorGUI() {
        super("Elevator Simulator");  // set window title
        
        // make the main window
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // create the panels
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        // create the control panel
        JPanel controls = new JPanel();
        controls.setLayout(new FlowLayout());
        
        // add algorithm selection
        JLabel algoLbl = new JLabel("Algorithm:");
        controls.add(algoLbl);
        
        String[] algorithms = {"SCAN", "LOOK", "MYLIFT"};
        algoBox = new JComboBox(algorithms);
        controls.add(algoBox);
        
        // add buttons
        startBtn = new JButton("Start");
        stepBtn = new JButton("Step");
        resetBtn = new JButton("Reset");
        
        controls.add(startBtn);
        controls.add(stepBtn);
        controls.add(resetBtn);
        
        // add control panel to main panel
        mainPanel.add(controls, BorderLayout.NORTH);
        
        // create building view
        buildingView = new JPanel() {
            // override the paint method to draw elevator
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawElevator(g);
            }
        };
        buildingView.setBackground(Color.WHITE);
        
        // add building view to main panel
        mainPanel.add(buildingView, BorderLayout.CENTER);
        
        // add status label
        statusLbl = new JLabel("Select algorithm and press Start");
        mainPanel.add(statusLbl, BorderLayout.SOUTH);
        
        // add panel to frame
        add(mainPanel);
        
        // setup event handlers
        startBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // toggle simulation
                if(isRunning) {
                    stopSimulation();
                } else {
                    startSimulation();
                }
            }
        });
        
        stepBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // run one step
                runOneStep();
            }
        });
        
        resetBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // reset simulation
                resetSimulation();
            }
        });
        
        // create a timer for animation
        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // run one step while timer is active
                if(isRunning) {
                    runOneStep();
                }
            }
        });
        
        // show the window
        setVisible(true);
        
        // try to load the building
        try {
            loadBuilding();
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading building: " + ex.getMessage());
            statusLbl.setText("Error: " + ex.getMessage());
        }
    }
    
    // load a building from file
    private void loadBuilding() throws IOException {
        // for now just hardcode input.txt
        myBuilding = Building.FromFile("input.txt");
        steps = 0;
        statusLbl.setText("Loaded building with " + 
                myBuilding.getFloors().GetFloors().size() + " floors");
        repaint();
    }
    
    // start the simulation
    private void startSimulation() {
        if(myBuilding == null) {
            try {
                loadBuilding();
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                return;
            }
        }
        
        // create the algorithm
        String algoName = (String)algoBox.getSelectedItem();
        try {
            if(algoName.equals("SCAN")) {
                currentAlgo = new Scan(myBuilding);
            } else if(algoName.equals("LOOK")) {
                currentAlgo = new Look(myBuilding);
            } else { // "MYLIFT"
                currentAlgo = new MyLift(myBuilding);
            }
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Error creating algorithm: " + ex.getMessage());
            return;
        }
        
        // update UI state
        isRunning = true;
        startBtn.setText("Stop");
        algoBox.setEnabled(false);
        
        // start the timer
        timer.start();
    }
    
    // stop the simulation
    private void stopSimulation() {
        isRunning = false;
        startBtn.setText("Start");
        timer.stop();
    }
    
    // reset the simulation
    private void resetSimulation() {
        // stop first if running
        if(isRunning) {
            stopSimulation();
        }
        
        // reset variables
        myBuilding = null;
        currentAlgo = null;
        steps = 0;
        
        // reset ui
        algoBox.setEnabled(true);
        statusLbl.setText("Simulation reset");
        
        // reload building
        try {
            loadBuilding();
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
        
        // redraw
        repaint();
    }
    
    // run one step of simulation
    private void runOneStep() {
        if(myBuilding == null || currentAlgo == null) {
            // need to initialize
            try {
                loadBuilding();
                startSimulation();
                return;
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                return;
            }
        }
        
        try {
            // get current state for display
            LiftState lift = myBuilding.getLift();
            int floor = lift.getCurrentFloor();
            boolean up = lift.isGoingUp();
            
            // execute algorithm step
            myBuilding = currentAlgo.NextStep();
            steps++;
            
            // update status
            statusLbl.setText("Step " + steps + ": Floor " + floor + ", going " + 
                    (up ? "UP" : "DOWN"));
            
            // check if all requests are done
            boolean done = true;
            for(FloorState fs : myBuilding.getFloors().GetFloors()) {
                if(!fs.GetFloorRequests().isEmpty()) {
                    done = false;
                    break;
                }
            }
            
            if(done) {
                stopSimulation();
                statusLbl.setText("All requests complete in " + steps + " steps");
            }
            
            // update view
            repaint();
            
        } catch(Exception ex) {
            stopSimulation();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            statusLbl.setText("Error: " + ex.getMessage());
        }
    }
    
    // draws the elevator
    private void drawElevator(Graphics g) {
        // if no building loaded, show message
        if(myBuilding == null) {
            g.setColor(Color.RED);
            g.drawString("No building loaded!", 100, 100);
            return;
        }
        
        // get number of floors
        int floors = myBuilding.getFloors().GetFloors().size();
        
        // calculate dimensions
        int width = getWidth() - 40;
        int height = floors * FLOOR_HEIGHT;
        
        // draw building outline
        g.setColor(Color.BLACK);
        g.drawRect(20, 20, width, height);
        
        // draw floors
        for(int i = 0; i < floors; i++) {
            int y = 20 + (floors - i - 1) * FLOOR_HEIGHT;
            
            // draw floor line
            g.setColor(Color.GRAY);
            g.drawLine(20, y, 20 + width, y);
            
            // draw floor number
            g.setColor(Color.BLACK);
            g.drawString("Floor " + i, 25, y + FLOOR_HEIGHT - 10);
            
            // check for requests
            FloorState floor = myBuilding.getFloors().GetFloors().get(i);
            if(!floor.GetFloorRequests().isEmpty()) {
                // show requests as red circle
                g.setColor(Color.RED);
                g.fillOval(width - 30, y + 10, 20, 20);
                g.setColor(Color.WHITE);
                g.drawString("" + floor.GetFloorRequests().size(), width - 24, y + 24);
            }
        }
        
        // draw elevator shaft
        int shaftX = width / 2 - ELEVATOR_WIDTH / 2;
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(shaftX - 5, 20, ELEVATOR_WIDTH + 10, height);
        
        // draw elevator
        LiftState lift = myBuilding.getLift();
        int elevFloor = lift.getCurrentFloor();
        int elevY = 20 + (floors - elevFloor - 1) * FLOOR_HEIGHT;
        
        g.setColor(Color.BLUE);
        g.fillRect(shaftX, elevY + 5, ELEVATOR_WIDTH, FLOOR_HEIGHT - 10);
        g.setColor(Color.WHITE);
        g.drawRect(shaftX, elevY + 5, ELEVATOR_WIDTH, FLOOR_HEIGHT - 10);
        
        // display elevator info
        g.setColor(Color.WHITE);
        g.drawString("Floor: " + elevFloor, shaftX + 10, elevY + 30);
        
        // draw arrow for direction
        if(lift.isGoingUp()) {
            g.drawString("▲", shaftX + ELEVATOR_WIDTH - 20, elevY + 30);
        } else {
            g.drawString("▼", shaftX + ELEVATOR_WIDTH - 20, elevY + 30);
        }
    }
    
    // main method to start the GUI
    public static void main(String[] args) {
        // try to make it look like the OS
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) { 
            // just ignore if we can't set the look
        }
        
        // create the GUI
        new ElevatorGUI();
    }
}
