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

package io.theysay.preceive.batch.plugins.tsv;

import io.theysay.preceive.batch.sources.Sources;
import io.theysay.preceive.batch.utils.Datum;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TestTSVSource {

    @Test
    public void testReadExamples() throws Exception {
        List<Datum> read = Sources.read("classpath://sources/example.tsv", Datum.class);
        Assert.assertEquals(5, read.size());

    }

    @Test
    public void testReadBroken() throws Exception {
        List<Datum> read = Sources.read("classpath://sources/broken.tsv", Datum.class);
        Assert.assertEquals(4, read.size());

    }
    @Test (expected = java.lang.UnsupportedOperationException.class)
    public void testReadMissing() throws Exception {
        List<Datum> read = Sources.read("foobar://sources/broken.tsv", Datum.class);
        Assert.assertEquals(4, read.size());

    }
}
