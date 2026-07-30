package dev.bapmain.gravitycurve;

import dev.bapmain.gravitycurve.data.GravityCurveManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

@Mod("gravitycurve")
public class GravityCurveMod {

    public GravityCurveMod(IEventBus modEventBus) {
        NeoForge.EVENT_BUS.addListener(this::onAddReloadListeners);
    }

    private void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(GravityCurveManager.INSTANCE);
    }
}