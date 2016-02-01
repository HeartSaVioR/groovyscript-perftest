Groovy Script - Performance Test
----

Performance test for running script using one-liner simple script.

- GroovyScriptNoPreCompile : evaluate whole script per each trial
- GroovyScriptPreCompile : compile script first, and bind & run compiled script
- GroovyScriptNativeShell : Use GroovyShell, evaluate whole script per each trial

How to performance test: Just run `mvn test` and see the result of GroovyScriptPerfTest.

You can find detailed information from target/contiperf-report/(index.html | summary.csv).
