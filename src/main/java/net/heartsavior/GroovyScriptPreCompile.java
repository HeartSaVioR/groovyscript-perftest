package net.heartsavior;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.Map;


public class GroovyScriptPreCompile<O> implements GroovyScript<O> {
    private final String expression;
    private final ScriptEngine scriptEngine;

    private CompiledScript compiledScript;

    public GroovyScriptPreCompile(String expression, ScriptEngine scriptEngine) {
        this.expression = expression;
        this.scriptEngine = scriptEngine;
    }

    public O evaluate(Map<String, Object> params) throws ScriptException {
        O evaluatedResult = null;

        // lazy compilation
        if (compiledScript == null && scriptEngine instanceof Compilable) {
            System.out.println("compiling script...");
            compiledScript = ((Compilable) scriptEngine).compile(expression);
        }

        if (params != null) {
            try {
                getEngineScopeBindings().putAll(params);
                if (compiledScript != null) {
                    evaluatedResult = (O) compiledScript.eval();
                } else {
                    evaluatedResult = (O) scriptEngine.eval(expression);
                }
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
