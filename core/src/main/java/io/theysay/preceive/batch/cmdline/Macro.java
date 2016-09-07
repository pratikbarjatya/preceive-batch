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
package io.theysay.preceive.batch.cmdline;

import io.theysay.preceive.batch.utils.Datum;
import io.theysay.preceive.batch.utils.IOUtils;
import io.theysay.preceive.batch.utils.JsonUtils;

import java.io.IOException;

public class Macro extends Action {
    private String[] arguments;

    public Macro(String key, String usage, String... arguments) {
        super(key, usage);
        if (arguments == null || arguments.length == 0)
            throw new IllegalArgumentException(key + " has no arguments");
        this.arguments = arguments;
    }

    public String[] getArguments() {
        return arguments;
    }

    @Override
    public void execute(ShellContext context) throws Exception {
        context.push(arguments);
    }

    public static Macro[] loadMacros(String uri) throws IllegalArgumentException {
        Datum config;
        try {
            config = JsonUtils.readItem(IOUtils.getText(uri));
        } catch (IOException e) {
            throw new IllegalArgumentException("No macros found at uri", e);
        }

        Datum[] macroConfig = config.asDataArray("macros");

        if (macroConfig == null) {
            throw new IllegalArgumentException("No macros found in " + uri);
        }

        Macro[] macros = new Macro[macroConfig.length];
        for (int i = 0 ; i < macroConfig.length ; i++) {
            Datum datum = macroConfig[i];
            String key = datum.asString("option");
            String usage = datum.asString("usage");
            String[] arguments = datum.asStringArray("arguments");

            macros[i] = new Macro(key, usage, arguments);
        }
        return macros;
    }
}
