package at.yedel.faux;



import at.yedel.faux.utils.Logger;



public class Faux {
    private static final Faux INSTANCE = new Faux();

    public static Faux getInstance() {
        return INSTANCE;
    }

    private boolean initialized;

    public void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        long startTime = System.currentTimeMillis();
        Logger.info("Starting Faux");
        Logger.info("Faux initialization took " + (System.currentTimeMillis() - startTime) + "ms");
    }
}
