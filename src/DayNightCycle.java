/**
 * This class models the Day-Night cycle that keeps track
 * of the clock and the day-counter of the simulator.
 * 
 * This class receives the current step of the simulation from
 * a (Simulator) object reference and returns the clock and the day counter.
 * 
 * It has methods such as getCurrentDay, getCurrentTimeString (clock) and 
 * getPartOfDay.
 *
 * @author Luca-Dorin Anton and Horia Pavel.
 * @version v1.0
 */
public class DayNightCycle
{
    /* Constats are defined for consistency through code*/
    public static final int HOUR_LENGHT = 60; // One hour is 60 steps in simulation

    private static final int DAY_LENGHT = 1440; // Inherently one day is 1440 steps
    /* The day is made out of 4 quarters of 360 steps so the daylight
    starts at the step 360: 6:00 AM.  Between 00:00 and 6:00 is considered to be NIGHT*/
    private static final int DAY_START = 360;
    /* Day ends at 1080: 18:00 PM  - from now on comes NIGHT*/
    private static final int DAY_END = 1080;
    // This constant defines the initial state of the simulator when it is opened.
    private static final int DAY_START_OFFSET = 720;

    // Two 'public' constants for the DAY and NIGHT to be referenced throughout the code.
    public static final int NIGHT = 1212;
    public static final int DAY = 2121;
    
    /* ^ Constants are defined above ^ */
    
    /* The simulator that provides the current step of the simulation */
    private Simulator sim;

    /**
     * Constructor for class objects.
     * @ param sim - Simulator reference needed for calculus.
     */
    public DayNightCycle(Simulator sim) {
        this.sim = sim;
    }

    /**
     * This method returns the current day of the simulation based on the
     * step and on the current DAY_START_OFFSET (which now is 720). This means
     * that the simulation will start from 12:00 (Day 1) and will continue.
     * 
     * @return int - Integer representing the day of the simulation.
     */
    public int getCurrentDay() {
        return ((sim.getStep() + DAY_START_OFFSET)/DAY_LENGHT) + 1;
    }

    /**
     * Here the time string is returned based on the steps provided by the
     * simulator.
     * Constants defined above are used.
     * 
     * @return String - the clock String that will be displayed at the bottom
     * of the simulator GUI.
     */
    public String getCurrentTimeString() {
        int time = (sim.getStep() + DAY_START_OFFSET)%DAY_LENGHT;
        int hour = time/HOUR_LENGHT;
        int minute = time%HOUR_LENGHT;
        String res;
        if(hour <= 9) {
            res = "0" + hour;  
        } else {
            res = "" + hour; 
        }

        if(minute <= 9) {
            res += ":0" + minute;
        } else {
            res+= ":" +minute;
        }

        return res;
    }
    //*******************************************************
    /**
     * This method returns the current step of the day using the modulo
     * operator. The timeInt is needed in order to maintain the day night
     * cycle correct throught the gradient changing in the SimulatorView class.
     * @return int - the step of the current day - starting from 0 to 1439.
     */
    public int getCurrentTimeInt() {
        return (sim.getStep() + DAY_START_OFFSET)%DAY_LENGHT;
    }

    /**
     * This method returns the part of the day based on the steps and the DAY_START_OFFSED
     * @return - int - constant value that could be either DAY or NIGHT.
     */
    public int getPartOfDay() {
        if((sim.getStep() + DAY_START_OFFSET)%DAY_LENGHT >= DAY_START && (sim.getStep() + DAY_START_OFFSET)%DAY_LENGHT < DAY_END){
            return DayNightCycle.DAY;
        } else {
            return DayNightCycle.NIGHT;
        }
    }
}
