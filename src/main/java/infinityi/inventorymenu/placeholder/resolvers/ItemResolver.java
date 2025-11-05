package infinityi.inventorymenu.placeholder.resolvers;

import infinityi.inventorymenu.action.Action;
import infinityi.inventorymenu.action.type.TeleportAction;
import infinityi.inventorymenu.menu.layout.MenuItem;
import infinityi.inventorymenu.placeholder.providers.PlaceholderProvider;
import infinityi.inventorymenu.placeholder.providers.PlayerProvider;
import infinityi.inventorymenu.placeholder.providers.ServerProvider;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public record ItemResolver(MenuItem item, ServerPlayerEntity player) {

    public Set<PlaceholderProvider> constructProviders() {
        Set<PlaceholderProvider> providers = new HashSet<>();
        providers.add(new PlayerProvider(player));
        if (player.getEntityWorld().getServer() instanceof MinecraftServer server) providers.add(new ServerProvider(server));
        for (Action action : item().actions()) if (action instanceof TeleportAction tp) providers.add(tp);
        return providers;
    }
    public ItemStack resolve() {
        ItemStack itemStack = item.resolveItemStack(player);
        Set<PlaceholderProvider> providers = constructProviders();
        Optional.ofNullable(itemStack.get(DataComponentTypes.CUSTOM_NAME))
                .ifPresent(name -> itemStack
                        .set(DataComponentTypes.CUSTOM_NAME, PlaceholderResolver.resolve(name, providers, player)));
        Optional.ofNullable(itemStack.get(DataComponentTypes.LORE))
                .ifPresent(lore -> itemStack
                        .set(DataComponentTypes.LORE, new LoreComponent(PlaceholderResolver.resolve(lore.lines(), providers, player))));
        return itemStack.copy();
    }
}
