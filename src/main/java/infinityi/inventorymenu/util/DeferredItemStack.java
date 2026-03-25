package infinityi.inventorymenu.util;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.item.ItemStack;

public record DeferredItemStack(JsonElement data) {
    public static final Codec<DeferredItemStack> CODEC = CodecUtils.JSON_ELEMENT.xmap(
            json -> new DeferredItemStack(json.deepCopy()),
            deferred -> deferred.data.deepCopy()
    );

    public ItemStack resolve() {
        return ItemStack.CODEC.parse(JsonOps.INSTANCE, data.deepCopy())
                .getOrThrow(IllegalStateException::new);
    }
}
