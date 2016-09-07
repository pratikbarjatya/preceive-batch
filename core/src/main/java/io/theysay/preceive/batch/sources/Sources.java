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

package io.theysay.preceive.batch.sources;

import io.theysay.preceive.batch.utils.Providers;
import io.theysay.preceive.batch.utils.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class Sources {
    private Sources() {
    }

    private final static Logger _LOGGER = LoggerFactory.getLogger(Sources.class);

    private static final Providers<Source, Resource> _providers = new Providers<>(SourceProvider.class);

    public static <T> Source<T> create(String uri_spec, Class<T> target) throws IOException {
        return create(Resource.valueOf(uri_spec), target);
    }

    /**
     * @throws IOException                   - if IOException occurs
     * @throws UnsupportedOperationException - if unable to match file and type
     */
    @SuppressWarnings("unchecked")
    public static <T> Source<T> create(Resource resource, Class<T> target) throws IOException, UnsupportedOperationException {
        try {
            return _providers.create(resource, target);
        } catch (IOException | UnsupportedOperationException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Source<T> tryCreate(Resource resource, Class<T> target) throws IOException {
        try {
            return _providers.tryCreate(resource, target);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }


    public static <T> Iterable<T> foreach(final Source<T> source) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new SourceIterator<T>(source);
            }
        };
    }

    public static <T> CompositeSource<T> sources(Class<T> target, String... uris) {
        CompositeSource<T> sources = new CompositeSource<>();
        for (String uri : uris) {
            try {
                Source<T> source = Sources.create(uri, target);
                sources.add(source);
            } catch (IOException e) {
                _LOGGER.error("Unable to create source " + uri, e);
            }
        }
        return sources;
    }

    /**
     * Read all items in the source
     */
    public static <T> List<T> read(Source<T> source) {
        ArrayList<T> result = new ArrayList<>();
        for (T t : foreach(source)) {
            result.add(t);
        }
        return result;
    }

    /**
     * Read all items in the source
     */
    public static <T> List<T> read(String uri, Class<T> target) throws IOException {
        return read(create(uri, target));
    }


}
