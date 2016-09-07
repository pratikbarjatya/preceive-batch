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

package io.theysay.preceive.batch.api;

import io.theysay.preceive.batch.destination.Destination;
import io.theysay.preceive.batch.http.Response;
import io.theysay.preceive.batch.pipeline.PipeProcess;
import io.theysay.preceive.batch.utils.Datum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EndPointProcessor implements PipeProcess<Datum, Datum> {
    private final static Logger _LOGGER = LoggerFactory.getLogger(EndPointProcessor.class);
    private final EndPoint[] endPoints;
    private final PreCeiveClient client;
    private DataFormat formatter;

    public EndPointProcessor(PreCeiveClient client,
                             DataFormat format,
                             EndPoint... endPoints) {
        this.client = client;
        this.formatter = format;
        this.endPoints = endPoints;
    }

    public DataFormat getFormatter() {
        return formatter;
    }

    public PreCeiveClient getClient() {
        return client;
    }

    public EndPoint[] getEndPoints() {
        return endPoints;
    }

    @Override
    public void execute(Datum inputData, Destination<Datum> destination) {

        Datum result = formatter.createInitialResult(inputData);
        String text = formatter.getText(inputData);
        Response response;

        try {

            for (EndPoint endPoint : endPoints) {
                Datum requestBody = endPoint.copyParameters();
                requestBody.put("text", text);
                response = client.post(endPoint.getPath(), requestBody);
                Object content = response.parse();
                result = formatter.addResponseToResult(result, content, endPoint.getField());
            }
        } catch (Exception e) {
            _LOGGER.error("Failed to process " + result.asString("id"), e);
            result = formatter.addError(result, e);
        }
        destination.write(result);
    }

}
