package xyz.upperlevel.uppercore.message;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.placeholder.PlaceholderRegistry;
import xyz.upperlevel.uppercore.placeholder.PlaceholderValue;
import xyz.upperlevel.uppercore.util.TextUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class Message {
    @Getter
    private List<PlaceholderValue<String>> lines;

    public List<String> get(Player player) {
        return lines.stream().map(p -> p.resolve(player)).collect(Collectors.toList());
    }

    public List<String> get(Player player, PlaceholderRegistry placeholders) {
        return lines.stream().map(p -> p.resolve(player, placeholders)).collect(Collectors.toList());
    }

    public List<String> get(Player player, String k1, String v2) {
        return get(player, PlaceholderRegistry.wrap(
                k1, v2
        ));
    }

    public List<String> get(Player player, String k1, String v1, String k2, String v2) {
        return get(player, PlaceholderRegistry.wrap(
                k1, v1,
                k2, v2
        ));
    }

    public List<String> get(Player player, String k1, String v1, String k2, String v2, String k3, String v3) {
        return get(player, PlaceholderRegistry.wrap(
                k1, v1,
                k2, v2,
                k3, v3
        ));
    }

    //--------------FILTER

    public Message filter(PlaceholderRegistry reg) {
        return new Message(
                lines.stream()
                        .map(p -> PlaceholderValue.rawStringValue(p.resolve(null, reg)))
                        .collect(Collectors.toList())
        );
    }

    public Message filter(String k1, String v1) {
        return filter(PlaceholderRegistry.wrap(
                k1, v1
        ));
    }

    public Message filter(String k1, String v1, String k2, String v2) {
        return filter(PlaceholderRegistry.wrap(
                k1, v1,
                k2, v2
        ));
    }

    public Message filter(String k1, String v1, String k2, String v2, String k3, String v3) {
        return filter(PlaceholderRegistry.wrap(
                k1, v1,
                k2, v2,
                k3, v3
        ));
    }


    //--------------SEND

    public void send(Player player) {
        for(PlaceholderValue<String> message : lines)
            player.sendMessage(TextUtil.translateCustom(message.resolve(player)));
    }

    public void send(Player player, PlaceholderRegistry placeholders) {
        for(PlaceholderValue<String> message : lines)
            player.sendMessage(TextUtil.translateCustom(message.resolve(player, placeholders)));
    }

    public void send(Player player, String k1, String v1) {
        send(player, PlaceholderRegistry.wrap(
                k1, v1
        ));
    }

    public void send(Player player, String k1, String v1, String k2, String v2) {
        send(player, PlaceholderRegistry.wrap(
                k1, v1,
                k2, v2
        ));
    }

    public void send(Player player, String k1, String v1, String k2, String v2, String k3, String v3) {
        send(player, PlaceholderRegistry.wrap(
                k1, v1,
                k2, v2,
                k3, v3
        ));
    }


    public void send(CommandSender sender) {
        for(PlaceholderValue<String> message : lines)
            sender.sendMessage(TextUtil.translateCustom(message.resolve(null)));
    }

    public void send(CommandSender sender, PlaceholderRegistry placeholders) {
        for(PlaceholderValue<String> message : lines)
            sender.sendMessage(TextUtil.translateCustom(message.resolve(null, placeholders)));
    }

    public void send(CommandSender sender, String k1, String v1) {
        send(sender, PlaceholderRegistry.wrap(
                k1, v1
        ));
    }

    public void send(CommandSender sender, String k1, String v1, String k2, String v2) {
        send(sender, PlaceholderRegistry.wrap(
                k1, v1,
                k2, v2
        ));
    }

    public void send(CommandSender sender, String k1, String v1, String k2, String v2, String k3, String v3) {
        send(sender, PlaceholderRegistry.wrap(
                k1, v1,
                k2, v2,
                k3, v3
        ));
    }

    //--------------BROADCAST
    public void broadcast(Iterable<? extends Player> players) {
        for(Player player : players)
            for(PlaceholderValue<String> message : lines)
                player.sendMessage(message.resolve(player));
    }

    public void broadcast(Iterable<? extends Player> players, PlaceholderRegistry placeholders) {
        for(Player player : players)
            for(PlaceholderValue<String> message : lines)
                player.sendMessage(message.resolve(player, placeholders));
    }

    public void broadcast(Iterable<? extends Player> players, String k1, String v1) {
        broadcast(players, PlaceholderRegistry.wrap(
                k1, v1
        ));
    }

    public void broadcast(Iterable<? extends Player> players, String k1, String v1, String k2, String v2) {
        broadcast(players, PlaceholderRegistry.wrap(
                k1, v1,
                k2, v2
        ));
    }

    public void broadcast(Iterable<? extends Player> players, String k1, String v1, String k2, String v2, String k3, String v3) {
        broadcast(players, PlaceholderRegistry.wrap(
                k1, v1,
                k2, v2,
                k3, v3
        ));
    }


    public void broadcast() {
        broadcast(Bukkit.getOnlinePlayers());
    }

    public void broadcast(PlaceholderRegistry placeholders) {
        broadcast(Bukkit.getOnlinePlayers(), placeholders);
    }

    public void broadcast(String k1, String v1) {
        broadcast(Bukkit.getOnlinePlayers(), PlaceholderRegistry.wrap(
                k1, v1
        ));
    }

    public void broadcast(String k1, String v1, String k2, String v2) {
        broadcast(Bukkit.getOnlinePlayers(), PlaceholderRegistry.wrap(
                k1, v1,
                k2, v2
        ));
    }

    public void broadcast(String k1, String v1, String k2, String v2, String k3, String v3) {
        broadcast(Bukkit.getOnlinePlayers(), PlaceholderRegistry.wrap(
                k1, v1,
                k2, v2,
                k3, v3
        ));
    }

    public static Message fromConfig(Object obj) {
        if(obj == null)
            return null;
        if(obj instanceof Collection) {
            return new Message(
                    ((Collection<?>) obj).stream()
                            .map(o -> PlaceholderValue.stringValue(o.toString()))
                            .collect(Collectors.toList())
            );
        } else {
            return new Message(Collections.singletonList(PlaceholderValue.stringValue(obj.toString())));
        }
    }
}
