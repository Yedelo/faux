package at.yedel.advantimations.config;



import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.gui.controllers.slider.FloatSliderController;
import net.minecraft.network.chat.Component;



public interface ScalableOption {
    String DESCRIPTION_BC_IM_INDECISIVE = "Some animations use a float to scale, which can also be modified instead of fully cancelled.";

    float getScalingMultiplier();
    void setScalingMultiplier(float scalingMultiplier);
    static Option<Float> createScalingMultiplierOption(ScalableOption defaultValue, ScalableOption configValue) {
        return Option.<Float>createBuilder()
            .name(Component.literal("Scaling Multiplier"))
            .description(OptionDescription.of(Component.literal(DESCRIPTION_BC_IM_INDECISIVE)))
            .binding(
                defaultValue.getScalingMultiplier(),
                configValue::getScalingMultiplier,
                configValue::setScalingMultiplier
            )
            .customController(option -> new FloatSliderController(option, -2f, 2f, 0.01f))
            .build();
    }
}
