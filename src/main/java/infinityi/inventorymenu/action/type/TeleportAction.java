package infinityi.inventorymenu.action.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.action.Action;
import infinityi.inventorymenu.action.ActionType;
import infinityi.inventorymenu.placeholder.providers.PlaceholderProvider;
import infinityi.inventorymenu.util.teleportutil.TeleportCost;
import infinityi.inventorymenu.util.teleportutil.tplocation.TPLocation;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;


public record TeleportAction(TPLocation target, TeleportCost cost,
                             Boolean safe_check) implements Action, PlaceholderProvider {
    public static final MapCodec<TeleportAction> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    TPLocation.CODEC.fieldOf("pos").forGetter(TeleportAction::target),
                    TeleportCost.CODEC.optionalFieldOf("cost", TeleportCost.empty()).forGetter(TeleportAction::cost),
                    Codec.BOOL.optionalFieldOf("safe_check", false).forGetter(TeleportAction::safe_check)
            ).apply(instance, TeleportAction::new));

    public static Map<String, Supplier<Object>> createKeySuppliers(ServerPlayer player, TeleportAction action) {
        MinecraftServer server = player != null && player.level() != null ? player.level().getServer() : null;
        return Map.of(
                "xp_cost", () -> {
                    BlockPos pos = server != null ? action.target.getPos(server) : null;
                    return action.cost.calcCost(player, pos);
                },
                "xp_cost_type", () -> action.cost.isPoint().toString(),
                "target_pos", () -> {
                    BlockPos pos = server != null ? action.target.getPos(server) : null;
                    if (pos == null) return "Offline";
                    return String.format("X: %s Y: %s Z: %s", pos.getX(), pos.getY(), pos.getZ());
                },
                "target_name", () -> {
                    if (server == null) return "?";
                    return Optional.ofNullable(action.target.getPlayer(server)).map(s -> s.getName().getString()).orElse("?");
                },
                "distance", () -> action.target.getDistance(player)
        );
    }

    public static boolean isDangerLocation(ServerLevel world, BlockPos pos) {
        if (world == null || pos == null) {
            return true;
        }
        BlockPos floorPos = pos.below();
        BlockPos headPos = pos.above();
        if (!world.hasChunk(floorPos.getX() >> 4, floorPos.getZ() >> 4)) {
            return false;
        }
        BlockState floorState = world.getBlockState(floorPos);
        if (!floorState.isFaceSturdy(world, floorPos, net.minecraft.core.Direction.UP)) return true;
        BlockState feetState = world.getBlockState(pos);
        BlockState headState = world.getBlockState(headPos);
        if (feetState.isSuffocating(world, pos) || headState.isSuffocating(world, headPos)) return true;
        return isDangerBlock(feetState) || isDangerBlock(headState);
    }

    public static int distanceBetween(BlockPos pos1, BlockPos pos2) {
        return pos1.distChessboard(pos2);
    }

    private static boolean isDangerBlock(BlockState state) {
        return state.is(Blocks.LAVA) || state.is(Blocks.FIRE) || state.is(Blocks.CACTUS) || state.is(Blocks.SWEET_BERRY_BUSH) || state.is(Blocks.POWDER_SNOW);
    }

    @Deprecated
    private static int chebyshevDistance(BlockPos a, BlockPos b) {
        int dx = Math.abs(a.getX() - b.getX());
        int dy = Math.abs(a.getY() - b.getY());
        int dz = Math.abs(a.getZ() - b.getZ());
        return Math.max(dx, Math.max(dy, dz));
    }

    @Override
    public void execute(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        if (server == null) return;
        BlockPos pos = target.getPos(server);
        if (pos == null) {
            player.sendSystemMessage(Component.literal("§cTarget location/player is unavailable."));
            return;
        }
        if (cost.hasCost(player, pos)) {
            target.teleport(player, safe_check, cost);
        } else {
            player.sendSystemMessage(Component.translatable("Not enough experience.").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public ActionType getType() {
        return ActionType.TELEPORT;
    }

    @Override
    public Optional<String> getKey(String key, ServerPlayer player) {
        Map<String, Supplier<Object>> keySuppliers = createKeySuppliers(player, this);
        return Optional.ofNullable(keySuppliers.get(key))
                .map(Supplier::get)
                .map(String::valueOf);
    }

}