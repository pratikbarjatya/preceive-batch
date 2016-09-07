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

package io.theysay.preceive.batch.api;

import io.theysay.preceive.batch.http.ClientListener;
import io.theysay.preceive.batch.http.Response;

import java.util.concurrent.atomic.AtomicLong;

public class RequestMonitor implements ClientListener {

    private AtomicLong clientErrors = new AtomicLong();
    private AtomicLong serverErrors = new AtomicLong();
    private AtomicLong rateLimits = new AtomicLong();
    private AtomicLong others = new AtomicLong();
    private AtomicLong responseTime = new AtomicLong();
    private AtomicLong numberOfRequests = new AtomicLong();
    private final long start;

    public RequestMonitor() {
        this(System.currentTimeMillis());
    }

    public RequestMonitor(long start) {
        this.start = start;
    }

    @Override
    public void record(Response response) {
        responseTime.addAndGet(response.getElapsed());
        numberOfRequests.incrementAndGet();
        if (response.isOK())
            return;
        if (response.isRateLimited()) {
            rateLimits.incrementAndGet();
        } else if (response.isClientError()) {
            clientErrors.incrementAndGet();
        } else if (response.isServerError()) {
            serverErrors.incrementAndGet();
        } else {
            others.incrementAndGet();
        }
    }

    @Override
    public void record(Throwable throwable) {
        numberOfRequests.incrementAndGet();
        others.incrementAndGet();
    }

    public long now() {
        return System.currentTimeMillis();
    }

    public Status status() {
        return new Status(numberOfRequests.get(),
                             now() - start,
                             responseTime.get(),
                             clientErrors.get(),
                             serverErrors.get(),
                             others.get(),
                             rateLimits.get())
            ;
    }

    public String toString() {
        return status().toString();
    }

    public void reset() {
        clientErrors.set(0);
        serverErrors.set(0);
        rateLimits.set(0);
        others.set(0);
        responseTime.set(0);
        numberOfRequests.set(0);
    }

    public static class Status {
        public final long totalRequests;
        public final long totalElapsedTime;
        public final long sumResponseTime;
        public final long clientErrors;
        public final long serverErrors;
        public final long otherErrors;
        public final long rateLimits;

        public Status(long totalRequests, long totalElapsedTime, long sumResponseTime, long clientErrors, long serverErrors, long otherErrors, long rateLimits) {
            this.totalRequests = totalRequests;
            this.totalElapsedTime = totalElapsedTime;
            this.sumResponseTime = sumResponseTime;
            this.clientErrors = clientErrors;
            this.serverErrors = serverErrors;
            this.otherErrors = otherErrors;
            this.rateLimits = rateLimits;
        }

        public double elapsedSeconds() {
            return totalElapsedTime / (1000.0);
        }

        public double requestsPerMinute() {
            return 60000.0 * totalRequests / totalElapsedTime;
        }

        public double meanResponseTime() {
            return 1.0 * sumResponseTime / totalRequests;
        }

        public long totalErrors() {
            return clientErrors + serverErrors + otherErrors;
        }

        public String toString() {
            return String.format("[%8.3f] Req : %d (%.3f/minute) mean time: %.3f (ms)  errors (%d,%d,%d) ", elapsedSeconds(), totalRequests, requestsPerMinute(), meanResponseTime(), clientErrors, serverErrors, otherErrors);
        }
    }
}
