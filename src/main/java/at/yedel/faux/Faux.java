package at.yedel.faux;



//? if fabric {
import at.yedel.faux.platform.fabric.FauxFabric;
//?} else if neoforge {
//import at.yedel.faux.platform.neoforge.FauxNeoforge;
//?}
import at.yedel.faux.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class Faux {
    private static final Faux INSTANCE = new Faux();

    public static Faux getInstance() {
        return INSTANCE;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger("Faux");
    private boolean initialized;

    public void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        long startTime = System.currentTimeMillis();
        LOGGER.info("Starting Faux");
        LOGGER.info("Work directory is {}", Constants.workDir);
        //? if fabric{
        FauxFabric.getInstance().initialize();
        //?} else if neoforge {
        //FauxNeoforge.getInstance().initialize();
        //?}
        LOGGER.info("Faux initialization took {} ms", System.currentTimeMillis() - startTime);
        if (Boolean.getBoolean("faux.exit-after-run")) {
            LOGGER.info("Property faux.exit-after-run is true, exiting...");
            System.exit(0);
        }
    }
}
