package net.heartsavior;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import javax.script.ScriptException;
import java.util.Map;


public class GroovyScriptNativeShell<O> implements GroovyScript<O> {
    private final String expression;

    private transient groovy.lang.Script parsedScript;

    public GroovyScriptNativeShell(String expression) {
        this.expression = expression;
    }

    public O evaluate(Map<String, Object> params) throws ScriptException {
        groovy.lang.Script parsedScript = getParsedScript();
        O evaluatedResult = null;

        if (params != null) {
            try {
                Binding binding = new Binding(params);
                parsedScript.setBinding(binding);
                evaluatedResult = (O) parsedScript.run();
            } catch (groovy.lang.MissingPropertyException e) {
                throw new ScriptException(e);
            }
        }

        return evaluatedResult;
    }

    private groovy.lang.Script getParsedScript() {
        if (parsedScript == null) {
            GroovyShell groovyShell = new GroovyShell();
            parsedScript = groovyShell.parse(expression);
        }
        return parsedScript;
    }
}
