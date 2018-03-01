import java.util.Queue;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
/**
 * Abstract class Prey - Implements the simple behaviour of the prey in the simulation.
 * 
 * The main pruposes of the prey are to find food and breed. They also might change their
 * habitat based on the weather.
 *
 * @author Luca-Dorin Anton and Horia Pavel
 * @version v1.0
 */
public abstract class Prey extends Animal
{
    private static final Random rand = Randomizer.getRandom();
    
    /**
     * Abstract constructor for Prey type objects
     * @param randomAge boolean - Set the new born age to 0 if false / otherwise set random age for initial animals in the simulation.
     * @param field Field the field of the Prey obj
     * @param location Location - the location of the Prey object.
     * @param sim Simulator - the simulator that operates this object.
     */
    public Prey(boolean randomAge, Field field, Location location, Simulator sim) {
        super(randomAge,field,location, sim);
    }

    /**
     * Abstract method implemented:
     * 
     * This method implements the main functionality of the actor: Prey
     * The animal looks near itself and if it finds food (Plants) it will go in that direction and eat it.
     * 
     * If the animal is not it it's habitat it will die.
     * 
     * The animal will give birth when possible.
     * 
     * @param newActors List<Actor> the new born that are added to the simulator after this step.
     */
    public void act(List<Actor> newActors) {
        
        if (!isInHabitat(super.getLocation()) ) {
            setDead();
            return;
        }
        
        DayNightCycle cycle = super.getSim().getDayNightCycle();
        
        incrementAge();
        if(cycle.getPartOfDay() == DayNightCycle.DAY || // If it is night it has 50% percent chance to move
        rand.nextDouble() >= 0.5 ||                       
        super.getFoodLevel() <= getMaxFoodLevel()/4) {  // It searches for food only if the food level is below 25%.
            incrementHunger();
            if(isAlive()){
                Location nextLocation = null;
                if ( super.getFoodLevel() < getMaxFoodLevel()/2 ){
                    nextLocation = findFood(); // The animal searches for food in it's vicinity.
                }  
                if (nextLocation == null) { // If it doesn't find any food it will go randomly checking if the location is in habitat.
                    List<Location> randomAdjLoc = super.getField().getFreeAdjacentLocations(getLocation());
                    for (Location l : randomAdjLoc) {
                        if ( isInHabitat(l) ) {
                            nextLocation = l;
                            break;
                        }
                    }
                }

                if (nextLocation == null) { // This means that the animal is no longer in the habitat so it should die.
                    setDead();
                }else { // Otherwise the animal made a right move so it should breed if it has proper conditions.
                    List<Animal> newBorn = giveBirth();
                    for (Animal a : newBorn) {
                        newActors.add(a);
                    }
                    setLocation(nextLocation); // The animal changes it's location.
                }
            }
        }
    }

    /**
     * Abstract method:
     * 
     * Returns the maximum food level of the animal defined in specific classes.
     * 
     * @return int - the maximum food level of the animal
     */
    @Override
    abstract protected int getMaxFoodLevel();

    /**
     * Abstract method:
     * 
     * Returns the food value of the animal (the prey can be eaten by predators) defined below in the specific classes.
     * 
     * @param foodValue int - the food value of the animal.
     */
    abstract public int getFoodValue();

    /**
     * This method implements food seeking ability that animal have. Though it is not as complex as predators'
     * - the predators only search for food in their vicinity.
     */
    private Location findFood() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            if (isInHabitat(where)) {
                Object p = field.getObjectAt(where);
                if(p instanceof Plant) { // If it find's a plant object ...
                    Plant plant = (Plant) p;
                    if(plant.isAlive()) { 
                        plant.setDead();
                        increaseFoodLevel(plant.getFoodValue()); // Then it feeds itself.
                        return where;
                    }
                }
            }
        }// Otherwise return null .. so the animal can seek a random place to go to.
        return null;
    }
}
