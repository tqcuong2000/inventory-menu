package infinityi.inventoryMenu.TeleportUtil.TPLocation;

import com.mojang.serialization.Codec;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;

public interface TPLocation {
    Codec<TPLocation> CODEC = StringIdentifiable.createCodec(TPLocationType::values)
            .dispatch(TPLocation::getType, TPLocationType::getCodec);


    TPLocationType getType();

    void teleport(ServerPlayerEntity player, boolean safecheck);

    BlockPos position(ServerPlayerEntity player);
}
