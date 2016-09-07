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

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Pattern;

public class Resource {

    public static final Pattern SCHEME_PATTERN = Pattern.compile("^[a-z][a-z\\.\\+\\-]+:", Pattern.CASE_INSENSITIVE);
    public static final String APPLICATION_JSON = "application/json";
    public static final String TAB_SEPARATED_VALUES = "text/tab-separated-values";
    public static final String XLSX_FILE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";


    private final static MimetypesFileTypeMap MIME_TYPES = new MimetypesFileTypeMap();
    public static final String GZIP_MIME_TYPE = "application/x-gzip";
    public static final String GZ_EXTENSION = ".gz";

    private URI uri;
    private Datum options;

    public Resource(URI uri) {
        this.uri = uri;
        this.options = Utilities.parseEncodedMap(uri.getRawFragment());
    }

    public Datum getOptions() {
        return options;
    }

    public Datum getQueryParams() {
        return Utilities.parseEncodedMap(getUri().getRawQuery());
    }

    public URI getUri() {
        return uri;
    }

    public String getQuery() {
        return getUri().getQuery();
    }

    public String getPath() {
        return getUri().getPath();
    }

    public String getHost() {
        return getUri().getHost();
    }

    public String getUserInfo() {
        return getUri().getUserInfo();
    }

    public int getPort() {
        return getUri().getPort();
    }

    public String getScheme() {
        return getUri().getScheme();
    }

    public URL toURL() throws MalformedURLException {
        return getUri().toURL();
    }

    public InputStream getInputStream() throws IOException {
        return toURL().openStream();
    }

    public String getFragment() {
        return getUri().getFragment();
    }

    public String getContentType() {
        String specified = options.asString("Content-Type");
        if (specified != null)
            return specified;

        String path = getPath();

        String rawType = getContentTypeFor(path);
        if (rawType.equals(GZIP_MIME_TYPE) && path.toLowerCase().endsWith(GZ_EXTENSION)) {
            return getContentTypeFor(path.substring(0, path.length() - 3));
        } else {
            return rawType;
        }
    }

    public String getName() {
        String path = getPath();
        int index = path.lastIndexOf('/');
        return (index >= 0) ? path.substring(index + 1) : path;
    }

    private int extensionDot(String name) {
        int dot = name.lastIndexOf('.');
        if (name.toLowerCase().endsWith(GZ_EXTENSION)) {
            int priordot = name.lastIndexOf('.', name.length() - GZ_EXTENSION.length() - 1);
            dot = priordot;
        }
        return dot;
    }

    public String getExtension() {
        String name = getName();
        int dot = extensionDot(name);
        return (dot > 0) ? name.substring(dot + 1) : "";
    }

    public URI getURIWithoutFragment() {
        return this.uri.resolve(this.getName());
    }

    public boolean matchType(String contentType) {
        return getContentType().equals(contentType);
    }

    public boolean isGzipped() {
        return getContentTypeFor(getPath()).equals(GZIP_MIME_TYPE);
    }

    @Override
    public String toString() {
        return uri.toString();
    }


    public Resource insertBeforeExtension(String insert) {

        String name = getName();
        int dot = extensionDot(name);
        String newName;
        if (dot < 0) {
            newName = name + insert;
        } else if (dot > 0) {
            newName = name.substring(0, dot) + "." + insert + name.substring(dot);
        } else {
            newName = insert + name.substring(dot);
        }
        String fragment = uri.getRawFragment();
        String newNameFragment = (fragment != null) ? newName + "#" + fragment : newName;
        return new Resource(uri.resolve(newNameFragment));
    }


    public static Resource valueOf(String resource) {
        URI uri = uri(resource);
        return new Resource(uri);
    }


    /**
     * Delegated call to URI parsing to ensure certain constraints.
     */
    public static URI uri(String specifier) {
        try {
            URI uri;
            if (SCHEME_PATTERN.matcher(specifier).find()) {
                uri = new URI(specifier);
            } else {
                uri = implicitFileScheme(specifier);
            }
            // Now Sanity check
            switch (uri.getScheme()) {
                case "classpath":
                    return new URI("classpath",
                                      null,
                                      removeDoubleSlash(uri.getSchemeSpecificPart()),
                                      uri.getRawFragment());
                default:
                    return uri;
            }


        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e); // If we are given an dodgy specifier
        }
    }

    private static URI implicitFileScheme(String specifier) throws URISyntaxException {
        int hash = specifier.indexOf('#');
        if (hash < 0) {
            return new File(specifier).toURI();
        } else {
            URI base = new File(specifier.substring(0, hash)).toURI();
            return base.resolve(specifier.substring(hash));
        }
    }


    private static String removeDoubleSlash(String input) {
        while (input.startsWith("//")) input = input.substring(1);
        return input;
    }


    public static String getContentTypeFor(String filePath) {
        return MIME_TYPES.getContentType(filePath);
    }

}
