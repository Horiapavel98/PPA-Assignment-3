import java.util.List;
import java.util.Iterator;
import java.util.Random;
import java.util.LinkedList;
/**
 * A class representing shared characteristics of animals.
 * Abstract class - impemented through the specific hierarchy.
 * 
 * 
 * 
 * @author Luca-Dorin Anton and Horia Pavel
 * @version v1.0
 */
public abstract class Animal extends Actor
{
    // Whether the animal is alive or not.
    private boolean alive;
    // Age of the animal.
    private int age;
    // Current foodLevel of the animal.
    private int foodLevel;
    // Gender boolean variable.
    private boolean isFemale;

    private static final Random rand = Randomizer.getRandom();

    /**
     * Create a new animal at location in field.
     * 
     * @param randomAge - if animal is a new born or not.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param simulator - the Simulator in which this animal operates.
     */
    public Animal(boolean randomAge, Field field, Location location, Simulator sim)
    {
        super(field,location, sim);
        alive = true;
        this.isFemale = rand.nextBoolean();
        if(randomAge) {
            age = rand.nextInt(getMaxAge());
            foodLevel = rand.nextInt(getMaxFoodLevel());
        }
        else {
            age = 0;
            foodLevel = getMaxFoodLevel();
        }
    }

    /**
     * Returns the current food level of the animal.
     * @return int - the food level as an integer value.
     */
    protected int getFoodLevel() {
        return foodLevel;
    }

    /**
     * Increases the food level of the animal.
     * @param newFoodLevel - the food level which increase the current food level.
     */
    protected void increaseFoodLevel(int newFoodLevel) {
        foodLevel = (foodLevel + newFoodLevel)%getMaxFoodLevel();
    }

    /**
     * Abstract method: Returns the maximum food level defined in each specific class.
     */
    abstract protected int getMaxFoodLevel();

    /**
     * Abstract method: Returns the maximum age defined in each specific class.
     */
    abstract protected int getMaxAge();

    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(super.getLocation() != null) {
            super.removeFromField();
        }
    }

    /**
     * Increments the hunger of the animal by decrementing the foodLevel.
     * If the foodLevel is 0 then the animal dies of starvation.
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Increments the age of the animal.
     * If the age exceets the MAX_AGE constant the animal will die.
     * Note: the MAX_AGE constant is defined in each specific class and it may vary.
     */
    protected void incrementAge()
    {
        age++;
        if(age > getMaxAge()) {
            setDead();
        }
    }

    /**
     * This method implemnts the act of giving birth to young.
     * @return List<Animal> - the list of new born babies to be scattered around
     * the breeeding couple in the simulator.
     */
    protected List<Animal> giveBirth() {
        List<Animal> newBorn = new LinkedList<>();
        if (isFemale) {
            Field field = getField();
            List<Location> free = field.getFreeAdjacentLocations(getLocation());
            for (Iterator<Location> it = free.iterator(); it.hasNext(); ) {
                if ( !isInHabitat(it.next()) ) {
                    it.remove();
                } 
            }
            int births = breed();
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                newBorn.add(getNewAnimal(field, loc));
            }
        }
        return newBorn;
    }
    
    /**
     * Abstract method:
     * Returns the habitat of the animal defined in specific classes to see if the 
     * animal is in the correct habitat.
     * @param l - the current location of the animal.
     * @return - boolean - true if the animal is in habitat / false if not.
     */
    abstract protected boolean isInHabitat(Location l);
    
    /**
     * Abstract method:
     * 
     * In order for the giveBirth method to remain abstract at this level
     * this method provides the Animal type of the new baby that has been
     * born.
     * 
     * @param field - the current field.
     * @loc - the current location of the animal.
     * 
     * @return Animal - the new Animal at that location in field.
     */
    abstract protected Animal getNewAnimal(Field field, Location loc);

    /**
     * Abstract method:
     * 
     * Returns the breeding probability defined in specific classes to determine
     * whether the couple will have babies or not.
     * 
     * @return double - the probability (0% to 100%)
     */
    abstract protected double getBreedingProbability();

    /**
     * Abstract method:
     * 
     * Returns the maximum amount of young that a couple can have defined in specific
     * classes.
     * 
     * @return int - the maximum number of babies.
     */
    abstract protected int getMaxSpawnSize();

    /**
     * This method calculates randomly how many young babies a couple can have
     * and returns the number.
     * @return births - the number of new born babies.
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= getBreedingProbability()) {
            births = rand.nextInt(getMaxSpawnSize()) + 1;
        }
        return births;
    }

    /**
     * Abstract method:
     * 
     * Not all animals have the same breeding age so this method is right to be 
     * defined as abstract. It retunrns the breeding age defined in the specific
     * classes.
     * @return int - the breeding age of the animal.
     */
    abstract protected int getBreedingAge();

    /**
     * Returns if the animal is a female or not.
     * @return boolean - isFemale.
     */
    protected boolean isFemale() {
        return isFemale;
    }

    /**
     * Each animal that can breed must look nearby and find a oposit gender of 
     * the same specie. Then it can breed.
     * @param boolean - true if if can breed / false if not.
     */
    public boolean canBreed()
    {
        if (!isFemale){
            return age >= getBreedingAge() && foodLevel >= getMaxFoodLevel()/2;
        }

        if (age >= getBreedingAge() && foodLevel >= getMaxFoodLevel()/2) {
            List possibleMates = getField().getSurroundingObjects(getLocation());
            for ( Object obj : possibleMates ) {
                if ( obj.getClass().getSimpleName().equals(this.getClass().getSimpleName()) ) {
                    Animal mate = (Animal) obj; 
                    if ( !mate.isFemale() ) {
                        if ( mate.canBreed() ) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
