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

import java.io.Closeable;
import java.io.IOException;

public class TimePoller {
    protected long last;
    private long every;

    public TimePoller() {
        this(5000L);
    }

    public TimePoller(long every) {
        this.every = every;
    }

    public long getLast() {
        return last;
    }

    public long getEvery() {
        return every;
    }

    public void setEvery(long every) {
        this.every = every;
    }

    public boolean poll() {
        long now = now();
        if ((now - getLast()) <= getEvery()) return false;
        last = now;
        return true;
    }

    /**
     * Poll and if true call runnable.run()
     *
     * @param runnable
     * @return
     */
    public boolean poll(Runnable runnable) {
        if (!poll()) return false;

        runnable.run();
        return true;
    }


    protected long now() {
        return System.currentTimeMillis();
    }

    public Runnable wrap(final Runnable nested) {
        return new Wrapper(nested);
    }


    public static class Increment extends TimePoller {
        long time;

        public Increment() {
            this(0);
        }

        public Increment(long time) {
            this.time = time;
        }

        @Override
        protected long now() {
            return ++time;
        }
    }

    private class Wrapper implements Runnable, Closeable {
        private final Runnable target;

        public Wrapper(Runnable target) {
            this.target = target;
        }

        @Override
        public void close() throws IOException {
            target.run();
            IOUtils.close(target);
        }

        @Override
        public void run() {
            poll(target);
        }
    }
}
