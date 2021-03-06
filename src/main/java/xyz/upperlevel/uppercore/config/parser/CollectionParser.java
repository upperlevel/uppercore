package xyz.upperlevel.uppercore.config.parser;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

import java.util.Collection;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class CollectionParser<T extends Collection> extends ConfigParser {
    private final Supplier<T> collectionSupplier;
    private final ConfigParser parser;
    private final Tag expectedTag;

    public CollectionParser(Class<T> handleClass, Supplier<T> setSupplier, ConfigParser parser, Tag expectedTag) {
        super(handleClass);
        this.collectionSupplier = setSupplier;
        this.parser = parser;
        this.expectedTag = expectedTag;
    }

    @Override
    public T parse(Node root) {
        checkTag(root, expectedTag);
        SequenceNode node = (SequenceNode) root;

        T collection = collectionSupplier.get();
        for (Node entry : node.getValue()) {
            collection.add(parser.parse(entry));
        }

        return collection;
    }
}
