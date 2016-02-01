/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.heartsavior;

import org.junit.Assert;
import org.junit.Test;

import javax.script.ScriptException;
import java.util.HashMap;

public class GroovyScriptTest {
    @Test
    public void testBindingsAreBoundOnlyWhenEvaluation() {
        String groovyExpression = "a > 10 && b < 30";

        GroovyScriptNativeShell<Boolean> groovyScript = new GroovyScriptNativeShell<>(groovyExpression);
        HashMap<String, Object> params = new HashMap<>();
        params.put("a", 20);
        params.put("b", 10);
        try {
            groovyScript.evaluate(params);
        } catch (ScriptException e) {
            e.printStackTrace();
            Assert.fail("It shouldn't throw ScriptException");
        }

        params.clear();
        params.put("no_related_field", 3);
        try {
            groovyScript.evaluate(params);
            Assert.fail("It should not evaluate correctly");
        } catch (ScriptException e) {
            // no-op, that's what we want
        }
    }
}
