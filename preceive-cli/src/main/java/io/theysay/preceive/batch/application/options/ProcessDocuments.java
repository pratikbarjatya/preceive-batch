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
import io.theysay.preceive.batch.sources.Source;
import io.theysay.preceive.batch.sources.Sources;
import io.theysay.preceive.batch.utils.Datum;
import io.theysay.preceive.batch.utils.Resource;

public class ProcessDocuments extends Action {

    public ProcessDocuments() {
        super("process-documents",
            "Process each provided document one at a time and write the results for each document to a separate file.\n" +
                "-output value is used as a template inserting the current inputfile name before the extension.\n" +
                "For example for texts.tsv -output results/output.json -> results/output.texts.tsv.json.\n" +
                "The extension is included to ensure that the results for x.tsv and x.json do not overwrite each other.\n" +
                "A good example would be -output results/.json -> results/texts.tsv.json"
        );
    }

    @Override
    public void execute(ShellContext context) throws Exception {
        String inputs[] = context.arguments();
        Resource outputTemplate = Resource.valueOf(OutputOptions.OUTPUT.get());


        int backlog = ProcessOptions.BACKLOG.get();
        int threads = ProcessOptions.THREADS.get();

        for (String inputFile : inputs) {
            context.println("Processing " + inputFile);
            try {
                Resource resource = Resource.valueOf(inputFile);
                Source<Datum> source = Sources.create(resource, Datum.class);

                Resource destinationResource = outputTemplate.insertBeforeExtension(resource.getName());
                Destination<Datum> outputWriter = Destinations.create(destinationResource, Datum.class);


                EndPointProcessor processor = ProcessOptions.newProcessor();
                ApiBatchProcess apiBatchProcessor = new ApiBatchProcess(backlog, threads, source, processor, outputWriter);

                apiBatchProcessor.getPipeline().run(
                    context.printer(apiBatchProcessor)
                );
            } catch (Exception e) {
                context.println("Error processing " + inputFile);
            }
            context.println("Processing " + inputFile);
        }
    }
}