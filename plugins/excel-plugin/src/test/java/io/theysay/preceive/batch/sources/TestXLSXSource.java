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

package io.theysay.preceive.batch.sources;

import io.theysay.preceive.batch.utils.Datum;
import io.theysay.preceive.batch.utils.Resource;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class TestXLSXSource {

    @Test
    public void testReadActiveSheet() throws Exception {
        Resource xlsxResource = Resource.valueOf("classpath://test1.xlsx");
        Source<Datum> source = Sources.create(xlsxResource, Datum.class);
        List<Datum> read = Sources.read(source);
        Assert.assertEquals(6, read.size());
        for (Datum datum : read) {
            Assert.assertNotNull(datum.asString("id"));
            Assert.assertNotNull(datum.asString("text"));
            Assert.assertNull(datum.asString("timestamp"));
        }

    }

    @Test
    public void testReadActiveSheetByName() throws Exception {
        Resource xlsxResource = Resource.valueOf("classpath://test1.xlsx#Texts");
        Source<Datum> source = Sources.create(xlsxResource, Datum.class);
        List<Datum> read = Sources.read(source);
        Assert.assertEquals(6, read.size());
        for (Datum datum : read) {
            Assert.assertNotNull(datum.asString("id"));
            Assert.assertNotNull(datum.asString("text"));
            Assert.assertNull(datum.asString("timestamp"));
        }

    }

    @Test(expected = IOException.class)
    public void testEmptySheet() throws Exception {
        Resource xlsxResource = Resource.valueOf("classpath://test1.xlsx#Empty");
        Sources.create(xlsxResource, Datum.class);
    }

    @Test(expected = IOException.class)
    public void testMissingSheet() throws Exception {
        Resource xlsxResource = Resource.valueOf("classpath://test1.xlsx#Missing");
        Sources.create(xlsxResource, Datum.class);
    }

    @Test
    public void testOtherDataSheet() throws Exception {
        Resource xlsxResource = Resource.valueOf("classpath://test1.xlsx#OtherData");
        Source<Datum> source = Sources.create(xlsxResource, Datum.class);
        List<Datum> read = Sources.read(source);
        Assert.assertEquals(6, read.size());
        for (Datum datum : read) {
            Assert.assertNotNull(datum.asString("id"));
            Assert.assertNotNull(datum.asString("text"));
            Assert.assertNotNull(datum.asString("timestamp"));
        }

    }
}
