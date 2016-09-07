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

package io.theysay.preceive.batch.http;

import io.theysay.preceive.batch.api.RequestMonitor;
import io.theysay.preceive.batch.api.EndPoint;
import io.theysay.preceive.batch.api.EndPointProcessor;
import io.theysay.preceive.batch.api.PreCeiveClient;
import io.theysay.preceive.batch.options.ClientOptions;
import io.theysay.preceive.batch.options.DataOptions;
import io.theysay.preceive.batch.pipeline.PipeProcess;
import io.theysay.preceive.batch.pipeline.Pipelines;
import io.theysay.preceive.batch.sources.Source;
import io.theysay.preceive.batch.sources.Sources;
import io.theysay.preceive.batch.utils.Datum;
import org.junit.Assert;
import org.junit.Test;

public class TestPreceiveClient {

    @Test
    public void testPreceiveException() throws Exception {
        ClientSettings clientSettings = getClientSettings();


        PreCeiveClient preceiveClient = new PreCeiveClient(clientSettings);

        EndPoint[] endPoints = new EndPoint[]{EndPoint.valueOf("sentiment=/v1/sentiment")};

        PipeProcess<Datum, Datum> endpointProcessor = new EndPointProcessor(
                preceiveClient,
                DataOptions.getDataFormat(),
                endPoints);
        Source<Datum> sources = Sources.create("classpath://test-input/100-texts.json", Datum.class);
        @SuppressWarnings("unchecked")
        long records = Pipelines.run(10, 2, sources, endpointProcessor);
        Assert.assertEquals(100, records);
        RequestMonitor.Status status = preceiveClient.getMonitor().status();
        // With 2 attempts when network and ratelimit issues.

        long expectedTotal = 100 + (status.otherErrors + status.rateLimits) / 2; // With 2 attempts
        Assert.assertEquals(4, status.clientErrors);
        Assert.assertEquals(1, status.serverErrors);
        Assert.assertEquals(2, status.otherErrors);
        Assert.assertEquals(expectedTotal, status.totalRequests);

        double seconds = status.totalElapsedTime / 1000.0;
        double minutes = seconds / 60;

        double requestsPerMinute = status.totalRequests / minutes;
        double meanResponseTime = ((double) status.sumResponseTime) / status.totalRequests;

        Assert.assertEquals(requestsPerMinute, status.requestsPerMinute(), 0.001);
        Assert.assertEquals(meanResponseTime, status.meanResponseTime(), 0.001);
        Assert.assertEquals(seconds, status.elapsedSeconds(), 0.001);

        Assert.assertEquals(
                String.format("[%8.3f] Req : %d (%.3f/minute) mean time: %.3f (ms)  errors (%d,%d,%d) ",
                        seconds,
                        status.totalRequests,
                        requestsPerMinute,
                        meanResponseTime,
                        status.clientErrors,
                        status.serverErrors,
                        status.otherErrors
                ),
                status.toString());
    }

    @Test(expected = ApiException.AuthenicationRejected.class)
    public void testAuthenticationTest() throws Exception {
        ClientSettings clientSettings = getClientSettings();
        PreCeiveClient preceiveClient;

        preceiveClient = new PreCeiveClient(clientSettings);
        preceiveClient.validateConnection();
        // All Ok

        clientSettings.setUsername("Fred");
        clientSettings.setUsername("Flinestone");
        preceiveClient = new PreCeiveClient(clientSettings);
        preceiveClient.validateConnection();

    }

    public ClientSettings getClientSettings() {
        ClientSettings clientSettings = ClientOptions.getClientSettings();


        clientSettings.setRateLimitPauseMillis(1);
        clientSettings.setMaximumAttempts(2);
        clientSettings.setService("test://api.theysay.io"); // Use the test service to simulate responses.
        clientSettings.setUsername("test"); // Use the test service to simulate responses.
        clientSettings.setPassword("test"); // Use the test service to simulate responses.
        return clientSettings;
    }
}
