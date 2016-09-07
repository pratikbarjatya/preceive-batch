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

package io.theysay.preceive.batch.utils;

import java.util.ArrayList;

public class Providers<Implementation, Argument> {

    private Class<? extends Provider<Implementation, Argument>> providerClass;
    private ArrayList<? extends Provider<Implementation, Argument>> providers;


    public Providers(Class<? extends Provider<Implementation, Argument>> providerClass) {
        this.providerClass = providerClass;
        this.providers = new ArrayList<>(Classes.loadServices(providerClass));
    }

    public Implementation create(Argument argument, Class targetClass) throws Exception {
        Implementation implementation = tryCreate(argument, targetClass);
        if (implementation == null)
            throw new UnsupportedOperationException("Unable to create implementation for " + argument + " of " + targetClass);
        else
            return implementation;
    }

    public Implementation tryCreate(Argument argument, Class targetClass) throws Exception {
        for (Provider<Implementation, Argument> provider : providers) {
            Implementation implementation = provider.create(argument, targetClass);
            if (implementation != null) {
                return implementation;
            }
        }
        return null;
    }
}
