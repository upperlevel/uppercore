package xyz.upperlevel.uppercore.config.parser;

import lombok.Getter;
import lombok.Setter;
import org.yaml.snakeyaml.nodes.*;
import xyz.upperlevel.uppercore.config.*;
import xyz.upperlevel.uppercore.config.exceptions.*;
import xyz.upperlevel.uppercore.util.Pair;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingInt;

public class ConstructorConfigParser<T> extends ConfigParser {
    private ObjectConstructor<T> targetConstructor;
    @Getter
    private final ConstructorType type;
    @Getter
    private final Class<T> declaredClass;
    @Getter
    private final boolean inlineable;
    @Getter
    private final Map<String, Property> nodesByName;
    @Getter
    private final List<Property> positionalArguments;
    private final Set<String> unfoldingNodes = new HashSet<>();

    @Getter
    @Setter
    private Predicate<String> ignoreUnmatchedProperties = s -> false;

    public ConstructorConfigParser(Class<T> declaredClass, ConfigParserRegistry registry, Parameter[] parameters, ObjectConstructor<T> constructor, boolean inlineable) {
        super(declaredClass);
        this.declaredClass = declaredClass;

        this.inlineable = inlineable;
        targetConstructor = constructor;

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

                if (unfoldingNodes.contains(property.name)) {
                    throw onUsedPropertyUnfolding(property.name);
                }

                addUnfoldNodes(property.name);

                if (nodesByName.put(property.name, property) != null) {
                    // The constructor class may be different (think about an external constructor)
                    throw new IllegalArgumentException(
                            "Found duplicate config value in " +
                            parameter.getDeclaringExecutable().getDeclaringClass().getName() +
                            ", name: '" + property.name + "'"
                    );
                }
            }
        }
    }

    public void addUnfoldNodes(String pname) {
        int li = pname.length();
        while (true) {
            li = pname.lastIndexOf('.', li - 1);
            if (li < 0) break;
            String subn = pname.substring(0, li);

            if (nodesByName.containsKey(subn)) {
                throw onUsedPropertyUnfolding(subn);
            }
            if (!unfoldingNodes.add(subn)) return;
        }
    }

    protected RuntimeException onUsedPropertyUnfolding(String name) {
        return new IllegalArgumentException(
                "Unfolding already used property in class " + declaredClass.getName() + ", name: '" + name + "'"
        );
    }

    /**
     * A ConstructorConfigParser is regular when it is defined with parameters, not with a catch-everything config
     * object nor with a raw yaml Node, in the last two cases it is named as "special".
     * @return true only if the node is "special"
     */
    public boolean isSpecial() {
        return type != ConstructorType.NORMAL;
    }

    protected T parseSpecial(Node root) {
        Object param;
        switch (type) {
            case RAW_NODE: param = root; break;
            case RAW_CONFIG: param = new TrackingConfig(root); break;
            default: throw new IllegalStateException();
        }

        try {
            return targetConstructor.construct(new Object[]{param});
        } catch (Exception e) {
            throw new ConfigException(null, null, e.getMessage(), root.getStartMark(), null, e);
        }
    }

    public void mapUnfold(String prefix, MappingNode node) {
        if (node.getValue().isEmpty()) return;
        for (NodeTuple t : node.getValue()) {
            fill(prefix, t.getKeyNode(), t.getValueNode());
        }
    }

    public void fill(String prefix, Node key, Node value) {
        String name = prefix + extractName(key);

        if (unfoldingNodes.contains(name)) {
            checkNodeId(value, NodeId.mapping);
            mapUnfold( prefix + name + ".", (MappingNode) value);
            return;
        }

        Property entry = nodesByName.get(name);
        if (entry == null) {
            if (ignoreUnmatchedProperties.test(name)) return;

            throw new PropertyNotFoundParsingException(key, name, declaredClass);
        }
        if (entry.parsed != null) {
            throw new DuplicatePropertyConfigException(key, entry.source, name);
        }
        entry.parse(key, value);
    }

    @Override
    public T parse(Node root) {
        if (isSpecial()) {
            return parseSpecial(root);
        }
        resetEntries();
        if (root.getNodeId() != NodeId.mapping) {
            if (inlineable) {
                return parseInline(root);
            }
            throw new WrongNodeTypeConfigException(root, NodeId.mapping);
        }
        MappingNode rootMap = (MappingNode) root;
        for (NodeTuple tuple : rootMap.getValue()) {
            fill("", tuple.getKeyNode(), tuple.getValueNode());
        }
        // Check for required but uninitialized properties
        List<Property> uninitializedProperties = nodesByName.values().stream()
                .filter(n -> n.required && n.parsed == null)
                .collect(Collectors.toList());
        if (!uninitializedProperties.isEmpty()) {
            throw new RequiredPropertyNotFoundConfigException(root, uninitializedProperties.stream().map(p -> p.name).collect(Collectors.toList()));
        }
        return constructObject(root);
    }

    protected T parseInline(Node root) {
        assert inlineable;// Cannot parseInline a non-inlineable object (or at least, it shouldn't be done)

        if (root.getNodeId() == NodeId.scalar) {
            if (positionalArguments.size() > 1) {
                throw new ConfigException(declaredClass.getSimpleName() + " does not take only one argument", root);
            }
            // Single argument properties can be constructed even without an explicit list
            if (!positionalArguments.isEmpty()) {
                positionalArguments.get(0).parse(null, root);
            }
        } else if (root.getNodeId() == NodeId.sequence) {
            SequenceNode node = ((SequenceNode) root);
            int argsLen = node.getValue().size();
            if (argsLen > positionalArguments.size()) {
                throw new ConfigException("Too many arguments (max: " + positionalArguments.size() + ")", root);
            }
            for (int i = 0; i < argsLen; i++) {
                positionalArguments.get(i).parse(null, node.getValue().get(i));
            }
        } else {
            throw new WrongNodeTypeConfigException(root, NodeId.scalar, NodeId.sequence);
        }
        return constructObject(root);
    }

    protected T constructObject(Node root) {
        int size = positionalArguments.size();

        Object[] args = new Object[size];
        int index = 0;

        for (int i = 0; i < positionalArguments.size(); i++) {
            args[index++] = positionalArguments.get(i).getOrDef();
        }
        try {
            return targetConstructor.construct(args);
        } catch (InvocationTargetException e) {
            throw new ConfigException(null, null, e.getCause().getMessage(), root.getStartMark(), null, e.getCause());
        } catch (Exception e) {
            throw new ConfigException(null, null, e.getMessage(), root.getStartMark(), null, e);
        }
    }

    protected String extractName(Node rawNode) {
        if (rawNode.getNodeId() != NodeId.scalar) {
            throw new WrongNodeTypeConfigException(rawNode, NodeId.scalar);
        }
        return ((ScalarNode) rawNode).getValue();
    }

    protected void resetEntries() {
        nodesByName.values().forEach(Property::reset);
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
        return nodesByName.containsKey(name);
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
            registry.register((Class)parser.getHandleClass(), parser);
            loaded++;
        }
    }

    protected class Property {
        public String name;
        public boolean required;
        public Parser parser;
        public Object def = null;

        public Node source;
        public Object parsed;

        public Property(Parameter parameter, ConfigParserRegistry registry) {
            name = "";
            required = true;

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
                ConfigParser nonOptionalParser = registry.getFor(optType);
                parser = node -> Optional.of(nonOptionalParser.parse(node));
            } else {
                parser = registry.getFor(parameter.getParameterizedType())::parse;
            }
        }

        public void parse(Node key, Node value) {
            source = key;
            parsed = parser.parse(value);
        }

        public Object getOrDef() {
            return parsed == null ? def : parsed;
        }

        public void reset() {
            source = null;
            parsed = null;
        }
    }

    public interface ObjectConstructor<T> {
        T construct(Object[] arguments) throws Exception;
    }

    public interface Parser {
        Object parse(Node root);
    }

    public enum ConstructorType {
        NORMAL,     // A normal constructor
        RAW_NODE,   // A constructor that accepts a raw node
        RAW_CONFIG, // A constructor that accepts a Config instance
    }
}
