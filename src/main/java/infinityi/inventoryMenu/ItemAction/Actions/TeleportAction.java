package infinityi.inventoryMenu.ItemAction.Actions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventoryMenu.ItemAction.Action;
import infinityi.inventoryMenu.ItemAction.ActionType;
import infinityi.inventoryMenu.MenuLayout.MenuLayout;
import infinityi.inventoryMenu.TeleportUtil.TPLocation.TPLocation;
import infinityi.inventoryMenu.TeleportUtil.TeleportCost;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

import java.util.HashMap;
import java.util.Map;

public record TeleportAction(TPLocation target, TeleportCost cost, Boolean safe_check) implements Action {
    public static final MapCodec<TeleportAction> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
            TPLocation.CODEC.fieldOf("pos").forGetter(TeleportAction::target),
                    TeleportCost.CODEC.optionalFieldOf("cost", TeleportCost.empty()).forGetter(TeleportAction::cost),
            Codec.BOOL.optionalFieldOf("safe_check",false).forGetter(TeleportAction::safe_check)
    ).apply(instance, TeleportAction::new));

    private static boolean isDangerBlock(BlockState state) {
        return state.isOf(Blocks.LAVA) || state.isOf(Blocks.FIRE) || state.isOf(Blocks.CACTUS) || state.isOf(Blocks.SWEET_BERRY_BUSH) || state.isOf(Blocks.POWDER_SNOW);
    }

    @Override
    public void execute(ServerPlayerEntity player, MenuLayout layout) {
        BlockPos pos = target.getPos(player.getServer());
        boolean isPlayerTarget = target.getPlayer(player.getServer()) != null;
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
    public JsonElement getData() {
        return CODEC.codec().encode(this, JsonOps.INSTANCE, new JsonObject())
                .getOrThrow(error -> new IllegalStateException("Error encoding! " + error));
    }

    @Override
    public Map<String, String> placeholderData(ServerPlayerEntity player) {
        Map<String, String> placeholders = new HashMap<>();
        BlockPos pos = target.getPos(player.getServer());
        ServerPlayerEntity targetPlayer = target.getPlayer(player.getServer());

        placeholders.put("xpcost", String.valueOf(cost.calcCost(player, pos)));
        placeholders.put("xpcosttype", String.valueOf(cost.isPoint()));
        placeholders.put("targetpos", pos == null ? "§cOffline!" : String.format("X: %s Y: %s Z: %s", pos.getX(), pos.getY(), pos.getZ()));
        placeholders.put("targetname", targetPlayer == null ? "" : targetPlayer.getName().getString());
        placeholders.put("distance", String.valueOf(target.getDistance(player)));
        return placeholders;
    }

    public static boolean isDangerLocation(ServerWorld world, BlockPos pos) {
        if (world == null || pos == null) {
            return true;
        }
        BlockPos floorPos = pos.down();
        BlockPos headPos = pos.up();
        Chunk chunk = world.getChunk(floorPos);
        if (!world.getBlockState(floorPos).isSolidBlock(world, headPos)) return true;
        BlockState feetState = world.getBlockState(pos);
        BlockState headState = world.getBlockState(headPos);
        if (feetState.shouldSuffocate(world, pos) || headState.shouldSuffocate(world, headPos)) return true;
        return isDangerBlock(feetState) || isDangerBlock(headState);
    }

    public static int distanceBetween(BlockPos pos1, BlockPos pos2){
        return pos1.getChebyshevDistance(pos2);
    }
}