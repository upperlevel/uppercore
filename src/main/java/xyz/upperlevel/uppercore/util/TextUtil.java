package xyz.upperlevel.uppercore.util;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public final class TextUtil {

    private TextUtil() {
    }

    public static final int MAX_LINES = 10;

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

    public static void sendMessages(CommandSender sender, List<String> messages) {
        for (String msg : messages)
            sender.sendMessage(msg);
    }
}
