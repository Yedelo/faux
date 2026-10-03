package at.yedel.advantimations.mixin;



import at.yedel.advantimations.config.AdvantimationsConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.item.properties.numeric.UseCycle;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;



@Mixin(UseCycle.class)
public class UseCycleMixin {
    @ModifyExpressionValue(method = "get", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getUseItemRemainingTicks()I"))
    private int advantimations$cancelBrushingAnimation(int original, @Local LivingEntity holder) {
        return (int) AdvantimationsConfig.getInstance().cancelBrushingAnimation.getScaledThirdPersonResult(holder, original);
    }
}
