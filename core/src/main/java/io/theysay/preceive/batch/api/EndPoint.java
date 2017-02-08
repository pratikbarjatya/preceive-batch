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

package io.theysay.preceive.batch.api;

import io.theysay.preceive.batch.utils.DataPath;
import io.theysay.preceive.batch.utils.Datum;
import io.theysay.preceive.batch.utils.Utilities;

import java.util.Objects;

public final class EndPoint {
    public static final DataPath RESPONSE_FIELDNAME = new DataPath("response");
    private final DataPath field;
    private String path;
    private Datum parameters;

    public EndPoint(String field[], String path, Datum parameters) {
        this(new DataPath(field), path, parameters);
    }

    public EndPoint(DataPath field, String path, Datum parameters) {
        this.field = field;
        this.path = path;
        this.parameters = parameters;
    }

    public String getPath() {
        return path;
    }

    public DataPath getField() {
        return field;
    }

    public Datum getParameters() {
        return parameters;
    }

    /**
     * Parse this form of String
     * {{
     * FIELD                PATH       PARAMS
     * [field.field.field=][/v1/path][?key=value&key=value]
     * }}
     * Therefore split by '?' then split the left by '='
     *
     * @param spec
     * @return
     */
    public static EndPoint valueOf(String spec) {

        String querystring = "";
        DataPath field = RESPONSE_FIELDNAME;

        int query = spec.indexOf('?');
        // Separate out parameters
        if (query > 0) {
            querystring = spec.substring(query + 1);
            spec = spec.substring(0, query);
        }
        // Separate out field name
        int equals = spec.indexOf('=');
        if (equals > 0) {
            field = DataPath.valueOf(spec.substring(0, equals));
            spec = spec.substring(equals + 1);
        }
        String path = spec;

        Datum simple = Utilities.parseEncodedMap(querystring);
        Datum nested = simple.parseKeyValues();

        return new EndPoint(field, path, nested);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EndPoint)) return false;
        EndPoint endPoint = (EndPoint) o;
        return Objects.equals(field, endPoint.field) &&
                Objects.equals(path, endPoint.path) &&
                Objects.equals(parameters, endPoint.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, path, parameters);
    }

    public Datum copyParameters() {
        return parameters.copy();
    }
}
