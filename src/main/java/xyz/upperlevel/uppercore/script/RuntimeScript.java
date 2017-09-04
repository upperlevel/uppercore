package xyz.upperlevel.uppercore.script;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import lombok.Getter;

import lombok.RequiredArgsConstructor;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

@RequiredArgsConstructor
public class RuntimeScript implements Script {

	@Getter
	private final ScriptEngine engine;
	private final String script;

	@Override
	public Object run(Bindings bindings) throws ScriptException {
		return engine.eval(script, bindings);
	}

	@Override
	public Object run() throws ScriptException {
		return engine.eval(script);
	}

	@Override
	public Object run(ScriptContext context) throws ScriptException {
		return engine.eval(script, context);
	}

	@Override
	public Bindings createBindings() {
		return engine.createBindings();
	}
}
