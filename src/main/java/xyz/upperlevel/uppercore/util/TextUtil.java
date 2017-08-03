package xyz.upperlevel.uppercore.util;

import net.md_5.bungee.api.chat.BaseComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public final class TextUtil {
    public static final char CUSTOM_CONTROL_CHAR = '^';
    public static final char CONFIG_CONTROL_CHAR = '&';
    public static final char CONTROL_CHAR = 167;
    public static final int MAX_LINES = 10;
    private static final int CENTER_PX = 154;
    private static final int LINE_PX = CENTER_PX * 2;
    private static final String LINE = StringUtils.repeat("-", LINE_PX / (DefaultFontInfo.MINUS.getLength() + 1));

    private static final BitSet codes = new BitSet();

    static {
        for(ChatColor c : ChatColor.values()) {
            char ch = c.getChar();
            codes.set(ch);
            codes.set(Character.toUpperCase(ch));
        }
    }

    public static int getPages(int headerSize, int entriesSize, int footerSize) {
        double entriesPerPage = MAX_LINES - headerSize - footerSize;
        return (int) Math.ceil(entriesSize / entriesPerPage);
    }

    public static List<String> getPage(List<String> header, List<String> entries, List<String> footer, int page) {
        List<String> result = new ArrayList<>();
        result.addAll(header);
        int entriesPerPage = MAX_LINES - header.size() - footer.size();
        if (page < 0)
            page = 0;
        if (entriesPerPage > 0)
            result.addAll(entries.subList(page * entriesPerPage, Math.min(entries.size(), (page + 1) * entriesPerPage)));
        result.addAll(footer);
        return result;
    }

    public static List<BaseComponent[]> getComponentPage(List<BaseComponent[]> header, List<BaseComponent[]> entries, List<BaseComponent[]> footer, int page) {
        List<BaseComponent[]> result = new ArrayList<>();
        result.addAll(header);
        int entriesPerPage = MAX_LINES - header.size() - footer.size();
        if (page < 0)
            page = 0;
        if (entriesPerPage > 0)
            result.addAll(entries.subList(page * entriesPerPage, Math.min(entries.size(), (page + 1) * entriesPerPage)));
        result.addAll(footer);
        return result;
    }


    public static void sendMessages(CommandSender sender, List<String> messages) {
        for (String msg : messages)
            sender.sendMessage(msg);
    }

    public static void sendComponentMessages(CommandSender sender, List<BaseComponent[]> messages) {
        for (BaseComponent[] msg : messages)
            sender.spigot().sendMessage(msg);
    }

    public static String translateCustom(String text) {
        char[] b = text.toCharArray();
        if(b.length == 0) return text;
        if(b.length >= 2 && b[0] == CUSTOM_CONTROL_CHAR) {
            ChatColor color = ChatColor.getByChar(b[1]);
            if (color != null)
                return separator(color);
            switch (b[1]) {
                case '|':
                    return center(b, 2, false);
                case '>':
                    return leftPad(b, 2, false);
                case '<':
                    return new String(b, 2, b.length - 2);
                case CUSTOM_CONTROL_CHAR:
                    return new String(b, 1, b.length - 1);
            }
        }
        return text;
    }

    public static String translate(String text) {
        char[] b = text.toCharArray();
        if(b.length == 0) return text;
        if(b.length >= 2 && b[0] == CUSTOM_CONTROL_CHAR) {
            ChatColor color = ChatColor.getByChar(b[1]);
            if (color != null)
                return separator(color);
            switch (b[1]) {
                case '|':
                    return center(b, 2, true);
                case '>':
                    return leftPad(b, 2, true);
                case '<':
                    return translatePlain(b, 2);
                case CUSTOM_CONTROL_CHAR:
                    return translatePlain(b, 1);
            }
        }
        return translatePlain(b, 0);
    }

    public static String translatePlain(char[] b, int offset) {
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == CONFIG_CONTROL_CHAR && codes.get(b[i + 1])) {
                b[i] = CONTROL_CHAR;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
                i++;
            }
        }
        return new String(b, offset, b.length - offset);
    }

    public static String translatePlain(String string) {
        return translatePlain(string.toCharArray(), 0);
    }

    public static String center(char[] b, int offset, boolean translate) {
        int size = translate ? translateAndMeasure(b, offset) : measure(b, offset);

        int halvedMessageSize = size / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;

        StringBuilder sb = new StringBuilder(b.length - offset + toCompensate/spaceLength);

        while(compensated < toCompensate){
            sb.append(" ");
            compensated += spaceLength;
        }
        sb.append(b, offset, b.length - offset);
        return sb.toString();
    }

    public static String leftPad(char[] b, int offset, boolean translate) {
        int size = translate ? translateAndMeasure(b, offset) : measure(b, offset);

        int toCompensate = LINE_PX - size;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;

        int times = toCompensate / spaceLength;

        StringBuilder sb = new StringBuilder(b.length - offset + times);
        for(int i = 0; i < times; i++)
            sb.append(' ');
        sb.append(b, offset, b.length - offset);
        return sb.toString();
    }

    public static String separator(ChatColor color) {
        return color + LINE;
    }

    private static int translateAndMeasure(char[] b, int offset) {
        boolean isBold = false;
        int size = 0;
        for (int i = offset, len = b.length; i < len; i++) {
            char c = b[i];
            if (c == CONFIG_CONTROL_CHAR || c == CONTROL_CHAR) {
                ChatColor color = ChatColor.getByChar(Character.toLowerCase(b[i + 1]));
                if(color != null) {
                    b[i] = CONTROL_CHAR;
                    isBold = color == ChatColor.BOLD;
                    i++;
                }
            } else {
                DefaultFontInfo info = DefaultFontInfo.getDefaultFontInfo(c);
                size += isBold ? info.getBoldLength() : info.getLength();
                size++;
            }
        }
        return size;
    }

    private static int measure(char[] b, int offset) {
        boolean isBold = false;
        int size = 0;
        for (int i = offset, len = b.length; i < len; i++) {
            char c = b[i];
            if (c == CONTROL_CHAR) {
                isBold = Character.toLowerCase(b[++i]) != ChatColor.BOLD.getChar();
            } else {
                DefaultFontInfo info = DefaultFontInfo.getDefaultFontInfo(c);
                size += isBold ? info.getBoldLength() : info.getLength();
                size++;
            }
        }
        return size;
    }

    private TextUtil() {}
}
