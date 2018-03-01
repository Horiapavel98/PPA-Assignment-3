import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
/**
 * A graphical view of the simulation grid.
 * The view displays a colored rectangle for each location 
 * representing its contents. It uses a default background color.
 * Colors for each type of species can be defined using the
 * setColor method.
 * 
 * @author David J. Barnes and Michael Kölling
 *                  extension by Luca-Dorin Anton and Horia Pavel
 * @version 2016.02.29
 */
public class SimulatorView extends JFrame
{
    // Colors used for empty locations.
    private static final Color EMPTY_COLOR = Color.white;

    // Color used for objects that have no defined color.
    private static final Color UNKNOWN_COLOR = Color.gray;
    // Ocean colors
    private static final Color SURFACE_COLOR = new Color(128,179,255);
    private static final Color OCEAN_COLOR = new Color(0,61,153);
    // Sky colors.
    private static final Color MIDNIGHT_COLOR = new Color(7, 26, 76);
    private static final Color MORNING_COLOR = new Color(237,181,28);
    private static final Color NOON_COLOR = new Color(226, 253,255);
    private static final Color EVENING_COLOR = new Color(226,68,0);
    // The water surface level when the weather is normal.
    private static final int NORMAL_WATER_SURFACE_LEVEL = 5;
    // Defining the labels along with the prefixes.
    private final String STEP_PREFIX = "Step: ";
    private final String TIME_PREFIX = "Time: ";
    private final String WEATHER_PREFIX = "Weather: ";
    private final String DAY_PREFIX = "Day No.: ";
    private JLabel stepLabel, statusLabel, infoLabel;
    
    private JPanel statsPane; // Status pane in the right of the simulator.
    
    private FieldView fieldView;

    private Map<Class, JTextArea> labelMap;

    private Simulator simulator;

    // A map for storing colors for participants in the simulation
    private Map<Class, Color> colors;
    // A statistics object computing and storing simulation information
    private FieldStats stats;

    /**
     * Create a view of the given width and height.
     * For each operation a new thread is created in order to display it nicely.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     * @param sim Simulator - the simulator object reference
     */
    public SimulatorView(int height, int width, Simulator simulator)
    {
        // Initialize the functional needs of this class.
        this.simulator = simulator;
        stats = new FieldStats();
        colors = new LinkedHashMap<>();
        labelMap = new HashMap<>();

        // Set the title and initialize the default status of the labels.
        setTitle("The Deep Ocean Simulation");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        infoLabel = new JLabel("  ", JLabel.CENTER);
        statusLabel = new JLabel(" | " + TIME_PREFIX + " | " + WEATHER_PREFIX + " | " + DAY_PREFIX + " | " + " | ", JLabel.CENTER);

        fieldView = new FieldView(height, width);

        // Create the main panel.
        Container contents = getContentPane();

        JPanel infoPane = new JPanel(new BorderLayout());

        // Create the menu pane for placing the buttons.
        JPanel menuPane = new JPanel();
        menuPane.setLayout(new FlowLayout());
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new GridLayout(0,1,15,12));
        menuPane.setBorder(new EmptyBorder(6, 6, 6, 12));

        /* Creating the buttons along with their functionality 
        and adding each of them to the buttonPane. */
        JButton simulateOneStepButton = new JButton("Simulate one step");
        simulateOneStepButton.addActionListener(e -> {
                simulator.stopSimulation(); 
                delay(50); // stop the simulation, delay a bit and then create a new thread.
                SimulatorThread simTh = new SimulatorThread(new SimulatorRunnable(simulator));
                simTh.setSteps(1); // input the number of steps needed and ...
                simTh.start();  // .. start 
            });
        buttonPane.add(simulateOneStepButton);

        JButton simulate50StepsButton = new JButton("Simulate 50 steps");
        simulate50StepsButton.addActionListener(e -> {
                simulator.stopSimulation();
                delay(50);// stop the simulation, delay a bit and then create a new thread.
                SimulatorThread simTh = new SimulatorThread(new SimulatorRunnable(simulator));
                simTh.setSteps(50);// input the number of steps needed and ...
                simTh.start();  // .. start
            });
        buttonPane.add(simulate50StepsButton);
        buttonPane.setAlignmentY(Component.CENTER_ALIGNMENT);
        JButton simulate4000StepsButton = new JButton("Simulate 4000 steps");
        simulate4000StepsButton.addActionListener(e -> {
                simulator.stopSimulation();
                delay(50);// stop the simulation, delay a bit and then create a new thread.
                SimulatorThread simTh = new SimulatorThread(new SimulatorRunnable(simulator));
                simTh.setSteps(4000);   // input the number of steps needed and ...
                simTh.start();  // .. start
            });
        buttonPane.add(simulate4000StepsButton);

