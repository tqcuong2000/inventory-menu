package infinityi.inventorymenu.util;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import infinityi.inventorymenu.InventoryMenu;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public final class DeferredItemStack {
    public static final Codec<DeferredItemStack> CODEC = CodecUtils.JSON_ELEMENT.xmap(
            json -> new DeferredItemStack(json.deepCopy()),
            deferred -> deferred.data().deepCopy()
    );

    private final JsonElement data;
    private volatile ItemStack cached;

    public DeferredItemStack(JsonElement data) {
        this.data = data;
    }

    public JsonElement data() {
        return this.data;
    }

    public ItemStack resolve() {
        if (cached == null) {
            synchronized (this) {
                if (cached == null) {
                    cached = ItemStack.CODEC.parse(JsonOps.INSTANCE, data)
                            .resultOrPartial(err -> InventoryMenu.LOGGER.error("Failed to parse ItemStack in DeferredItemStack: {}", err))
                            .orElse(ItemStack.EMPTY);
                }
            }
        }
        return cached.copy();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeferredItemStack that)) return false;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    @Override
    public String toString() {
        return "DeferredItemStack[data=" + data + "]";
    }
}
