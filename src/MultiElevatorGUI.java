import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * GUI for multiple elevators
 */
public class MultiElevatorGUI extends JFrame {
    // UI components
    JPanel mainPanel;
    JPanel buildingView;
    JButton startBtn;
    JButton stepBtn;
    JButton resetBtn;
    JComboBox numElevsBox;
    JLabel statusLbl;
    
    // simulation variables
    MultiLiftBuilding myBuilding;
    MultiLiftAlgorithm currentAlgo;
    boolean isRunning = false;
    Timer timer;
    int steps = 0;
    
    // constants for drawing
    int FLOOR_HEIGHT = 50;  // pixels per floor
    int ELEVATOR_WIDTH = 50;
    
    // random colors for elevators
    Color[] elevatorColors = {
        new Color(65, 105, 225),  // royal blue
        new Color(220, 20, 60),   // crimson
        new Color(34, 139, 34),   // forest green
        new Color(255, 140, 0),   // dark orange
        new Color(138, 43, 226)   // blue violet
    };
    
    // constructor
    public MultiElevatorGUI() {
        super("Multi-Elevator Simulator");
        
        // setup window
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // make the main panel with border layout
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        // control panel at top
        JPanel controls = new JPanel();
        controls.setLayout(new FlowLayout());
        
        // elevator count selector
        JLabel numElevsLbl = new JLabel("Number of Elevators:");
        controls.add(numElevsLbl);
        
        String[] elevCounts = {"2", "3", "4", "5"};
        numElevsBox = new JComboBox(elevCounts);
        controls.add(numElevsBox);
        
        // control buttons
        startBtn = new JButton("Start");
        stepBtn = new JButton("Step");
        resetBtn = new JButton("Reset");
        
        controls.add(startBtn);
        controls.add(stepBtn);
        controls.add(resetBtn);
        
        // add controls to main panel
        mainPanel.add(controls, BorderLayout.NORTH);
        
        // building view in center - using anonymous inner class to override paintComponent
        buildingView = new JPanel() {
            // drawing function
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBuilding(g);
            }
        };
        buildingView.setBackground(Color.WHITE);
        
        // add building view to main panel
        mainPanel.add(buildingView, BorderLayout.CENTER);
        
        // status label at bottom
        statusLbl = new JLabel("Select options and press Start");
        mainPanel.add(statusLbl, BorderLayout.SOUTH);
        
        // add panel to frame
        add(mainPanel);
        