        JLabel stepsLabel = new JLabel("Input the number of steps ...");
        buttonPane.add(stepsLabel);

        JTextField textInput = new JTextField();
        textInput.setSize(30, 1);
        buttonPane.add(textInput);

        JButton submitSteps = new JButton("Simulate steps");
        submitSteps.addActionListener(e -> {
                try {
                    int nrOfSteps = Integer.parseInt(textInput.getText().toString());
                    simulator.stopSimulation();
                    delay(50);// stop the simulation, delay a bit and then create a new thread.
                    SimulatorThread simTh = new SimulatorThread(new SimulatorRunnable(simulator));
                    simTh.setSteps(nrOfSteps);  // input the number of steps needed and ...
                    simTh.start();  // .. start
                    submitSteps.setText("Simulate steps!");
                } catch (Exception ex) {
                    submitSteps.setText("Invalid number!");
                }
            });
        buttonPane.add(submitSteps);

        JButton stopSimulationButton = new JButton("Stop simulation");
        stopSimulationButton.addActionListener(e -> simulator.stopSimulation());
        buttonPane.add(stopSimulationButton);

        JButton resetButton = new JButton("Reset Simulation");
        resetButton.addActionListener(e -> {
                simulator.stopSimulation();
                delay(50); // delay for 50 miliseconds and reset the simulator.
                simulator.reset();
            });
        buttonPane.add(resetButton);

        menuPane.add(buttonPane); // the button pane to the menu pane.
        
        /* Create the stats pane on the right of the screen.
           This pane will display each specie with it's number.*/
        JPanel statsFlow = new JPanel();
        statsFlow.setLayout(new FlowLayout());
        statsPane = new JPanel();
        statsPane.setLayout(new GridLayout(0,1,15,12));
        statsPane.setBorder(new EmptyBorder(2, 2, 2, 2));
        statsFlow.add(statsPane);
        JScrollPane scrollPane = new JScrollPane(statsFlow);

        // Add all the components to the main pane.
        infoPane.add(stepLabel, BorderLayout.WEST);
        infoPane.add(infoLabel, BorderLayout.CENTER);
        contents.add(infoPane, BorderLayout.NORTH);
        contents.add(fieldView, BorderLayout.CENTER);
        contents.add(statusLabel, BorderLayout.SOUTH);
        contents.add(scrollPane, BorderLayout.EAST);
        contents.add(menuPane, BorderLayout.LINE_START);

