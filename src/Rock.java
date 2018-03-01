import java.awt.Color;
import java.util.List;
/**
 * This class represents the Rock entity.
 * Essentially it is a no-op actor.
 *
 * @author Luca-Dorin Anton and Horia Pavel
 * @version v1.0
 */
public class Rock extends Terrain
{
    // Each concrete class has a color.
    private static final Color COLOR = new Color(192,192,192);
    
    /**
     * Constructor for Rock objects
     * @param field - the current field.
     * @param location - the location of the rock.
     * @param sim - the simulator that operates this rock actor.
     */
    public Rock(Field field, Location location, Simulator sim) {
        super(field,location, sim);
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
     * Abstract method implemented:
     * It does nothing.
     * @param newActors - the new rocks to be displayed.
     */
    public void act(List<Actor> newActors) {
        // I have nothin' to do. I am a rock.
    }
}
