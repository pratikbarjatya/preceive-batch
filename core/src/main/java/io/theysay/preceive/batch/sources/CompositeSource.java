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

import io.theysay.preceive.batch.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.Iterator;
import java.util.LinkedList;

public class CompositeSource<T> implements Source<T>, Closeable, Iterable<T> {
    private final Logger _logger = LoggerFactory.getLogger(getClass());

    private LinkedList<Source<T>> sources = new LinkedList<>();
    private Source<T> current = null;


    public void add(Source<T> source) {
        sources.add(source);
    }

    @Override
    public T next() {

        T next;

        while (ensureCurrentSource()) {
            try {
                next = current.next();

                if (next != null) return next;

            } catch (Exception e) {
                onError(e);
            }

            closeCurrent();

        }
        return null;
    }

    private boolean ensureCurrentSource() {
        if (current != null) return true;
        if (sources.isEmpty()) return false;
        current = sources.pop();
        return true;
    }

    private boolean closeCurrent() {
        IOUtils.close(current);
        current = (sources.isEmpty()) ? null : sources.pop();
        return current != null;
    }

    protected void onError(Exception e) {
        _logger.error("Unexpected exception", e);
    }

    @Override
    public void close() {
        while (ensureCurrentSource()) {
            closeCurrent();
        }
    }

    public int remaining() {
        return sources.size();
    }

    @Override
    public Iterator<T> iterator() {
        return new SourceIterator<>(this);
    }
}
