package dev.bapmain.gravitycurve.mixin;

import dev.devce.rocketnautics.content.physics.GlobalSpacePhysicsHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GlobalSpacePhysicsHandler.class)
public interface GravityOverrideAccessor {

    @Accessor("gravityOverride")
    static Float getGravityOverride() {
        throw new AssertionError();
    }
}