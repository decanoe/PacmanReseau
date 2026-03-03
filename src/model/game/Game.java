package model.game;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Class for representing turn based games
 * <p>
 * Contains support for multiple PropertyChangeListener
 * <ul>
 * <li>"turn" an int for the current turn</li>
 * <li>"frame_rate" a string for showing the frame rate</li>
 */
public abstract class Game implements Runnable {
    /** The current turn */
    public int turn = 0;
    /** The turn on wich the game will stop (-1 if not specified) */
    private int max_turn;

    /** Wether the game is running */
    private boolean running;
    /**
     * The running state of the game
     * @return True if the game is currently running, False else
     */
    public boolean is_running() { return running; }
    /** The thread running the game (null if the game isn't running) */
    Thread thread = null;

    /** The time to sleep between turns when running (in milliseconds) */
    private long sleep_time;
    /** The number of turns to use to compute tps */
    private int tps_buffer = 5;

    /**
     * Constructs a Game with a maximun number of turns and a running speed
     * <p>
     * At creation, the game isn't initialized and is not running
     * @param max_turn an int representing the maximum number of turns (-1 if not specified)
     * @param speed an double representing the time (in seconds) between turns when running
     */
    public Game(int max_turn, double speed) { this.turn = 0; this.max_turn = max_turn; this.running = false; set_speed(speed); }
    /**
     * Constructs a Game with a running speed
     * <p>
     * At creation, the game isn't initialized and is not running
     * @param speed an double representing the time (in seconds) between turns when running
     */
    public Game(double speed) { this(-1, speed); }

    /**
     * Initializes the game and sets the current turn to 0
     */
    public void init() { set_turn(0); initialize_game(); }
    /**
     * Sets the current turn to a specified value
     * @param new_turn an int representing the value wanted for the turn
     * <p>
     * Fires a property change on "turn"
     */
    public void set_turn(int new_turn) {
        int old_turn = turn;
        turn = new_turn;
        support.firePropertyChange("turn", old_turn, new_turn);
    }
    /**
     * Plays one turn or ends the game if ending conditions are met
     */
    public void step() {
        if (game_continue() && (max_turn < 0 || turn < max_turn)) {
            take_turn();
            set_turn(turn + 1);
        }
        else {
            running = false;
            game_over();
        }
    }
    /**
     * Pauses the game if it was running
     * <p>
     * Fires a property change on "frame_rate"
     */
    public void pause() { running = false; support.firePropertyChange("frame_rate", "", "0"); }
    /**
     * Runs the game turn by turn
     * <p>
     * Fires a property change on "frame_rate" each turn
     * @implNote This method should not be called outside a thread !
     */
    public void run() {
        Queue<Long> frame_times = new LinkedList<Long>();
        int frames = 0;
        support.firePropertyChange("frame_rate", "", "???");

        long elapsed = 0;
        while (running) {
            if (elapsed < sleep_time) {
                try {
                    Thread.sleep(sleep_time - elapsed);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            long step_start_time = System.currentTimeMillis();
            if (Thread.currentThread() != thread) return;

            if (frames >= tps_buffer) {
                frames--;
                long elapsed_n_frames = System.currentTimeMillis() - frame_times.poll();
                support.firePropertyChange("frame_rate", "", String.valueOf(Math.round(elapsed_n_frames / tps_buffer / 10.) * .01));
            }
            frame_times.add(step_start_time);
            frames++;

            step();

            elapsed = (System.currentTimeMillis() - step_start_time);
        }
    }
    /**
     * Starts running the game inside a new thread
     * <p>
     * If the game is already running, the old thread will stop after it is done waiting for it's new turn
     */
    public void launch() { running = true; thread = new Thread(this); thread.start(); }
    /**
     * Set the speed of the game when running
     * @param speed an double representing the time (in seconds) between turns when running
     */
    public void set_speed(double speed) { sleep_time = Math.round(speed * 1000); }

    /** support for PropertyChangeListeners */
    protected final PropertyChangeSupport support = new PropertyChangeSupport(this);
    /**
     * adds a PropertyChangeListener to this game
     * @param listener the listener to add
     * @see Game this class doc for a list of all property
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
    /**
     * adds a PropertyChangeListener to this game for a specific property
     * @param property the property to listen, as a string
     * @param listener the listener to add
     * @see Game this class doc for a list of all property
     */
    public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
        support.addPropertyChangeListener(property, listener);
    }

    /**
     * method to be implemented by a subclass
     * <p>
     * it will be called on {@link #init}
     */
    protected abstract void initialize_game();
    /**
     * method to be implemented by a subclass
     * <p>
     * it will be called on {@link #step}
     */
    protected abstract void take_turn();
    /**
     * method to be implemented by a subclass
     * <p>
     * returns wether the game should continue or stop
     * @return True if the game sould continue, False else
     */
    protected abstract boolean game_continue();
    /**
     * method to be implemented by a subclass
     * <p>
     * it will be called on {@link #step} when a game over occurs (when game_continue() returns false)
     */
    protected abstract void game_over();
}