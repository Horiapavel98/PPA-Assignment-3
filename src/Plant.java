import java.util.Random;
import java.util.List;
import java.util.ArrayList;
/**
 * Abstract class Plant - Implements the abstract behaviour of 
 * the plants in this simulations.
 *
 * @author Luca-Dorin Anton and Horia Pavel
 * @version v1.0
 */
public abstract class Plant extends Actor
{
    // How much food a plant provides.
    private int foodValue;
    // Constants for providing the food value in a random way.
    private static final double LOWER_FOOD_VALUE = 0.05;
    private static final double HIGHER_FOOD_VALUE = 0.95;

    private static final Random rand = Randomizer.getRandom();
    // If the plant is alive or not.
    private boolean isAlive;

    /**
     * Abstract constructor for Plant objects:
     * @param field - the current field.
     * @param location - the location of the plant.
     * @param sim - the simulator that operates this plant actor.
     */
    public Plant(Field field, Location location, Simulator sim) {
        super(field,location, sim);
        this.foodValue = getFoodValue();
        isAlive = true;
        if (rand.nextDouble() <= LOWER_FOOD_VALUE && foodValue >= 1) {
            foodValue --;
        }else if(rand.nextDouble() >= HIGHER_FOOD_VALUE) {
            foodValue ++;
        }
    }

    /**
     * Abstract method:
     * Returns the food value of the plant defined in specific classes.
     * 
     * Each plant may have a different food value.
     */
    abstract public int getFoodValue();

    /**
     * Set the state of the plant to dead.
     */
    public void setDead() {
        isAlive = false;
        if (super.getLocation() != null)  {
            super.removeFromField();
        }
    }

    /**
     * Abstract method:
     * Returns the habitat of the plant defined in specific classes to see if the 
     * plant is in the correct habitat.
     * @param l - the current location of the plant.
     * @return - boolean - true if the plant is in habitat / false if not.
     */
    abstract protected boolean isInHabitat(Location loc);

    /**
     * Abstract method implemented:
     * The plants will grow as defined in the abstract method grow.
     * @param newActors - the new plants to be displayed.
     */
    public void act(List<Actor> newActors) {
        if(!isInHabitat(super.getLocation())) {
         setDead();
         return;
        }
        DayNightCycle cycle = super.getSim().getDayNightCycle();
        if(cycle.getPartOfDay() == DayNightCycle.DAY) {
            List<Plant> newPlants = new ArrayList<Plant>();
            grow(newPlants);
            for (Plant plant : newPlants) {
                newActors.add(plant);
            }
        }
    }

    /**
     * Abstract method:
     * 
     * Implements the growth of the plant. As our plants have different behaviours
     * when it comes to growing this method is meant to be abstract.
     *
     * @param newPlants - the new plants that will grow at the next step in simulation.
     */
    abstract protected void grow(List<Plant> newPlants);
    
    /**
     * Checks if the plant is alive or not.
     * @return boolean - true if the plant is alive / false if not.
     */
    public boolean isAlive() {
        return isAlive;
    }
}
