package dev.bapmain.gravitycurve.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record GravityCurveData(double startY, double fullY) {

    public static final GravityCurveData DEFAULT = new GravityCurveData(2000.0, 5000.0);

    public static final Codec<GravityCurveData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.DOUBLE.fieldOf("start_y").forGetter(GravityCurveData::startY),
                    Codec.DOUBLE.fieldOf("full_y").forGetter(GravityCurveData::fullY)
            ).apply(instance, GravityCurveData::new)
    );
}