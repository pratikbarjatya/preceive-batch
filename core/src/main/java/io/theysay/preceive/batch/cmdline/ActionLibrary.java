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

import java.util.Comparator;

/**
 * This is an Service that can be used to add custom
 * options and actions to the client.
 */
public interface ActionLibrary {
    int CORE = 1;
    int PLUGIN = 1000;
    int APPLICATION = 2000;
    int DEFAULT = APPLICATION;

    public static final Comparator<ActionLibrary> PRIORITY = new Comparator<ActionLibrary>() {
        @Override
        public int compare(ActionLibrary left, ActionLibrary right) {
            return Integer.compare(left.priority(), right.priority());
        }
    };

    /**
     * This field is used to ensure consist registration order.
     * <p/>
     * All the libraries are collected - sorted by this key and then registered.
     * This ensures a more consistent ordering
     * e.g.
     * 0 +    -  Core Options
     * <p/>
     * 1000 + - Plugins
     * <p/>
     * 2000 + - Low priority Options
     *
     * @return a String formatted
     */
    public int priority();

    public void install(ShellContext context);

    public static class StaticFields implements ActionLibrary {


        private final int priority;

        public StaticFields() {
            this(DEFAULT);
        }

        public StaticFields(int priority) {
            this.priority = priority;
        }


        @Override
        public void install(ShellContext context) {
            context.register(getClass());
        }

        @Override
        public int priority() {
            return priority;
        }

    }
}
