package infinityi.inventoryMenu.TeleportUtil.TPLocation;

import com.mojang.serialization.MapCodec;
import infinityi.inventoryMenu.TeleportUtil.TPLocation.LocationType.GlobalTPLocation;
import infinityi.inventoryMenu.TeleportUtil.TPLocation.LocationType.PlayerTPLocation;
import infinityi.inventoryMenu.TeleportUtil.TPLocation.LocationType.Vec3dTPLocation;
import net.minecraft.util.StringIdentifiable;

public enum TPLocationType implements StringIdentifiable {
    PLAYER_LOCATION("player", PlayerTPLocation.CODEC),
    VEC3D_LOCATION("position", Vec3dTPLocation.CODEC),
    GLOBAL_LOCATION("location", GlobalTPLocation.CODEC);
    private final String name;
    private final MapCodec<? extends TPLocation> codec;

    TPLocationType(String name, MapCodec<? extends TPLocation> codec) {
        this.name = name;
        this.codec = codec;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public MapCodec<? extends TPLocation> getCodec() {
        return this.codec;
    }
}
