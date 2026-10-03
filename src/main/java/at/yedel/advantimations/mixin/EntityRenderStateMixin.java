package at.yedel.advantimations.mixin;



import at.yedel.advantimations.utils.IdentifiableState;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;



@Mixin(EntityRenderState.class)
public class EntityRenderStateMixin implements IdentifiableState {
    @Unique
    private boolean advantimations$isPlayer;

    @Unique
    private boolean advantimations$isSelf;

    @Override
    public void advantimations$updateId(Entity entity) {
        advantimations$isPlayer = entity instanceof Player;
        advantimations$isSelf = entity instanceof LocalPlayer;
    }

    @Override
    public boolean advantimations$isPlayer() {
        return advantimations$isPlayer;
    }

    @Override
    public boolean advantimations$isSelf() {
        return advantimations$isSelf;
    }
}
