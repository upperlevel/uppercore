package xyz.upperlevel.uppercore.config.parser;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;
import xyz.upperlevel.uppercore.config.exceptions.WrongValueConfigException;

public class EnumConfigParser<T extends Enum<T>> extends ConfigParser {
    private Class<T> handleClass;

    public EnumConfigParser(Class<T> handleClass) {
        super(handleClass);
        this.handleClass = handleClass;
    }

    @Override
    public T parse(Node root) {
        checkTag(root, Tag.STR);
        ScalarNode node = (ScalarNode) root;
        String s = node.getValue().replace(' ', '_').toUpperCase();
        try {
            return  Enum.valueOf(handleClass, s);
        } catch (IllegalArgumentException e) {
            throw new WrongValueConfigException(node, node.getValue(), handleClass.getName());
        }
    }
}
