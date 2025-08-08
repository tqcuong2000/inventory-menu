package infinityi.inventoryMenu.TeleportUtil;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import infinityi.inventoryMenu.ItemAction.Actions.TeleportAction;
import infinityi.inventoryMenu.TeleportUtil.TeleportRequestManager.TeleportRequestManager;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;

import java.util.Objects;
import java.util.UUID;

public record TPLoc(String playerName, GlobalPos pos) {
    public static final Codec<TPLoc> CODECS = Codec.xor(BlockPos.CODEC, Codec.STRING).xmap(
            either -> either.map(l -> new TPLoc("", TPLoc.create(l)),
                    r -> new TPLoc(r, TPLoc.create(new BlockPos(0, 0, 0)))), tpLoc -> {
                if (tpLoc.playerName.isEmpty()) return Either.left(tpLoc.pos.pos());
                return  Either.right(tpLoc.playerName);
            }
    );

    public static final Codec<TPLoc> CODEC = Codec.xor(GlobalPos.CODEC, CODECS).xmap(
            either -> either.map(l -> new TPLoc("", l),
                    r -> r), tpLoc -> {
                if (Objects.equals(tpLoc.pos, TPLoc.create(new BlockPos(0, 0, 0)))) return Either.left(tpLoc.pos);
                return Either.right(tpLoc);
            }
    );

    public static GlobalPos create(BlockPos pos){
        return new GlobalPos(ServerWorld.OVERWORLD, pos);
    }
    public void teleport(ServerPlayerEntity player, boolean safeCheck) {
        if (player.getServer() == null) return;
        if (!playerName.isEmpty()){
            ServerPlayerEntity targetPlayer = getPlayer(player.getServer());
            if (targetPlayer == null) {
                player.sendMessage(Text.translatable("%s §cis not online.", this.playerName));
                return;
            }
            TeleportRequestManager.createRequest(player, targetPlayer, safeCheck);
            if (player.getServer() == null) return;
        }
        ServerWorld destinationWorld = player.getServer().getWorld(pos.dimension());
        if (destinationWorld == null) {
            player.sendMessage(Text.translatable("§cThat world does not exist! %s", pos.dimension().getValue().toString()));
            return;
        }
        if (safeCheck) {
            if (TeleportAction.isDangerLocation(destinationWorld, pos.pos())) {
                player.sendMessage(Text.translatable("§cYou cannot teleport to a dangerous location!"));
                return;
            }
        }
        player.teleport(destinationWorld, pos.pos().getX() + 0.5, pos.pos().getY(), pos.pos().getZ() + 0.5, PositionFlag.DELTA, player.headYaw, player.lastPitch, false);
        player.closeHandledScreen();


    }

    public BlockPos getPos(MinecraftServer server) {
        if(!playerName.isEmpty()){
            ServerPlayerEntity targetPlayer = getPlayer(server);
            if (targetPlayer == null) return null;
            return getPlayer(server).getBlockPos();
        }
        return pos.pos();
    }

    public String targetName(MinecraftServer server){
        if (!playerName.isEmpty()){
            ServerPlayerEntity target = getPlayer(server);
            if (target != null) return target.getName().getString();
            return String.format("%s is offline", playerName);
        }
        return "";
    }

    public Integer getDistance(ServerPlayerEntity player){
        BlockPos blockPos = getPos(player.getServer());
        if (blockPos == null) return 0;
        return (int) player.getBlockPos().getChebyshevDistance(blockPos);
    }

    private ServerPlayerEntity getPlayer(MinecraftServer server) {
        return playerName.length() > 16 ? server.getPlayerManager().getPlayer(UUID.fromString(playerName)) : server.getPlayerManager().getPlayer(playerName);
    }
}
