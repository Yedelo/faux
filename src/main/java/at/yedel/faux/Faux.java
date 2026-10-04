package at.yedel.faux;



//? if fabric {
import at.yedel.faux.platform.fabric.FauxFabric;
//?} else if neoforge {
//import at.yedel.faux.platform.neoforge.FauxNeoforge;
//?}
import at.yedel.faux.launch.FauxConstants;
import at.yedel.faux.utils.Constants;
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
        Logger.info("Starting Faux {} for {}", FauxConstants.VERSION, FauxConstants.LOADER);
        Logger.info("Work directory is {}", Constants.workDir);
        //? if fabric{
        FauxFabric.getInstance().initialize();
        //?} else if neoforge {
        //FauxNeoforge.getInstance().initialize();
        //?}
        Logger.info("Faux initialization took {} ms", System.currentTimeMillis() - startTime);
        if (Boolean.getBoolean("faux.exit-after-run")) {
            Logger.info("Property faux.exit-after-run is true, exiting...");
            System.exit(0);
        }
    }
}
