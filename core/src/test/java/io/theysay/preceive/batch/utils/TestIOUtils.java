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

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestIOUtils {

    @Test
    public void testGunzip() throws Exception {
        String raw = IOUtils.getText("classpath://test-input/100-texts.json").replace("\r\n", "\n");
        String gzipped = IOUtils.getText("classpath://test-input/100-texts.json.gz");
        Assert.assertEquals(raw, gzipped);
    }

    @Test
    public void testOutputStream() throws Exception {
        Assert.assertNull(IOUtils.outputStream("classpath://test-input/100-texts.json"));
        Assert.assertNull(IOUtils.outputStream("http://www.google.com"));

        IOUtils.write("temp.txt", "Hello World");
        IOUtils.write("temp.txt.gz", "Hello World");
        Assert.assertEquals("Hello World", IOUtils.getText("temp.txt"));
        Assert.assertEquals("Hello World", IOUtils.getText("temp.txt.gz"));


    }

    @Test
    public void testGZip() throws Exception {

        String text = "Hello World aa aa aaa aaa aa aaa";
        IOUtils.write("temp.txt", text);
        IOUtils.write("temp.txt.gz", text);
        Assert.assertEquals(text, IOUtils.getText("temp.txt"));
        Assert.assertEquals(text, IOUtils.getText("temp.txt.gz"));
        // Use the IO to get the raw bytes.
        byte[] bytes = Files.readAllBytes(Paths.get("temp.txt"));
        byte[] gzipped = Files.readAllBytes(Paths.get("temp.txt.gz"));
        Assert.assertTrue(bytes.length != gzipped.length);
    }

    @Test(expected = IOException.class)
    public void testWriteImpossible() throws Exception {
        IOUtils.write(".", "Foobar");
    }

    @Test
    public void testClose() throws Exception {
        IOUtils.close(null); // No Exception thrown
        IOUtils.close(new Closeable() {
            @Override
            public void close() throws IOException {
                throw new IOException("This should be ignored");
            }
        });
    }

    @Test
    public void testTabSeparatedText() throws Exception {
        String[][] strings = IOUtils.readTabSeparated("classpath://test.tsv");
        Assert.assertEquals(6, strings.length); // Empty lines removed
        for (String[] row : strings) {
            Assert.assertEquals(2, row.length); // Empty lines removed
        }
    }

}
