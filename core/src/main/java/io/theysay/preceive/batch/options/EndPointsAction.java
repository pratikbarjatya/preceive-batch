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

import java.util.Set;
import java.util.TreeSet;
import io.theysay.preceive.batch.api.EndPoint;
import io.theysay.preceive.batch.cmdline.Action;
import io.theysay.preceive.batch.cmdline.ShellContext;

public class EndPointsAction extends Action {

    private EndPoint[] endPoints;

    public EndPointsAction(String key, String usage) {
        super(key, usage);
    }

    @Override
    public void execute(ShellContext context) throws Exception {
        String[] arguments = context.arguments();
        if (arguments.length == 0)
            throw new IllegalArgumentException("At least one endpoint needs to be specified.");
        EndPoint[] ep = new EndPoint[arguments.length];
        for (int i = 0; i < ep.length; i++) {
            ep[i] = EndPoint.valueOf(arguments[i]);
        }
        this.endPoints = ep;
    }

    private void validate() {
        if (endPoints == null)
            throw new IllegalArgumentException("At least one endpoint needs to be specified.");

        TreeSet<String> paths = new TreeSet<String>();
        for (EndPoint endPoint : endPoints) {
            String path = String.join(".", endPoint.getField().getPath());
            if (!paths.add(path))
                // exact match
                throw new IllegalArgumentException("Clashing endpoint field: " + path);
        }

        // search for prefix match, e.g. foo & foo.bar
        for (String path : paths) {
            // NOTE: we add the . before the \ufffe because:
            //   foo & foo.bar is not OK
            //   foo & food is OK
            Set<String> longerPaths = paths.subSet(path, path + ".\ufffe");
            if (longerPaths.size() > 1)
                throw new IllegalArgumentException("Clashing endpoint fields: " + longerPaths);
        }
    }

    public EndPoint[] get() {
        validate();
        return endPoints;
    }
}
