package infinityi.inventorymenu.placeholder.resolvers;

import infinityi.inventorymenu.action.Action;
import infinityi.inventorymenu.action.type.TeleportAction;
import infinityi.inventorymenu.menu.layout.MenuItem;
import infinityi.inventorymenu.placeholder.providers.PlaceholderProvider;
import infinityi.inventorymenu.placeholder.providers.PlayerProvider;
import infinityi.inventorymenu.placeholder.providers.ServerProvider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public record ItemResolver(MenuItem item, ServerPlayer player) {

    public Set<PlaceholderProvider> constructProviders() {
        Set<PlaceholderProvider> providers = new HashSet<>();
        providers.add(new PlayerProvider(player));
        if (player.level().getServer() instanceof MinecraftServer server) providers.add(new ServerProvider(server));
        for (Action action : item().actions()) if (action instanceof TeleportAction tp) providers.add(tp);
        return providers;
    }
    public ItemStack resolve() {
        ItemStack itemStack = item.resolveItemStack(player);
        Set<PlaceholderProvider> providers = constructProviders();
        Optional.ofNullable(itemStack.get(DataComponents.CUSTOM_NAME))
                .ifPresent(name -> itemStack
                        .set(DataComponents.CUSTOM_NAME, PlaceholderResolver.resolve(name, providers, player)));
        Optional.ofNullable(itemStack.get(DataComponents.LORE))
                .ifPresent(lore -> itemStack
                        .set(DataComponents.LORE, new ItemLore(PlaceholderResolver.resolve(lore.lines(), providers, player))));
        return itemStack.copy();
    }
}
