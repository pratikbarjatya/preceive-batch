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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class Datum extends LinkedHashMap<String, Object> {
    private static final Map<String, Object> CONSTANTS;

    static {
        CONSTANTS = new LinkedHashMap<>();
        CONSTANTS.put("true", Boolean.TRUE);
        CONSTANTS.put("false", Boolean.FALSE);
        CONSTANTS.put("yes", Boolean.TRUE);
        CONSTANTS.put("no", Boolean.FALSE);
        CONSTANTS.put("on", Boolean.TRUE);
        CONSTANTS.put("off", Boolean.FALSE);
        CONSTANTS.put("0", 0);
        CONSTANTS.put("1", 1);

    }

    public static final String AUTO_ID = "_autoid";
    public static final String ROW_ID = "_rowid";
    public static final String ID = "id";
    public static final String TEXT = "text";

    public Datum(Map<? extends String, ?> m) {
        super(m);
    }

    public Datum() {
    }

    public Datum(String field, Object value) {
        this();
        with(field, value);
    }


    public Datum with(String field, Object value) {
        put(field, value);
        return this;
    }

    public <T> T apply(String... path) {
        Datum current = this;
        int indexOfLastMap = path.length - 1;
        String last = path[indexOfLastMap];
        int index = 0;
        while (index < indexOfLastMap) {
            String element = path[index++];
            Object value = current.get(element);

            if (!(value instanceof Datum)) return null;
            current = (Datum) value;
        }
        return (T) current.get(last);
    }

    public String asString(String... path) {
        Object value = apply(path);
        return (value != null) ? value.toString() : null;
    }

    public String asString(DataPath path) {
        return asString(path.getPath());
    }

    public Number asNumber(String... path) {
        Object v = apply(path);
        if (v instanceof Number) {
            return ((Number) v);
        } else if (v instanceof String) {
            try {
                return Double.parseDouble(v.toString().trim());
            } catch (NumberFormatException nfe) {
                // It is not a number so fall through
            }
        }
        return null;// No number at the provided location
    }

    public Datum[] asDataArray(String... path) {
        Object value = apply(path);
        if (value instanceof Datum[]) {
            return (Datum[]) value;
        } else if (value instanceof Collection) {
            Collection c = (Collection) value;
            return (Datum[]) c.toArray(new Datum[c.size()]);
        } else {
            return null;
        }
    }

    public Datum asDatum(String... path) {
        Object value = apply(path);
        if (value instanceof Datum) {
            return (Datum) value;
        } else {
            return null;
        }
    }

    public String[] asStringArray(String... path) {
        Object value = apply(path);
        if (value instanceof String[]) {
            return (String[]) value;
        } else if (value instanceof Collection) {
            Collection c = (Collection) value;
            String[] result = new String[c.size()];
            int index = 0;
            for (Object o : c) {
                result[index++] = o.toString();
            }
            return result;
        } else {
            return null;
        }
    }


    public Datum copy() {
        return new Datum(this);
    }

    public void set(String[] path, Object content) {
        Datum current = this;
        for (int i = 0 ; i < path.length - 1 ; i++) {
            Datum next = (Datum) current.get(path[i]);
            if (next == null) {
                next = new Datum();
                current.put(path[i], next);
            }
            current = next;
        }
        current.put(path[path.length - 1], content);
    }

    public void set(DataPath path, Object content) {
        set(path.getPath(), content);
    }

    public Datum parseKeyValues() {
        Datum result = new Datum();
        for (Map.Entry<String, Object> entry : this.entrySet()) {
            DataPath path = DataPath.valueOf(entry.getKey());
            Object value = entry.getValue();
            if (value instanceof String) {
                value = tryConvert((String) value);
            }
            result.set(path, value);
        }
        return result;
    }

    private Object tryConvert(String value) {
        if (value == null) return null;
        value = value.trim();
        if (value.isEmpty()) return value;

        Object constant = CONSTANTS.get(value.toLowerCase());
        if (constant != null)
            return constant;
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException nfe) {
            return value;
        }
    }
}
