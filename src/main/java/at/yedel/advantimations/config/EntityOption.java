package at.yedel.advantimations.config;



import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import java.util.function.Consumer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;



public class EntityOption implements FirstPersonOption, ScalableOption {
    protected boolean enabled;
    protected float scalingMultiplier;
    protected boolean enabledInFirstPerson;
    protected boolean enabledOnSelf;
    protected boolean enabledOnOtherPlayers;
    protected boolean enabledOnOtherEntities;

    public static void createGroup(String groupName, String groupDescription, EntityOption defaultValue, EntityOption configValue, Consumer<Configuration> configurator, ConfigCategory.Builder builder) {
        Configuration configuration = new Configuration();
        configurator.accept(configuration);
        builder.group(OptionGroup.createBuilder()
            .name(Component.literal(groupName))
            .description(OptionDescription.of(Component.literal(groupDescription)))
            .collapsed(configuration.collapsed)
            .option(Option.<Boolean>createBuilder()
                .name(Component.literal("Enabled"))
                .binding(
                    defaultValue.isEnabled(),
                    configValue::isEnabled,
                    configValue::setEnabled
                )
                .controller(BooleanControllerBuilder::create)
                .build()
            )
            .optionIf(configuration.canBeScaled, ScalableOption.createScalingMultiplierOption(defaultValue, configValue))
            .optionIf(configuration.canBeEnabledInFirstPerson, Option.<Boolean>createBuilder()
                .name(Component.literal("Enabled in First Person"))
                .description(OptionDescription.of(Component.literal("Enable this option for yourself in first person.")))
                .binding(
                    defaultValue.enabledInFirstPerson,
                    configValue::isEnabledInFirstPerson,
                    configValue::setEnabledInFirstPerson
                )
                .controller(TickBoxControllerBuilder::create)
                .build()
            )
            .optionIf(configuration.canBeEnabledOnSelf, Option.<Boolean>createBuilder()
                .name(Component.literal("Enabled on Self"))
                .description(OptionDescription.of(Component.literal("Enable this option for yourself in third person.")))
                .binding(
                    defaultValue.isEnabledOnSelf(),
                    configValue::isEnabledOnSelf,
                    configValue::setEnabledOnSelf
                )
                .controller(TickBoxControllerBuilder::create)
                .build()
            )
            .optionIf(configuration.canBeEnabledOnOtherPlayers, Option.<Boolean>createBuilder()
                .name(Component.literal("Enabled on Other Players"))
                .description(OptionDescription.of(Component.literal("Enable this option for other players.")))
                .binding(
                    defaultValue.isEnabledOnOtherPlayers(),
                    configValue::isEnabledOnOtherPlayers,
                    configValue::setEnabledOnOtherPlayers
                )
                .controller(TickBoxControllerBuilder::create)
                .build()
            )
            .optionIf(configuration.canBeEnabledOnOtherEntities, Option.<Boolean>createBuilder()
                .name(Component.literal("Enabled on Other Entities"))
                .description(OptionDescription.of(Component.literal("Enable this option for other non-player entities, such as zombies.")))
                .binding(
                    defaultValue.isEnabledOnOtherEntities(),
                    configValue::isEnabledOnOtherEntities,
                    configValue::setEnabledOnOtherEntities
                )
                .controller(TickBoxControllerBuilder::create)
                .build()
            )
            .build());
    }

    @Override
    public boolean shouldApplyInFirstPerson() {
        return enabled && enabledInFirstPerson;
    }

    public <T extends Number> float getScaledFirstPersonResult(T originalValue) {
        float asFloat = originalValue.floatValue();
        return getFirstPersonResult(asFloat, asFloat * scalingMultiplier);
    }

    public boolean shouldApplyInThirdPerson(EntityRenderState state) {
        return getThirdPersonResult(state, false, true);
    }

    public <T> T getThirdPersonResult(EntityRenderState state, T originalValue, T newValue) {
        return getThirdPersonResult(state.advantimations$isPlayer(), state.advantimations$isSelf(), originalValue, newValue);
    }

    public <T extends Number> float getScaledThirdPersonResult(Entity entity, T originalValue) {
        float asFloat = originalValue.floatValue();
        return getThirdPersonResult(entity, asFloat, asFloat * getScalingMultiplier());
    }

