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

public class Request {


    private String url;
    private String method;
    private byte[] body;

    public Request(String method, String url, byte[] body) {
        this.body = body;
        this.method = method;
        this.url = url;
    }

    public String getURL() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public byte[] getBody() {
        return body;
    }

    public static Request delete(String path) {
        return new Request("DELETE", path, null);
    }

    public static Request get(String path) {
        return new Request("GET", path, null);
    }

    public static Request post(String path, byte[] data) {
        return new Request("POST", path, data);
    }

    public static Request put(String path, byte[] data) {
        return new Request("PUT", path, data);
    }

}
