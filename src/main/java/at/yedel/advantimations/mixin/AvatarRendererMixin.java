/*
    this file has to be really weird
    i have to change a lot of things but i cant comment out the entire thing
    as there are other preprocessor things inside it that use multiline comments
    and you can't do something like the following:
    /* test /* test inside *∕ *∕
    see even there i had to use another type of slash. its a big complicated mess and versioned files would be cool
 */
package at.yedel.advantimations.mixin;



import at.yedel.advantimations.config.AdvantimationsConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.At;
/*? >= 1.21.9 {*/
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.world.entity.Avatar;
/*?} else {*/

/*import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.player.AbstractClientPlayer;

*//*?}*/
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;



@Mixin(/*? >= 1.21.9 {*/ AvatarRenderer.class /*?} else {*//*PlayerRenderer.class*//*?}*/)
public abstract class AvatarRendererMixin {
    @ModifyExpressionValue(
        method = /*? >=1.21.9 {*/"extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V"/*?} else {*//*"Lnet/minecraft/client/renderer/entity/player/PlayerRenderer;extractRenderState(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;F)V"*//*?}*/,
        at = @At(
            value = "INVOKE",
            target =
                /*? if neoforge && >=1.21.8 {*/
                //"Lnet/minecraft/world/item/ItemStack;canPerformAction(Lnet/neoforged/neoforge/common/ItemAbility;)Z"
                /*?} else {*/
                "Lnet/minecraft/world/item/ItemStack;is" + /*? >= 26.1 {*/"(Ljava/lang/Object;)Z"/*?} else {*//*"(Lnet/minecraft/world/item/Item;)Z"*//*?}*/
                /*?}*/
        )
    )
    private boolean advantimations$cancelSpyglassAnimation(boolean original, @Local(argsOnly = true) /*? >= 1.21.9 {*/ Avatar /*?} else {*/ /*AbstractClientPlayer *//*?}*/ player) {
        return AdvantimationsConfig.getInstance().cancelSpyglassAnimation.getThirdPersonResult(/*? >= 1.21.9 {*/player.asLivingEntity() /*?} else {*//*player*//*?}*/, original, false);
    }
}