        // Set location, prepare and set visible.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocation(100, 50);
        pack();
        setVisible(true);
    }

    /**
     * Delay method for giving time to threads to overlap.
     */
    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }

    /**
     * Define a color to be used for a given class of animal.
     * @param animalClass The animal's Class object.
     * @param color The color to be used for the given class.
     */
    public void setColor(Class animalClass, Color color)
    {
        colors.put(animalClass, color);
    }

    /**
     * Display a short information label at the top of the window.
     */
    public void setInfoText(String text)
    {
        infoLabel.setText(text);
    }

    /**
     * @return The color to be used for a given class of animal.
     */
    private Color getColor(Class animalClass)
    {
        Color col = colors.get(animalClass);
        if(col == null) {
            // no color defined for this class
            return UNKNOWN_COLOR;
        }
        else {
            return col;
        }
    }

    /**
     * Show the current status of the field.
     * @param step Which iteration step it is.
     * @param field The field whose status is to be displayed.
     */
    public void showStatus(int step, Field field)
    {
        if(!isVisible()) {
            setVisible(true);
        }

        stepLabel.setText(STEP_PREFIX + step);
        stats.reset();

        fieldView.preparePaint();

        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Object animal = field.getObjectAt(row, col);
                if(animal != null) {
                    stats.incrementCount(animal.getClass());
                    fieldView.drawMark(col, row, getColor(animal.getClass()));
                }
                else {
                    fieldView.drawMark(col, row, getBackgroundColor(row,field));
                }
            }
        }
        stats.countFinished();
        // Creating labels.
        if(labelMap.isEmpty()) {
            createLabels();
        } else {
            updateLabels();
        }
        String weather; // Creating weather text.
        if(simulator.getCurrentWeather() == Weather.DRY) {
            weather = "DRY";
        } else if(simulator.getCurrentWeather() == Weather.NORMAL) {
            weather = "NORMAL";
        } else {
            weather = "RAINING";
        }

        String partOfDay; // Creating partOfDay text.
        if(simulator.getDayNightCycle().getPartOfDay() == DayNightCycle.DAY) {
            partOfDay = "~Day~";
        } else {
            partOfDay = "~Night~";
        }
        // Setting up the bottom label.
        statusLabel.setText(" | " + TIME_PREFIX + simulator.getDayNightCycle().getCurrentTimeString() +  
            " | " + WEATHER_PREFIX + weather + " | " + DAY_PREFIX + simulator.getDayNightCycle().getCurrentDay()
            +" | " + partOfDay + " | ");
        fieldView.repaint();
    }

    /**
     * Private method for creating labels and initialize them with the correct values.
     */
    private void createLabels() {
        labelMap.clear();
        statsPane.removeAll();

        Map<Class, Counter> map = stats.getFieldStats(simulator.getField());
        for (Class key : map.keySet()) {
            Counter c = map.get(key);
            JTextArea label = new JTextArea(" " + c.getName() + "\n---------------\n" + c.getCount());
            label.setEditable(false);  
            label.setCursor(null);  
            label.setOpaque(false);  
            label.setFocusable(false);
            label.setLineWrap(true);
            label.setWrapStyleWord(true);

            labelMap.put(key, label);
            statsPane.add(label);
            
            // Using borders to facilitate their display.
            // For instance if a certaint specie falls below 500 population the border will
            // be orange.
            if(c.getCount() == 0) {
                label.setBorder(BorderFactory.createMatteBorder(5,5,5,5,Color.BLACK));
            } else if (c.getCount() <= 99) {
                label.setBorder(BorderFactory.createMatteBorder(5,5,5,5,Color.RED));
            } else if(c.getCount() <= 499) {
                label.setBorder(BorderFactory.createMatteBorder(5,5,5,5,Color.ORANGE));
            } else {
                label.setBorder(BorderFactory.createMatteBorder(5,5,5,5,Color.GREEN));
            }
        }
    }

    /** 
     * Private method for updating the labels during the simulations.
     */
    private void updateLabels() {
        Map<Class, Counter> map = stats.getFieldStats(simulator.getField());
        if(map.size() != labelMap.size()) {
            createLabels(); // If a label shows up -- recreate the labels to display the new label.
        }

        for (Class key : map.keySet()) {
            Counter c = map.get(key);
            JTextArea t = labelMap.get(key);
            if(c != null) {
                t.setText(" " + c.getName() + "\n---------------\n" + c.getCount());
                if(c.getCount() == 0) {
                    t.setBorder(BorderFactory.createMatteBorder(5,5,5,5,Color.BLACK));
                } else if (c.getCount() <= 99) {
                    t.setBorder(BorderFactory.createMatteBorder(5,5,5,5,Color.RED));
                } else if(c.getCount() <= 499) {
                    t.setBorder(BorderFactory.createMatteBorder(5,5,5,5,Color.ORANGE));
                } else {
                    t.setBorder(BorderFactory.createMatteBorder(5,5,5,5,Color.GREEN));
                }
            } else {
                t.setText(" " + key.toString() + "\n---------------\n0");
                t.setBorder(BorderFactory.createMatteBorder(5,5,5,5,Color.BLACK));
            }

        }
    }
    
    /**
     * This method resets the status of the field.
     */
    public void resetStats() {
        stats = new FieldStats();   
    }

    /**
     * This method calculates the gradient of the background in order to display a transition between night and day
     * and between surface water level and deep water level.
     * @param row int - the row of the field to calculate the water color
     * @param field Field - the field reference.
     * @return Color - the color of the tile in the field.
     */
    private Color getBackgroundColor(int row, Field field) {
        int depth = field.getDepth(); // .. getting the depth.
        double rowd = (double) row;
        double gradientPercent = 1 - rowd/depth; // Calculating the gradient percent for the certain row.

        double waterSurfaceLevel = NORMAL_WATER_SURFACE_LEVEL + simulator.getCurrentWeather(); // Get the water surface level depending on the weather.
        int r,g,b;
        if (row < waterSurfaceLevel){
            int timeInt = simulator.getDayNightCycle().getCurrentTimeInt(); // Calculating the sky gradient depending on the time of day.
            int dayPhase = timeInt/(DayNightCycle.HOUR_LENGHT*6); // Different day phases are set to different colors - See constants defined above.
            if(dayPhase == 0) {
                double timeIntD = (double) timeInt;
                double timePercent = 1- (timeIntD/359);
                r = (int) Math.round(MIDNIGHT_COLOR.getRed()*timePercent + MORNING_COLOR.getRed()*(1-timePercent));
                g = (int) Math.round(MIDNIGHT_COLOR.getGreen()*timePercent + MORNING_COLOR.getGreen()*(1-timePercent));
                b = (int) Math.round(MIDNIGHT_COLOR.getBlue()*timePercent + MORNING_COLOR.getBlue()*(1-timePercent));
            } else if(dayPhase == 1){
                double timeIntD = (double) timeInt-360;
                double timePercent = 1- (timeIntD/359);
                r = (int) Math.round(MORNING_COLOR.getRed()*timePercent + NOON_COLOR.getRed()*(1-timePercent));
                g = (int) Math.round(MORNING_COLOR.getGreen()*timePercent + NOON_COLOR.getGreen()*(1-timePercent));
                b = (int) Math.round(MORNING_COLOR.getBlue()*timePercent + NOON_COLOR.getBlue()*(1-timePercent));
            } else if(dayPhase ==2) {
                double timeIntD = (double) timeInt-720;
                double timePercent = 1- (timeIntD/359);
                r = (int) Math.round(NOON_COLOR.getRed()*timePercent + EVENING_COLOR.getRed()*(1-timePercent));
                g = (int) Math.round(NOON_COLOR.getGreen()*timePercent + EVENING_COLOR.getGreen()*(1-timePercent));
                b = (int) Math.round(NOON_COLOR.getBlue()*timePercent + EVENING_COLOR.getBlue()*(1-timePercent));
            } else {
                double timeIntD = (double) timeInt-1080;
                double timePercent = 1- (timeIntD/359);
                r = (int) Math.round(EVENING_COLOR.getRed()*timePercent + MIDNIGHT_COLOR.getRed()*(1-timePercent));
                g = (int) Math.round(EVENING_COLOR.getGreen()*timePercent + MIDNIGHT_COLOR.getGreen()*(1-timePercent));
                b = (int) Math.round(EVENING_COLOR.getBlue()*timePercent + MIDNIGHT_COLOR.getBlue()*(1-timePercent));
            }
        }else {
            r = (int) Math.round(SURFACE_COLOR.getRed()*gradientPercent + OCEAN_COLOR.getRed()*(1-gradientPercent));
            g = (int) Math.round(SURFACE_COLOR.getGreen()*gradientPercent + OCEAN_COLOR.getGreen()*(1-gradientPercent));
            b = (int) Math.round(SURFACE_COLOR.getBlue()*gradientPercent + OCEAN_COLOR.getBlue()*(1-gradientPercent));
        }
        return new Color(r,g,b); // return the desired color.
    }

    /**
     * Determine whether the simulation should continue to run.
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        return stats.isViable(field);
    }

    /**
     * Provide a graphical view of a rectangular field. This is 
     * a nested class (a class defined inside a class) which
     * defines a custom component for the user interface. This
     * component displays the field.
     * This is rather advanced GUI stuff - you can ignore this 
     * for your project if you like.
     */
    private class FieldView extends JPanel
    {
        private final int GRID_VIEW_SCALING_FACTOR = 6;

        private int gridWidth, gridHeight;
        private int xScale, yScale;
        Dimension size;
        private Graphics g;
        private Image fieldImage;

        /**
         * Create a new FieldView component.
         */
        public FieldView(int height, int width)
        {
            gridHeight = height;
            gridWidth = width;
            size = new Dimension(0, 0);
        }

        /**
         * Tell the GUI manager how big we would like to be.
         */
        public Dimension getPreferredSize()
        {
            return new Dimension(gridWidth * GRID_VIEW_SCALING_FACTOR,
                gridHeight * GRID_VIEW_SCALING_FACTOR);
        }

        /**
         * Prepare for a new round of painting. Since the component
         * may be resized, compute the scaling factor again.
         */
        public void preparePaint()
        {
            if(! size.equals(getSize())) {  // if the size has changed...
                size = getSize();
                fieldImage = fieldView.createImage(size.width, size.height);
                g = fieldImage.getGraphics();

                xScale = size.width / gridWidth;
                if(xScale < 1) {
                    xScale = GRID_VIEW_SCALING_FACTOR;
                }
                yScale = size.height / gridHeight;
                if(yScale < 1) {
                    yScale = GRID_VIEW_SCALING_FACTOR;
                }
            }
        }

        /**
         * Paint on grid location on this field in a given color.
         */
        public void drawMark(int x, int y, Color color)
        {
            g.setColor(color);
            g.fillRect(x * xScale, y * yScale, xScale-1, yScale-1);
        }

        /**
         * The field view component needs to be redisplayed. Copy the
         * internal image to screen.
         */
        public void paintComponent(Graphics g)
        {
            if(fieldImage != null) {
                Dimension currentSize = getSize();
                if(size.equals(currentSize)) {
                    g.drawImage(fieldImage, 0, 0, null);
                }
                else {
                    // Rescale the previous image.
                    g.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
                }
            }
        }
    }
}
