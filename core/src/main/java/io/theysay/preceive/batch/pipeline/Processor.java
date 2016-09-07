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
import io.theysay.preceive.batch.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicInteger;


public class Processor<In, Out> implements PipeRunnable {
    private final Logger logger;

    private final Source<In> inbound;
    private final Destination<Out> outbound;
    private final PipeProcess<In, Out> pipeProcess;

    public Processor(Source<In> inbound, Destination<Out> outbound, PipeProcess<In, Out> pipeProcess) {
        this.inbound = inbound;
        this.outbound = outbound;
        this.pipeProcess = pipeProcess;
        logger = LoggerFactory.getLogger(pipeProcess.getClass());
    }

    @Override
    public void run() {
        try {
            In argument;
            while ((argument = inbound.next()) != null) {
                try {
                    pipeProcess.execute(argument, outbound);
                } catch (Exception e) {
                    logger.error("Exception during processing ", e);
                }
            }
        } finally {
            IOUtils.close(outbound);
        }

    }


    @SuppressWarnings("unchecked")
    public static <In, Out> Processor<In, Out>[] fork(int count, Source<In> inbound, PipeProcess<In, Out> pipeProcess, Destination<Out> outbound) {
        Processor<In, Out>[] results = (Processor<In, Out>[]) new Processor[count];
        AtomicInteger counter = new AtomicInteger(count);
        for (int i = 0; i < count; i++) {
            results[i] = new Processor<>(inbound, new CountDown<>(counter, outbound), pipeProcess);
        }
        return results;

    }

    private static class CountDown<T> implements Destination<T>, Closeable {
        private final AtomicInteger liveThreads;
        private final Destination<T> underlying;

        public CountDown(AtomicInteger liveThreads, Destination<T> underlying) {
            this.liveThreads = liveThreads;
            this.underlying = underlying;
        }

        @Override
        public void write(T output) {
            underlying.write(output);
        }

        @Override
        public void close() {
            if (liveThreads.decrementAndGet() == 0) {
                IOUtils.close(underlying);
            }
        }

    }

}
