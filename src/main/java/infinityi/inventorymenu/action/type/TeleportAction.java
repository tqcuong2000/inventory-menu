package infinityi.inventorymenu.action.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.action.Action;
import infinityi.inventorymenu.action.ActionType;
import infinityi.inventorymenu.placeholder.providers.PlaceholderProvider;
import infinityi.inventorymenu.util.teleportutil.TeleportCost;
import infinityi.inventorymenu.util.teleportutil.tplocation.TPLocation;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;


public record TeleportAction(TPLocation target, TeleportCost cost,
                             Boolean safe_check) implements Action, PlaceholderProvider {
    public static final MapCodec<TeleportAction> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    TPLocation.CODEC.fieldOf("pos").forGetter(TeleportAction::target),
                    TeleportCost.CODEC.optionalFieldOf("cost", TeleportCost.empty()).forGetter(TeleportAction::cost),
                    Codec.BOOL.optionalFieldOf("safe_check", false).forGetter(TeleportAction::safe_check)
            ).apply(instance, TeleportAction::new));

    public static Map<String, Supplier<Object>> createKeySuppliers(ServerPlayer player, TeleportAction action) {
        return Map.of(
                "xp_cost", () -> action.cost.calcCost(player, action.target.getPos(player.level().getServer())),
                "xp_cost_type", () -> action.cost.isPoint().toString(),
                "target_pos", () -> {
                        BlockPos pos = action.target.getPos(player.level().getServer());
                        return String.format("X: %s Y: %s Z: %s", pos.getX(), pos.getY(), pos.getZ());
                        },
                "target_name", () -> Optional.ofNullable(action.target.getPlayer(player.level().getServer())).map(s -> s.getName().getString()).orElse("?"),
                "distance", () -> action.target.getDistance(player)
        );
    }

    public static boolean isDangerLocation(ServerLevel world, BlockPos pos) {
        if (world == null || pos == null) {
            return true;
        }
        BlockPos floorPos = pos.below();
        BlockPos headPos = pos.above();
        world.getChunk(floorPos);
        if (!world.getBlockState(floorPos).isRedstoneConductor(world, headPos)) return true;
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

    @Override
    public void execute(ServerPlayer player) {
        BlockPos pos = target.getPos(player.level().getServer());
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

    @Deprecated
    private static int chebyshevDistance(BlockPos a, BlockPos b) {
        int dx = Math.abs(a.getX() - b.getX());
        int dy = Math.abs(a.getY() - b.getY());
        int dz = Math.abs(a.getZ() - b.getZ());
        return Math.max(dx, Math.max(dy, dz));
    }

}