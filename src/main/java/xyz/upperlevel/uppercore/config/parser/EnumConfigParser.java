package xyz.upperlevel.uppercore.config.parser;

import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;
import xyz.upperlevel.uppercore.config.exceptions.WrongValueConfigException;

public class EnumConfigParser<T extends Enum<T>> extends ConfigParser<T> {
    public EnumConfigParser(Class<T> handleClass) {
        super(handleClass);
    }

    @Override
    public T parse(Plugin plugin, Node root) {
        checkTag(root, Tag.STR);
        ScalarNode node = (ScalarNode) root;
        String s = node.getValue().replace(' ', '_').toUpperCase();
        try {
            return  Enum.valueOf(getHandleClass(), s);
        } catch (IllegalArgumentException e) {
            throw new WrongValueConfigException(node, node.getValue(), getHandleClass().getName());
        }
    }
}
