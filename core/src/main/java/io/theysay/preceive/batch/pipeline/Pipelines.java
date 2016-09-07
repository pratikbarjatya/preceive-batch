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

/**
 * A static class with some handy 'pre-canned' configurations
 */
public final class Pipelines {
    private Pipelines() {
    }

    /**
     * Create and run a pipeline returning the number of elements processed.
     */
    @SafeVarargs
    public static <In, Out> long run(int backlog, int threads,
                                     Source<In> sources,
                                     PipeProcess<In, Out> processor,
                                     Runnable monitor,
                                     Destination<Out>... destinations) {
        Pipe<Out> pipeline = create(backlog, threads, sources, processor, destinations);
        return pipeline.run(monitor);

    }

    /**
     * Create and run a pipeline returning the number of elements processed.
     */
    @SafeVarargs
    public static <In, Out> long run(int backlog, int threads,
                                     Source<In> sources,
                                     PipeProcess<In, Out> processor,
                                     Destination<Out>... destinations) {
        Pipe<Out> pipeline = create(backlog, threads, sources, processor, destinations);

        return pipeline.run();

    }

    /**
     * Create a pipeline but does not start or run it.
     */
    @SafeVarargs
    public static <In, Out> Pipe<Out> create(int backlog, int threads, Source<In> sources, PipeProcess<In, Out> processor, Destination<Out>... destinations) {
        return Pipe.produce(backlog, sources)
                .map(backlog, threads, processor)
                .tee(destinations);
    }

}
