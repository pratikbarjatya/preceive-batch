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

import io.theysay.preceive.batch.utils.IOUtils;
import io.theysay.preceive.batch.utils.Line;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class LineSource implements Source<Line> {

//    private final Logger _LOGGER = LoggerFactory.getLogger(getClass());

    private final String source;
    private final BufferedReader reader;

    private long lineNumber;

    public LineSource(String source, InputStream input) {
        this(source, new InputStreamReader(input, StandardCharsets.UTF_8));
    }

    public LineSource(String source, Reader reader) {
        this(source, new BufferedReader(reader));
    }

    public LineSource(String source, BufferedReader reader) {
        this.reader = reader;
        this.source = source;
    }

    @Override
    public Line next() {
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                return new Line(++lineNumber, line, source);
            }
        } catch (Exception e) {
//            _LOGGER.error("Problem reading lines", e);
        }
        IOUtils.close(reader);
        return null;
    }
}
