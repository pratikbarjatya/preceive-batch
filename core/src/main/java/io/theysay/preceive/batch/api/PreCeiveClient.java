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

import io.theysay.preceive.batch.http.*;
import io.theysay.preceive.batch.utils.JsonUtils;
import io.theysay.preceive.batch.utils.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PreCeiveClient {
    private final static Logger _LOGGER = LoggerFactory.getLogger(PreCeiveClient.class);
    public static final String TOPIC_RESOURCE_LIST = "/v1/resources/topics/keywords";

    private AbstractHttpClient client;
    private RequestMonitor monitor;

    public PreCeiveClient(ClientSettings configuration) {
        this(new DefaultHttpClient(configuration));
    }

    public PreCeiveClient(AbstractHttpClient client) {
        this.client = client;
        this.monitor = new RequestMonitor();
        this.client.addListener(monitor);
    }

    public RequestMonitor getMonitor() {
        return monitor;
    }

    public PreCeiveClient listen(ClientListener listener) {
        client.addListener(listener);
        return this;
    }

    public Response post(String path, Object requestBody) throws IOException {

        String url = completePath(path);
        byte[] body = JsonUtils.toBytes(requestBody);
        int retries = getConfiguration().getMaximumAttempts();
        long backoff = getConfiguration().getRateLimitPauseMillis();

        Response response = null;

        while (retries-- > 0) {
            try {
                response = client.post(url, body);
                if (!response.isRateLimited()) return response;
                _LOGGER.warn("Hit the rate limits backing off for " + backoff + " milliseconds");
            } catch (IOException e) {
                // Networking problem....
                _LOGGER.error("Networking problem pausing for " + backoff + " milliseconds before retrying");
            }
            Utilities.pause(backoff);
        }
        if (response != null)
            return response;//
        else
            throw new IOException("Connectivity issue");
    }

    public String completePath(String path) {
        return getConfiguration().fullPath(path);
    }

    public boolean checkCredentials() throws IOException {
        return client.get(completePath(TOPIC_RESOURCE_LIST)).getStatus() != StatusCode.Unauthorized;
    }

    public String getApiService() {
        return getConfiguration().getService();
    }

    public void validateConnection() throws IOException {
        if (!checkCredentials())
            throw new ApiException.AuthenicationRejected("Username and password combination rejected for " + getApiService());
    }

    public ClientSettings getConfiguration() {
        return client.getConfiguration();
    }
}
