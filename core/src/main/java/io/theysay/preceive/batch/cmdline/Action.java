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

import io.theysay.preceive.batch.utils.Classes;

public abstract class Action {
    private String key;
    private String usage;

    public Action(String key, String usage) {
        this.key = key;
        this.usage = usage;
    }

    public String getKey() {
        return key;
    }

    public String getUsage() {
        return usage;
    }

    public String usage() {
        return getUsage();
    }

    public String[] usageLines() {
        return usage().split("(\\r|\\n)+");
    }

    public abstract void execute(ShellContext context) throws Exception;


    public static Property<String> string(String key, String defaultValue, String usage) {
        return new Property<>(key, defaultValue, String.class, usage);
    }

    public static Property<Integer> integer(String key, int defaultValue, String usage) {
        return new Property<>(key, defaultValue, Integer.class, usage);
    }

    /**
     * Common case of setting property values
     *
     * @param <T> Type of Property must either have a static .valueOf(String) or a single String constructor
     */
    public static class Property<T extends Object> extends Action {
        private T value;
        private Class targetClass;

        public Property(String key, T defaultValue, String usage) {
            this(key, defaultValue, defaultValue.getClass(), usage);
        }

        public Property(String key, T defaultValue, Class targetClass, String usage) {
            super(key, usage);
            this.targetClass = targetClass;
            this.value = defaultValue;
        }

        @Override
        public void execute(ShellContext context) throws Exception {
            String stringValue = context.nextArgument("value");
            //noinspection unchecked
            this.value = convert(stringValue);
        }

        public T get() {
            return this.value;
        }


        @SuppressWarnings("unchecked")
        protected T convert(String stringValue) {
            return (T) Classes.valueOf(targetClass, stringValue);
        }

    }


}
