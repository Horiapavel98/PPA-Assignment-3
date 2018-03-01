import java.util.List;
import java.awt.Color;
import java.util.Random;
/**
 * Algae is a specific class implementing the plant behaviour.
 * It can spring up from soil and it grows up to a certain limit.
 *
 * @author Luca-Dorin Anton and Horia Pavel
 * @version v1.0
 */
public class Algae extends Plant
{
    // Each specific class has a color.
    private static final Color COLOR = new Color(3, 150, 26);
    // How much food this plant provides
    private static final int FOOD_VALUE = 70;
    // The chance of spawning/growing.
    private static final double SPAWN_ALGAE_CHANCE = 0.0015;
    // It can go up until this threshhold
    private static final int MIN_HABITAT_HEIGHT = 20;

    private static final Random rand = Randomizer.getRandom();
    
    /**
     * Constructor for Algae objects
     * @param field - the current field.
     * @param location - the location of the plant.
     * @param sim - the simulator that operates this plant actor.
     */
    public Algae(Field field, Location location, Simulator sim) {
        super(field, location, sim);
    }

    /**
     * Abstract method implemented:
     * 
     * Plants can grow and thus they 'produce' another plants.
     * In particular Algae can grow only upwards.
     * 
     * @param newPlants List<Plant> the new plants that grow from this one.
     */
    protected void grow(List<Plant> newPlants) {
        if (isConnectedToGround(super.getLocation())) {// If it has any connection to ground (Soil).
            List<Location> freeLocations = super.getField().getFreeAdjacentLocations(super.getLocation());
            for(Location l : freeLocations) {
                if (l.getRow() + 1 == super.getLocation().getRow() && rand.nextDouble() <= SPAWN_ALGAE_CHANCE // If there is enough space and it is in habitat ...
                    && l.getCol() == super.getLocation().getCol() && isInHabitat(l)) {          
                    newPlants.add(new Algae(super.getField(),l, super.getSim()));   /// Then grow there.
                }
            }
        }
    }
    
    /**
     * Firstly, before growing a plant checks if it is still connected to ground so it has the ability to grow.
     * This is done with this recursive method. Algae looks down recursively to check if it is conected to ground.
     * 
     * @param currentLocation Location - the current location of the Plant.
     */
    private boolean isConnectedToGround(Location currentLocation) {
        Location newLocation = new Location(currentLocation.getRow() + 1,
                            currentLocation.getCol());
        Object below = super.getField()
                            .getObjectAt(newLocation);
        if (below instanceof Algae) {
            return isConnectedToGround(newLocation);
        }else {
            if (below instanceof Soil) {
                return true;
            }else {
                return false;
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
        int minHabitatCells = MIN_HABITAT_HEIGHT + weatherNow;
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
