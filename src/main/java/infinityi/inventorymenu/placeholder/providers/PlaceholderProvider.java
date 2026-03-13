package infinityi.inventorymenu.placeholder.providers;

import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;

public interface PlaceholderProvider {

    Optional<String> getKey(String key, ServerPlayer player);
}
