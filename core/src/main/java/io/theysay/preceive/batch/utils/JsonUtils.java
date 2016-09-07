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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

/**
 * Simple wrapper to hide implementation of Json serialization.
 * <p/>
 * Currently uses Jackson
 */
public class JsonUtils {

    private static final ObjectMapper jsonMapper;

    static {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule customMapping = new SimpleModule("PreCeive-Batch");
        SimpleAbstractTypeResolver resolver = new SimpleAbstractTypeResolver();
        resolver.addMapping(Map.class, Datum.class);
        customMapping.setAbstractTypes(resolver);
        mapper.registerModule(customMapping);
        jsonMapper = mapper;
    }

    public static Datum[] readArray(String input) throws IOException {
        return readArray(new StringReader(input));
    }

    public static Datum[] readArray(Reader input) throws IOException {
        return jsonMapper.readValue(input, Datum[].class);
    }

    public static Object read(Reader input) throws IOException {
        return jsonMapper.readValue(input, Object.class);
    }

    public static Object read(String input) throws IOException {
        return read(new StringReader(input));
    }

    public static Datum readItem(Reader input) throws IOException {
        return jsonMapper.readValue(input, Datum.class);
    }

    public static Datum readItem(String input) throws IOException {
        return readItem(new StringReader(input));
    }

    public static String toString(Object obj) throws IOException {
        return jsonMapper.writeValueAsString(obj);
    }

    public static byte[] toBytes(Object obj) throws IOException {
        return jsonMapper.writeValueAsBytes(obj);
    }

}
