/**
 * A particular class that implements Runnable interface
 * in order for the SimulatorThread to run in proper conditions.
 *
 * @author Luca-Dorin Anton and Horia Pavel
 * @version v1.0
 */
public class SimulatorRunnable implements Runnable
{
    private Simulator simulator;
    
    private SimulatorThread thread;
    
    /**
     * Constructor for SimulatorRunnable objects.
     * @param simulator - Simulator object passed.
     */
    public SimulatorRunnable(Simulator simulator) {
        this.simulator = simulator;
    }
    
    /**
     * Set the current thread.
     * @param thread - SimulatorThread - the thread to be set.
     */
    public void setThread(SimulatorThread thread) {
        this.thread = thread;
    }
    
    /**
     * Method implemented from the Runnable interface.
     */
    public void run() {
        simulator.simulate(thread.getSteps());
    }
    
}