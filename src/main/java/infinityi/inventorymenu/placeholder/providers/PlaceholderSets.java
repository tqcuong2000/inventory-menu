package infinityi.inventorymenu.placeholder.providers;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerPlayer;

public class PlaceholderSets {

    public static Set<PlaceholderProvider> playerServerSet(ServerPlayer player){
        Set<PlaceholderProvider> providers = new HashSet<>();
        providers.add(new PlayerProvider(player));
        Optional.of(player.level().getServer()).map(server -> providers.add(new ServerProvider(server)));
        return providers;
    }
}
