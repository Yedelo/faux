/*? if spear {*/
package at.yedel.advantimations.mixin;



import at.yedel.advantimations.config.AdvantimationsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.effects.SpearAnimations;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;



@Mixin(SpearAnimations.class)
public abstract class SpearAnimationsMixin {
    @Inject(method = "thirdPersonAttackItem", at = @At("HEAD"), cancellable = true)
    private static void advantimations$cancelSpearAnimation(ArmedEntityRenderState state, PoseStack poseStack, CallbackInfo ci) {
        if (AdvantimationsConfig.getInstance().cancelSpearAnimation.shouldApplyInThirdPerson(state)) ci.cancel();
    }

    @Inject(method = "thirdPersonHandUse", at = @At("HEAD"), cancellable = true)
    private static <T extends HumanoidRenderState> void advantimations$cancelSpearAnimation(ModelPart arm, ModelPart head, /*? if >= 26.3 {*/ HumanoidArm holdingArm /*?} else {*/ /*boolean bl *//*?}*/, ItemStack itemStack, HumanoidRenderState state, CallbackInfo ci) {
        if (AdvantimationsConfig.getInstance().cancelSpearAnimation.shouldApplyInThirdPerson(state)) ci.cancel();
    }

    @Inject(method = "thirdPersonUseItem", at = @At("HEAD"), cancellable = true)
    private static void advantimations$cancelSpearAnimation(ArmedEntityRenderState state, PoseStack poseStack, float timeHeld, HumanoidArm arm, ItemStack actualItem, CallbackInfo ci) {
        if (AdvantimationsConfig.getInstance().cancelSpearAnimation.shouldApplyInThirdPerson(state)) ci.cancel();
    }

    @Inject(method = "firstPersonUse", at = @At("HEAD"), cancellable = true)
    private static void advantimations$cancelSpearAnimation(float f, PoseStack poseStack, float g, HumanoidArm arm, ItemStack itemStack, CallbackInfo ci) {
        if (AdvantimationsConfig.getInstance().cancelSpearAnimation.shouldApplyInFirstPerson()) ci.cancel();
    }

    @Inject(method = "firstPersonAttack", at = @At("HEAD"), cancellable = true)
    private static void advantimations$cancelSpearAnimation(float f, PoseStack poseStack, int i, HumanoidArm arm, CallbackInfo ci) {
        if (AdvantimationsConfig.getInstance().cancelSpearAnimation.shouldApplyInFirstPerson()) ci.cancel();
    }
}
/*?}*/