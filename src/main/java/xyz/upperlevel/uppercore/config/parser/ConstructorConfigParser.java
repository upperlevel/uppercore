package xyz.upperlevel.uppercore.config.parser;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.nodes.*;
import xyz.upperlevel.uppercore.config.*;
import xyz.upperlevel.uppercore.config.exceptions.*;
import xyz.upperlevel.uppercore.util.Pair;

import javax.sound.midi.Track;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingInt;
import static xyz.upperlevel.uppercore.util.GenericUtil.extractClassFromType;

public class ConstructorConfigParser<T> extends ConfigParser<T> {
    private ObjectConstructor<T> targetConstructor;
    @Getter
    private final ConstructorType type;
    private final boolean passPlugin;
    @Getter
    private final boolean inlineable;
    @Getter
    private final Map<String, Property> nodesByName;
    @Getter
    private final List<Property> positionalArguments;

    @Getter
    @Setter
    private Predicate<String> ignoreUnmatchedProperties = s -> false;

    public ConstructorConfigParser(Class<T> declaredClass, ConfigParserRegistry registry, Parameter[] parameters, ObjectConstructor<T> constructor, boolean inlineable) {
        super(declaredClass);
        this.inlineable = inlineable;
        targetConstructor = constructor;

        if (parameters.length > 0 && parameters[0].isAnnotationPresent(CurrentPlugin.class)) {
            passPlugin = true;

            if (parameters[0].getType() != Plugin.class) {
                throw new IllegalStateException("@CurrentPlugin must hold be a Plugin (" + declaredClass.getName() + ")");
            }

            parameters = Arrays.copyOfRange(parameters, 1, parameters.length);
        } else {
            passPlugin = false;
        }

        if (parameters.length == 1 && parameters[0].getType() == Node.class) {
            // Raw node constructor (special case)
            // The constructor will manually parse the node
            type = ConstructorType.RAW_NODE;
            nodesByName = null;
            positionalArguments = null;
        } else if (parameters.length == 1 && parameters[0].getType() == Config.class) {
            // Raw config constructor (special case)
            // The constructor will manually parse the data from the Config instance
            type = ConstructorType.RAW_CONFIG;
            nodesByName = null;
            positionalArguments = null;
        } else {
            type = ConstructorType.NORMAL;
            nodesByName = new HashMap<>();

            positionalArguments = new ArrayList<>(parameters.length);

            // Parse arguments
            for (Parameter parameter : parameters) {
                Property property = new Property(parameter, registry);
                positionalArguments.add(property);
                if (nodesByName.put(property.name, property) != null) {
                    // The constructor class may be different (think about an external constructor)
                    throw new IllegalArgumentException("Found duplicate config value in " + parameter.getDeclaringExecutable().getDeclaringClass().getName());
                }
            }
        }
    }

    public boolean isSpecial() {
        return type != ConstructorType.NORMAL;
    }

    protected T parseSpecial(Plugin plugin, Node root) {
        Object param;
        switch (type) {
            case RAW_NODE: param = root; break;
            case RAW_CONFIG: param = new TrackingConfig(root); break;
            default: throw new IllegalStateException();
        }
        Object[] args;
        if (passPlugin) {
            args = new Object[]{plugin, param};
        } else {
            args = new Object[]{param};
        }
        try {
            return targetConstructor.construct(args);
        } catch (Exception e) {
            throw new IllegalStateException("Could not instantiate " + getHandleClass().getName(), e);
        }
    }

    @Override
    public T parse(Plugin plugin, Node root) {
        if (isSpecial()) {
            return parseSpecial(plugin, root);
        }
        resetEntries();
        if (root.getNodeId() != NodeId.mapping) {
            if (inlineable) {
                return parseInline(plugin, root);
            }
            throw new WrongNodeTypeConfigException(root, NodeId.mapping);
        }
        MappingNode rootMap = (MappingNode) root;
        for (NodeTuple tuple : rootMap.getValue()) {
            String name = extractName(tuple.getKeyNode());
            Property entry = nodesByName.get(name);
            if (entry == null) {
                if (ignoreUnmatchedProperties.test(name)) {
                    continue;
                }
                throw new PropertyNotFoundParsingException(tuple.getKeyNode(), name, getHandleClass());
            }
            if (entry.parsed != null) {
                NodeTuple duplicate = rootMap.getValue().stream()
                        .filter(n -> n != tuple && extractName(n.getKeyNode()).equals(name))
                        .findAny()
                        .get();
                throw new DuplicatePropertyConfigException(tuple.getKeyNode(), duplicate.getKeyNode(), name);
            }
            Node value = tuple.getValueNode();
            entry.parse(plugin, value);
        }
        // Check for required but uninitialized properties
        List<Property> uninitializedProperties = nodesByName.values().stream()
                .filter(n -> n.required && n.parsed == null)
                .collect(Collectors.toList());
        if (!uninitializedProperties.isEmpty()) {
            throw new RequiredPropertyNotFoundConfigException(root, uninitializedProperties.stream().map(p -> p.name).collect(Collectors.toList()));
        }
        return constructObject(plugin);
    }

