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

import io.theysay.preceive.batch.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@SuppressWarnings("unchecked")
public class MulticastDestination<T> implements Destination<T>, Closeable {
    private final static Logger _LOGGER = LoggerFactory.getLogger(MulticastDestination.class);

    private ArrayList<Destination<T>> destinations;


    public MulticastDestination(Destination<T> destination, Collection<Destination<T>> destinations) {
        this(destination);
        addAll(destinations);
    }

    public MulticastDestination(Collection<Destination<T>> destinations) {
        this.destinations = new ArrayList<>(destinations);

    }

    public MulticastDestination(Destination<T> destination) {
        this(Collections.singletonList(destination));
    }

    public void add(Destination<T> destination) {
        this.destinations.add(destination);
    }

    public void addAll(Collection<Destination<T>> destinations) {
        this.destinations.addAll(destinations);
    }

    @Override
    public void close() throws IOException {
        for (Destination<T> destination : destinations) {
            IOUtils.close(destination);
        }
    }

    @Override
    public void write(T output) {
        for (Destination destination : destinations) {
            try {
                destination.write(output);
            } catch (Exception e) {
                _LOGGER.error("Multicasting:", e);
            }
        }
    }
}
