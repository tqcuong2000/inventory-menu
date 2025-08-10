package infinityi.inventorymenu.placeholders.resolvers;

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

public record ItemResolver(MenuItem item, ServerPlayerEntity player) implements PlaceholderResolver {

    @Override
    public List<PlaceholderProvider> constructProviders() {
        List<PlaceholderProvider> providers = new ArrayList<>();
        providers.add(new PlayerProvider(player));
        if (player.getServer() instanceof MinecraftServer server) providers.add(new ServerProvider(server));
        if (item.action() instanceof TeleportAction action) providers.add(action);
        return providers;
    }

    @Override
    public ItemStack resolve() {
        ItemStack itemStack = item.resolveItemStack(player);
        List<PlaceholderProvider> providers = constructProviders();
        Optional.ofNullable(itemStack.get(DataComponentTypes.CUSTOM_NAME))
                .ifPresent(name -> itemStack
                        .set(DataComponentTypes.CUSTOM_NAME, PlaceholderResolver.resolve(name, providers, player)));
        Optional.ofNullable(itemStack.get(DataComponentTypes.LORE))
                .ifPresent(lore -> itemStack
                        .set(DataComponentTypes.LORE, new LoreComponent(PlaceholderResolver.resolve(lore.lines(), providers, player))));
        return itemStack.copy();
    }
}
