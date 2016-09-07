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

package io.theysay.preceive.batch.destination;

import io.theysay.preceive.batch.utils.Providers;
import io.theysay.preceive.batch.utils.Resource;

import java.io.IOException;

public class Destinations {

    private static final Providers<Destination, Resource> _providers = new Providers<>(DestinationProvider.class);

    public static <T> Destination<T> create(String uri_spec, Class<T> target) throws IOException {
        return create(uri_spec, target, -1);
    }

    public static <T> Destination<T> create(String uri_spec, Class<T> target, int batchSize) throws IOException {
        Resource uri = Resource.valueOf(uri_spec);
        if (batchSize < 0) {
            return create(uri, target);
        } else {
            return new RollOverDestination<T>(uri, target, batchSize);
        }
    }

    @SuppressWarnings("unchecked")
    /**
     * @throws IOException - if IOException occurs
     * @throws UnsupportedOperationException - if unable to match file and type
     */
    public static <T> Destination<T> create(Resource uri, Class<T> target) throws IOException, UnsupportedOperationException {
        try {
            return _providers.create(uri, target);
        } catch (IOException | UnsupportedOperationException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
