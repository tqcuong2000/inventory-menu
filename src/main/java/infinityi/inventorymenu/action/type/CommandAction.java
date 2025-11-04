package infinityi.inventorymenu.action.type;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.action.Action;
import infinityi.inventorymenu.action.ActionType;
import infinityi.inventorymenu.placeholder.providers.PlaceholderSets;
import infinityi.inventorymenu.placeholder.resolvers.PlaceholderResolver;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

public record CommandAction(List<String> commands, boolean asPlayer, boolean silent) implements Action {

    public static final Codec<List<String>> COMMAND = Codec.xor(Codec.STRING, Codec.list(Codec.STRING))
            .xmap(either -> either.map(List::of, r -> r), list -> {
                if (list.size() == 1) return Either.left(list.getFirst());
                return Either.right(list);
            });
    public static final MapCodec<CommandAction> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            COMMAND.fieldOf("command").forGetter(CommandAction::commands),
            Codec.BOOL.optionalFieldOf("as_player", false).forGetter(CommandAction::asPlayer),
            Codec.BOOL.optionalFieldOf("silent", false).forGetter(CommandAction::silent)
    ).apply(inst, CommandAction::new));

    @Override
    public void execute(ServerPlayerEntity player) {
        MinecraftServer serverWorld = player.getServer();
        if (serverWorld == null) return;
        ServerCommandSource source = asPlayer ? player.getCommandSource() : serverWorld.getCommandSource();
        if (silent) source = source.withSilent();
        CommandManager manager = serverWorld.getCommandManager();
        for (String command : commands) {
            command = PlaceholderResolver.resolve(Text.of(command), PlaceholderSets.playerServerSet(player), player).getString();
            manager.executeWithPrefix(source, command);
        }
    }


    @Override
    public ActionType getType() {
        return null;
    }
}
