/**
 * Copyright 2016 flipkart.com zjsonpatch.
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
package io.fabric8.zjsonpatch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.core.IsEqual.equalTo;

/**
 * @author ctranxuan (streamdata.io).
 */
public abstract class AbstractTest {
    static ObjectMapper objectMapper = new ObjectMapper();
    static ArrayNode jsonNode;
    static ArrayNode errorNode;

    protected AbstractTest(String fileName) throws IOException {
        String path = "/testdata/" + fileName + ".json";
        InputStream resourceAsStream = JsonDiffTest.class.getResourceAsStream(path);
        String testData = IOUtils.toString(resourceAsStream, "UTF-8");
        JsonNode testsNode = objectMapper.readTree(testData);
        jsonNode = (ArrayNode) testsNode.get("ops");
        errorNode = (ArrayNode) testsNode.get("errors");
    }

    @Test
    public void testPatchAppliedCleanly() throws Exception {
        for (int i = 0; i < jsonNode.size(); i++) {
            JsonNode first = jsonNode.get(i).get("node");
            JsonNode second = jsonNode.get(i).get("expected");
            JsonNode patch = jsonNode.get(i).get("op");
            String message = jsonNode.get(i).has("message") ? jsonNode.get(i).get("message").toString() : "";

            System.out.println("Test # " + i);
            System.out.println(first);
            System.out.println(second);
            System.out.println(patch);

            JsonNode secondPrime = JsonPatch.apply(patch, first);
            System.out.println(secondPrime);
            Assert.assertThat(message, secondPrime, equalTo(second));
        }
    }

//    @Test
//    public void testPatchSyntax() throws Exception {
//        for (int i = 0; i < jsonNode.size(); i++) {
//            JsonNode first = jsonNode.get(i).get("node");
//            JsonNode second = jsonNode.get(i).get("expected");
//            JsonNode patch = jsonNode.get(i).get("op");
//
//            System.out.println("Test # " + i);
//            System.out.println(first);
//            System.out.println(second);
//            System.out.println(patch);
//
//            JsonNode diff = JsonDiff.asJson(first, second);
//            System.out.println(diff);
//            Assert.assertThat(diff, equalTo(patch));
//        }
//    }

    @Test(expected = RuntimeException.class)
    public void testErrorsAreCorrectlyReported() {
        for (int i = 0; i < errorNode.size(); i++) {
            JsonNode first = errorNode.get(i).get("node");
            JsonNode patch = errorNode.get(i).get("op");

            System.out.println("Error Test # " + i);
            System.out.println(first);
            System.out.println(patch);

            JsonNode secondPrime = JsonPatch.apply(patch, first);
            System.out.println(secondPrime);
        }

        if (errorNode.size() == 0) {
            throw new RuntimeException("dummy exception");
        }
    }

}
