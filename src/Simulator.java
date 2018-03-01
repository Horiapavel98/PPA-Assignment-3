import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 200;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 120;

    private static final double SEAGULL_CREATION_PROBABILITY = 0.055;
    private static final double SHARK_CREATION_PROBABILITY = 0.0025;
    private static final double KILLERWHALE_CREATION_PROBABILITY = 0.0015;
    private static final double COD_CREATION_PROBABILITY = 0.04;
    private static final double MACKEREL_CREATION_PROBABILITY = 0.03;
    private static final double ANCHOVY_CREATION_PROBABILITY = 0.06;
    private static final double ALGAE_CREATION_PROBABILITY = 0.4;
    private static final double PLANKTON_CREATION_PROBABILITY = 0.2;

    private static final double ROCK_CREATION_PROBABILITY = 0.5;
    private static final double SOIL_CREATION_PROBABILITY = 0.5;

    private static final int MAX_TERRAIN_HEIGHT = 5;

    private boolean testing = false;

    // List of animals in the field.
    private List<Actor> actors;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    
    private DayNightCycle dnCycle;

    private boolean continueSimulating;
    
    private Weather weather;
    
    private int currentWeather;

    private static final Random rand = Randomizer.getRandom();

    public static void main(String[] args) {
        new Simulator();
    }
    
    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }

    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        actors = new ArrayList<>();
        field = new Field(depth, width);
        dnCycle = new DayNightCycle(this);
        weather = new Weather();
        continueSimulating = false;
        currentWeather = Weather.NORMAL;

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width, this);
        view.setColor(Seagull.class,Seagull.getClassColor());
        view.setColor(Shark.class,Shark.getClassColor());
        view.setColor(KillerWhale.class,KillerWhale.getClassColor());
        view.setColor(Cod.class,Cod.getClassColor());
        view.setColor(Mackerel.class,Mackerel.getClassColor());
        view.setColor(Anchovy.class,Anchovy.getClassColor());
        view.setColor(Algae.class,Algae.getClassColor());
        view.setColor(Plankton.class,Plankton.getClassColor());
        view.setColor(Rock.class,Rock.getClassColor());
        view.setColor(Soil.class,Soil.getClassColor());

        // Setup a valid starting point.
        reset();
    }

    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(4000);
    }
    
    public int getStep() {
        return step;
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        startSimulation();
        for(int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
            if(!continueSimulating) {
                step = numSteps + 2;
            }

            // delay(60);   // uncomment this to run more slowly
        }
    }

    private void startSimulation() {
        continueSimulating = true;
    }   

    public void stopSimulation() {
        continueSimulating = false;
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * fox and rabbit.
     */
    public void simulateOneStep()
    {
        step++;
        
        currentWeather = weather.getWeather();
        
        //System.out.println(weather.timeTillDecision + "- " + weatherNow);
        
        // Provide space for newborn animals.
        List<Actor> newActors = new ArrayList<>();        
        // Let all rabbits act.
        //System.out.println(dnCycle.getPartOfDay());
        for(Iterator<Actor> it = actors.iterator(); it.hasNext(); ) {
            if(!continueSimulating) {
                break;
            }
            Actor actor = it.next();
            if(actor instanceof Animal) {
                Animal animal = (Animal) actor;
                if(!animal.isAlive()) {
                    it.remove();
                }else {
                    actor.act(newActors);
                }
            }else if ( actor instanceof Plant ) {
                Plant plant = (Plant) actor;
                if ( !plant.isAlive() ) {
                    it.remove();
                }else {
                    actor.act(newActors);
                }
            }else {
                actor.act(newActors);
            }
        }

        // Add the newly born foxes and rabbits to the main lists.
        actors.addAll(newActors);

        view.showStatus(step, field);
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        actors.clear();
        field = new Field(field.getDepth(), field.getWidth());
        continueSimulating = false;
        populate(testing);
        view.resetStats();
        //System.out.println(field.getObjectAt(2, 2).toString());
        // Show the starting state in the view.
        view.showStatus(step, field);
    }

    private void generateTerrain() {
        for (int col = 0 ; col < field.getWidth() ; col ++ ) {
            int colHeight = rand.nextInt(MAX_TERRAIN_HEIGHT) + 1;
            for ( int row = field.getDepth() - 1 ; row >= field.getDepth() - colHeight ; row -- ) {
                if (rand.nextDouble() <= ROCK_CREATION_PROBABILITY) {
                    Location location = new Location(row,col);
                    Rock rock = new Rock(field,location, this);
                    actors.add(rock);
                }else {
                    Location location = new Location(row,col);
                    Soil soil = new Soil(field,location, this);
                    actors.add(soil);
                }
            }
        }
    }

    private void populate(boolean testing) {
        if (testing) {
            //generateTerrain();
            
        }else {
            populate();
        }
    }

    /**
     * Randomly populate the field with foxes and rabbits.
     */
    private void populate()
    {
        generateTerrain();

        for (int row = 0 ; row < field.getDepth() ; row ++ ) {
            for (int col = 0 ; col < field.getWidth() ; col ++ ) {
                if ( field.getObjectAt(row,col) == null ) {
                    Location currentLocation = new Location(row,col);
                    int depth = field.getDepth();
                    Actor actor = null;
                    if ( rand.nextDouble() <= KILLERWHALE_CREATION_PROBABILITY && KillerWhale.isInHabitatStatic(currentLocation,this)){
                        actor = new KillerWhale(true,field,currentLocation, this);
                    }else if ( rand.nextDouble() <= SHARK_CREATION_PROBABILITY && Shark.isInHabitatStatic(currentLocation,this)) {
                        actor = new Shark(true,field,currentLocation, this);
                    }else if ( rand.nextDouble() <= SEAGULL_CREATION_PROBABILITY && Seagull.isInHabitatStatic(currentLocation,this)) {
                        actor = new Seagull(true,field,currentLocation, this);
                    }else if ( rand.nextDouble() <= COD_CREATION_PROBABILITY && Cod.isInHabitatStatic(currentLocation,this)) {
                        actor = new Cod(true,field,currentLocation, this);
                    }else if ( rand.nextDouble() <= MACKEREL_CREATION_PROBABILITY && Mackerel.isInHabitatStatic(currentLocation,this)) {
                        actor = new Mackerel(true,field,currentLocation, this);
                    }else if ( rand.nextDouble() <= PLANKTON_CREATION_PROBABILITY && Plankton.isInHabitatStatic(currentLocation,this)) {
                        actor = new Plankton(field,currentLocation, this);
                    }else if ( rand.nextDouble() <= ANCHOVY_CREATION_PROBABILITY && Anchovy.isInHabitatStatic(currentLocation,this)) {
                        actor = new Anchovy(true,field,currentLocation, this); // Ok
                    }
                    if ( actor != null ) {
                        actors.add(actor);
                    }
                }
            }
        }
    }

    /**
     * Pause for a given time.
     * @param millisec  The time to pause for, in milliseconds
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
     * Returns the DayNightCycle object.
     * @return DayNightCycle object.
     */
    public DayNightCycle getDayNightCycle() {
        return dnCycle;
    }
    
    /**
     * Returns the current weather.
     * @return int - currentWeather - the weather constant to be returned.
     */
    public int getCurrentWeather() {
        return currentWeather;
    }
    
    /**
     * Returns the field of the simulator.
     * @return field Field - the field to be returned.
     */
    public Field getField() {
        return field;
    }
}
