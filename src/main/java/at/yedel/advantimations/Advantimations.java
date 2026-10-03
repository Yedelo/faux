package at.yedel.advantimations;



import at.yedel.advantimations.config.AdvantimationsConfig;
/*? if fabric {*/
import net.fabricmc.api.ClientModInitializer;
/*?} else if neoforge {*/
/*import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
    *//*?}*/



// Maud
/*? if neoforge */ //@Mod("advantimations")
public class Advantimations /*? if fabric {*/implements ClientModInitializer/*?}*/ {
	/*? if fabric {*/
	@Override
	public void onInitializeClient() {
		AdvantimationsConfig.init();
	}
	/*?} elif neoforge {*/
	/*public Advantimations(ModContainer container) {
		AdvantimationsConfig.init();
		container.registerExtensionPoint(IConfigScreenFactory.class, (tainer, parent) -> AdvantimationsConfig.getScreen(parent));
	}
	*//*?}*/
}