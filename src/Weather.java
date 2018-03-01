import java.util.Random;
/**
 * This class models the weather through our simulation.
 * The weather of the simulation keeps going for a certain time
 * then it changes. After changing it cannot change again for another
 * certain number steps set by a constant variable.
 * 
 * It has a single method that defines the next weather of the simulation.
 * 
 * The default weather is NORMAL (meaning sunny/partly cloudy).
 *
 * @author Luca-Dorin Anton and Horia Pavel
 * @version v1.0
 */
public class Weather
{
    /* Constant variables defined below */
    /* Sets the basic amount of steps that needs to pass in order
       for the current weather to change. */
    private static final int OFFSET_DECISION_STEPS = 300;
    // The new duration of the current weather can vary with this amount.
    private static final int MAX_DECISION_OFFSET = 100;
    // Constants defining the different states of the weather.
    public static final int RAINING = -1;
    public static final int NORMAL = 0;
    public static final int DRY = 1;
    /* Constant variables defined above */

    private int currentWeather;
    
    public int timeTillDecision; // Keeps the time unitil the weather changes.

    private static final Random rand = Randomizer.getRandom();

    /**
     * Constructor for Weather type objects.
     */
    public Weather() {
        currentWeather = NORMAL;
        timeTillDecision = OFFSET_DECISION_STEPS;
    }

    /**
     * This method computes the next state of the weather based on random
     * numbers. If the timeTillDecision is not yet done it just returns the
     * current weather.
     * @return int - the next state of the weather based on randomness.
     */
    public int getWeather() {
        if (timeTillDecision == 0) {
            if ( rand.nextBoolean() ) {
                timeTillDecision = OFFSET_DECISION_STEPS + rand.nextInt(MAX_DECISION_OFFSET);
            }else {
                timeTillDecision = OFFSET_DECISION_STEPS - rand.nextInt(MAX_DECISION_OFFSET);
            }
            int nextWeather = rand.nextInt(3);
            if ( nextWeather == 0) {
                currentWeather = DRY;
            }else if (nextWeather ==1) {
                currentWeather = NORMAL;
            }else {
                currentWeather = RAINING;
            }
            return currentWeather;
        }else {
            timeTillDecision --;
            return currentWeather;
        }
    }
}
