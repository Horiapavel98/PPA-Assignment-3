import java.awt.Color;
import java.util.Random;
import java.util.List;
/**
 * Soil class has a basic functionality.
 * It doesn't allow Algae to grow if it is night.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Soil extends Terrain
{
    // Each concrete class has a color.
    private static final Color COLOR = new Color(200,113,0);
    // The chance of spawning/growing.
    private static final double SPAWN_ALGAE_CHANCE = 0.02;

    private static final Random rand = Randomizer.getRandom();
    /**
     * Constructor for Soil objects
     * @param field - the current field.
     * @param location - the location of the soil.
     * @param sim - the simulator that operates this soil actor.
     */
    public Soil(Field field, Location location, Simulator sim) {
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
     * The soil produces the first Algae entity if there is nothing growing 
     * from it.
     * It only produces it if it is daylight.
     * @param newActors - the new algae to be displayed.
     */
    public void act(List<Actor> newActors) {
        Simulator sim = super.getSim();
        DayNightCycle cycle = sim.getDayNightCycle(); // Get the day-night-cycle.
        if(cycle.getPartOfDay() == DayNightCycle.DAY) { // Calculate the growth depending on the time of the day.
            List<Location> freeLocations = super.getField().getFreeAdjacentLocations(super.getLocation());
            for(Location l : freeLocations) {
                if (l.getRow() + 1 == super.getLocation().getRow() && rand.nextDouble() <= SPAWN_ALGAE_CHANCE && l.getCol() == super.getLocation().getCol()) {
                    newActors.add(new Algae(super.getField(),l, super.getSim())); 
                }
            }
        }
    }
}
