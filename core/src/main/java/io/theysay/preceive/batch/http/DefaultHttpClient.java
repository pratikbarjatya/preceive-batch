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

package io.theysay.preceive.batch.http;

import io.theysay.preceive.batch.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class DefaultHttpClient extends AbstractHttpClient {
    private final static Logger _LOGGER = LoggerFactory.getLogger(DefaultHttpClient.class);


    public DefaultHttpClient(ClientSettings configuration) {
        super(configuration);
    }

    @Override
    public Response sendImpl(Request request) throws IOException {

        HttpURLConnection connection = null;
        String responseMessage;
        int responseCode = 0;
        String url = request.getURL();
        long start = System.currentTimeMillis();
        long elapsed = 0;
        String body;
        Map<String, List<String>> headerFields;

        try {
            connection = Connections.create(request.getURL());
            configure(request, connection);

            writeBody(connection, request);

            responseCode = connection.getResponseCode();
            responseMessage = connection.getResponseMessage();
            headerFields = connection.getHeaderFields();

            String encoding = connection.getHeaderField("Content-Encoding");


            if (responseCode >= 200 && responseCode <= 299) {
                body = readContent(encoding, connection.getInputStream());
            } else {
                body = readContent(encoding, connection.getErrorStream());
            }
            elapsed = System.currentTimeMillis() - start;
            return new Response(responseCode, elapsed, responseMessage, body, headerFields);
        } catch (IOException e) {
            // Networing problem....
            if (connection != null) {
                connection.disconnect();
            }
            throw e;
        } finally {
            _LOGGER.debug(String.format("%d\t%d\t%s", responseCode, elapsed, url));
        }
    }

    public void configure(Request request, HttpURLConnection connection) throws ProtocolException, UnsupportedEncodingException {
        connection.setRequestMethod(request.getMethod());
        connection.setReadTimeout(configuration.getReadTimeout());
        connection.setConnectTimeout(configuration.getConnectTimeout());

        // Shared headers
        for (Header header : configuration.headers()) {
            connection.setRequestProperty(header.name, header.value);
        }
    }

    protected HttpURLConnection create(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }

    protected void writeBody(HttpURLConnection connection, Request request) throws IOException {
        if (request.getBody() != null) {
            connection.setDoOutput(true);
            OutputStream output = null;
            try {
                output = connection.getOutputStream();
                output.write(request.getBody());
            } finally {
                IOUtils.close(output);
            }
        }
    }

    protected BufferedReader createReader(String encoding, InputStream inputStream) throws IOException {
        if (encoding != null && encoding.equalsIgnoreCase("gzip"))
            inputStream = new GZIPInputStream(inputStream);
        return new BufferedReader(new InputStreamReader(inputStream, configuration.getCharset()));
    }


    protected String readContent(String encoding, InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return null;
        }
        return IOUtils.getText(createReader(encoding, inputStream));
    }

}
