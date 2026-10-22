package infinityi.inventorymenu.menu;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.action.Action;
import infinityi.inventorymenu.action.type.MessageAction;
import infinityi.inventorymenu.action.type.NoAction;
import infinityi.inventorymenu.dataparser.ConfigManager;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public record MenuPredicate(Identifier predicate, Action whenTrue, Action whenFalse) {

    public static final MenuPredicate EMPTY = new MenuPredicate(Identifier.fromNamespaceAndPath("inventory-menu", "empty"), new NoAction(), new NoAction());
    public static final Codec<MenuPredicate> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Identifier.CODEC.fieldOf("id").forGetter(MenuPredicate::predicate),
            Action.CODEC.optionalFieldOf("on_true", new NoAction()).forGetter(MenuPredicate::whenTrue),
            Action.CODEC.optionalFieldOf("on_false", new NoAction()).forGetter(MenuPredicate::whenFalse)
    ).apply(inst, MenuPredicate::new));

    public boolean test(ServerPlayer player, String context) {
        ServerLevel serverWorld = player.level();
        MinecraftServer server = serverWorld.getServer();
        LootParams worldContext = new LootParams.Builder(serverWorld)
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(player.blockPosition()))
                .withParameter(LootContextParams.DAMAGE_SOURCE, player.damageSources().playerAttack(player))
                .create(LootContextParamSets.ENTITY);

        LootContext lootContext = new LootContext.Builder(worldContext)
                .withOptionalRandomSeed(serverWorld.getSeed())
                .create(Optional.empty());
        boolean result = read(server).map(condition -> condition.test(lootContext)).orElse(true);
        defaultMessage(player, result, context);
        if (result) executeAction(whenTrue, player);
        if (!result) executeAction(whenFalse, player);
        return result;
    }

    public Optional<LootItemCondition> read(MinecraftServer server) {
        return server.reloadableRegistries()
                .lookup()
                .lookupOrThrow(Registries.PREDICATE)
                .get(ResourceKey.create(Registries.PREDICATE, predicate))
                .map(Holder::value);
    }

    private void defaultMessage(ServerPlayer player, boolean result, String context) {
        if (!ConfigManager.getConfig().show_predicate_message) return;
        if (!(whenFalse.unwrapped() instanceof NoAction) || result) return;
        if (context.equals("menu")) {
            new MessageAction(List
                    .of(Component.translatable("§cYou can't open this menu")), false)
                    .execute(player);
            player.closeContainer();
        }
        if (context.equals("item")) {
            new MessageAction(List
                    .of(Component.translatable("§cYou can't run this action")), false)
                    .execute(player);
        }
    }

    private void executeAction(Action action, ServerPlayer player) {
        if (!(action.unwrapped() instanceof NoAction)) action.executeWithCost(player);
    }
}
