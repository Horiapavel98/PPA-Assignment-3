import java.util.Queue;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
/**
 * Abstract class Predator - This abstract class implements
 * the functionality of the Actor class through the act method defined here.
 * 
 * Deeper from here there are the specific classes that only have parameters
 * in order to be more extendible.
 * 
 * This is a complex class since Predators cannot only see their near location
 * but they can search for food within a defined range and chase the prey.
 *
 * @author Luca-Dorin Anton and Horia Pavel
 * @version v1.0
 */
public abstract class Predator extends Animal
{    

    /**
     * Abstract constructor of the Predator type objects.
     * @param randomAge - boolean - if the new animal is a new born or not.
     * @param field - Field - the field of the predator.
     * @param location - Location - the location of the animal.
     * @param sim - Simulator - the current simulator operating this animal.
     */
    public Predator(boolean randomAge, Field field, Location location, Simulator sim) {
        super(randomAge,field,location,sim);
    }
    
    /**
     * Abstract method implemented:
     * 
     * Checks if the animal is in habitat - if not it is set to dead.
     * 
     * If the animal is alive then increment the hunger and age and start looking
     * for food (hunt method). If the food is found then go to that location and eat it.
     * If it cannot find food then it moves randomly. If it cannot find a random
     * adjacent location that is in habitat then it means that this animal is not
     * in habitat and it should have been dead by now - so setDead();
     * 
     * If the animal is in habitat then seeks if it can breed and update currentLocation
     * 
     * @param newActors - List<Actor> - the new born babies after a step.
     */
    public void act(List<Actor> newActors) {
        
        if (!isInHabitat(super.getLocation()) ) {
            setDead();
            return;
        }
        
        incrementAge();
        incrementHunger();
        if(isAlive()){
            Queue<Location> huntQueue = new LinkedList<>();
            int[][] fieldMatrix = new int[super.getField().getDepth()][super.getField().getWidth()];
            huntQueue.add(super.getLocation());
            fieldMatrix[super.getLocation().getRow()][super.getLocation().getCol()] = 1;
            Location nextLocation = hunt(0,huntQueue,fieldMatrix);
            if ( nextLocation == null) {
                List<Location> randomAdjLoc = super.getField().getFreeAdjacentLocations(getLocation());
                for (Location l : randomAdjLoc) {
                    if ( isInHabitat(l) ) {
                        nextLocation = l;
                        break;
                    }
                }
            }

            if (nextLocation == null) {
                setDead();
            }else {
                List<Animal> newBorn = giveBirth();
                for (Animal a : newBorn) {
                    newActors.add(a);
                }
                setLocation(nextLocation);
            }
        }
    }

    /**
     * Abstract method:
     * Return the hunting range of the predator defined in specific classes.
     */
    abstract protected int getMaxHuntingRange();

