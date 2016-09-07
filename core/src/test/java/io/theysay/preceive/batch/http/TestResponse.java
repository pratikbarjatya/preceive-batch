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

public class TestResponse {

    public static final String EXAMPLE_ERROR_RESPONSE = "{\"errors\":[{\"message\":\"Text must be more than 10\"}]}";
    public static final String EXAMPLE_LIST_RESPONSE = "[{\"id\":\"57a08990954324034f92fe0e\",\"classLabel\":\"YUMMY\",\"text\":\"roasted peanuts\",\"weight\":400.0},{\"id\":\"57a08b6c954324034f92fe12\",\"classLabel\":\"YUCKY\",\"text\":\"salted peanuts\",\"weight\":12.0},{\"id\":\"57a08b7a954324034f92fe14\",\"classLabel\":\"YUCKY\",\"text\":\"yellow peanuts\",\"weight\":-45.0}]";
    public static final String EXAMPLE_OBJECT_RESPONSE = "{\"id\":\"57a08990954324034f92fe0e\",\"classLabel\":\"YUMMY\",\"text\":\"roasted peanuts\",\"weight\":400.0}";

    @Test(expected = ApiException.Client.class)
    public void testValidateClientException() throws Exception {
        Response response = new Response(StatusCode.BadRequest, EXAMPLE_ERROR_RESPONSE);
        response.validate();

    }

    @Test(expected = ApiException.RateLimit.class)
    public void testValidateRateLimitException() throws Exception {
        Response response = new Response(StatusCode.TooManyRequests, EXAMPLE_ERROR_RESPONSE);
        response.validate();

    }

    @Test(expected = ApiException.Server.class)
    public void testValidateServerException() throws Exception {
        Response response = new Response(StatusCode.GatewayTimeout, EXAMPLE_ERROR_RESPONSE);
        response.validate();

    }

    @Test(expected = ApiException.AuthenicationRejected.class)
    public void testValidateUnauthorisedException() throws Exception {
        Response response = new Response(StatusCode.Unauthorized, EXAMPLE_ERROR_RESPONSE);
        response.validate();
    }

    @Test(expected = ApiException.Client.class)
    public void testAsEntityClientException() throws Exception {
        Response response = new Response(StatusCode.BadRequest, EXAMPLE_ERROR_RESPONSE);
        response.asEntity();

    }

    @Test(expected = ApiException.RateLimit.class)
    public void testAsEntityRateLimitException() throws Exception {
        Response response = new Response(StatusCode.TooManyRequests, EXAMPLE_ERROR_RESPONSE);
        response.asEntity();

    }

    @Test(expected = ApiException.Server.class)
    public void testAsEntityServerException() throws Exception {
        Response response = new Response(StatusCode.GatewayTimeout, EXAMPLE_ERROR_RESPONSE);
        response.asEntity();

    }

    @Test(expected = ApiException.class)
    public void testAsEntityUnknownException() throws Exception {
        Response response = new Response(StatusCode.UNKNOWN, EXAMPLE_ERROR_RESPONSE);
        response.asEntity();

    }

    @Test(expected = ApiException.AuthenicationRejected.class)
    public void testAsEntityUnauthorisedException() throws Exception {
        Response response = new Response(StatusCode.Unauthorized, EXAMPLE_ERROR_RESPONSE);
        response.asEntity();
    }

    @Test
    public void testAsEntity() throws Exception {
        Response response = new Response(StatusCode.OK, EXAMPLE_OBJECT_RESPONSE);
        Datum entity = response.asEntity();
        Assert.assertEquals("57a08990954324034f92fe0e", entity.asString("id"));
        Assert.assertEquals("YUMMY", entity.asString("classLabel"));
        Assert.assertEquals("roasted peanuts", entity.asString("text"));
    }

    @Test
    public void testAsList() throws Exception {
        Response response = new Response(StatusCode.OK, EXAMPLE_LIST_RESPONSE);
        Datum entities[] = response.asList();
        Assert.assertEquals(3, entities.length);
        Datum entity = entities[0];
        Assert.assertEquals("57a08990954324034f92fe0e", entity.asString("id"));
        Assert.assertEquals("YUMMY", entity.asString("classLabel"));
        Assert.assertEquals("roasted peanuts", entity.asString("text"));
    }

}
