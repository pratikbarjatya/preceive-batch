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

import java.util.Arrays;

public class DataPath {

    public static DataPath NONE = new DataPath();
    private final String[] path;

    public DataPath(String... path) {
        this.path = path;
    }

    public boolean isEmpty() {
        return path.length == 0;
    }

    public static DataPath valueOf(String s) {
        if (s == null) return DataPath.NONE;
        s = s.trim();
        if (s.isEmpty()) return DataPath.NONE;
        String path[] = s.split("\\s*\\.\\s*");
        return new DataPath(path);
    }

    public String[] getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataPath)) return false;
        DataPath dataPath = (DataPath) o;
        return Arrays.equals(path, dataPath.path);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(path);
    }
}
