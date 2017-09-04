package xyz.upperlevel.uppercore.command.argument.exceptions;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import lombok.Getter;

import static org.bukkit.ChatColor.LIGHT_PURPLE;
import static org.bukkit.ChatColor.RED;

public class ParseException extends Exception {

    @Getter
    private final String messageFormatted;

    public ParseException(String in, String type) {
        super(getMessage(in, type, false));
        messageFormatted = getMessage(in, type, true);
    }

    private static String getMessage(String in, String type, boolean format) {
        return (format ? RED : "") + "\"" + (format ? LIGHT_PURPLE : "") + in + (format ? RED : "") + "\"" + " is invalid for type \"" + type + "\".";
    }

}
