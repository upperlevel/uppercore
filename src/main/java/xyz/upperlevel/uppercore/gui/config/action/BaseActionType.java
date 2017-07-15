package xyz.upperlevel.uppercore.gui.config.action;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import xyz.upperlevel.uppercore.gui.config.action.exceptions.BadParameterUseException;
import xyz.upperlevel.uppercore.gui.config.action.exceptions.RequiredParameterNotFoundException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class BaseActionType<T extends Action> extends ActionType<T> {

    private Map<String, Parameter> parameters;
    private int requiredArgs = -1;

    public BaseActionType(String type) {
        super(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T load(Object config) {
        if (parameters == null)
            throw new IllegalStateException("ActionType's parameters not initialized!");
        if (parameters.size() == 0) {//No parameter
            return create(Collections.emptyMap());
        } else if (config == null) {//No parameter
            if (requiredArgs == 0)
                return create(Collections.emptyMap());
            else
                throw new RequiredParameterNotFoundException(getFirstRequired().name);
        } else if (config instanceof Map) {//Multiple parameter
            Map<String, Object> c = (Map<String, Object>) config;
            Map<String, Object> pars = parameters.values().stream()
                    .map(p -> {
                        Object v = c.get(p.name);
                        if (v == null) {
                            v = p.defValue;
                            if (v == null) {
                                if (p.isRequired())
                                    throw new RequiredParameterNotFoundException(p.name);
                                else
                                    return null;
                            }
                        } else
                            v = p.parser.load(v);
                        return Maps.immutableEntry(p.name, v);
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            return create(pars);
        } else {//One parameter
            if (requiredArgs == 1) {
                Map<String, Object> pars = parameters.values().stream()
                        .map(p -> {
                            Object v = p.required ? config : p.defValue;
                            if (v == null)
                                return null;
                            v = p.parser.load(v);
                            return Maps.immutableEntry(p.name, v);
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                return create(pars);
            } else
                throw new BadParameterUseException();
        }
    }

    private Parameter getFirstRequired() {
        return parameters.values().stream().filter(Parameter::isRequired).findFirst().orElse(null);
    }

    public abstract T create(Map<String, Object> parameters);

    @Override
    @SuppressWarnings("unchecked")
    public Object save(T action) {
        Map<String, Object> raw = read(action);
        Map<String, Object> toSave = parameters.values()
                .stream()
                .map(p -> {
                    Object v = raw.get(p.name);
                    if(v == null) {
                        if(p.isRequired())
                            throw new IllegalStateException("Required parameter not given! name: " + p.name);
                        return null;
                    }
                    if(Objects.equals(v, p.defValue))
                        return null;
                    return Maps.immutableEntry(p.name, p.parser.save(v));
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        switch (toSave.size()) {
            case 0: return null;
            case 1: return toSave.values().iterator().next();
            default: return toSave;
        }
    }

    public abstract Map<String, Object> read(T action);

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters.stream().collect(Collectors.toMap(Parameter::getName, Function.identity()));
        this.requiredArgs = (int) parameters.stream().filter(Parameter::isRequired).count();
    }

    public void setParameters(Parameter... parameters) {
        setParameters(Arrays.asList(parameters));
    }

    public Collection<Parameter> getParameters() {
        return Collections.unmodifiableCollection(parameters.values());
    }

    public static class Parameter<T> {
        @Getter
        private final String name;
        @Getter
        private final Parser<T> parser;
        @Getter
        private final T defValue;

        @Getter
        @Setter
        @Accessors(chain = true)
        private boolean required;

        public Parameter(String name, Parser<T> parser, T defVaule) {
            this.name = name;
            this.parser = parser;
            this.defValue = defVaule;
            required = defVaule == null;
        }

        public Parameter(String name, Parser<T> parser) {
            this(name, parser, null);
        }

        public static <T> Parameter<T> of(String name, Parser<T> parser, T defValue, boolean required) {
            return new Parameter<>(name, parser, defValue).setRequired(required);
        }

        public static <T> Parameter<T> of(String name, Parser<T> parser, T defValue) {
            return new Parameter<>(name, parser, defValue);
        }

        public static <T> Parameter<T> of(String name, Parser<T> parser, boolean required) {
            return new Parameter<>(name, parser).setRequired(required);
        }

        public static <T> Parameter<T> of(String name, Parser<T> parser) {
            return new Parameter<>(name, parser);
        }

        @Override
        public String toString() {
            StringJoiner joiner = new StringJoiner(", ");
            fillToString(joiner);
            return '{' + joiner.toString() + '}';
        }

        protected void fillToString(StringJoiner joiner) {
            joiner.add("name: " + name);
            joiner.add("parser: " + parser.getClass().getSimpleName());
            if(defValue != null)
                joiner.add("def: " + defValue);
            if(required)
                joiner.add("required");
        }
    }
}
