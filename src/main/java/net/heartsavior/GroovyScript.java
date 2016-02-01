package net.heartsavior;

import javax.script.ScriptException;
import java.util.Map;

public interface GroovyScript<O> {
    O evaluate(Map<String, Object> params) throws ScriptException;
}
