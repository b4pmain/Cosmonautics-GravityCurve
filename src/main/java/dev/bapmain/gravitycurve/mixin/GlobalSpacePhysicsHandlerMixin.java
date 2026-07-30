package dev.bapmain.gravitycurve.mixin;

import dev.devce.rocketnautics.api.orbit.DeepSpaceHelper;
import dev.devce.rocketnautics.content.physics.GlobalSpacePhysicsHandler;
import dev.bapmain.gravitycurve.data.GravityCurveData;
import dev.bapmain.gravitycurve.data.GravityCurveManager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GlobalSpacePhysicsHandler.class)
public class GlobalSpacePhysicsHandlerMixin {

    @Inject(
            method = "calculateGravityFactor(Lnet/minecraft/world/level/Level;D)D",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void gravitycurve$calculate(
            Level level,
            double y,
            CallbackInfoReturnable<Double> cir
    ) {
        // 1. Respect the existing creative / debug override
        Float override = GravityOverrideAccessor.getGravityOverride();
        if (override != null) {
            cir.setReturnValue(1.0 - override);
            return;
        }

        // 2. Deep space / pure space dimension is always full zero-g
        if (level.dimension().location().getPath().equals("space")
                || DeepSpaceHelper.isDeepSpace(level)) {
            cir.setReturnValue(1.0);
            return;
        }

        // 3. Use our configurable curve
        GravityCurveData curve = GravityCurveManager.get(level);

        if (y <= curve.startY()) {
            cir.setReturnValue(0.0);
        } else {
            double factor = Math.clamp(
                    (y - curve.startY()) / (curve.fullY() - curve.startY()),
                    0.0, 1.0
            );
            cir.setReturnValue(factor);
        }
    }
}