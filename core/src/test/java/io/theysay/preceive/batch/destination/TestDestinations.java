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

package io.theysay.preceive.batch.destination;

import io.theysay.preceive.batch.pipeline.Forwarder;
import io.theysay.preceive.batch.sources.IterableSource;
import io.theysay.preceive.batch.sources.Sources;
import io.theysay.preceive.batch.utils.Datum;
import io.theysay.preceive.batch.utils.IOUtils;
import io.theysay.preceive.batch.utils.Resource;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

public class TestDestinations {

    @Test
    public void testJSONDestination() throws Exception {


        List<Datum> items = Sources.read("classpath://test-input/100-texts.json", Datum.class);
        IterableSource<Datum> source = new IterableSource<>(items);
        Destination<Datum> output = Destinations.create("test.json", Datum.class);
        long count = Forwarder.copyAndClose(source, output);

        Assert.assertEquals(items.size(), count);

        List<Datum> written = Sources.read("test.json", Datum.class);
        // Remove the auto generated IDs...
        for (Datum datum : written) {
            datum.remove(Datum.AUTO_ID);
        }
        for (Datum datum : items) {
            datum.remove(Datum.AUTO_ID);
        }

        Assert.assertEquals(items, written);


    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnsupportedFileDestination() throws Exception {
        Destination<Datum> output = Destinations.create("test.bin", Datum.class);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnsupportedTypeDestination() throws Exception {
        Destination<Pattern> output = Destinations.create("test.json", Pattern.class);
    }

    @Test
    public void testRolloverDestination() throws Exception {
        IOUtils.clean("results");

        List<Datum> items = Sources.read("classpath://test-input/100-texts.json", Datum.class);
        IterableSource<Datum> source = new IterableSource<>(items);
        Destination<Datum> rollOver=Destinations.create("results/test.json",Datum.class,25);
        long count = Forwarder.copyAndClose(source, rollOver);
        Assert.assertEquals(items.size(), count);


        List<Datum> one = Sources.read("results/test.00000000.json", Datum.class);
        List<Datum> two = Sources.read("results/test.00000025.json", Datum.class);
        List<Datum> three = Sources.read("results/test.00000050.json", Datum.class);
        List<Datum> four = Sources.read("results/test.00000075.json", Datum.class);

        Assert.assertEquals(25,one.size());
        Assert.assertEquals(25,two.size());
        Assert.assertEquals(25,three.size());
        Assert.assertEquals(25,four.size());



    }
}
