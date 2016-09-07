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
import io.theysay.preceive.batch.destination.MulticastDestination;
import io.theysay.preceive.batch.sources.Source;
import io.theysay.preceive.batch.utils.IOUtils;
import io.theysay.preceive.batch.utils.TimePoller;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Pipe<T> implements Source<T>, Iterable<T> {


    public static final int NOT_STARTED = 0;
    public static final int STARTING = 1;
    public static final int STARTED = 2;
    public static final long DEFAULT_RESOLUTION = 200L;

    private final Pipe previous;
    private final CloseablePipe<T> output;
    private final PipeRunnable[] runnables;
    private final AtomicInteger state = new AtomicInteger(NOT_STARTED);
    private final int id;

    public Pipe(Pipe previous, CloseablePipe<T> output, PipeRunnable... runnables) {
        this.previous = previous;
        this.output = output;
        this.runnables = runnables;
        this.id = 1 + ((previous != null) ? previous.id : 0);
    }


    @SuppressWarnings("unchecked")
    public <R> Pipe<R> map(int backlog, int concurrency, PipeProcess<T, R> pipeProcess) {
        CloseablePipe<R> output = new CloseablePipe<>("map", backlog);
        CloseablePipe<T> input = this.output;
        Processor<T, R>[] processes = Processor.fork(concurrency, input, pipeProcess, output);
        return new Pipe<>(this, output, processes);
    }

    @SafeVarargs
    public final Pipe<T> tee(Destination<T>... destinations) {

        if (destinations.length == 0)
            return this;
        else
            return tee(Arrays.asList(destinations));
    }

    public Pipe<T> tee(Collection<Destination<T>> destinations) {
        CloseablePipe<T> output = new CloseablePipe<>("tee");
        MulticastDestination<T> mcDestination = new MulticastDestination<>(output, destinations);
        CloseablePipe<T> input = this.output;
        return new Pipe<>(this, output, new Forwarder<>(input, mcDestination));
    }


    private void startImpl(ExecutorService executorService) {
        if (!state.compareAndSet(NOT_STARTED, STARTING))
            throw new IllegalStateException("Already started this pipe!");

        if (previous != null) previous.startImpl(executorService);
        for (PipeRunnable runnable : runnables) {
            executorService.submit(runnable);
        }
        state.set(STARTED);
    }

    public Pipe<T> start(ExecutorService executorService) {
        startImpl(executorService);
        return this;
    }

    /**
     * Start processing the pipeline and return immediately.
     * Note that if the pipeline has been previously started an IllegalStateException will be thrown
     *
     * @return
     * @throws IllegalStateException - if the pipeline has already been started.
     */
    public Pipe<T> start() {
        ExecutorService service = Executors.newCachedThreadPool();
        try {
            start(service);
        } finally {
            service.shutdown(); // Ensure it quits when all threads complete.
        }
        return this;
    }

    /**
     * Run the pipeline and do not return until the pipeline has completed.
     * This waits until the pipeline has completed
     *
     * @return number of elements consumed
     */
    public long run() {
        return start().consume();
    }

    public long run(Runnable monitor) {
        return start().consume(monitor);
    }


    /**
     * Consume the pipeline and invoke the monitor every 5 seconds
     *
     * @param monitor to invoke
     * @return
     */
    public long consume(Runnable monitor) {
        return consume(5000L, monitor);
    }

    /**
     * Consume all the output counting the number of records consumed
     * This waits until the pipeline has completed
     * The provided runnable is invoked every number of milliseconds and once at the end.
     * If the runnable implements 'Closable' it will also be closed.
     *
     * @param runEveryMillis Millimum number of milliseconds to separate between monitor runs
     * @param monitor        - code to execute
     * @return number of elements consumed
     */
    public long consume(long runEveryMillis, Runnable monitor) {
        Runnable invocation = new TimePoller(runEveryMillis).wrap(monitor);

        // We now need to consume the results by reporting the metrics...
        // We poll for 'resolution' millis to check the poller. We assume an error or +- 10%

        long resolution = Math.max(1, Math.min(DEFAULT_RESOLUTION, runEveryMillis / 10));
        while (!output.isClosed()) {

            output.poll(resolution);
            invocation.run();

        }
        IOUtils.close(invocation);
        return output.getReads();
    }


    /**
     * Consume all the output counting the number of records consumed
     * This waits until the pipeline has completed
     *
     * @return number of items consumed
     */
    public long consume() {
        while (!output.isClosed()) {
            output.next();
        }
        return output.getReads();
    }


    public Pipe[] getPipeline() {
        LinkedList<Pipe> stack = new LinkedList<>();
        Pipe p = this;
        while (p != null) {
            stack.push(p);
            p = p.previous;
        }
        return stack.toArray(new Pipe[stack.size()]);
    }

    public String toString() {
        return String.format("[ ->%s ]", this.output);
    }

    public String printPipeline() {
        StringBuilder b = new StringBuilder();
        Pipe[] pipeline = getPipeline();
        for (int i = 0 ; i < pipeline.length ; i++) {
            Pipe pipe = pipeline[i];
            b.append(String.format("%2d : %s\n", i, pipe));
        }
        return b.toString();
    }

    @Override
    public Iterator<T> iterator() {
        return output.iterator();
    }

    @Override
    public T next() {
        return output.next();
    }


    public long getWrites() {
        return output.getWrites();
    }

    public long getReads() {
        return output.getReads();
    }

    public PipelineMonitor createMonitor() {
        Pipe<?> head = this;
        while (head.previous != null) {
            head = head.previous;
        }
        return new PipelineMonitor(head, this);
    }


    public static <T> Pipe<T> produce(int backlog, Source<T> source) {
        CloseablePipe<T> output = new CloseablePipe<>("Producer", backlog);
        Producer<T> producer = new Producer<>(source, output);
        return new Pipe<>(null, output, producer);
    }
}