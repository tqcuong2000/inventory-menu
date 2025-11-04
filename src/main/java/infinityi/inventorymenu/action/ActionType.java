package infinityi.inventorymenu.action;

import com.mojang.serialization.MapCodec;
import infinityi.inventorymenu.action.type.*;
import net.minecraft.util.StringIdentifiable;

public enum ActionType implements StringIdentifiable {
    NONE("none", NoAction.CODEC),
    MESSAGE("message", MessageAction.CODEC),
    NAVIGATE("navigate", MenuNavigationAction.CODEC),
    TELEPORT("teleport", TeleportAction.CODEC),
    COMMAND("command", CommandAction.CODEC);
    private final String name;
    private final MapCodec<? extends Action> codec;

    ActionType(String name, MapCodec<? extends Action> codec) {
        this.name = name;
        this.codec = codec;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public MapCodec<? extends Action> getCodec() {
        return this.codec;
    }
}