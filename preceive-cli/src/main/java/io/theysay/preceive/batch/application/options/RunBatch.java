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

package io.theysay.preceive.batch.application.options;

import io.theysay.preceive.batch.api.EndPointProcessor;
import io.theysay.preceive.batch.cmdline.Action;
import io.theysay.preceive.batch.cmdline.ShellContext;
import io.theysay.preceive.batch.destination.Destination;
import io.theysay.preceive.batch.destination.Destinations;
import io.theysay.preceive.batch.options.OutputOptions;
import io.theysay.preceive.batch.options.ProcessOptions;
import io.theysay.preceive.batch.processing.ApiBatchProcess;
import io.theysay.preceive.batch.sources.CompositeSource;
import io.theysay.preceive.batch.sources.Sources;
import io.theysay.preceive.batch.utils.Datum;

public class RunBatch extends Action {
    public RunBatch() {
        super("batch",
            "Process the files provided as arguments consistent with the previously specified options.\n" +
                "The results will be save to the file specified using the -output option.\n" +
                " e.g. ... -batch <file1> <file2> <file3>");
    }

    @Override
    public void execute(ShellContext context) throws Exception {
        CompositeSource<Datum> sources = Sources.sources(Datum.class, context.arguments());


        int batchSize = OutputOptions.ROLL_OVER_SIZE.get();

        Destination<Datum> outputWriter = Destinations.create(OutputOptions.OUTPUT.get(), Datum.class, batchSize);


        EndPointProcessor endpointProcessor = ProcessOptions.newProcessor();

        int backlog = ProcessOptions.BACKLOG.get();
        int connections = ProcessOptions.THREADS.get();

        ApiBatchProcess apiBatchProcessor = new ApiBatchProcess(backlog, connections, sources, endpointProcessor, outputWriter);
        apiBatchProcessor.testConnection();
        apiBatchProcessor.getRequestMonitor().reset();


        Runnable progressLogger = context.printer(apiBatchProcessor);

        apiBatchProcessor.getPipeline().run(progressLogger);
    }
}
