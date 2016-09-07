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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class IOUtils {
    private IOUtils() {
    }

    private static Set<String> supportsInputStream = new LinkedHashSet<>(Arrays.asList("http", "https", "file"));

    public static void close(Object o) {
        if (o instanceof AutoCloseable) {
            try {
                ((AutoCloseable) o).close();
            } catch (Exception e) {
                // ignored
            }
        }
    }


    public static InputStream inputStream(Resource resource) throws IOException {

        InputStream in;
        String scheme = resource.getScheme().toLowerCase();
        if (scheme.equals("classpath")) {
            // Classpath interceptor
            in = Classes.getResource(resource.getPath());

        } else if (supportsInputStream.contains(scheme)) {

            // Default behaviour
            in = resource.toURL().openStream();
        } else {
            in = null;
        }
        if (in != null && resource.isGzipped()) {
            in = new GZIPInputStream(in);
        }
        return in;
    }


    public static OutputStream outputStream(Resource resource) throws IOException {
        OutputStream output;
        if (!resource.getScheme().equals("file")) {
            return null;
        }

        File file = new File(resource.getURIWithoutFragment());
        // Ensure Directories are created.
        file.getParentFile().mkdirs(); // If False may exist already
        output = new FileOutputStream(file);
        if (resource.isGzipped()) {
            output = new GZIPOutputStream(output);
        }
        return output;
    }

    public static OutputStream outputStream(String uri) throws IOException {
        return outputStream(Resource.valueOf(uri));
    }


    public static BufferedWriter writer(String uri) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(outputStream(uri), StandardCharsets.UTF_8));
    }


    public static String getTextFromResource(String resourceName) throws IOException {
        return getText("classpath:/" + resourceName);
    }

    public static String getText(InputStream inputStream) throws IOException {
        return getText(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }


    public static String getText(Reader reader) throws IOException {
        try {
            StringBuilder b = new StringBuilder();
            char[] buf = new char[8192];
            int amt;
            do {
                amt = reader.read(buf);
                if (amt > 0) b.append(buf, 0, amt);
            } while (amt >= 0);
            return b.toString();
        } finally {
            IOUtils.close(reader);
        }
    }

    public static String getText(String uri) throws IOException {
        return getText(inputStream(Resource.valueOf(uri)));
    }


    public static void write(String uri, String contents) throws IOException {
        Writer writer = null;
        try {
            writer = writer(uri);
            writer.write(contents);
        } finally {
            IOUtils.close(writer);
        }
    }

    public static String[][] readTabSeparated(String uri) throws IOException {
        String[] lines = getText(uri).split("\n");
        ArrayList<String[]> result = new ArrayList<>();
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] cells = line.split("\t");
            result.add(cells);
        }
        return result.toArray(new String[result.size()][]);
    }

    public static void clean(File path) {
        if (path.isDirectory()) {
            File[] files = path.listFiles();
            for (File file : files) {
                clean(file);
            }
        }
        if (path.exists())
            path.delete();
    }

    public static void clean(String path) {
        clean(new File(path));
    }
}
