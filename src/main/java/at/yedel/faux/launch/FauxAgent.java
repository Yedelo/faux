package at.yedel.faux.launch;



import at.yedel.faux.Faux;

import java.lang.instrument.Instrumentation;



public class FauxAgent {
    public static void premain(String string, Instrumentation instrumentation) {
        Faux.getInstance().initialize();
    }
}