    protected T parseInline(Plugin plugin, Node root) {
        assert inlineable;// Cannot parseInline a non-inlineable object (or at least, it shouldn't be done)

        if (root.getNodeId() == NodeId.scalar) {
            if (positionalArguments.size() > 1) {
                throw new ConfigException(root, getHandleClass().getSimpleName() + " does not take only one argument");
            }
            // Single argument properties can be constructed even without an explicit list
            if (!positionalArguments.isEmpty()) {
                positionalArguments.get(0).parse(plugin, root);
            }
        } else if (root.getNodeId() == NodeId.sequence) {
            SequenceNode node = ((SequenceNode) root);
            int argsLen = node.getValue().size();
            if (argsLen > positionalArguments.size()) {
                throw new ConfigException(root, "Too many arguments (max: " + positionalArguments.size() + ")");
            }
            for (int i = 0; i < argsLen; i++) {
                positionalArguments.get(i).parse(plugin, node.getValue().get(i));
            }
        } else {
            throw new WrongNodeTypeConfigException(root, NodeId.scalar, NodeId.sequence);
        }
        return constructObject(plugin);
    }

    protected T constructObject(Plugin plugin) {
        int size = positionalArguments.size();

        if (passPlugin) {
            size++;
        }

        Object[] args = new Object[size];
        int index = 0;

        if (passPlugin) {
            args[index++] = plugin;
        }


        for (int i = 0; i < positionalArguments.size(); i++) {
            args[index++] = positionalArguments.get(i).getOrDef();
        }
        try {
            return targetConstructor.construct(args);
        } catch (Exception e) {
            throw new IllegalStateException("Could not instantiate " + getHandleClass().getName(), e);
        }
    }

    protected String extractName(Node rawNode) {
        if (rawNode.getNodeId() != NodeId.scalar) {
            throw new WrongNodeTypeConfigException(rawNode, NodeId.scalar);
        }
        return ((ScalarNode) rawNode).getValue();
    }

    protected void resetEntries() {
        nodesByName.values().forEach((Property n) -> n.parsed = null);
    }

    public void setIgnoreAllUnmatchedProperties(boolean ignoreAll) {
        ignoreUnmatchedProperties = s -> ignoreAll;
    }

    public boolean isUsingArgument(String name) {
        if (type != ConstructorType.NORMAL) {
            // Well, we can't know
            // If the constructor manages the build by itself we can't control
            // which parameter it uses, that can give us strange behaviours
            // in which the library ignores a parameter
            // That is one of the main reasons that the raw types aren't encouraged
            return false;
        }
        return nodesByName.keySet().contains(name);
    }

    private static <T> ConstructorConfigParser<T> createForClass(Class<T> clazz, ConfigParserRegistry registry) {
        Constructor<T>[] constructors = (Constructor<T>[]) clazz.getDeclaredConstructors();
        Constructor<T> targetConstructor = null;
        for (Constructor<T> constructor : constructors) {
            if (constructor.isAnnotationPresent(ConfigConstructor.class)) {
                if (targetConstructor != null) {
                    throw new IllegalStateException("Multiple ConfigConstructors in class " + clazz.getName());
                }
                targetConstructor = constructor;
            }
        }
        if (targetConstructor == null) {
            return null;
        }
        targetConstructor.setAccessible(true);
        Parameter[] parameters = targetConstructor.getParameters();
        ObjectConstructor<T> refinedConstructor = targetConstructor::newInstance;
        boolean inlineable = targetConstructor.getAnnotation(ConfigConstructor.class).inlineable();
        return new ConstructorConfigParser<>(clazz, registry, parameters, refinedConstructor, inlineable);
    }

    private static ConstructorConfigParser<?> createDeclarator(ConfigExternalDeclarator declarator, Method method, ConfigConstructor annotation, ConfigParserRegistry registry) {
        method.setAccessible(true);
        Parameter[] parameters = method.getParameters();
        Class<?> returnType = method.getReturnType();
        ObjectConstructor refinedConstructor = args -> method.invoke(declarator, args);
        @SuppressWarnings("unchecked")
        ConstructorConfigParser<?> parser = new ConstructorConfigParser(returnType, registry, parameters, refinedConstructor, annotation.inlineable());
        return parser;
    }

