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
import io.theysay.preceive.batch.utils.Datum;
import io.theysay.preceive.batch.utils.JsonUtils;
import io.theysay.preceive.batch.utils.Resource;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public abstract class HttpURLConnectionAdaptor extends HttpURLConnection {
    public static final String[] NON_HEADERS = new String[0];


    public static final URL DUMMY_URL = getDummy();

    private static URL getDummy() {
        try {
            return new URL("http://www.theysay.io");
        } catch (MalformedURLException e) {
            // Will never happen...
            return null;
        }
    }

    private ByteArrayOutputStream outputStream;
    private ConnectionResponse response;
    protected final Resource uri;

    public HttpURLConnectionAdaptor(Resource uri) {
        super(DUMMY_URL);
        this.outputStream = new ByteArrayOutputStream();
        this.uri = uri;
    }

    @Override
    public void disconnect() {

    }

    @Override
    public boolean usingProxy() {
        return false;
    }

    @Override
    public void connect() throws IOException {

    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return outputStream;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return getResponse().getInputStream();
    }

    @Override
    public InputStream getErrorStream() {
        return null;
    }


    @Override
    public String getHeaderField(int n) {
        return getResponse().getHeaderField(n);
    }


    private ConnectionResponse getResponse() {
        if (response == null) {
            try {
                response = createResponse();
            } catch (IOException ioe) {
                response = new ConnectionResponse(StatusCode.UNKNOWN, null);
            }
        }
        return response;
    }

    protected abstract ConnectionResponse createResponse() throws IOException;


    public Datum getRequestBodyData() throws IOException {
        try {
            return JsonUtils.readItem(outputStream.toString("UTF-8"));
        } catch (IOException e) {
            return new Datum();
        }
    }

    public static class ConnectionResponse {
        private StatusCode status;
        private ArrayList<String> headerFields = new ArrayList<>();
        private byte[] body;

        public ConnectionResponse(StatusCode status, String body) {
            this.status = status;
            this.body = (body != null) ? body.getBytes(StandardCharsets.UTF_8) : null;

            if (status != StatusCode.UNKNOWN) {
                headerFields.add("HTTP/1.1 " + status.code + " " + status.message);
                headerFields.add("Content-Type:application/json; charset=UTF-8");
                headerFields.add("Transfer-Encoding:chunked");
            }
        }

        public InputStream getInputStream() throws IOException {
            if (this.headerFields.isEmpty()) {
                throw new IOException("Networking issue occurred");
            }
            return new ByteArrayInputStream(body);
        }

        public String getHeaderField(int n) {
            return (n < headerFields.size()) ? headerFields.get(n) : null;
        }
    }


}
