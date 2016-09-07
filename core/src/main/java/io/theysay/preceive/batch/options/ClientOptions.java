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

package io.theysay.preceive.batch.options;

import io.theysay.preceive.batch.api.PreCeiveClient;
import io.theysay.preceive.batch.cmdline.Action;
import io.theysay.preceive.batch.cmdline.ActionLibrary;
import io.theysay.preceive.batch.http.ClientSettings;

public class ClientOptions extends ActionLibrary.StaticFields {

    public ClientOptions() {
        super(CORE);
    }

    public static Action.Property<String> USERNAME = Action.string("user", System.getenv("THEYSAY_USER"), "A PreCeive REST API username.");
    public static Action.Property<String> PASSWORD = Action.string("password", System.getenv("THEYSAY_PASSWORD"), "A PreCeive REST API password.");
    public static Action.Property<String> SERVICE = Action.string("service",
            System.getenv("THEYSAY_SERVICE") == null ?
                    "https://api.theysay.io"
                    :
                    System.getenv("THEYSAY_SERVICE")
            , "The PreCeive REST API service to use.");

    public static ClientSettings getClientSettings() {
        ClientSettings settings = new ClientSettings();
        settings.setService(SERVICE.get());
        settings.setUsername(USERNAME.get());
        settings.setPassword(PASSWORD.get());
        return settings;
    }

    public static PreCeiveClient getClient() {
        return new PreCeiveClient(getClientSettings());
    }
}
