package infinityi.inventorymenu.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;

import java.util.function.BiFunction;
import java.util.function.Function;

public class CodecUtils {
    public static <A, B, C> Codec<C> mergeFlat(
            Codec<A> aCodec,
            Codec<B> bCodec,
            BiFunction<A, B, C> combiner,
            Function<C, A> aExtractor,
            Function<C, B> bExtractor
    ) {
        return Codec.PASSTHROUGH.flatXmap(
                dyn -> {
                    DataResult<A> aRes = aCodec.parse(dyn);
                    DataResult<B> bRes = bCodec.parse(dyn);
                    return DataResult.unbox(DataResult.instance().apply2(combiner, aRes, bRes));
                },
                c -> {
                    JsonElement aDyn = aCodec.encodeStart(JsonOps.INSTANCE, aExtractor.apply(c)).result().orElseThrow();
                    JsonElement bDyn = bCodec.encodeStart(JsonOps.INSTANCE, bExtractor.apply(c)).result().orElseThrow();
                    if (aDyn instanceof JsonObject objA &&
                            bDyn instanceof JsonObject objB) {
                        objB.entrySet().forEach(e -> objA.add(e.getKey(), e.getValue()));
                        return DataResult.success(new Dynamic<>(JsonOps.INSTANCE, objA));
                    }
                    return DataResult.error(() -> "Failed to merge");
                }
        );
    }
}

