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

package io.theysay.preceive.batch.http;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class TestClientSettings {
    @Test
    public void testConfiguration1() throws Exception {
        ClientSettings settings = new ClientSettings();

        String[] expectedHeaderKeys = {
                "Accept-Charset",
                "Accept-Encoding",
                "Authorization",
                "Content-Type",
                "User-Agent",
                "Connection"

        };

        settings.setUsername("fred");
        settings.setPassword("flintstone");
        settings.setGzipped(true);
        settings.setKeepAlive(true);
        settings.setContentType("application/json");
        settings.setCharset("UTF-8");
        settings.setUserAgent("My Favorite Agent");

        List<Header> headers = settings.headers();
        List<String> headerNames = settings.headerNames();

        Assert.assertEquals(expectedHeaderKeys.length, headerNames.size());


        Assert.assertTrue(headerNames.containsAll(Arrays.asList(expectedHeaderKeys)));

        Assert.assertTrue(headers.contains(new Header("Accept-Charset", settings.getCharset())));
        Assert.assertTrue(headers.contains(new Header("Accept-Encoding", "gzip")));
        Assert.assertTrue(headers.contains(new Header("Authorization", "Basic ZnJlZDpmbGludHN0b25l")));
        Assert.assertTrue(headers.contains(new Header("Content-Type", "application/json")));
        Assert.assertTrue(headers.contains(new Header("User-Agent", "My Favorite Agent")));
        Assert.assertTrue(headers.contains(new Header("Connection", "Keep-Alive")));
        settings.toString(); // Check for NPE
    }

    @Test
    public void testConfiguration2() throws Exception {
        ClientSettings settings = new ClientSettings();

        String[] expectedHeaderKeys = {
                "Accept-Charset",
                "Content-Type",
                "User-Agent",


        };

        settings.setUsername(null);
        settings.setPassword(null);
        settings.setGzipped(false);
        settings.setKeepAlive(false);
        settings.setContentType("application/text");
        settings.setCharset("UTF-16");
        settings.setUserAgent("My Least Favorite Agent");

        List<Header> headers = settings.headers();
        List<String> headerNames = settings.headerNames();

        Assert.assertEquals(expectedHeaderKeys.length, headerNames.size());


        Assert.assertTrue(headerNames.containsAll(Arrays.asList(expectedHeaderKeys)));

        Assert.assertTrue(headers.contains(new Header("Accept-Charset", settings.getCharset())));
        Assert.assertTrue(headers.contains(new Header("Content-Type", "application/text")));
        Assert.assertTrue(headers.contains(new Header("User-Agent", "My Least Favorite Agent")));
        Assert.assertNotNull(settings.toString());
    }

    @Test
    public void testConfiguration3() throws Exception {
        ClientSettings settings = new ClientSettings();

        String[] expectedHeaderKeys = {
                "Authorization"
        };
        settings.setUsername("fred");
        settings.setPassword("flintstone");
        settings.setGzipped(false);
        settings.setKeepAlive(false);
        settings.setContentType(null);
        settings.setCharset(null);
        settings.setUserAgent(null);

        List<Header> headers = settings.headers();
        List<String> headerNames = settings.headerNames();

        Assert.assertEquals(expectedHeaderKeys.length, headerNames.size());


        Assert.assertTrue(headerNames.containsAll(Arrays.asList(expectedHeaderKeys)));
        Assert.assertTrue(headers.contains(new Header("Authorization", "Basic ZnJlZDpmbGludHN0b25l")));
        Assert.assertNotNull(settings.toString());
    }

    @Test
    public void testConfiguration4() throws Exception {
        String[] expectedHeaderKeys = {

        };


        ClientSettings settings = new ClientSettings();
        settings.setConnectTimeout(123);
        settings.setReadTimeout(456);
        Assert.assertEquals(123, settings.getConnectTimeout());
        Assert.assertEquals(456, settings.getReadTimeout());
        settings.setUsername(null);
        settings.setPassword(null);
        settings.setGzipped(false);
        settings.setKeepAlive(false);
        settings.setContentType(null);
        settings.setCharset(null);
        settings.setUserAgent(null);

        List<Header> headers = settings.headers();
        List<String> headerNames = settings.headerNames();

        Assert.assertEquals(expectedHeaderKeys.length, headerNames.size());

        Assert.assertNotNull(settings.toString());
    }
}