    public static List<ConstructorConfigParser<?>> createFromDeclarator(ConfigExternalDeclarator declarator, ConfigParserRegistry registry) {
        List<ConstructorConfigParser<?>> parsers = new ArrayList<>();

        for (Method method : declarator.getClass().getDeclaredMethods()) {
            ConfigConstructor annotation = method.getAnnotation(ConfigConstructor.class);
            if (annotation == null) {
                continue;
            }
            parsers.add(createDeclarator(declarator, method, annotation, registry));
        }
        if (parsers.isEmpty()) {
            throw new IllegalStateException("Class " + declarator.getClass() + " does not define any ConfigConstructor!");
        }
        return parsers;
    }

    /**
     * This method differs from {@link #createFromDeclarator(ConfigExternalDeclarator, ConfigParserRegistry)} because this
     * instantly registers the declared config constructors inside of the registry.
     * This permits the in-class config reference (you can first declare Date and then use it in the next method).
     * TODO: this behaviour is not supported for now because the order is quite randomic, in the future it will be
     * implemented trough a priority parameter inserted in the annotation
     *
     * @param declarator the declarator to load the parsers from
     * @param registry   the registry where the parsers will be inserted
     */
    public static void loadFromDeclarator(ConfigExternalDeclarator declarator, ConfigParserRegistry registry) {
        int loaded = 0;

        List<Pair<Integer, Method>> entries = new ArrayList<>();

        for (Method method : declarator.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(ConfigConstructor.class)) {
                continue;
            }
            int priority = 100;
            ExternalDeclaratorPriority priorityzer = method.getAnnotation(ExternalDeclaratorPriority.class);
            if (priorityzer != null) {
                priority = priorityzer.value();
            }
            entries.add(Pair.of(priority, method));
        }
        if (entries.isEmpty()) {
            throw new IllegalStateException("Class " + declarator.getClass() + " does not define any ConfigConstructor!");
        }

        entries.sort(comparingInt(p -> -p.getFirst()));

        for (Pair<Integer, Method> entry : entries) {
            Method method = entry.getSecond();
            ConfigConstructor annotation = method.getAnnotation(ConfigConstructor.class);

            ConstructorConfigParser<?> parser = createDeclarator(declarator, method, annotation, registry);
            registry.register(parser.getHandleClass(), (ConfigParser) parser);
            loaded++;
        }
    }

    protected class Property {
        public String name;
        public boolean required;
        public Parser parser;
        public Object def = null;
        public Object parsed;

        public Property(Parameter parameter, ConfigParserRegistry registry) {
            name = "";
            required = true;

            if (parameter.isAnnotationPresent(CurrentPlugin.class)) {
                throw new IllegalStateException("Only first argument can be @CurrentPlugin (" + parameter.getName() + ")");
            }

            ConfigProperty annotation = parameter.getAnnotation(ConfigProperty.class);
            if (annotation != null) {
                name = annotation.value();
                required = !annotation.optional();
            } else if (!parameter.isNamePresent() && !inlineable) {
                String methodName = parameter.getDeclaringExecutable().getName();

                throw new IllegalArgumentException("Cannot find value of " + parameter.getName() + ","
                        + " in class " + parameter.getDeclaringExecutable().getDeclaringClass().getName() + ","
                        + " in method " + methodName + ","
                        + " Use @ConfigProperty or compile with -parameters");
            } else {
                name = parameter.getName();
            }
            if (!required && parameter.getType().isPrimitive()) {
                throw new IllegalArgumentException("Cannot have optional primitive (parameter: " +
                        parameter.getName() +
                        ", class: " +
                        parameter.getDeclaringExecutable().getDeclaringClass() + ")");
            }
            if (parameter.getType() == Optional.class) {
                required = false;
                def = Optional.empty();
                Type optType = ((ParameterizedType) parameter.getParameterizedType()).getActualTypeArguments()[0];
                ConfigParser nonOptionalParser = registry.getFor(extractClassFromType(optType), optType);
                parser = (plugin, node) -> Optional.of(nonOptionalParser.parse(plugin, node));
            } else {
                parser = registry.getFor(parameter.getType(), parameter.getParameterizedType())::parse;
            }
        }

        public void parse(Plugin plugin, Node node) {
            parsed = parser.parse(plugin, node);
        }

        public Object getOrDef() {
            return parsed == null ? def : parsed;
        }
    }

    public interface ObjectConstructor<T> {
        T construct(Object[] arguments) throws Exception;
    }

    public interface Parser {
        Object parse(Plugin plugin, Node root);
    }

    public enum ConstructorType {
        NORMAL,     // A normal constructor
        RAW_NODE,   // A constructor that accepts a raw node
        RAW_CONFIG, // A constructor that accepts a Config instance
    }
}
