package infinityi.inventorymenu.placeholders.providers;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlaceholderSets {

    public static List<PlaceholderProvider> playerServerSet(ServerPlayerEntity player){
        List<PlaceholderProvider> providers = new ArrayList<>();
        providers.add(new PlayerProvider(player));
        Optional.ofNullable(player.getServer()).map(server -> providers.add(new ServerProvider(server)));
        return providers;
    }
}
