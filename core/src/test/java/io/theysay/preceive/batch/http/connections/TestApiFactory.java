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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class TestApiFactory implements HttpURLConnectionFactory {

    public static final String EXPECTED_AUTHORISATION = "Basic dGVzdDp0ZXN0";

    private final Map<String, StatusCode> keywords = new LinkedHashMap<>();

    public TestApiFactory() {
        this.keywords.put("aesthetic", StatusCode.BadRequest);
        this.keywords.put("1987", StatusCode.TooManyRequests);
        this.keywords.put("Gertrude", StatusCode.InternalServerError);
        this.keywords.put("awkward", StatusCode.UNKNOWN);
    }

    @Override
    public HttpURLConnection create(Resource url) throws IOException {

        if (url.getScheme().equalsIgnoreCase("test"))
            return new TestConnection(url);
        else
            return null;
    }


    public class TestConnection extends HttpURLConnectionAdaptor {

        public TestConnection(Resource uri) {
            super(uri);
        }

        @Override
        protected ConnectionResponse createResponse() throws IOException {

            checkHost();
            if (!checkAuthority())
                return new ConnectionResponse(StatusCode.Unauthorized, "");

            Datum bodyData = getRequestBodyData();
            String text = bodyData.asString("text");
            if (text != null) {
                for (String keyword : keywords.keySet()) {
                    if (text.contains(keyword)) {
                        return new ConnectionResponse(keywords.get(keyword), JsonUtils.toString(bodyData));
                    }
                }
            }

            return new ConnectionResponse(StatusCode.OK, JsonUtils.toString(bodyData));
        }

        private boolean checkAuthority() {
            String authorization = getRequestProperty("Authorization");

            return Objects.equals(authorization, EXPECTED_AUTHORISATION);
        }

        private void checkHost() throws IOException {
            String host = uri.getHost();
            if (host.contains("nonsuch"))
                throw new IOException("No such site");
        }
    }


}
