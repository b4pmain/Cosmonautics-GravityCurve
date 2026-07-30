package dev.bapmain.gravitycurve.data;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GravityCurveManager extends SimplePreparableReloadListener<Map<ResourceLocation, GravityCurveData>> {

    public static final GravityCurveManager INSTANCE = new GravityCurveManager();

    private static final Map<ResourceLocation, GravityCurveData> CURVES = new ConcurrentHashMap<>();

    // Global fallback (used when a dimension has no specific entry)
    private static GravityCurveData globalDefault = GravityCurveData.DEFAULT;

    @Override
    protected Map<ResourceLocation, GravityCurveData> prepare(ResourceManager manager, ProfilerFiller profiler) {
        Map<ResourceLocation, GravityCurveData> map = new HashMap<>();

        manager.listResources("gravity_curves", loc -> loc.getPath().endsWith(".json"))
                .forEach((location, resource) -> {
                    try (BufferedReader reader = resource.openAsReader()) {
                        JsonElement json = com.google.gson.JsonParser.parseReader(reader);
                        GravityCurveData data = GravityCurveData.CODEC.parse(JsonOps.INSTANCE, json)
                                .getOrThrow(err -> new RuntimeException("Failed to parse gravity curve " + location + ": " + err));

                        // data/gravitycurve/gravity_curves/minecraft/overworld.json
                        // → key = minecraft:overworld
                        String path = location.getPath(); // gravity_curves/minecraft/overworld.json
                        String dimPath = path.substring("gravity_curves/".length(), path.length() - 5);
                        ResourceLocation dimId = ResourceLocation.parse(dimPath.replace('/', ':'));

                        map.put(dimId, data);
                    } catch (Exception e) {
                        System.err.println("[GravityCurve] Failed to load " + location + ": " + e.getMessage());
                    }
                });

        return map;
    }

    @Override
    protected void apply(Map<ResourceLocation, GravityCurveData> map, ResourceManager manager, ProfilerFiller profiler) {
        CURVES.clear();
        CURVES.putAll(map);

        // Optional: treat a special "default" entry as the global fallback
        GravityCurveData def = CURVES.remove(ResourceLocation.fromNamespaceAndPath("gravitycurve", "default"));
        if (def != null) {
            globalDefault = def;
        } else {
            globalDefault = GravityCurveData.DEFAULT;
        }

        System.out.println("[GravityCurve] Loaded " + CURVES.size() + " dimension curves + global default "
                + globalDefault.startY() + " → " + globalDefault.fullY());
    }

    public static GravityCurveData get(@Nullable Level level) {
        if (level == null) return globalDefault;
        return CURVES.getOrDefault(level.dimension().location(), globalDefault);
    }

    public static GravityCurveData getGlobal() {
        return globalDefault;
    }
}