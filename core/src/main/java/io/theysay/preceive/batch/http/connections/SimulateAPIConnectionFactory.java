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

package io.theysay.preceive.batch.http.connections;

import io.theysay.preceive.batch.http.StatusCode;
import io.theysay.preceive.batch.http.connections.HttpURLConnectionAdaptor.ConnectionResponse;
import io.theysay.preceive.batch.utils.Datum;
import io.theysay.preceive.batch.utils.IOUtils;
import io.theysay.preceive.batch.utils.Resource;
import io.theysay.preceive.batch.utils.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;


public class SimulateAPIConnectionFactory implements HttpURLConnectionFactory {
    private static final Logger _LOGGER = LoggerFactory.getLogger(SimulateAPIConnectionFactory.class);

    public static final String SIMULATE_PROTOCOL = "simulate";
    public static final String NOT_FOUND = "{\"errors\":[{\"message\":\"The requested resource could not be found.\"}]}";
    public static final String TEXT_MISSING_OR_TOO_SHORT = "{\"errors\":[{\"message\":\"Text must be more than 10\"}]}";

    private URL dummyURL;
    private Map<String, ResponseEntry> responses;

    public SimulateAPIConnectionFactory() {
        this.responses = loadEntries();
        try {
            this.dummyURL = new URL("http://www.theysay.io");
        } catch (MalformedURLException e) {
            // Will never happen...
        }
    }

    @Override
    public HttpURLConnection create(Resource url) throws IOException {

        if (url.getScheme().equalsIgnoreCase(SIMULATE_PROTOCOL))
            return new SimulatedConnection(url);
        else
            return null;
    }

    public class SimulatedConnection extends HttpURLConnectionAdaptor {


        public SimulatedConnection(Resource uri) {
            super(uri);
        }

        @Override
        protected ConnectionResponse createResponse() throws IOException {
            String host = uri.getHost();
            if (host.contains("nonsuch"))
                throw new IOException("No such site");

            String path = uri.getPath();


            ResponseEntry responseEntry = responses.get(path);

            if (responseEntry == null)
                return new ConnectionResponse(StatusCode.NotFound, NOT_FOUND);

            int port = uri.getPort();
            // Pause for the 'port specified millis...
            Utilities.pause(port);


            return responseEntry.createResponse(this);
        }
    }

    private static abstract class ResponseEntry {
        final ResponseEntry previous;
        final String path;
        final String level;
        final String value;

        public ResponseEntry(ResponseEntry previous, String path, String level, String value) {
            this.previous = previous;
            this.path = path;
            this.level = level;
            this.value = value;
        }

        public abstract ConnectionResponse createResponse(SimulatedConnection connection) throws IOException;
    }

    private static class ResourceEntry extends ResponseEntry {
        public ResourceEntry(ResponseEntry previous, String path, String level, String value) {
            super(previous, path, level, value);
        }

        @Override
        public ConnectionResponse createResponse(SimulatedConnection connection) throws IOException {
            switch (connection.getRequestMethod()) {
                case "POST":
                    return new ConnectionResponse(StatusCode.Created, "");
                case "PUT":
                case "DELETE":
                    return new ConnectionResponse(StatusCode.OK, "");
                default:
                    return new ConnectionResponse(StatusCode.OK, value);

            }
        }
    }

    private static class AnalysisEntry extends ResponseEntry {
        public AnalysisEntry(ResponseEntry previous, String path, String level, String value) {
            super(previous, path, level, value);
        }

        /**
         * Find Entry with matching level param or failing that the very first entry loaded.
         *
         * @param level [documemt, sentence etc.]
         * @return The appropriate level
         */
        public ResponseEntry findLevel(String level) {
            ResponseEntry levelEntry = this;
            while (levelEntry.previous != null) {
                if (Objects.equals(level, levelEntry.level))
                    return levelEntry;
                levelEntry = levelEntry.previous;
            }
            return levelEntry;
        }

        @Override
        public ConnectionResponse createResponse(SimulatedConnection connection) throws IOException {
            Datum bodyData = connection.getRequestBodyData();
            String text = bodyData.asString("text");
            String level = bodyData.asString("level");

            if (text == null || text.length() < 10)
                return new ConnectionResponse(StatusCode.BadRequest, TEXT_MISSING_OR_TOO_SHORT);


            ResponseEntry appropriateLevel = findLevel(level);

            return new ConnectionResponse(StatusCode.OK, appropriateLevel.value);
        }
    }


    public static Map<String, ResponseEntry> loadEntries() {
        Map<String, ResponseEntry> responses = new LinkedHashMap<>();
        try {
            String[][] table = IOUtils.readTabSeparated("classpath://simulated/responses.tsv");
            for (String[] row : table) {
                String path = row[0];
                String level = row[1];
                String content = row[2];
                ResponseEntry previous = responses.get(path);
                if (path.contains("/resources/")) {
                    responses.put(path, new ResourceEntry(previous, path, level, content));
                } else {
                    responses.put(path, new AnalysisEntry(previous, path, level, content));
                }
            }
        } catch (IOException e) {
            _LOGGER.warn("Unable to load responses so none defined.");
        }
        return responses;
    }


}
