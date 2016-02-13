package net.heartsavior;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import javax.script.ScriptException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


public class GroovyScriptNativeShell<O> implements GroovyScript<O> {
    private final String expression;

    private final AtomicReference<GroovyShell> groovyShell = new AtomicReference<>();
    private transient ThreadLocal<groovy.lang.Script> parsedScript;

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
            parsedScript = new ThreadLocal<groovy.lang.Script>() {
                @Override
                protected groovy.lang.Script initialValue() {
                    return getGroovyShell().parse(expression);
                }
            };
        }

        return parsedScript.get();
    }

    private GroovyShell getGroovyShell() {
        GroovyShell shell = groovyShell.get();
        if (shell == null) {
            shell = new GroovyShell();
            if (!groovyShell.compareAndSet(null, shell)) {
                shell = groovyShell.get();
            }
        }
        return shell;
    }
}
