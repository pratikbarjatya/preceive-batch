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

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractHttpClient {
    protected ClientSettings configuration;
    protected CopyOnWriteArrayList<ClientListener> listeners = new CopyOnWriteArrayList<>();


    public AbstractHttpClient(ClientSettings configuration) {
        this.configuration = configuration;
    }


    public ClientSettings getConfiguration() {
        return configuration;
    }


    public void addListener(ClientListener listener) {
        this.listeners.add(listener);
    }

    protected final Response send(Request request) throws IOException {
        Response response;
        try {
            response = sendImpl(request);
            for (ClientListener listener : listeners) {
                listener.record(response);
            }
            return response;
        } catch (IOException e) {
            for (ClientListener listener : listeners) {
                listener.record(e);
            }
            throw e;
        }
    }

    protected abstract Response sendImpl(Request request) throws IOException;

    public Response get(String path) throws IOException {
        return send(Request.get(path));
    }

    public Response delete(String path) throws IOException {
        return send(Request.delete(path));
    }

    public Response post(String path, byte[] body) throws IOException {
        return send(Request.post(path, body));
    }

    public Response post(String path, String body) throws IOException {
        return post(path, body.getBytes(configuration.charset()));
    }

    public Response put(String path, byte[] body) throws IOException {
        return send(Request.put(path, body));
    }

    public Response put(String path, String body) throws IOException {
        return put(path, body.getBytes(configuration.charset()));
    }

}
