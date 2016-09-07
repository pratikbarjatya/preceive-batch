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

import io.theysay.preceive.batch.utils.Datum;
import io.theysay.preceive.batch.utils.JsonUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Response {

    private final StatusCode status;
    private final String message;
    private final long elapsed;
    private final String body;
    private final Map<String, List<String>> headers;

    public Response(StatusCode status, String body) {
        this(status, 0, status.message, body, new LinkedHashMap<String, List<String>>());
    }

    public Response(int status, long elapsed, String message, String body, Map<String, List<String>> headers) {
        this(StatusCode.valueOf(status), elapsed, message, body, headers);
    }

    public Response(StatusCode status, long elapsed, String message, String body, Map<String, List<String>> headers) {
        this.status = status;
        this.elapsed = elapsed;
        this.message = message;
        this.body = body;
        this.headers = headers;
//        if (body==null)
//            throw new NullPointerException("xxx");
    }


    public long getElapsed() {
        return elapsed;
    }

    public StatusCode getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getBody() {
        return body;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public boolean isOK() {
        return status.isOk();
    }

    public boolean isRateLimited() {
        return status == StatusCode.TooManyRequests;
    }

    public boolean isClientError() {
        return status.isClientError();
    }

    public boolean isServerError() {
        return status.isServerError();
    }


    public void validate() throws ApiException {
        if (isOK()) return;
        switch (status) {
            case TooManyRequests:
                throw new ApiException.RateLimit(message);
            case Unauthorized:
                throw new ApiException.AuthenicationRejected(message);
            default:
                if (isServerError())
                    throw new ApiException.Server(status, message);
                if (isClientError())
                    throw new ApiException.Client(status, message);
                throw new ApiException(status, body);

        }
    }

    public Datum[] asList() throws IOException {
        validate();
        return JsonUtils.readArray(body);
    }

    /**
     * @return
     * @throws IOException
     * @throws ApiException if the status code is not valid
     */
    public Datum asEntity() throws IOException, ApiException {
        validate();
        return JsonUtils.readItem(body);
    }

    /**
     * Parse with no validation
     *
     * @return
     */
    public Object parse() {
        if (body == null)
            return null;
        try {
            return JsonUtils.read(body);
        } catch (IOException e) {
            return new Datum().with("body", body); // No JSON message therefore take the raw body
        }
    }
}
