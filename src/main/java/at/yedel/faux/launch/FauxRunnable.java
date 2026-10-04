package at.yedel.faux.launch;



import at.yedel.faux.Faux;
import at.yedel.faux.utils.Logger;



public class FauxRunnable implements Runnable {
    @Override
    public void run() {
        Logger.warn("Faux ran from runnable and so it likely ran from a standard Fabric entrypoint. This can cause issues with loading, it's recommended to use the Java Agent instead.");
        Faux.getInstance().initialize();
    }
}
