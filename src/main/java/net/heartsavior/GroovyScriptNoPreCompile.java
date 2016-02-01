package net.heartsavior;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.Map;


public class GroovyScriptNoPreCompile<O> implements GroovyScript<O> {
    private final String expression;
    private final ScriptEngine scriptEngine;

    public GroovyScriptNoPreCompile(String expression, ScriptEngine scriptEngine) {
        this.expression = expression;
        this.scriptEngine = scriptEngine;
    }

    public O evaluate(Map<String, Object> params) throws ScriptException {
        O evaluatedResult = null;

        if (params != null) {
            try {
                getEngineScopeBindings().putAll(params);
                evaluatedResult = (O) scriptEngine.eval(expression);
            } finally {
                clearBindings();
            }
        }

        return evaluatedResult;
    }

    private Bindings getEngineScopeBindings() {
        return scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
    }

    private void clearBindings() {
        getEngineScopeBindings().clear();
    }
}
