package at.yedel.advantimations.mixin;



import at.yedel.advantimations.config.AdvantimationsConfig;
import at.yedel.advantimations.config.EntityOption;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;



@Mixin(HumanoidMobRenderer.class)
public abstract class HumanoidMobRendererMixin {
    @Inject(method = "extractHumanoidRenderState", at = @At(value = "RETURN"))
    private static void advantimations$modifyHumanoidRenderState(LivingEntity entity, HumanoidRenderState state, float f, ItemModelResolver itemModelResolver, CallbackInfo ci) {
        //~ if >= 26.3 'attackTime' -> 'swingAnimation'
        state.swingAnimation = AdvantimationsConfig.getInstance().cancelSwings.getScaledThirdPersonResult(entity, state.swingAnimation);
        state.isCrouching = AdvantimationsConfig.getInstance().cancelSneaking.getThirdPersonResult(entity, state.isCrouching, false);
        EntityOption option = state.isInWater ? AdvantimationsConfig.getInstance().cancelSwimmingAnimation : AdvantimationsConfig.getInstance().cancelCrawlingAnimation;
        state.swimAmount = option.getScaledThirdPersonResult(entity, state.swimAmount);
        state.isFallFlying = AdvantimationsConfig.getInstance().cancelElytraAnimation.getThirdPersonResult(entity, state.isFallFlying, false);
    }
}
