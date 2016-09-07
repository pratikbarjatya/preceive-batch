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
import io.theysay.preceive.batch.utils.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.NoSuchElementException;

public class TestSources {

    @Test
    public void testBadJson() throws Exception {
        // Contains 4 Ok and 1 broken json
        Source<Datum> source = Sources.create("classpath://sources/bad.json", Datum.class);

        Assert.assertEquals(4, count(source));

    }

    @Test(expected = IOException.class)
    public void testMissing() throws Exception {
        Source<Datum> source = Sources.create("classpath://sources/missin.json", Datum.class);
    }

    @Test
    public void testMissingAndOk() throws Exception {
        Source<Datum> source = Sources.sources(Datum.class, "classpath://sources/bad.json", "classpath://sources/missin.json");
        Assert.assertEquals(4, count(source));
    }

    @Test(expected = NoSuchElementException.class)
    public void testExhaustedIterator() throws Exception {
        Source<Datum> source = Sources.sources(Datum.class, "classpath://sources/bad.json");

        SourceIterator<Datum> iterator = new SourceIterator<>(source);
        iterator.next();
        iterator.next();
        iterator.next();
        iterator.next();
        iterator.next(); // Should throw exception
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNoRemove() throws Exception {
        Source<Datum> source = Sources.sources(Datum.class, "classpath://sources/bad.json");

        SourceIterator<Datum> iterator = new SourceIterator<>(source);
        iterator.remove();
    }

    public int count(Source<Datum> source) {
        try {
            int count = 0;
            for (Datum datum : Sources.foreach(source)) {
                count++;
            }
            return count;
        } finally {
            IOUtils.close(source);
        }
    }
}
