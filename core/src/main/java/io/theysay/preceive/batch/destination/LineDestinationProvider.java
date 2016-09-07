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
import io.theysay.preceive.batch.utils.Line;
import io.theysay.preceive.batch.utils.Resource;

import java.io.IOException;
import java.io.OutputStream;

@SuppressWarnings("unchecked")
public class LineDestinationProvider implements DestinationProvider {

    @Override
    public Destination create(Resource resource, Class target) throws IOException {
        if (!target.isAssignableFrom(Line.class)) return null;
        // Now we can assume T==Line
        OutputStream outputStream = IOUtils.outputStream(resource);
        if (outputStream == null) return null;
        return new LineDestination(outputStream);
    }
}
