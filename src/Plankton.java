import java.util.List;
import java.awt.Color;
import java.util.Random;
/**
 * This is the Plankton specific class
 * Plankton can grow diversely and randomly.
 *
 * @author Luca-Dorin Anton and Horia Pavel
 * @version v1.0
 */
public class Plankton extends Plant
{
    // Each concrete class has a color.
    private static final Color COLOR = new Color(81,251,106);
    // How much food this plant provides
    private static final int FOOD_VALUE = 65;
    // The chance of spawning/growing.
    private static final double SPAWN_PLANKTON_CHANCE = 0.0003;

    private static final Random rand = Randomizer.getRandom();
    // It can go up until this threshhold
    private static final int MIN_HABITAT_HEIGHT = 5;

    /**
     * Constructor for Plankton objects
     * @param field - the current field.
     * @param location - the location of the plant.
     * @param sim - the simulator that operates this plant actor.
     */
    public Plankton(Field field, Location location, Simulator sim) {
        super(field,location, sim);
    }

    /**
     * Abstract method implemented:
     * 
     * Plants can grow and thus they 'produce' another plants.
     * In particular Plankton can grow in all directions..
     * 
     * @param newPlants List<Plant> the new plants that grow from this one.
     */
    protected void grow(List<Plant> newPlants) {
        List<Location> freeLocations = super.getField().getFreeAdjacentLocations(super.getLocation());
        for(Location l : freeLocations) {
            double rowDouble = (double) l.getRow();
            if (rand.nextDouble() <= SPAWN_PLANKTON_CHANCE) {
                if(isInHabitat(l)) { // If it is in habitat ...
                    newPlants.add(new Plankton(super.getField(),l, super.getSim()));  // then grow ...
                }
            }
        }
    }

    /**
     * The simulator needs to know about the class color so it is provided here.
     * 
     * @return COLOR - the color constant of the class.
     */
    public static Color getClassColor(){
        return COLOR;
    }

    /**
     * Some animals may need to know about the food value of this plant so it is provided by this method.
     * 
     * @return FOOD_VALUE - the food value of this plant.
     */
    public int getFoodValue() {
        return FOOD_VALUE;
    }

    /**
     * This method is used to check if an actor is in it's habitat at every step.
     * The habitat depends on the weather - because the sea level may vary.
     * @param l Location - the location of the actor.
     * @return boolean - true if it is / false if not.
     */
    protected boolean isInHabitat(Location l) {
        int weatherNow = super.getSim().getCurrentWeather(); // The habitat depends on the weather - because the sea level may vary.
        int minHabitatCells = (int) MIN_HABITAT_HEIGHT + weatherNow;
        int locationDepth = l.getRow();

        if(minHabitatCells < locationDepth) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * This method is used only when the simulator is populated in the first place.
     * It checks whether a certain location is right for this actor to place.
     * The habitat depends on the weather - because the sea level may vary.
     * 
     * @param l Location - the location to be checked
     * @param sim Simulator - the simulator reference.
     * 
     * @return boolean true if it can be placed there / false if not.
     */
    public static boolean isInHabitatStatic(Location l, Simulator sim) {
        int weatherNow = sim.getCurrentWeather(); // The habitat depends on the weather - because the sea level may vary.
        int minHabitatCells = MIN_HABITAT_HEIGHT + weatherNow;
        int locationDepth = l.getRow();

        if(minHabitatCells < locationDepth ) {
            return true;
        } else {
            return false;
        }
    }
}
