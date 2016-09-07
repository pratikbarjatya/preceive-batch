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
package io.theysay.preceive.batch.processing;

import io.theysay.preceive.batch.api.EndPointProcessor;
import io.theysay.preceive.batch.api.RequestMonitor;
import io.theysay.preceive.batch.destination.Destination;
import io.theysay.preceive.batch.pipeline.Pipe;
import io.theysay.preceive.batch.pipeline.PipelineMonitor;
import io.theysay.preceive.batch.pipeline.Pipelines;
import io.theysay.preceive.batch.sources.Source;
import io.theysay.preceive.batch.utils.Datum;

import java.io.IOException;

/**
 * This class is merely a convenience wrapper to the moving parts of the pipeline.
 * As such it merely avoids unnecessary repetition of constructing a pipeline and monitors.
 *
 */
public final class ApiBatchProcess {
    private final PipelineMonitor pipelineMonitor;
    private final EndPointProcessor endpoints;
    private final Pipe pipeline;


    public ApiBatchProcess(int backlog, int threads, Source<Datum> sources, EndPointProcessor endpoints, Destination<Datum> destination) {
        this(endpoints, Pipelines.create(backlog, threads, sources, endpoints, destination));
    }

    public ApiBatchProcess(EndPointProcessor endpoints, Pipe pipeline) {
        this.endpoints = endpoints;
        this.pipeline = pipeline;
        this.pipelineMonitor = pipeline.createMonitor();
    }


    public RequestMonitor getRequestMonitor() {
        return endpoints.getClient().getMonitor();
    }

    public PipelineMonitor getPipelineMonitor() {
        return pipelineMonitor;
    }

    public Pipe getPipeline() {
        return pipeline;
    }


    public void testConnection() throws IOException {
        endpoints.getClient().validateConnection();
    }

    @Override
    public String toString() {
        RequestMonitor.Status status = getRequestMonitor().status();
        long produced = pipelineMonitor.getProduced();
        long processed = pipelineMonitor.getProcessed();
        return String.format("[%8.3f] %8d read %8d written, %8d requests { %6d ratelimited, %6d errors} ",
            status.elapsedSeconds(),
            produced,
            processed,
            status.totalRequests,
            status.rateLimits,
            status.totalErrors());
    }
}
