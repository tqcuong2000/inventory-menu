package infinityi.inventoryMenu.PlaceholdersResolver;

import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PlaceholderResolver {

    public static Text resolve(Text input, Map<String, String> values) {
        String template = input.getString();
        String regexString = "%(" + String.join("|", values.keySet()) + ")%";
        Pattern pattern = Pattern.compile(regexString);
        Matcher matcher = pattern.matcher(template);
        return Text.literal(matcher.replaceAll(match -> {
            String key = match.group(1);
            return values.get(key);
        })).setStyle(input.getStyle());
    }

    public static List<Text> resolve(List<Text> textList, Map<String, String> data) {
        return textList.stream().map(line -> resolve(line, data)).collect(Collectors.toList());
    }

}
