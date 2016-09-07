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
import io.theysay.preceive.batch.utils.IOUtils;

public class Forwarder<In> implements PipeRunnable {
    private final Source<In> inbound;
    private final Destination<In> outbound;

    public Forwarder(Source<In> inbound, Destination<In> outbound) {
        this.inbound = inbound;
        this.outbound = outbound;
    }

    @Override
    public void run() {
        copyAndClose(this.inbound, this.outbound);
    }

    /**
     * Copies but does not close the destination
     *
     * @return Number of items transferred
     */
    public static <In> long copy(Source<In> inbound, Destination<In> outbound) {
        In argument;
        long count = 0;
        while ((argument = inbound.next()) != null) {
            outbound.write(argument);
            count++;
        }
        return count;
    }

    /**
     * Copies and then closes the destination
     *
     * @return Number of items transferred
     */
    public static <In> long copyAndClose(Source<In> inbound, Destination<In> outbound) {
        try {
            return copy(inbound, outbound);
        } finally {
            IOUtils.close(outbound);
        }
    }

}
