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

import io.theysay.preceive.batch.utils.Datum;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Simple testing of the code of the Client using the simulated API and real calls.
 */
public class TestSimpleHttpClient {

    public static final String TOPIC_RESOURCE_URI = "simulate://api.theysay.io/v1/resources/topics/keywords";
    public static final String SENTIMENT_URI = "simulate://api.theysay.io/v1/sentiment";


    public static ClientSettings getClientSettings() {
        ClientSettings settings = new ClientSettings();
        settings.setUserAgent("Mozilla/5.0"); // Pretend to be somebody
        settings.setService("simulate://api.theysay.io");
        return settings;
    }

    public static DefaultHttpClient getClient() {
        return new DefaultHttpClient(getClientSettings());
    }

    @Test
    public void testGetFromURL() throws Exception {
        Response response = getClient().get("http://www.theysay.io/");
        Assert.assertEquals(StatusCode.OK, response.getStatus());
        Assert.assertNotNull(response.getBody());
    }

    @Test
    public void testPOSTResources() throws Exception {
        Response response = getClient().post(TOPIC_RESOURCE_URI, "{\"classLabel\":\"YUMMY\",\"text\":\"roasted peanuts\",\"weight\":400.0}");
        Assert.assertEquals(StatusCode.Created, response.getStatus());
    }

    @Test
    public void testPOSTAnalyses() throws Exception {
        Response none = getClient().post(SENTIMENT_URI, "{\"text\":\"This is a rather useful experience.\"}");
        Response document = getClient().post(SENTIMENT_URI, "{\"level\":\"document\",\"text\":\"This is a rather useful experience.\"}");
        Response sentence = getClient().post(SENTIMENT_URI, "{\"level\":\"sentence\",\"text\":\"This is a rather useful experience.\"}");
        Assert.assertEquals(none.getBody(), document.getBody());
        Assert.assertNotEquals(none.getBody(), sentence.getBody());
        Datum[] sentenceLevel = sentence.asList();
        Datum documentLevel = document.asEntity();
    }

    @Test
    public void testPUTFromURL() throws Exception {
        Response response = getClient().put(TOPIC_RESOURCE_URI, "{\"classLabel\":\"YUMMY\",\"text\":\"roasted peanuts\",\"weight\":400.0}");
        Assert.assertEquals(StatusCode.OK, response.getStatus());
    }

    @Test
    public void testDELETEFromURL() throws Exception {
        Response response = getClient().delete(TOPIC_RESOURCE_URI);
        Assert.assertEquals(StatusCode.OK, response.getStatus());
    }

    @Test
    public void testBadPostFromURL() throws Exception {
        Response response = getClient().post("simulate://api.theysay.io/v1/sentiment", "{\"text\":\"some\"}");
        Assert.assertEquals(StatusCode.BadRequest, response.getStatus());
    }

    @Test
    public void testBadPostFromURL2() throws Exception {
        Response response = getClient().post("simulate://api.theysay.io/v1/sentiment", "blah de blah");
        Assert.assertEquals(StatusCode.BadRequest, response.getStatus());
    }

    @Test(expected = IOException.class)
    public void testNetworkProblemFromURL() throws Exception {
        getClient().post("simulate://nonsuch.theysay.io/v1/sentiment", "blah de blah");
    }


}
