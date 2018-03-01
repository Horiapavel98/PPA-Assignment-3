import java.util.List;
import java.awt.Color;
/**
 * Abstract class Actor - Represents an abstract summary or all the existing
 * entities in the Simulator.
 *
 * @author Luca-Dorin Anton and Horia Pavel
 * @version v1.0
 */
public abstract class Actor
{
    // The animal's field.
    private Field field;
    // The animal's position in the field.
    private Location location;
    // Simulator object reference.
    private Simulator sim;

    /**
     * Abstract constructor for Actor inherited type.
     * @param field - the field is passed to the actor.
     * @param location - the location of the actor in the field.
     * @param sim - simulator object. (for extendibility we may have different simulators).
     */
    public Actor(Field field, Location location, Simulator sim) {
        this.field = field;
        this.location = location;
        this.sim = sim;
        field.place(this,location);
    }

    /**
     * Main method for acting throughout the simulation.
     * @param newActors - The list of newBorn young after one step.
     */
    abstract public void act(List<Actor> newActors);
    
    /**
     * Returns the location of the actor in the field.
     * @return location - the location of the actor in the field.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * This method updates the location of the actor within the field.
     * @param newLocation - the location to be set as new.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }

    /**
     * This method removes the actor from the field.
     * It clears the location and set all the references of the actor
     * to null.
     */
    protected void removeFromField() {
        field.clear(location);
        location = null;
        field = null;
    }

    /**
     * Sets the new field for the actor.
     * @param newField - new field to be set.
     */
    protected void setField(Field newField) {
        field = newField;
    }

    /**
     * This method returns the field of the actor.
     * @return field - the field returned.
     */
    protected Field getField()
    {
        return field;
    }
    
    /**
     * Returns the simulator object reference.
     * @param sim - the simulator of the current actor.
     */
    protected Simulator getSim() {
        return sim;
    }
}
