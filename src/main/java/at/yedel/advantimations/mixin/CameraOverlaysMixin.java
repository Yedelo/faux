package at.yedel.advantimations.mixin;



import at.yedel.advantimations.config.AdvantimationsConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
/*? >= 26.2 {*/
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.injection.At;
/*?} else {*/
//import net.minecraft.client.gui.Gui;
/*?}*/
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;



@Mixin(/*? >= 26.2 {*/ Hud.class /*?} else {*//*Gui.class*//*?}*/)
public abstract class CameraOverlaysMixin {
    @ModifyExpressionValue(method = /*? >= 26.1 {*/"extractCameraOverlays"/*?} else {*//*"renderCameraOverlays"*//*?}*/, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isScoping()Z"))
    private boolean advantimations$cancelSpyglassAnimation(boolean original) {
        return AdvantimationsConfig.getInstance().cancelSpyglassAnimation.getFirstPersonResult(original, false);
    }

}
