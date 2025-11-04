package infinityi.inventorymenu.placeholder.providers;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PlaceholderSets {

    public static Set<PlaceholderProvider> playerServerSet(ServerPlayerEntity player){
        Set<PlaceholderProvider> providers = new HashSet<>();
        providers.add(new PlayerProvider(player));
        Optional.ofNullable(player.getEntityWorld().getServer()).map(server -> providers.add(new ServerProvider(server)));
        return providers;
    }
}
