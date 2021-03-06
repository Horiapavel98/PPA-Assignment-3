import java.awt.Color;
/**
 * This class implements the behaviour of a Anchovy.
 * It has constants to define this behaviour and getter methods
 * to use them in the superclasses.
 *
 * @author Luca-Dorin Anton and Horia Pavel
 * @version v1.0
 */
public class Anchovy extends Prey
{
    // Each concrete class has a color.
    private static final Color COLOR = new Color(201, 35, 164);
    // Each Prey has a specific food value.
    private static final int MAX_FOOD_VALUE = 90;
    // Each animal has a breeding age
    private static final int BREEDING_AGE = 380;
    // How many young can give birth to.
    private static final int MAX_SPAWN_SIZE = 3;
    // The change to have live young.    
    private static final double BREEDING_PROBABILITY = 0.84;
    // Defines minimum amount of blocks that this entity can get to (by going up).
    private static final int MIN_HABITAT_HEIGHT = 5;
    // Defines maximum amount of blocks that this entity can get to (by going down).
    private static final double MAX_HABITAT_HEIGHT = 0.5;
    // How many steps this entity can live.
    private static final int MAX_AGE =1440;
    // How much food value can absorb.
    private static final int MAX_FOOD_LEVEL = 720;

    /**
     * Constructor for Anchovy objects
     * @param field - the current field.
     * @param location - the location of the anchovy.
     * @param sim - the simulator that operates this anchovy actor.
     */
    public Anchovy(boolean randomAge, Field field, Location loc, Simulator sim) {
        super(randomAge,field,loc, sim);
    }

    /**
     * Returns the food value of this enitity.
     * @return int - the food value of this entity.
     */
    public int getFoodValue() {
        return MAX_FOOD_VALUE;
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
     * Returns the maximum food level of the entity.
     * @return int - the maximum food level.
     */
    protected int getMaxFoodLevel() {
        return MAX_FOOD_LEVEL;
    }

    /**
     * Returns the breeding age of this entity.
     * @return int - the breeding age.
     */
    protected int getBreedingAge() {
        return BREEDING_AGE;
    }

    /**
     * Returns the maximum spawn size of this entity.
     * @return int - the maximum number of live young this entity can give birth to.
     */
    protected int getMaxSpawnSize() {
        return MAX_SPAWN_SIZE;
    }

    /**
     * Returns the breeding probability of this entity.
     * @return double - the probability as a double.
     */
    protected double getBreedingProbability() {
        return BREEDING_PROBABILITY;
    }

    /**
     * Returns the new born animal for after breeding.
     * @param field Field - the field of the entity.
     * @param loc Location - the location of the entity.
     * @return Animal - the new born animal.
     */
    protected Animal getNewAnimal(Field field, Location loc) {
        return new Anchovy(false, field, loc, super.getSim());
    }

    /**
     * Returns the maximum age of the entity.
     * @return int - the maximum age of this entity.
     */
    protected int getMaxAge() {
        return MAX_AGE;
    }

    /**
     * This method is used to check if an actor is in it's habitat at every step.
     * The habitat depends on the weather - because the sea level may vary.
     * @param l Location - the location of the actor.
     * @return boolean - true if it is / false if not.
     */
    protected boolean isInHabitat(Location l) {
        int fieldDepth = super.getField().getDepth();
        int weatherNow = super.getSim().getCurrentWeather();
        int minHabitatCells = MIN_HABITAT_HEIGHT + weatherNow - 1;
        int maxHabitatCells = (int) Math.round(MAX_HABITAT_HEIGHT*fieldDepth);
        int locationDepth = l.getRow();

        if(minHabitatCells < locationDepth && locationDepth < maxHabitatCells) {
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
        int fieldDepth = sim.getField().getDepth();
        int weatherNow = sim.getCurrentWeather();
        int minHabitatCells = MIN_HABITAT_HEIGHT + weatherNow - 1;
        int maxHabitatCells = (int) Math.round(MAX_HABITAT_HEIGHT*fieldDepth);
        int locationDepth = l.getRow();

        if(minHabitatCells < locationDepth && locationDepth < maxHabitatCells) {
            return true;
        } else {
            return false;
        }
    }
}
