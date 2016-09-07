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
import io.theysay.preceive.batch.utils.JsonUtils;
import io.theysay.preceive.batch.utils.Line;
import io.theysay.preceive.batch.utils.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

public class JsonDestinationProvider implements DestinationProvider {


    @Override
    public Destination create(Resource resource, Class targetClass) throws IOException {
        if (!Map.class.isAssignableFrom(targetClass)) return null;

        if (!resource.matchType(Resource.APPLICATION_JSON)) return null;

        Destination<Line> lineDestination = Destinations.create(resource, Line.class);
        return new Implementation(lineDestination);
    }

    public static class Implementation implements Destination<Map>, Closeable {
        private final Logger _LOGGER = LoggerFactory.getLogger(getClass());

        private final Destination<Line> underlying;

        public Implementation(Destination<Line> underlying) {
            this.underlying = underlying;
        }

        @Override
        public void write(Map output) {
            try {
                underlying.write(new Line(JsonUtils.toString(output)));
            } catch (IOException e) {
                _LOGGER.error("Error formating JSON ", e);
            }
        }

        @Override
        public void close() throws IOException {
            IOUtils.close(underlying);
        }
    }
}
