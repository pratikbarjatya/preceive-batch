/*
 * Apache 2 Licence
 *
 * Copyright 2016 TheySay Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-
 * INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 */

package io.theysay.preceive.batch.application;

import io.theysay.preceive.batch.sources.Sources;
import io.theysay.preceive.batch.utils.Datum;
import io.theysay.preceive.batch.utils.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TestMain {


    @Test
    public void testExampleOptions() throws Exception {

        Assert.assertEquals(0, Main.run());
        Assert.assertEquals(0, Main.run("-help"));
        Assert.assertEquals(-1, Main.run("-foo"));
    }

    @Test
    public void testRealRun() throws Exception {
        Path inputPath = Paths.get("src/dist/corpus/getting_started.json");
        Path resultPath = Paths.get("results.json");
        Files.deleteIfExists(resultPath);

        Assert.assertEquals(0, Main.run(
            "-user", "test",
            "-password", "test",
            "-service", "simulate://api.theysay.io",
            "-threads", "1",
            "-copy-input-data-as", "1",
            "-endpoints", "sentiment=/v1/sentiment",
            "-output", "results.json",
            "-batch", "src/dist/corpus/getting_started.json"
        ));
        List<String> inputs = Files.readAllLines(inputPath, StandardCharsets.UTF_8);
        List<String> results = Files.readAllLines(resultPath, StandardCharsets.UTF_8);
        Assert.assertEquals(inputs.size(), results.size());
    }

    @Test
    public void testDocumentAnalysis() throws Exception {


        Assert.assertEquals(0, Main.run(
            "-user", "test",
            "-password", "test",
            "-service", "simulate://api.theysay.io",
            "-output", "results.json",
            "-document-analysis", "src/dist/corpus/getting_started.json"
        ));
        List<Datum> results = Sources.read("results.json", Datum.class);
        Assert.assertEquals(5, results.size());
        for (Datum result : results) {
            Assert.assertNotNull(result.asDatum("input"));
            Assert.assertNotNull(result.asDatum("results", "sentiment", "sentiment"));
            Assert.assertNotNull(result.asDataArray("results", "emotion", "emotions"));
            Assert.assertNotNull(result.asDataArray("results", "topics", "scores"));
        }
    }

    @Test
    public void testForEachRun() throws Exception {
        Path inputPath1 = Paths.get("src/dist/corpus/getting_started.json");
        Path inputPath2 = Paths.get("src/dist/corpus/getting_started.tsv");
        Path resultPath1 = Paths.get("results/getting_started.json.json");
        Path resultPath2 = Paths.get("results/getting_started.tsv.json");
        IOUtils.clean("results");

        Assert.assertEquals(0, Main.run(
            "-user", "test",
            "-password", "test",
            "-service", "simulate://api.theysay.io",
            "-copy-input-data-as", "1",
            "-endpoints", "sentiment=/v1/sentiment",
            "-output", "results/.json",
            "-process-documents", "src/dist/corpus/getting_started.json", "src/dist/corpus/getting_started.tsv","null"
        ));
        List<String> inputs1 = Files.readAllLines(inputPath1, StandardCharsets.UTF_8);
        List<String> inputs2 = Files.readAllLines(inputPath2, StandardCharsets.UTF_8);
        List<String> results1 = Files.readAllLines(resultPath1, StandardCharsets.UTF_8);
        List<String> results2 = Files.readAllLines(resultPath2, StandardCharsets.UTF_8);
        Assert.assertEquals(inputs1.size(), results1.size());
        Assert.assertEquals(inputs2.size(), results2.size());
    }
}
