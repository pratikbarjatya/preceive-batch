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

package io.theysay.preceive.batch.utils;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestResource {
    File absolute = new File("src/test/resources/help.txt").getAbsoluteFile();

    public final String expectedAbsolutePath(File file) {
        String rep = file.getPath().replace('\\', '/');
        if (!rep.startsWith("/")) rep = "/" + rep;
        return rep;
    }

    @Test(expected = IllegalArgumentException.class)
    public void testError() throws Exception {
        Resource.valueOf("#';)(*^");
    }

    @Test
    public void testFileNoScheme() throws Exception {

        Resource resource;
        resource = Resource.valueOf("src/test/resources/help.txt");
        Assert.assertEquals("file", resource.getScheme());
        String expected = expectedAbsolutePath(absolute);
        Assert.assertEquals(expected, resource.getPath());
        Assert.assertEquals("text/plain", resource.getContentType());

        Assert.assertNotNull(resource.getInputStream());

        resource = Resource.valueOf("src/test/resources/help.txt#option=2000");
        Assert.assertEquals("file", resource.getScheme());
        Assert.assertEquals(expectedAbsolutePath(absolute), resource.getPath());
        Assert.assertEquals("text/plain", resource.getContentType());
        Assert.assertEquals("option=2000", resource.getFragment());


        Assert.assertNotNull(resource.getInputStream());

    }

    @Test
    public void testInsertNewName() throws Exception {
        Resource template, expected,actual;

        template = Resource.valueOf("src/test/resources/help.txt.gz");
        expected = Resource.valueOf("src/test/resources/help.0000.txt.gz");
        actual   =template.insertBeforeExtension("0000");
        Assert.assertEquals(expected.getPath(),actual.getPath());
        Assert.assertEquals(expected.getFragment(),actual.getFragment());

        template = Resource.valueOf("src/test/resources/help.txt");
        expected = Resource.valueOf("src/test/resources/help.0000.txt");
        actual   =template.insertBeforeExtension("0000");
        Assert.assertEquals(expected.getPath(),actual.getPath());
        Assert.assertEquals(expected.getFragment(),actual.getFragment());

        template = Resource.valueOf("src/test/resources/help.txt#option=2000");
        expected = Resource.valueOf("src/test/resources/help.0000.txt#option=2000");
        actual   =template.insertBeforeExtension("0000");
        Assert.assertEquals(expected.getPath(),actual.getPath());
        Assert.assertEquals(expected.getFragment(),actual.getFragment());

        template = Resource.valueOf("src/test/resources/help");
        expected = Resource.valueOf("src/test/resources/help0000");
        actual   =template.insertBeforeExtension("0000");
        Assert.assertEquals(expected.getPath(),actual.getPath());
        Assert.assertEquals(expected.getFragment(),actual.getFragment());

        template = Resource.valueOf("src/test/resources/");
        expected = Resource.valueOf("src/test/resources/0000");
        actual   =template.insertBeforeExtension("0000");
        Assert.assertEquals(expected.getPath(),actual.getPath());
        Assert.assertEquals(expected.getFragment(),actual.getFragment());

    }

    @Test
    public void testFileWithScheme() throws Exception {
        Resource resource;
        // If valid URI return a valid result
        String specifier = absolute.toURI().toString();
        resource = Resource.valueOf(specifier);
        Assert.assertEquals("file", resource.getScheme());
        Assert.assertEquals(expectedAbsolutePath(absolute), resource.getPath());
        Assert.assertEquals("text/plain", resource.getContentType());

        Assert.assertNotNull(resource.getInputStream());

        resource = Resource.valueOf(specifier + "#option=2000");
        Assert.assertEquals("file", resource.getScheme());
        Assert.assertEquals(expectedAbsolutePath(absolute), resource.getPath());
        Assert.assertEquals("text/plain", resource.getContentType());
        Assert.assertEquals("option=2000", resource.getFragment());

        Assert.assertNotNull(resource.getInputStream());

    }

    @Test
    public void testAbsoluteFile() throws Exception {
        String uri = absolute.toURI().toString();
        String path = expectedAbsolutePath(absolute);

        Resource resource;
        resource = Resource.valueOf(uri);
        Assert.assertEquals("file", resource.getScheme());
        Assert.assertEquals(path, resource.getPath());
        Assert.assertEquals("text/plain", resource.getContentType());
        Assert.assertNotNull(IOUtils.inputStream(resource));


        resource = Resource.valueOf(path);
        Assert.assertEquals("file", resource.getScheme());
        Assert.assertEquals(path, resource.getPath());
        Assert.assertEquals("text/plain", resource.getContentType());
        Assert.assertNotNull(IOUtils.inputStream(resource));
        // Ignores excessives '/'
        resource = Resource.valueOf("//" + path);
        Assert.assertEquals("file", resource.getScheme());
        Assert.assertEquals(path, resource.getPath());
        Assert.assertEquals("text/plain", resource.getContentType());
        Assert.assertNotNull(IOUtils.inputStream(resource));

        // Ignores excessives '/'
        resource = Resource.valueOf("//" + path + "#option=2000");
        Assert.assertEquals("file", resource.getScheme());
        Assert.assertEquals(path, resource.getPath());
        Assert.assertEquals("option=2000", resource.getFragment());
        Assert.assertEquals("text/plain", resource.getContentType());
        Assert.assertNotNull(IOUtils.inputStream(resource));


    }

    @Test
    public void testHttpResource() throws Exception {
        Resource resource = Resource.valueOf("http://www.theysay.io/index.html?reload=true");
        Assert.assertEquals("http", resource.getScheme());
        Assert.assertEquals("/index.html", resource.getPath());
        Assert.assertEquals("text/html", resource.getContentType());
        Assert.assertEquals("reload=true", resource.getQuery());
        Assert.assertEquals("true", resource.getQueryParams().asString("reload"));
    }

    @Test
    public void testClasspathResource() throws Exception {
        Resource resource = Resource.valueOf("classpath://help.txt");
        Assert.assertEquals("classpath", resource.getScheme());
        Assert.assertEquals("/help.txt", resource.getPath());
        Assert.assertEquals("text/plain", resource.getContentType());
        Assert.assertNotNull(IOUtils.inputStream(resource));
    }

    @Test
    public void testContentType() throws Exception {
        Assert.assertEquals("text/html", Resource.valueOf("/data/foobar/index.html").getContentType());
        Assert.assertEquals("text/tab-separated-values", Resource.valueOf("/data/foobar/index.tsv").getContentType());
        Assert.assertEquals("text/tab-separated-values",
            Resource.valueOf("src/test/resources/help.txt#Content-Type=text/tab-separated-values")
                .getContentType());
        Assert.assertEquals("text/csv", Resource.valueOf("/data/foobar/index.csv").getContentType());
        Assert.assertEquals("application/json", Resource.valueOf("/data/foobar/index.json").getContentType());
        Assert.assertEquals("text/plain", Resource.valueOf("/data/foobar/index.txt").getContentType());
        Assert.assertEquals("application/json", Resource.valueOf("/data/foobar/index.json.gz").getContentType());
        Assert.assertEquals(true, Resource.valueOf("/data/foobar/index.json.gz").isGzipped());
        Assert.assertEquals(false, Resource.valueOf("/data/foobar/index.json").isGzipped());
        Assert.assertEquals("text/tab-separated-values", Resource.valueOf("/data/foobar/index.tsv.gz").getContentType());


    }

    @Test
    public void testExtension() throws Exception {
        Assert.assertEquals("json.gz", Resource.valueOf("/data/foobar/index.json.gz").getExtension());
        Assert.assertEquals("json", Resource.valueOf("/data/foobar/index.json").getExtension());


    }

    @Test
    public void testMongoStyleURI() throws Exception {
        Resource mongo = Resource.valueOf("mongodb://username:password@host1:1000/database?option=value#collection=inboundqueue");
        Assert.assertEquals("mongodb", mongo.getScheme());
        Assert.assertEquals("host1", mongo.getHost());
        Assert.assertEquals(1000, mongo.getPort());
        Assert.assertEquals("username:password", mongo.getUserInfo());
        Assert.assertEquals("collection=inboundqueue", mongo.getFragment());
        Assert.assertEquals("inboundqueue", mongo.getOptions().asString("collection"));
        Assert.assertNull(IOUtils.inputStream(mongo));
        Assert.assertNull(IOUtils.outputStream(mongo));
    }


    @Test
    public void testParams() throws Exception {
        Resource params = Resource.valueOf("src/test/resources/help.txt#charset=UTF-16LE&Content-Type=application/tab-separated-values&option=1000");
        Assert.assertEquals("UTF-16LE", params.getOptions().asString("charset"));
        Assert.assertEquals("application/tab-separated-values", params.getOptions().asString("Content-Type"));
        Assert.assertEquals("application/tab-separated-values", params.getOptions().asString("Content-Type"));
        Assert.assertEquals(1000, params.getOptions().asNumber("option").intValue());
    }

    @Test
    public void testOutputStream() throws Exception {
        Resource resource;
        resource = Resource.valueOf("test-file.tsv#charset=UTF-8");
        Assert.assertNotNull(IOUtils.outputStream(resource));
        Files.deleteIfExists(Paths.get("test-dir/test.tsv"));
        Files.deleteIfExists(Paths.get("test-dir"));
        resource = Resource.valueOf("test-dir/test.tsv#charset=UTF-8");
        Assert.assertNotNull(IOUtils.outputStream(resource));
    }
/*
    @Test
    public void testWindowsFileWithDriveLetter() throws Exception {

        Resource resource;
        resource = Resource.valueOf("c:\\temp\\file.txt");
        Assert.assertEquals("file", resource.getScheme());
        Assert.assertEquals("/c:/temp/file.txt", resource.getPath());
        Assert.assertEquals("text/plain", resource.getContentType());
    }
    @Test
    public void testWindowsFileWithScheme() throws Exception {

        Resource resource;
        resource = Resource.valueOf("file:///C:/Documents%20and%20Settings/davris/FileSchemeURIs.txt");
        Assert.assertEquals("file", resource.getScheme());
        Assert.assertEquals("/C:/Documents and Settings/davris/FileSchemeURIs.txt", resource.getPath());
        Assert.assertEquals("text/plain", resource.getContentType());
        resource = Resource.valueOf("C:\\Documents and Settings\\davris\\FileSchemeURIs.txt");
        Assert.assertEquals("file", resource.getScheme());
        Assert.assertEquals("/C:/Documents and Settings/davris/FileSchemeURIs.txt", resource.getPath());
        Assert.assertEquals("text/plain", resource.getContentType());
    }
    @Test
    public void testUNCFilePaths() throws Exception {

        Resource resource;
        Resource reference= Resource.valueOf("file://Server01/user/docs/Letter.txt");
        resource = Resource.valueOf("\\\\Server01\\user\\docs\\Letter.txt");
        Assert.assertEquals("file", resource.getScheme());
        Assert.assertEquals(reference.getHost(), resource.getHost());
        Assert.assertEquals(reference.getPath(), resource.getPath());
        Assert.assertEquals("text/plain", resource.getContentType

}
*/
}