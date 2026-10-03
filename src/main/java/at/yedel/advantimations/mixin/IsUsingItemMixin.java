package at.yedel.advantimations.mixin;



import at.yedel.advantimations.config.AdvantimationsConfig;
import at.yedel.advantimations.config.EntityOption;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.item.properties.conditional.IsUsingItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TridentItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;



@Mixin(IsUsingItem.class)
public abstract class IsUsingItemMixin {
    @ModifyExpressionValue(method = "get", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isUsingItem()Z"))
    private boolean advantimations$cancelItemUseConditions(boolean original, @Local(argsOnly = true) ItemStack stack, @Local(argsOnly = true) LivingEntity entity, @Local(argsOnly = true) ItemDisplayContext displayContext) {
        boolean firstPerson = displayContext.firstPerson();
        boolean thirdPerson = !firstPerson;
        boolean shouldCancel = switch (stack.getItem()) {
            case ShieldItem ignored: {
                EntityOption option = AdvantimationsConfig.getInstance().cancelShieldAnimation;
                yield (option.shouldApplyInFirstPerson() && firstPerson) || (option.getThirdPersonResult(entity, !original, true) && thirdPerson);
            }
            case BowItem ignored: {
                EntityOption option = AdvantimationsConfig.getInstance().cancelBowArrowModel;
                yield (option.shouldApplyInFirstPerson() && firstPerson) || (option.getThirdPersonResult(entity, !original, true) && thirdPerson);
            }
            case CrossbowItem ignored: {
                EntityOption option = AdvantimationsConfig.getInstance().cancelCrossbowArrowModel;
                yield (option.shouldApplyInFirstPerson() && firstPerson) || (option.getThirdPersonResult(entity, !original, true) && thirdPerson);
            }
            case TridentItem ignored: {
                EntityOption option = AdvantimationsConfig.getInstance().cancelTridentSpearAnimation;
                yield (option.shouldApplyInFirstPerson() && firstPerson) || (option.getThirdPersonResult(entity, !original, true) && thirdPerson);
            }
            case InstrumentItem ignored: {
                EntityOption option = AdvantimationsConfig.getInstance().cancelHornTootAnimation;
                yield (option.shouldApplyInFirstPerson() && firstPerson) || (option.getThirdPersonResult(entity, !original, true) && thirdPerson);
            }
            default: {
                yield !original;
            }
        };
        return !shouldCancel;
    }
}
