package xyz.upperlevel.uppercore.script;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.upperlevel.uppercore.economy.EconomyManager;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;

import javax.script.*;
import java.util.function.Function;

public interface Script {

    Object run(Bindings bindings) throws ScriptException;

    Object run() throws ScriptException;

    Object run(ScriptContext context) throws ScriptException;

    default Object execute(Player player) throws ScriptException {
        final Thread currentThread = Thread.currentThread();
        final ClassLoader oldLoader = currentThread.getContextClassLoader();
        try {
            currentThread.setContextClassLoader(ScriptSystem.getClassLoader());
            Bindings b = createBindings();
            b.put("player", player);
            b.put("placeholder", (Function<String, String>) str -> PlaceholderUtil.resolve(player, str));
            b.put("balance", EconomyManager.get(player));

            return run(b);
        } finally {
            currentThread.setContextClassLoader(oldLoader);
        }
    }

    Bindings createBindings();

    ScriptEngine getEngine();

    static Script of(ScriptEngine engine, String str) throws ScriptException {
        if (engine instanceof Compilable)
            return new PrecompiledScript(((Compilable) engine).compile(str));
        else
            return new RuntimeScript(engine, str);
    }

    static Script fake(Object result) {
        return new FakeScript(result);
    }

    Script EMPTY = new FakeScript(null);

    @RequiredArgsConstructor
    class FakeScript implements Script {
        private final Object result;

        @Override
        public Object run(Bindings bindings) throws ScriptException {
            return result;
        }

        @Override
        public Object run() throws ScriptException {
            return result;
        }

        @Override
        public Object run(ScriptContext context) throws ScriptException {
            return result;
        }

        @Override
        public Bindings createBindings() {
            return new SimpleBindings();
        }

        @Override
        public ScriptEngine getEngine() {
            return null;
        }
    };

}
