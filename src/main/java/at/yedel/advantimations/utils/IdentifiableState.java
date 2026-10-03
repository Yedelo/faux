package at.yedel.advantimations.utils;



import net.minecraft.world.entity.Entity;



public interface IdentifiableState {
    default void advantimations$updateId(Entity entity) {}
    default boolean advantimations$isPlayer() { return false; }
    default boolean advantimations$isSelf() { return false; }
}