    /**
     * Hunting method that uses a complex algorithm to find the food.
     * 
     * At each step it increases the current range of hunting, checks if it is still int the max hunting range
     * and it looks for food that is in habitat.
     * 
     * If it finds food (Prey instance) then it calls the findPrey location that gives the predator the path
     * to the prey. If the prey is exactly near the predator then the predator will feed. Otherwise the predator
     * will calculate its path (recursively) to the prey using findPrey method and it will go in the prey's direction.
     * 
     * @param currentRange - int - the current hunting range of the animal
     * @param huntQueue - Queue<Location> - the hunting queue that services the hunting locations.
     * @param fieldMatrix - int[][] - the matrix of the field with initial values of 0.
     */
    protected Location hunt(int currentRange, Queue<Location> huntQueue,int[][] fieldMatrix) {
        Field field = super.getField();
        if (huntQueue.isEmpty() || currentRange >= getMaxHuntingRange()) {
            return null;
        }
        currentRange ++;
        Location currentLocation = huntQueue.remove();
        List<Location> adjacent = field.adjacentLocations(currentLocation);
        for (Location l : adjacent) {
            if (isInHabitat(l) && fieldMatrix[l.getRow()][l.getCol()] == 0){
                Object obj = field.getObjectAt(l);
                if (obj == null) {
                    huntQueue.add(l);
                    fieldMatrix[l.getRow()][l.getCol()] = fieldMatrix[currentLocation.getRow()][currentLocation.getCol()] + 1;
                }else if (obj instanceof Prey) {
                    Prey prey = (Prey) obj;
                    if (prey.isAlive()) {
                        fieldMatrix[l.getRow()][l.getCol()] = fieldMatrix[currentLocation.getRow()][currentLocation.getCol()] + 1;
                        Location properLocation = findPrey(fieldMatrix,prey.getLocation());
                        List<Location> initialAdjLocations = field.adjacentLocations(getLocation());
                        for (Location loc : initialAdjLocations) {
                            if (loc.equals(properLocation)) {
                                prey.setDead();
                                increaseFoodLevel(prey.getFoodValue());
                                return properLocation;
                            }
                        }
                        return  properLocation;
                    }else {
                        huntQueue.add(l);
                        fieldMatrix[l.getRow()][l.getCol()] = fieldMatrix[currentLocation.getRow()][currentLocation.getCol()] + 1;
                    }

                }else {
                    fieldMatrix[l.getRow()][l.getCol()] = -1;
                }
            }
        }
        return hunt(currentRange,huntQueue,fieldMatrix);
    }
    
    /**
     * This method finds the prey based on it's location. The distances are mapped in the field matrix
     * with the predator location being noted as '1' and the prey's location as a number of steps 
     * (may vary from 2 -> to the maximum hunting range of the predator).
     * 
     * @param fieldMatrix  - int[][] - the integer field matrix for calculations.
     * @param currentLocation - Location - the prey location.
     * 
     * @return Location the location (direction) that the predator should move to.
     */
    private Location findPrey(int[][] fieldMatrix, Location currentLocation) {
        int row = currentLocation.getRow();
        int col = currentLocation.getCol();
        int val = fieldMatrix[row][col];
        int depth = getField().getDepth();
        int width = getField().getWidth();
        while (val > 2) {
            boolean changed = false;
            if (row-1 >= 0 && changed == false) {
                if(fieldMatrix[row-1][col] == val - 1) {
                    row --;
                    val --;
                    changed = true;
                }
            }
            if(col-1 >= 0 && changed == false) {
                if(fieldMatrix[row][col-1] == val - 1) {
                    col --;
                    val --;
                    changed = true;
                }
            }
            if (col+1 < width && changed == false){
                if(fieldMatrix[row][col+1] == val - 1) {
                    col++;
                    val --;
                    changed = true;
                }
            }
            if (row+1 < depth && changed == false){
                if(fieldMatrix[row+1][col] == val - 1) {

                    row++;
                    val --;
                    changed = true;
                }
            }
            if (row+1 < depth && col+1 < width && changed == false) {
                if(fieldMatrix[row + 1][col + 1] == val - 1) {
                    row++;
                    col++;
                    val--;
                    changed = true;
                }
            }
            if (row+1 <  depth && col-1 >= 0 && changed == false) {
                if(fieldMatrix[row + 1][col - 1] == val - 1) {
                    row++;
                    col--;
                    val--;
                    changed = true;
                }
            } 
            if (row-1 >= 0 && col+1 < width && changed ==false) {
                if(fieldMatrix[row - 1][col + 1] == val - 1) {
                    row--;
                    col++;
                    val--;
                    changed = true;
                }
            }
            if (row-1 >= 0 && col-1 >= 0 && changed == false) {
                if(fieldMatrix[row - 1][col - 1] == val - 1) {
                    row--;
                    col--;
                    val--;
                    changed = true;
                }
            }
        }
        return new Location(row,col);
    }
}

