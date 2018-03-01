
/**
 * Abstract class Terrain - The terrain is an actor that does not move.
 * It is still an actor because the classes that inherit from it may have
 * different functionality (can be extended).
 * 
 * @author Luca-Dorin Anton and Horia Pavel
 * @version v1.0
 */
public abstract class Terrain extends Actor
{
    /**
     * Abstract constructor for Terrain objects.
     * @param field - the field.
     * @param location - the location of the Terrain object.
     * @param sim - the simulator that operates this actor.
     */
    public Terrain(Field field, Location location, Simulator sim) {
        super(field,location, sim);
    }
}
