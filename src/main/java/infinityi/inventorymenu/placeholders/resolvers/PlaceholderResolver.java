package infinityi.inventorymenu.placeholders.resolvers;

import infinityi.inventorymenu.placeholders.providers.PlaceholderProvider;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public interface PlaceholderResolver {

    static Text resolve(Text input, Set<PlaceholderProvider> providers, ServerPlayerEntity player) {
        String template = input.getString();
        if (!template.contains("%")) return input;
        String regexString = "%([^%]+)%";
        Pattern pattern = Pattern.compile(regexString);
        Matcher matcher = pattern.matcher(template);
        return Text.literal(matcher.replaceAll(match -> {
            String key = match.group(1);
            return providers.stream()
                    .map(p -> p.getKey(key, player))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst()
                    .orElse("");
        })).setStyle(input.getStyle());
    }

    static List<Text> resolve(List<Text> textList, Set<PlaceholderProvider> providers, ServerPlayerEntity player) {
        return textList.stream().map(line -> resolve(line, providers, player)).collect(Collectors.toList());
    }

}
