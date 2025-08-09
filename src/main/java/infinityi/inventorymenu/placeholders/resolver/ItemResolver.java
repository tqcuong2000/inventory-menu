package infinityi.inventorymenu.placeholders.resolver;

import infinityi.inventorymenu.itemaction.actions.TeleportAction;
import infinityi.inventorymenu.menulayout.layout.MenuItem;
import infinityi.inventorymenu.placeholders.providers.PlaceholderProvider;
import infinityi.inventorymenu.placeholders.providers.PlayerProvider;
import infinityi.inventorymenu.placeholders.providers.ServerProvider;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemResolver {
    public final MenuItem item;
    public final ServerPlayerEntity player;
    private final List<PlaceholderProvider> providers;

    public ItemResolver(MenuItem item, ServerPlayerEntity player) {
        this.item = item;
        this.player = player;
        this.providers = new ArrayList<>();
        constructProviders();
    }

    private void constructProviders(){
        this.providers.add(new PlayerProvider(player));
        if (player.getServer() instanceof MinecraftServer server) this.providers.add(new ServerProvider(server));
        if (item.action() instanceof TeleportAction action) this.providers.add(action);
    }

    public ItemStack resolve(){
        ItemStack itemStack = item.resolveItemStack(player);
        Optional.ofNullable(itemStack.get(DataComponentTypes.CUSTOM_NAME))
                .ifPresent(name -> itemStack
                        .set(DataComponentTypes.CUSTOM_NAME, PlaceholderResolver.resolve(name, providers, player)));
        Optional.ofNullable(itemStack.get(DataComponentTypes.LORE))
                .ifPresent(lore -> itemStack
                        .set(DataComponentTypes.LORE, new LoreComponent(PlaceholderResolver.resolve(lore.lines(), providers, player))));
        return itemStack.copy();
    }
}
