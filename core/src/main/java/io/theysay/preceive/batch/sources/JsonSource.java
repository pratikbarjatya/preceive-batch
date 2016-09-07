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

import io.theysay.preceive.batch.utils.Datum;
import io.theysay.preceive.batch.utils.JsonUtils;
import io.theysay.preceive.batch.utils.Line;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonSource implements Source<Datum> {
    private final Logger _LOGGER = LoggerFactory.getLogger(getClass());

    private Source<Line> lines;

    public JsonSource(Source<Line> lines) {
        this.lines = lines;
    }


    @Override
    public Datum next() {
        Line line;
        while ((line = lines.next()) != null) {
            if (line.isEmpty()) continue;
            try {

                Datum json = JsonUtils.readItem(line.line);
                return json.with(Datum.AUTO_ID, line.location());
            } catch (Exception e) {
                _LOGGER.error("JSON Parse Problem at " + line.location(), e);
            }
        }
        return null;
    }
}
