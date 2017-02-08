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

package io.theysay.preceive.batch.cmdline;

import io.theysay.preceive.batch.api.EndPoint;
import io.theysay.preceive.batch.http.ClientSettings;
import io.theysay.preceive.batch.options.ClientOptions;
import io.theysay.preceive.batch.options.EndPointsAction;
import io.theysay.preceive.batch.options.ProcessOptions;
import io.theysay.preceive.batch.sources.CompositeSource;
import io.theysay.preceive.batch.sources.Sources;
import io.theysay.preceive.batch.utils.Datum;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringWriter;
import java.util.NoSuchElementException;

public class TestCmdLine {

    @Test
    public void testAutoRegistration() throws Exception {
        String[] args = {"-password", "passy", "-user", "usery", "-service", "servicy"};
        ShellContext context = new ShellContext();
        context.push(args).execute();
        Action.Property<String> password = ClientOptions.PASSWORD;
        Action.Property<String> username = ClientOptions.USERNAME;
        Action.Property<String> service = ClientOptions.SERVICE;

        Assert.assertTrue(context.actions().contains(password));
        Assert.assertTrue(context.actions().contains(username));
        Assert.assertTrue(context.actions().contains(service));
        Assert.assertTrue(context.actions().contains(ExampleLibrary.DUMMY));

        ClientSettings clientSettings = ClientOptions.getClientSettings();

        Assert.assertEquals("passy", clientSettings.getPassword());
        Assert.assertEquals("usery", clientSettings.getUsername());
        Assert.assertEquals("servicy", clientSettings.getService());
    }

    @Test
    public void testValueOf() throws Exception {
        String[] args = {"-endpoints", "sentence=/v1/sentiment?level=sentence", "document=/v1/sentiment?level=document"};
        ShellContext context = new ShellContext();
        context.push(args).execute();
        EndPointsAction endpoints = ProcessOptions.ENDPOINTS;
        Assert.assertTrue(context.actions().contains(endpoints));
        EndPoint[] endPoints = endpoints.get();
        Assert.assertEquals(EndPoint.valueOf("sentence=/v1/sentiment?level=sentence"), endPoints[0]);
        Assert.assertEquals(EndPoint.valueOf("document=/v1/sentiment?level=document"), endPoints[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testTwoUnspecifiedEndPointFields() throws Exception {
        String[] args = {"-endpoints", "/v1/sentiment", "/v1/postag"};
        ShellContext context = new ShellContext();
        context.push(args).execute();
        EndPointsAction endpoints = ProcessOptions.ENDPOINTS;
        Assert.assertTrue(context.actions().contains(endpoints));
        endpoints.get();
    }

    @Test(expected=IllegalArgumentException.class)
    public void testClashingEndPointFields() throws Exception {
        String[] args = {"-endpoints", "foo=/v1/sentiment", "bar=/v1/topic", "foo=/v1/postag"};
        ShellContext context = new ShellContext();
        context.push(args).execute();
        EndPointsAction endpoints = ProcessOptions.ENDPOINTS;
        Assert.assertTrue(context.actions().contains(endpoints));
        endpoints.get();
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSubPathEndPointFields() throws Exception {
        String[] args = {"-endpoints", "foo.bar=/v1/sentiment", "foo=/v1/postag"};
        ShellContext context = new ShellContext();
        context.push(args).execute();
        EndPointsAction endpoints = ProcessOptions.ENDPOINTS;
        Assert.assertTrue(context.actions().contains(endpoints));
        endpoints.get();
    }

    @Test
    public void testNotActuallySubPathEndPointFields() throws Exception {
        String[] args = {"-endpoints", "foo=/v1/sentiment", "food=/v1/postag"};
        ShellContext context = new ShellContext();
        context.push(args).execute();
        EndPointsAction endpoints = ProcessOptions.ENDPOINTS;
        Assert.assertTrue(context.actions().contains(endpoints));
        EndPoint[] endPoints = endpoints.get();
        Assert.assertEquals(endPoints.length, 2);
    }

    @Test
    public void testNumeric() throws Exception {
        String[] args = {"-threads", "99"};
        ShellContext context = new ShellContext();
        context.push(args).execute();
        Action.Property<Integer> threads = ProcessOptions.THREADS;
        Assert.assertTrue(context.actions().contains(threads));

        Integer threadCount = threads.get();

        Assert.assertEquals(99, threadCount.intValue());
    }

    @Test(expected = NoSuchElementException.class)
    public void testMissingArguments() throws Exception {
        String[] args = {"-threads"};
        ShellContext context = new ShellContext();
        context.push(args).execute();
    }

    @Test(expected = IllegalStateException.class)
    public void testUnused() throws Exception {
        String[] args = {"-threads", "99", "foo", "bar"};
        ShellContext context = new ShellContext();
        context.push(args).execute();
    }

    @Test(expected = NoSuchElementException.class)
    public void testMissingCmd() throws Exception {
        String[] args = {"-foobar"};
        ShellContext context = new ShellContext();
        context.push(args).execute();
    }

    @Test
    public void testNull() throws Exception {
        String[] args = {"-user", "null"};
        ShellContext context = new ShellContext();
        context.push(args).execute();
        Action.Property<String> username = ClientOptions.USERNAME;
        Assert.assertNull(username.get());
    }

    @Test
    public void testSources() throws Exception {
        String[] args = {"classpath://test-input/100-texts.json", "src/test/resources/test-input/100-texts.json"};
        ShellContext context = new ShellContext();
        context.push(args);
        CompositeSource<Datum> sources = Sources.sources(Datum.class, context.arguments());
        Assert.assertEquals(2, sources.remaining());
        int count = 0;
        for (Datum source : sources) {
            Assert.assertNotNull(source.asString("text"));
            count++;
        }
        Assert.assertEquals(200, count);
    }

    @Test
    public void testHelpOutput() throws Exception {
        String[] args = {"-help"};
        StringWriter writer = new StringWriter();

        ShellContext context = new ShellContext(writer);
        context.push(args).execute();
        context.flush();
        context.close();
        String output = writer.toString();
        Assert.assertTrue(output.contains(ClientOptions.USERNAME.getKey()));
        Assert.assertTrue(output.contains(ClientOptions.USERNAME.usage()));
    }

    @Test
    public void testMacro() throws Exception {
        new ShellContext().execute("-macro");
        Assert.assertEquals(1000, ProcessOptions.THREADS.get().intValue());
        Assert.assertEquals(2345, ProcessOptions.BACKLOG.get().intValue());
        new ShellContext().execute("-reset");
        Assert.assertEquals(1, ProcessOptions.THREADS.get().intValue());
        Assert.assertEquals(100, ProcessOptions.BACKLOG.get().intValue());
    }

    @Test
    public void testLoadMacro() throws Exception {
        Macro[] macros = Macro.loadMacros("classpath://macro/macros.json");
        Assert.assertEquals(2, macros.length);
        for (Macro macro : macros) {
            Assert.assertNotNull(macro.getKey());
            Assert.assertNotNull(macro.getUsage());
            Assert.assertNotNull(macro.getArguments());
            Assert.assertTrue(0 < macro.getArguments().length);
        }
    }
}
