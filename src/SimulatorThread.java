/**
 * SimulatorThread is a class alocating a separate thread in order
 * for the Simulator operations to be displayed sequentially on the
 * screeen (that is, when a user clicks a button the actual number of
 * steps modifying the simulation can be seen properly on the screen).
 * 
 *
 * @author Luca-Dorin Anton and Horia Pavel.
 * @version v1.0
 */
public class SimulatorThread extends Thread
{
    private int steps;
    // Takes an implemented type of Runnable class defined in SimulatorRunnable.
    private SimulatorRunnable run;

    /**
     * Constructor for SimulatorThread objects.
     * @param r - SimulatorRunnable object implementing the Runnable interface.
     */
    public SimulatorThread(SimulatorRunnable r) {
        super(r);
        run = r;
        run.setThread(this);
    }

    /**
     * Set the number of steps for the new thread.
     * Some simulations may have different number of steps.
     * @param steps - int - the number of steps.
     */
    public void setSteps(int steps){
        this.steps = steps;
    }

    /**
     * Get the number of steps of the current simulation.
     * @return int - the number of steps in that are allocated to the current simulation.
     */
    public int getSteps(){
        return steps;
    }

}