package xyz.upperlevel.uppercore.script;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import lombok.RequiredArgsConstructor;

import javax.script.*;

@RequiredArgsConstructor
public class PrecompiledScript implements Script {
    
    private final CompiledScript script;

    @Override
    public Object run(Bindings bindings) throws ScriptException {
        return script.eval(bindings);
    }

    @Override
    public Object run() throws ScriptException {
        return script.eval();
    }

    @Override
    public Object run(ScriptContext context) throws ScriptException {
        return script.eval(context);
    }

    @Override
    public Bindings createBindings() {
        return script.getEngine().createBindings();
    }

    @Override
    public ScriptEngine getEngine() {
        return script.getEngine();
    }
}
