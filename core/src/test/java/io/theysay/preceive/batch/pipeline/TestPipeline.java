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

package io.theysay.preceive.batch.pipeline;

import io.theysay.preceive.batch.destination.Destination;
import io.theysay.preceive.batch.sources.Source;
import io.theysay.preceive.batch.sources.Sources;
import io.theysay.preceive.batch.utils.Datum;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicLong;

public class TestPipeline {

    public static final int TOTAL_LENGTH = 12382;

    @Test
    public void testCoreUsage() throws Exception {
        int backlog = 10;
        int threads = 4;

        Source<Datum> sourceData = Sources.create("classpath://test-input/100-texts.json", Datum.class);
        PipeProcess<Datum, Integer> textLength = new TextLength();

        Accumulator accumulator = new Accumulator();

        Pipe<Integer> pipeline =
                Pipe.produce(10, sourceData)
                        .map(backlog, threads, textLength) // Issue Requests
                        .tee(accumulator)                  // Write to destination
                        .start();

        long sum = 0;
        long count = 0;
        for (Integer integer : pipeline) {
            count++;
            sum += integer;
        }

        Assert.assertEquals(100, count);
        Assert.assertEquals(TOTAL_LENGTH, sum);
        Assert.assertEquals(sum, accumulator.sum.get());

    }

    @Test
    public void testConsume() throws Exception {
        int backlog = 10;
        int threads = 4;

        Source<Datum> sourceData = Sources.create("classpath://test-input/100-texts.json", Datum.class);
        PipeProcess<Datum, Integer> textLength = new TextLength();

        Accumulator accumulator = new Accumulator();

        long numberOfRecords =
                Pipe.produce(10, sourceData)
                        .map(backlog, threads, textLength) // Issue Requests
                        .tee(accumulator)                  // Write to destination
                        .run();

        Assert.assertEquals(100, numberOfRecords);
        Assert.assertEquals(TOTAL_LENGTH, accumulator.sum.get());
    }

    @Test
    public void testProcessErrors() throws Exception {
        int backlog = 10;
        int threads = 4;

        Source<Datum> sourceData = Sources.create("classpath://test-input/100-texts.json", Datum.class);
        PipeProcess<Datum, Integer> textLength = new TextLengthWithErrors();

        Accumulator accumulator = new Accumulator();

        long numberOfRecords =
                Pipe.produce(10, sourceData)
                        .map(backlog, threads, textLength) // Issue Requests
                        .tee(accumulator)                  // Write to destination
                        .run();

        int input = 100;
        Assert.assertEquals(input - (input / 10), numberOfRecords); // We have processed all 10%
    }

    @Test(expected = IllegalStateException.class)
    public void testMultipleStart() throws Exception {
        int backlog = 10;
        int threads = 4;

        Source<Datum> sourceData = Sources.create("classpath://test-input/100-texts.json", Datum.class);
        PipeProcess<Datum, Integer> textLength = new TextLength();

        Accumulator accumulator = new Accumulator();

        Pipe<Integer> pipeline =
                Pipe.produce(10, sourceData)
                        .map(backlog, threads, textLength) // Issue Requests
                        .tee(accumulator)                  // Write to destination
                        .start();
        pipeline.start();
    }

    @Test
    public void testStackPrint() throws Exception {
        String expected = " 0 : [ ->Producer ]\n" +
                " 1 : [ ->map ]\n" +
                " 2 : [ ->tee ]\n";

        int backlog = 10;
        int threads = 4;

        Source<Datum> sourceData = Sources.create("classpath://test-input/100-texts.json", Datum.class);
        PipeProcess<Datum, Integer> textLength = new TextLength();

        Accumulator accumulator = new Accumulator();

        Pipe<Integer> pipeline =
                Pipe.produce(10, sourceData)
                        .map(backlog, threads, textLength) // Issue Requests
                        .tee(accumulator)                  // Write to destination
                ;

        String stackDump = pipeline.printPipeline();
        Assert.assertEquals(expected, stackDump);
    }


    static class Accumulator implements Destination<Integer> {
        private AtomicLong sum = new AtomicLong(0);

        @Override
        public void write(Integer output) {
            sum.getAndAdd(output);
        }

        @Override
        public void close() {

        }
    }

    static class TextLength implements PipeProcess<Datum, Integer> {
        @Override
        public void execute(Datum argument, Destination<Integer> destination) {
            int length = argument.asString("text").length();
            destination.write(length);
        }
    }

    static class TextLengthWithErrors implements PipeProcess<Datum, Integer> {
        private AtomicLong count = new AtomicLong();


        @Override
        public void execute(Datum argument, Destination<Integer> destination) {
            long item = count.incrementAndGet() % 10;
            if (item == 0)
                throw new NullPointerException("Fake exception");
            int length = argument.asString("text").length();
            destination.write(length);
        }
    }
}
