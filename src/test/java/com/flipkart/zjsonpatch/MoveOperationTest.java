/**
 * Copyright (C) 2016 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.flipkart.zjsonpatch;

import com.fasterxml.jackson.databind.JsonNode;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author ctranxuan (streamdata.io).
 */
public class MoveOperationTest extends AbstractTest {
    public MoveOperationTest() throws IOException {
        super("move");
    }

    @Test
    public void testMoveValueGeneratedHasNoValue() throws IOException {
        JsonNode jsonNode1 = objectMapper.readTree("{ \"foo\": { \"bar\": \"baz\", \"waldo\": \"fred\" }, \"qux\": { \"corge\": \"grault\" } }");
        JsonNode jsonNode2 = objectMapper.readTree("{ \"foo\": { \"bar\": \"baz\" }, \"qux\": { \"corge\": \"grault\", \"thud\": \"fred\" } }");
        JsonNode patch = objectMapper.readTree("[{\"op\":\"move\",\"from\":\"/foo/waldo\",\"path\":\"/qux/thud\"}]");

        JsonNode diff = JsonDiff.asJson(jsonNode1, jsonNode2);

        assertThat(diff, equalTo(patch));
    }

    @Test
    public void testMoveArrayGeneratedHasNoValue() throws IOException {
        JsonNode jsonNode1 = objectMapper.readTree("{ \"foo\": [ \"all\", \"grass\", \"cows\", \"eat\" ] }");
        JsonNode jsonNode2 = objectMapper.readTree("{ \"foo\": [ \"all\", \"cows\", \"eat\", \"grass\" ] }");
        JsonNode patch = objectMapper.readTree("[{\"op\":\"move\",\"from\":\"/foo/1\",\"path\":\"/foo/3\"}]");

        JsonNode diff = JsonDiff.asJson(jsonNode1, jsonNode2);

        assertThat(diff, equalTo(patch));
    }
}
