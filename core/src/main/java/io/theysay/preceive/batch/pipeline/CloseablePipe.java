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

package io.theysay.preceive.batch.pipeline;

import io.theysay.preceive.batch.destination.Destination;
import io.theysay.preceive.batch.sources.Source;
import io.theysay.preceive.batch.sources.SourceIterator;

import java.io.Closeable;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public final class CloseablePipe<T> implements Closeable, Source<T>, Destination<T>, Iterable<T> {

    public static final int DEFAULT_BACKLOG = 100;
    private BlockingQueue<T> queue;
    private AtomicBoolean closed = new AtomicBoolean(false);
    private AtomicLong writes = new AtomicLong();
    private AtomicLong reads = new AtomicLong();
    private long timeout = 100;
    private String name;


    public CloseablePipe(String name) {
        this(name, new ArrayBlockingQueue<T>(DEFAULT_BACKLOG));
    }

    public CloseablePipe(String name, int backlog) {
        this(name, new ArrayBlockingQueue<T>(backlog));
    }

    public CloseablePipe(String name, BlockingQueue<T> queue) {
        this.queue = queue;
        this.name = name;
    }

    @Override
    public void close() {
        this.closed.set(true);
    }


    @Override
    public void write(T value) {
        while (!closed.get()) {
            try {
                this.queue.put(value);
                this.writes.incrementAndGet();
                return;
            } catch (InterruptedException ie) {/* ignored */ }
        }
    }

    @Override
    public T next() {
        T result;
        do {
            result = poll(timeout);
        } while (result == null && !isClosed());
        return result;
    }

    public T poll(long maxMilliseconds) {
        return pollI(maxMilliseconds);
    }

    public boolean isClosed() {
        return this.closed.get() && this.queue.isEmpty();
    }

    private T pollI(long maxMilliseconds) {
        if (isClosed()) return null;
        try {
            // wait a little
            T result = this.queue.poll(maxMilliseconds, TimeUnit.MILLISECONDS);
            if (result != null)
                this.reads.incrementAndGet();
            return result;
        } catch (InterruptedException ie) {
            return null;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new SourceIterator<>(this);
    }

    @Override
    public String toString() {
        return name;
    }

    public long getWrites() {
        return writes.get();
    }

    public long getReads() {
        return reads.get();
    }
}
