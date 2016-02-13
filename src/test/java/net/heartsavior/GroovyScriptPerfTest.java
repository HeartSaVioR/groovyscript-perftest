package net.heartsavior;

import org.codehaus.groovy.jsr223.GroovyScriptEngineImpl;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.databene.contiperf.report.CSVSummaryReportModule;
import org.databene.contiperf.report.HtmlReportModule;
import org.junit.Rule;
import org.junit.Test;

import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GroovyScriptPerfTest {

    public static final int TEST_DURATION_MILLIS = 90 * 1000;
    public static final int OPERATIONS_PER_TEST = 10000;
    public static final int WARMUP_MILLIS = 30 * 1000;

    @Rule
    public ContiPerfRule rule = new ContiPerfRule(
            new HtmlReportModule(),
            new CSVSummaryReportModule());

    // For avoiding cache hit of evaluation
    /*
    private Map<String, Object> testParam = new HashMap<>(7);
    private static final String GROOVY_EXPRESSION = "a > 10 && b < 30 && c > 20 && d < 40 && e > 50 && f < 70 && g > 90";
    private Map<String, Object> createParam() {
        testParam.put("a", new Random().nextInt(100));
        testParam.put("b", new Random().nextInt(100));
        testParam.put("c", new Random().nextInt(100));
        testParam.put("d", new Random().nextInt(100));
        testParam.put("e", new Random().nextInt(100));
        testParam.put("f", new Random().nextInt(100));
        testParam.put("g", new Random().nextInt(100));
        return testParam;
    }
    */

    // For cache hit of evaluation
    private Map<String, Object> testParam = new HashMap<>(1);
    private static final String GROOVY_EXPRESSION = "a > 10";
    private Map<String, Object> createParam() {
        testParam.put("a", new Random().nextInt(100));
        return testParam;
    }

    private static GroovyScriptEngineImpl groovyScriptEnginePreCompile = new GroovyScriptEngineImpl();
    private static GroovyScriptEngineImpl groovyScriptEngineNoPreCompile = new GroovyScriptEngineImpl();
    private static GroovyScript<Boolean> groovyScriptPreCompile = new GroovyScriptPreCompile<>(GROOVY_EXPRESSION,
            groovyScriptEnginePreCompile);
    private static GroovyScript<Boolean> groovyScriptNoPreCompile = new GroovyScriptNoPreCompile<>(GROOVY_EXPRESSION,
            groovyScriptEngineNoPreCompile);
    private static GroovyScript<Boolean> groovyScriptNativeShell = new GroovyScriptNativeShell<>(GROOVY_EXPRESSION);
    private static GroovyScript<Boolean> groovyScriptNativeShellPerEachThread = new GroovyScriptNativeShellPerEachThread<>(GROOVY_EXPRESSION);

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

    @Test
    @PerfTest(duration = TEST_DURATION_MILLIS, threads = 1, warmUp = WARMUP_MILLIS)
    public void testEvaluateWithGroovyScriptNativeShellPerEachThread() {
        runTest(groovyScriptNativeShellPerEachThread);
    }

    @Test
    @PerfTest(duration = TEST_DURATION_MILLIS, threads = 100, warmUp = WARMUP_MILLIS)
    public void testEvaluateWithGroovyScriptNativeShellInMultipleThreads() {
        runTest(groovyScriptNativeShell);
    }

    @Test
    @PerfTest(duration = TEST_DURATION_MILLIS, threads = 100, warmUp = WARMUP_MILLIS)
    public void testEvaluateWithGroovyScriptNativeShellPerEachThreadInMultipleThreads() {
        runTest(groovyScriptNativeShellPerEachThread);
    }

    private void runTest(GroovyScript<Boolean> groovyScript) {
        for (int curr = 0 ; curr < OPERATIONS_PER_TEST ; curr++) {
            Map<String, Object> param = createParam();
            try {
                groovyScript.evaluate(param);
            } catch (ScriptException e) {
                e.printStackTrace();
            }
        }
    }

}