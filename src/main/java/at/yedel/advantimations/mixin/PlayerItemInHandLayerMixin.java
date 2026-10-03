package at.yedel.advantimations.mixin;



import at.yedel.advantimations.config.AdvantimationsConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.injection.At;
/*? >= 1.21.9 {*/
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
/*?} else {*/
//import net.minecraft.client.renderer.entity.state.PlayerRenderState;
/*?}*/
import org.spongepowered.asm.mixin.Mixin;



@Mixin(PlayerItemInHandLayer.class)
public abstract class PlayerItemInHandLayerMixin {
    @ModifyExpressionValue(
        /*? >= 26.1 {*/
        method = "submitArmWithItem(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lnet/minecraft/client/renderer/item/ItemStackRenderState;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V",
        /*?} else {*/
            /*/^? >= 1.21.11 {^/
            method = "submitArmWithItem(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lnet/minecraft/client/renderer/item/ItemStackRenderState;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V",
            /^?} else {^/
                /^/^? >= 1.21.9 {^/
                    method = "submitArmWithItem(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lnet/minecraft/client/renderer/item/ItemStackRenderState;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V",
                /^?} else {^/
                    //method = "renderArmWithItem(Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;Lnet/minecraft/client/renderer/item/ItemStackRenderState;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
                /^?}^/
            ^//^?}^/
        *//*?}*/
        at = @At(value = "FIELD", target = /*? >= 1.21.9 {*/"Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;isUsingItem:Z"/*?} else {*//*"Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;isUsingItem:Z"*//*?}*/, opcode = Opcodes.GETFIELD)
    )
    private boolean advantimations$cancelSpyglassAnimation(boolean original, @Local(argsOnly = true) /*? >= 1.21.9 {*/ AvatarRenderState /*?} else {*//*PlayerRenderState*//*?}*/ state) {
        return AdvantimationsConfig.getInstance().cancelSpyglassAnimation.getThirdPersonResult(state, original, false);
    }
}
