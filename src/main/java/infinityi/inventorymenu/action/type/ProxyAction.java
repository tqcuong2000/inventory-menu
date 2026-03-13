package infinityi.inventorymenu.action.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.action.Action;
import infinityi.inventorymenu.action.ActionType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;


public record ProxyAction(ItemStack item, String serverName) implements Action{

    public static final MapCodec<ProxyAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStack.CODEC.fieldOf("item").forGetter(ProxyAction::item),
            Codec.STRING.fieldOf("server").forGetter(ProxyAction::serverName)
    ).apply(instance, ProxyAction::new));

    @Override
    public void execute(ServerPlayer player) {

    }

    @Override
    public ActionType getType() {
        return ActionType.PROXY;
    }

    public static void register(ServerPlayNetworking networking){

    }
}