        // event handlers
        startBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(isRunning) {
                    stopSimulation();
                } else {
                    startSimulation();
                }
            }
        });
        
        stepBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runOneStep();
            }
        });
        
        resetBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetSimulation();
            }
        });
        
        // timer for animation - runs step every second
        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(isRunning) {
                    runOneStep();
                }
            }
        });
        
        // make window visible
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
        // get selected elevator count
        int numElevators = Integer.parseInt((String)numElevsBox.getSelectedItem());
        
        // load from input.txt
        myBuilding = MultiLiftBuilding.FromFile("input.txt", numElevators);
        steps = 0;
        
        // show message
        statusLbl.setText("Loaded building with " + 
                myBuilding.getFloors().GetFloors().size() + " floors and " +
                numElevators + " elevators");
        
        // redraw
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
        
        // create algorithm
        try {
            currentAlgo = new MultiLiftLook(myBuilding);
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Error creating algorithm: " + ex.getMessage());
            return;
        }
        
        // update UI state
        isRunning = true;
        startBtn.setText("Stop");
        numElevsBox.setEnabled(false);
        
        // start timer
        timer.start();
    }
    
    // stop the simulation
    private void stopSimulation() {
        isRunning = false;
        startBtn.setText("Start");
        timer.stop();
    }
    
    // reset simulation
    private void resetSimulation() {
        // stop if running
        if(isRunning) {
            stopSimulation();
        }
        
        // reset variables
        myBuilding = null;
        currentAlgo = null;
        steps = 0;
        
        // update UI
        numElevsBox.setEnabled(true);
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
            // run one step of algorithm
            myBuilding = currentAlgo.NextStep();
            steps++;
            
            // get status info - summarize elevator locations
            StringBuilder status = new StringBuilder("Step " + steps + ": ");
            for(int i = 0; i < myBuilding.getNumLifts(); i++) {
                LiftState lift = myBuilding.getLift(i);
                status.append("E").append(i).append("@").append(lift.getCurrentFloor());
                if(i < myBuilding.getNumLifts() - 1) {
                    status.append(", ");
                }
            }
            
            statusLbl.setText(status.toString());
            
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
                statusLbl.setText("All requests complete in " + steps + " steps!");
            }
            
            // update display
            repaint();
            
        } catch(Exception ex) {
            stopSimulation();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            statusLbl.setText("Error: " + ex.getMessage());
        }
    }
    
    // draw the building and elevators
    private void drawBuilding(Graphics g) {
        // check if building is loaded
        if(myBuilding == null) {
            g.setColor(Color.RED);
            g.drawString("No building loaded!", 100, 100);
            return;
        }
        
        // get building info
        int floors = myBuilding.getFloors().GetFloors().size();
        int numElevs = myBuilding.getNumLifts();
        
        // calculate dimensions
        int width = getWidth() - 40;
        int height = floors * FLOOR_HEIGHT;
        
        // draw building outline
        g.setColor(Color.BLACK);
        g.drawRect(20, 20, width, height);
        
        // draw floors
        for(int i = 0; i < floors; i++) {
            int y = 20 + (floors - i - 1) * FLOOR_HEIGHT;
            
            // floor line
            g.setColor(Color.GRAY);
            g.drawLine(20, y, 20 + width, y);
            
            // floor number
            g.setColor(Color.BLACK);
            g.drawString("Floor " + i, 25, y + FLOOR_HEIGHT - 10);
            
            // check for requests
            FloorState floor = myBuilding.getFloors().GetFloors().get(i);
            if(!floor.GetFloorRequests().isEmpty()) {
                // show requests as red circle with count
                g.setColor(Color.RED);
                g.fillOval(width - 30, y + 10, 20, 20);
                g.setColor(Color.WHITE);
                g.drawString("" + floor.GetFloorRequests().size(), width - 24, y + 24);
            }
        }
        
        // gap between elevator shafts
        int shaftGap = 10;
        int totalShaftWidth = (ELEVATOR_WIDTH + shaftGap) * numElevs;
        int startX = (width - totalShaftWidth) / 2;
        
        // draw each elevator shaft and elevator
        for(int i = 0; i < numElevs; i++) {
            int shaftX = startX + i * (ELEVATOR_WIDTH + shaftGap);
            
            // elevator shaft
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(shaftX, 20, ELEVATOR_WIDTH, height);
            
            // get elevator info
            LiftState lift = myBuilding.getLift(i);
            int elevFloor = lift.getCurrentFloor();
            int elevY = 20 + (floors - elevFloor - 1) * FLOOR_HEIGHT;
            
            // elevator car
            g.setColor(elevatorColors[i % elevatorColors.length]);
            g.fillRect(shaftX + 5, elevY + 10, ELEVATOR_WIDTH - 10, FLOOR_HEIGHT - 20);
            g.setColor(Color.WHITE);
            g.drawRect(shaftX + 5, elevY + 10, ELEVATOR_WIDTH - 10, FLOOR_HEIGHT - 20);
            
            // elevator number
            g.drawString("#" + i, shaftX + 15, elevY + 30);
            
            // direction arrow
            if(lift.isGoingUp()) {
                g.drawString("▲", shaftX + ELEVATOR_WIDTH - 15, elevY + 30);
            } else {
                g.drawString("▼", shaftX + ELEVATOR_WIDTH - 15, elevY + 30);
            }
        }
    }
    
    // main method
    public static void main(String[] args) {
        // try to set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {
            // ignore errors
        }
        
        // create GUI
        new MultiElevatorGUI();
    }
}
