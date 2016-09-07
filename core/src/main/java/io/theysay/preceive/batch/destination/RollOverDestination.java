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
import io.theysay.preceive.batch.utils.Resource;

import java.io.Closeable;
import java.io.IOException;

public class RollOverDestination<T> implements Destination<T>, Closeable {
    private final Resource pattern;
    private final Class<T> targetClass;
    private final long maxElements;
    private Destination<T> current;
    private int count;

    public RollOverDestination(Resource pattern, Class<T> targetClass, int batchSize) {
        this.pattern = pattern;
        this.targetClass = targetClass;
        this.maxElements = batchSize;
        this.count = 0;
        current = null;
    }

    @Override
    public void write(T output) {
        // The count that the specified value will be.
        long currentBatchCount = count % maxElements;
        if (currentBatchCount == 0) {
            IOUtils.close(current);
            current = create();
        }
        count++;
        current.write(output);
    }

    @Override
    public void close() throws IOException {
        IOUtils.close(current);
    }

    private Destination<T> create() {
        String insert = String.format("%08d", count);
        Resource resource = pattern.insertBeforeExtension(insert);
        try {
            return Destinations.create(resource, targetClass);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
