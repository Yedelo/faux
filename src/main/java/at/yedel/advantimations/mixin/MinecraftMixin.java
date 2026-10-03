package at.yedel.advantimations.mixin;



import at.yedel.advantimations.config.AdvantimationsConfig;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.Minecraft;
//? if >= 26.3 {
import net.minecraft.client.player.LocalPlayer;
//?} else {
//import net.minecraft.client.renderer.ItemInHandRenderer;
//?}
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;



@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    //~ if >= 26.3 'Lnet/minecraft/client/renderer/ItemInHandRenderer;itemUsed(Lnet/minecraft/world/InteractionHand;)V' -> 'Lnet/minecraft/client/player/LocalPlayer;itemUsed(Lnet/minecraft/world/InteractionHand;)V', 'ItemInHandRenderer' -> 'LocalPlayer' {
    @WrapWithCondition(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;itemUsed(Lnet/minecraft/world/InteractionHand;)V", ordinal = 0))
    private boolean advantimations$cancelBlockInteractResets(LocalPlayer instance, InteractionHand hand) {
        return !AdvantimationsConfig.getInstance().cancelBlockInteractResets.shouldApplyInFirstPerson();
    }

    @WrapWithCondition(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;itemUsed(Lnet/minecraft/world/InteractionHand;)V", ordinal = 1))
    private boolean advantimations$cancelItemInteractResets(LocalPlayer instance, InteractionHand hand) {
        return !AdvantimationsConfig.getInstance().cancelItemInteractResets.shouldApplyInFirstPerson();
    }
    //~}
}
