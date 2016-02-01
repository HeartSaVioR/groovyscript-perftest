package net.heartsavior;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.databene.contiperf.report.CSVSummaryReportModule;
import org.databene.contiperf.report.HtmlReportModule;
import org.codehaus.groovy.jsr223.GroovyScriptEngineImpl;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GroovyScriptPerfTest {

    public static final int TEST_DURATION_MILLIS = 90 * 1000;
    public static final int OPERATIONS_PER_TEST = 10000;
    public static final int WARMUP_MILLIS = 30 * 1000;

    private static final List<Map<String, Object>> testParams = new ArrayList<>();

    @Rule
    public ContiPerfRule rule = new ContiPerfRule(
            new HtmlReportModule(),
            new CSVSummaryReportModule());

    public static final String GROOVY_EXPRESSION = "a > 10 && b < 30 && c > 20 && d < 40 && e > 50 && f < 70 && g > 90";

    private static GroovyScriptEngineImpl groovyScriptEnginePreCompile = new GroovyScriptEngineImpl();
    private static GroovyScriptEngineImpl groovyScriptEngineNoPreCompile = new GroovyScriptEngineImpl();
    private static GroovyScript<Boolean> groovyScriptPreCompile = new GroovyScriptPreCompile<>(GROOVY_EXPRESSION,
            groovyScriptEnginePreCompile);
    private static GroovyScript<Boolean> groovyScriptNoPreCompile = new GroovyScriptNoPreCompile<>(GROOVY_EXPRESSION,
            groovyScriptEngineNoPreCompile);
    private static GroovyScript<Boolean> groovyScriptNativeShell = new GroovyScriptNativeShell<>(GROOVY_EXPRESSION);

    @BeforeClass
    public static void setUpClass() {
        for (int i = 0; i <= 100000 ; i++) {
            HashMap<String, Object> pair = new HashMap<>();
            pair.put("a", new Random().nextInt(100));
            pair.put("b", new Random().nextInt(100));
            pair.put("c", new Random().nextInt(100));
            pair.put("d", new Random().nextInt(100));
            pair.put("e", new Random().nextInt(100));
            pair.put("f", new Random().nextInt(100));
            pair.put("g", new Random().nextInt(100));

            testParams.add(pair);
        }
    }

    @Test
    @PerfTest(duration = TEST_DURATION_MILLIS, threads = 1, warmUp = WARMUP_MILLIS)
    public void testEvaluateWithCompileIfPossible() {
        runTest(groovyScriptPreCompile);
    }

    @Test
    @PerfTest(duration = TEST_DURATION_MILLIS, threads = 1, warmUp = WARMUP_MILLIS)
    public void testEvaluateNoPreCompile() {
        runTest(groovyScriptNoPreCompile);
    }

    @Test
    @PerfTest(duration = TEST_DURATION_MILLIS, threads = 1, warmUp = WARMUP_MILLIS)
    public void testEvaluateWithGroovyScriptNativeShell() {
        runTest(groovyScriptNativeShell);
    }

    private void runTest(GroovyScript<Boolean> groovyScript) {
        for (int curr = 0 ; curr < OPERATIONS_PER_TEST ; curr++) {
            Map<String, Object> param = testParams.get(curr % testParams.size());
            try {
                groovyScript.evaluate(param);
            } catch (ScriptException e) {
                e.printStackTrace();
            }
        }
    }
}