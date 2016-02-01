package net.heartsavior;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import javax.script.ScriptException;
import java.util.Map;


public class GroovyScriptNativeShell<O> implements GroovyScript<O> {
    private final String expression;
    private final Script parsedScript;

    private GroovyShell groovyShell;

    public GroovyScriptNativeShell(String expression) {
        this.expression = expression;
        this.groovyShell = new GroovyShell();
        this.parsedScript = groovyShell.parse(this.expression);
    }

    public O evaluate(Map<String, Object> params) throws ScriptException {
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
}
