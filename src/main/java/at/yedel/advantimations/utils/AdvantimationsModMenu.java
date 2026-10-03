/*? if fabric {*/

package at.yedel.advantimations.utils;



import at.yedel.advantimations.config.AdvantimationsConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;



public class AdvantimationsModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return AdvantimationsConfig::getScreen;
    }
}

/*?}*/
