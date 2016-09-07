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

public class TestHeader {
    Header applicationJson = new Header("Content-Type", "application/json");
    Header textHtml = new Header("Content-Type", "text/html");
    Header keepAlive = new Header("Connection", "Keep-Alive");

    @Test
    public void testEquals() throws Exception {

        Assert.assertNotEquals(textHtml, null);
        Assert.assertNotEquals(textHtml, applicationJson);
        Assert.assertEquals(Header.KEEP_ALIVE, keepAlive);
        Assert.assertEquals(Header.KEEP_ALIVE, Header.KEEP_ALIVE);
        Assert.assertNotEquals(Header.KEEP_ALIVE, applicationJson);
        Assert.assertNotEquals(Header.GZIP_ENCODING, keepAlive);

    }

    @Test
    public void testHashcode() throws Exception {


        Assert.assertEquals(textHtml.hashCode(), applicationJson.hashCode());
        Assert.assertEquals(Header.KEEP_ALIVE.hashCode(), keepAlive.hashCode());

        Assert.assertNotEquals(Header.KEEP_ALIVE.hashCode(), applicationJson.hashCode());
        Assert.assertNotEquals(Header.GZIP_ENCODING.hashCode(), keepAlive.hashCode());

    }

    @Test
    public void testToString() throws Exception {

        Assert.assertNotEquals(Header.KEEP_ALIVE.toString(), applicationJson.toString());

        Assert.assertEquals(Header.KEEP_ALIVE.toString(), keepAlive.toString());
        Assert.assertNotEquals(textHtml.toString(), applicationJson.toString());
        Assert.assertNotEquals(Header.GZIP_ENCODING.toString(), keepAlive.toString());

    }
}
