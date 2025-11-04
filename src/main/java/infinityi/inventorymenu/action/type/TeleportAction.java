package infinityi.inventorymenu.action.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.action.Action;
import infinityi.inventorymenu.action.ActionType;
import infinityi.inventorymenu.placeholder.providers.PlaceholderProvider;
import infinityi.inventorymenu.util.teleportutil.TeleportCost;
import infinityi.inventorymenu.util.teleportutil.tplocation.TPLocation;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

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

    public static Map<String, Supplier<Object>> createKeySuppliers(ServerPlayerEntity player, TeleportAction action) {
        return Map.of(
                "xp_cost", () -> action.cost.calcCost(player, action.target.getPos(player.getEntityWorld().getServer())),
                "xp_cost_type", () -> action.cost.isPoint().toString(),
                "target_pos", () -> {
                        BlockPos pos = action.target.getPos(player.getEntityWorld().getServer());
                        return String.format("X: %s Y: %s Z: %s", pos.getX(), pos.getY(), pos.getZ());
                        },
                "target_name", () -> Optional.ofNullable(action.target.getPlayer(player.getEntityWorld().getServer())).map(s -> s.getName().getString()).orElse("?"),
                "distance", () -> action.target.getDistance(player)
        );
    }

    public static boolean isDangerLocation(ServerWorld world, BlockPos pos) {
        if (world == null || pos == null) {
            return true;
        }
        BlockPos floorPos = pos.down();
        BlockPos headPos = pos.up();
        world.getChunk(floorPos);
        if (!world.getBlockState(floorPos).isSolidBlock(world, headPos)) return true;
        BlockState feetState = world.getBlockState(pos);
        BlockState headState = world.getBlockState(headPos);
        if (feetState.shouldSuffocate(world, pos) || headState.shouldSuffocate(world, headPos)) return true;
        return isDangerBlock(feetState) || isDangerBlock(headState);
    }

    public static int distanceBetween(BlockPos pos1, BlockPos pos2) {
        return chebyshevDistance(pos1, pos2);
    }

    private static boolean isDangerBlock(BlockState state) {
        return state.isOf(Blocks.LAVA) || state.isOf(Blocks.FIRE) || state.isOf(Blocks.CACTUS) || state.isOf(Blocks.SWEET_BERRY_BUSH) || state.isOf(Blocks.POWDER_SNOW);
    }

    @Override
    public void execute(ServerPlayerEntity player) {
        BlockPos pos = target.getPos(player.getEntityWorld().getServer());
        if (cost.hasCost(player, pos)) {
            target.teleport(player, safe_check, cost);
        } else {
            player.sendMessage(Text.translatable("Not enough experience.").formatted(Formatting.RED));
        }
    }

    @Override
    public ActionType getType() {
        return ActionType.TELEPORT;
    }

    @Override
    public Optional<String> getKey(String key, ServerPlayerEntity player) {
        Map<String, Supplier<Object>> keySuppliers = createKeySuppliers(player, this);
        return Optional.ofNullable(keySuppliers.get(key))
                .map(Supplier::get)
                .map(String::valueOf);
    }

    private static int chebyshevDistance(BlockPos a, BlockPos b) {
        int dx = Math.abs(a.getX() - b.getX());
        int dy = Math.abs(a.getY() - b.getY());
        int dz = Math.abs(a.getZ() - b.getZ());
        return Math.max(dx, Math.max(dy, dz));
    }

}