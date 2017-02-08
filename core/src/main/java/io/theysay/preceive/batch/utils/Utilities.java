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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.util.concurrent.locks.LockSupport;

public final class Utilities {

    public static final String UTF_8 = "UTF-8";

    private Utilities() {
    }

    public static Datum parseEncodedMap(String query) {
        Datum query_pairs = new Datum();
        if (query == null || query.isEmpty()) return query_pairs;
        try {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (idx < 0) continue;
                query_pairs.put(URLDecoder.decode(pair.substring(0, idx), UTF_8), URLDecoder.decode(pair.substring(idx + 1), UTF_8));
            }
            return query_pairs;
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 is mandated by the JVM ");
        }
    }

    /**
     * Attempts to pause the currrent thread for the specified number of milliseconds
     * It will *not* hang forever values less than 1 will pause for 1us
     * Note that this uses LockSupport.parkNanos and thus may return prior to the completion of the number of milliseconds.
     *
     * @param milliseconds - maximum number of milliseconds to wait.
     */
    public static void pause(long milliseconds) {
        if (milliseconds < 1) {
            LockSupport.parkNanos(1000L); // 1 us
        } else {
            LockSupport.parkNanos(milliseconds * 1000000L);
        }
    }
}
