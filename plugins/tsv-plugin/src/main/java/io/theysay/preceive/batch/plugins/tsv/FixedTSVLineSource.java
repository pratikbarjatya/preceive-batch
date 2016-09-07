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

package io.theysay.preceive.batch.plugins.tsv;

import io.theysay.preceive.batch.sources.Source;
import io.theysay.preceive.batch.utils.Datum;
import io.theysay.preceive.batch.utils.Line;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fixed format first cell is the 'id' the second is the 'text'
 * No header row.
 */
public class FixedTSVLineSource implements Source<Datum> {
    private final Logger _LOGGER = LoggerFactory.getLogger(getClass());

    private Source<Line> lines;

    public FixedTSVLineSource(Source<Line> lines) {
        this.lines = lines;

    }


    @Override
    public Datum next() {

        String newline = TSVOptions.NEWLINE_ESCAPE.get();
        Line line;
        while ((line = lines.next()) != null) {
            if (line.isEmpty()) continue;
            String[] split = line.line.split("\t");
            if (split.length != 2) {
                _LOGGER.warn("Only 1 cell at " + line.location());
                continue;
            }
            String id = split[0].trim();
            String text = split[1].trim();
            Datum data = new Datum()
                             .with(Datum.ID, id)
                             .with(Datum.TEXT, text.replace(newline, "\n"))
                             .with(Datum.AUTO_ID, line.location());
            return data;
        }
        return null;
    }
}
