package at.yedel.advantimations.config;



import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionAddable;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import net.minecraft.network.chat.Component;



public class SimpleFirstPersonOption implements FirstPersonOption {
    private boolean enabled;

    public SimpleFirstPersonOption(boolean enabled) {
        this.enabled = enabled;
    }

    public static void createOption(String name, String description, SimpleFirstPersonOption defaultValue, SimpleFirstPersonOption configValue, OptionAddable builder) {
        createOption(name, description, defaultValue, configValue, builder, false);
    }

    public static void createOption(String name, String description, SimpleFirstPersonOption defaultValue, SimpleFirstPersonOption configValue, OptionAddable builder, boolean addScalingMultiplier) {
        builder.option(
            Option.<Boolean>createBuilder()
                .name(Component.literal(name))
                .description(OptionDescription.of(Component.literal(description)))
                .binding(
                    defaultValue.isEnabled(),
                    configValue::isEnabled,
                    configValue::setEnabled
                )
                .controller(BooleanControllerBuilder::create)
                .build()
        );
    }

    @Override
    public boolean shouldApplyInFirstPerson() {
        return enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static SimpleFirstPersonOption enabledOption() {
        return new SimpleFirstPersonOption(true);
    }

    public static SimpleFirstPersonOption disabledOption() {
        return new SimpleFirstPersonOption(false);
    }
}