    public <T> T getThirdPersonResult(Entity entity, T originalValue, T newValue) {
        return getThirdPersonResult(entity instanceof Player, entity instanceof LocalPlayer, originalValue, newValue);
    }

    public <T> T getThirdPersonResult(boolean player, boolean self, T originalValue, T newValue) {
        if (enabled) {
            if (enabledOnSelf && self) {
                return newValue;
            }
            if (enabledOnOtherPlayers && player && !self) {
                return newValue;
            }
            if (enabledOnOtherEntities && !player) {
                return newValue;
            }
        }
        return originalValue;
    }

    public EntityOption enabled() {
        this.enabled = true;
        return this;
    }

    public EntityOption scalingMultiplier(float scalingMultiplier) {
        this.scalingMultiplier = scalingMultiplier;
        return this;
    }

    public EntityOption enabledInFirstPerson() {
        this.enabledInFirstPerson = true;
        return this;
    }

    public EntityOption enabledOnSelf() {
        this.enabledOnSelf = true;
        return this;
    }

    public EntityOption enabledOnOtherPlayers() {
        this.enabledOnOtherPlayers = true;
        return this;
    }

    public EntityOption enabledOnOtherEntities() {
        this.enabledOnOtherEntities = true;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public float getScalingMultiplier() {
        return scalingMultiplier;
    }

    @Override
    public void setScalingMultiplier(float scalingMultiplier) {
        this.scalingMultiplier = scalingMultiplier;
    }

    public boolean isEnabledInFirstPerson() {
        return enabledInFirstPerson;
    }

    public void setEnabledInFirstPerson(boolean enabledInFirstPerson) {
        this.enabledInFirstPerson = enabledInFirstPerson;
    }

    public boolean isEnabledOnSelf() {
        return enabledOnSelf;
    }

    public void setEnabledOnSelf(boolean enabledOnSelf) {
        this.enabledOnSelf = enabledOnSelf;
    }

    public boolean isEnabledOnOtherPlayers() {
        return enabledOnOtherPlayers;
    }

    public void setEnabledOnOtherPlayers(boolean enabledOnOtherPlayers) {
        this.enabledOnOtherPlayers = enabledOnOtherPlayers;
    }

    public boolean isEnabledOnOtherEntities() {
        return enabledOnOtherEntities;
    }

    public void setEnabledOnOtherEntities(boolean enabledOnOtherEntities) {
        this.enabledOnOtherEntities = enabledOnOtherEntities;
    }

    public static EntityOption selfConfiguredOption() {
        return new EntityOption().enabledInFirstPerson().enabledOnSelf();
    }

    public static class Configuration {
        public static final Consumer<Configuration> PERSPECTIVE_INDEPENDENT_OPTION_CONFIGURATOR = (configuration) ->
            configuration.canBeEnabledInFirstPerson().canBeEnabledOnSelf().canBeEnabledOnOtherPlayers().canBeEnabledOnOtherEntities();

        public static final Consumer<Configuration> THIRD_PERSON_OPTION_CONFIGURATOR = (configuration) ->
            configuration.canBeEnabledOnSelf().canBeEnabledOnOtherPlayers().canBeEnabledOnOtherEntities();

        private boolean canBeScaled;
        private boolean canBeEnabledInFirstPerson;
        private boolean canBeEnabledOnSelf;
        private boolean canBeEnabledOnOtherPlayers;
        private boolean canBeEnabledOnOtherEntities;
        private boolean collapsed;

        public Configuration canBeScaled() {
            this.canBeScaled = true;
            return this;
        }

        public Configuration canBeEnabledInFirstPerson() {
            this.canBeEnabledInFirstPerson = true;
            return this;
        }

        public Configuration canBeEnabledOnSelf() {
            this.canBeEnabledOnSelf = true;
            return this;
        }

        public Configuration canBeEnabledOnOtherPlayers() {
            this.canBeEnabledOnOtherPlayers = true;
            return this;
        }

        public Configuration canBeEnabledOnOtherEntities() {
            this.canBeEnabledOnOtherEntities = true;
            return this;
        }

        public Configuration collapsed() {
            this.collapsed = true;
            return this;
        }
    }
}
