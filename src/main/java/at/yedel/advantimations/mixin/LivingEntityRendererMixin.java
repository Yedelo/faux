package at.yedel.advantimations.mixin;



import at.yedel.advantimations.config.AdvantimationsConfig;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;



@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V", at = @At("RETURN"))
    private void advantimations$modifyLivingEntityRenderState(LivingEntity entity, LivingEntityRenderState state, float f, CallbackInfo ci) {
        state.isAutoSpinAttack = AdvantimationsConfig.getInstance().cancelRiptideAnimation.getThirdPersonResult(entity, state.isAutoSpinAttack, false);
        state.walkAnimationSpeed = AdvantimationsConfig.getInstance().cancelLimbMovements.getScaledThirdPersonResult(entity, state.walkAnimationSpeed);
        state.walkAnimationPos = AdvantimationsConfig.getInstance().weirderLimbMovements.getScaledThirdPersonResult(entity, state.walkAnimationPos);
    }
}
