//? if >= 26.3 {
package at.yedel.advantimations.mixin;



import at.yedel.advantimations.config.AdvantimationsConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.player.FirstPersonHandsAndItems;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;



// mojang on some bs
@Mixin(FirstPersonHandsAndItems.class)
public abstract class FirstPersonHandsAndItemsMixin {
    @ModifyExpressionValue(
        method = "tick",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getItemSwapScale(F)F")
    )
    private float advantimations$cancelAttackCooldownResets(float original) {
        return AdvantimationsConfig.getInstance().cancelAttackCooldownResets.getFirstPersonResult(original, 1F);
    }

    @Inject(
        method = "shouldInstantlyReplaceVisibleItem",
        at = @At("HEAD"), cancellable = true
    )
    private void advantimations$cancelSlotSwappingResets(ItemStack currentlyVisibleItem, ItemStack expectedItem, LocalPlayer player, CallbackInfoReturnable<Boolean> cir) {
        if (AdvantimationsConfig.getInstance().cancelSlotSwappingResets.shouldApplyInFirstPerson()) {
            cir.setReturnValue(true);
        }
    }
}
//?}