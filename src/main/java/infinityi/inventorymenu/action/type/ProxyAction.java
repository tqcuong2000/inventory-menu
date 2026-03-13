package infinityi.inventorymenu.menu.itemtype;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.action.Action;
import infinityi.inventorymenu.menu.layout.MenuItem;
import infinityi.inventorymenu.menu.layout.MenuItemType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public record ProxyItem(ItemStack item, String serverName) implements MenuItem {

    public static final MapCodec<ProxyItem> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStack.CODEC.fieldOf("item").forGetter(ProxyItem::item),
            Codec.STRING.fieldOf("server")
    ).apply(instance, StandardItem::new));
    @Override
    public List<Action> actions() {
        return List.of();
    }

    @Override
    public ItemStack resolveItemStack(ServerPlayerEntity player) {
        return this.item.copy();
    }

    @Override
    public MenuItemType getType() {
        return MenuItemType.PROXY;
    }
}
