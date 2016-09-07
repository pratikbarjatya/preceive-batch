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

import io.theysay.preceive.batch.api.EndPoint;
import io.theysay.preceive.batch.utils.DataPath;
import io.theysay.preceive.batch.utils.Datum;
import org.junit.Assert;
import org.junit.Test;

public class TestEndpoint {
    @Test
    public void testEqualsHashCode() throws Exception {
        EndPoint one = EndPoint.valueOf("a.b.x=/v1/sentiment?level=sentence&fruit=banana");
        EndPoint two = EndPoint.valueOf("a.b.x=/v1/sentiment?level=sentence&fruit=banana");
        EndPoint diff1 = EndPoint.valueOf("a.c.x=/v1/sentiment?level=sentence&fruit=banana");
        EndPoint diff2 = EndPoint.valueOf("a.b.x=/v2/sentiment?level=sentence&fruit=banana");
        EndPoint diff3 = EndPoint.valueOf("a.b.x=/v2/sentiment?level=sentence&fruit=orange");
        Assert.assertEquals(one, one);
        Assert.assertEquals(one, two);
        Assert.assertEquals(one.hashCode(), two.hashCode());

        Assert.assertFalse(one == two);
        Assert.assertFalse(one.equals(null));
        Assert.assertFalse(one.equals("a.b.x=/v1/sentiment?level=sentence&fruit=banana"));
        Assert.assertFalse(one.equals(diff1));
        Assert.assertFalse(one.equals(diff2));
        Assert.assertFalse(one.equals(diff3));
    }

    @Test
    public void testParamCopy() throws Exception {


        EndPoint reference = EndPoint.valueOf("a.b.x=/v1/sentiment?level=sentence&fruit=banana");

        Datum parameters = reference.getParameters();
        Assert.assertEquals("sentence", parameters.get("level"));
        Assert.assertEquals("banana", parameters.get("fruit"));


        Datum copy = reference.copyParameters();
        Assert.assertFalse(parameters == copy);
        Assert.assertTrue(parameters.equals(copy));

        copy.put("Foo", "Bah");
        Assert.assertTrue(parameters.get("Foo") == null);
        Assert.assertFalse(copy.get("Foo") == null);

    }

    @Test
    public void testParseParameters() throws Exception {

        String[] variants = {
                "a.b.x=/v1/sentiment?level=sentence",
                "a=/v1/sentiment?level=sentence",
                "/v1/sentiment?level=sentence"
        };
        Datum expected = new Datum("level", "sentence");
        for (String variant : variants) {
            EndPoint endPoint = EndPoint.valueOf(variant);
            Assert.assertEquals(expected, endPoint.getParameters());
        }
    }

    @Test
    public void testParsePath() throws Exception {

        String[] variants = {
                "a.b.x=/v1/sentiment?level=sentence&ff.xx=xx",
                "a.b.x=/v1/sentiment",
                "/v1/sentiment?level=sentence",
                "/v1/sentiment",
        };
        for (String variant : variants) {
            EndPoint endPoint = EndPoint.valueOf(variant);
            Assert.assertEquals("/v1/sentiment", endPoint.getPath());
        }
    }

    @Test
    public void testParseNoField() throws Exception {
        String[] variants = {
                "/v1/sentiment?level=sentence&ff.xx=xx",
                "/v1/sentiment",
                "/v1/sentiment?level=sentence"
        };
        DataPath fieldPath = EndPoint.RESPONSE_FIELDNAME;
        testFieldPaths(variants, fieldPath);
    }

    @Test
    public void testParseSinglePathField() throws Exception {
        String[] variants = {
                "a=/v1/sentiment?level=sentence&ff.xx=xx",
                "a=/v1/sentiment",
                "a=/v1/sentiment?level=sentence"
        };
        DataPath fieldPath = new DataPath("a");
        testFieldPaths(variants, fieldPath);
    }

    @Test
    public void testParseMultiPathField() throws Exception {
        String[] variants = {
                "a.b=/v1/sentiment?level=sentence&ff.xx=xx",
                "a.b=/v1/sentiment",
                "a.b=/v1/sentiment?level=sentence"
        };
        DataPath fieldPath = new DataPath("a", "b");

        testFieldPaths(variants, fieldPath);
    }

    protected void testFieldPaths(String[] variants, DataPath fieldPath) {
        for (String variant : variants) {
            EndPoint endPoint = EndPoint.valueOf(variant);
            Assert.assertEquals(fieldPath, endPoint.getField());
        }
    }

}
