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

package io.theysay.preceive.batch.http;

import io.theysay.preceive.batch.utils.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ClientSettings {
    private String username = null;
    private String password = null;
    private String service = null;
    private int readTimeout = 1000 * 60;
    private int connectTimeout = 1000 * 10;
    private boolean gzipped = true;
    private boolean keepAlive = true;
    private String charset = "UTF-8";
    private String contentType = "application/json";
    private String userAgent = null;
    private long rateLimitPauseMillis = 10000L;
    private int maximumAttempts = 10;

    public ClientSettings() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }


    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public boolean isGzipped() {
        return gzipped;
    }

    public void setGzipped(boolean gzipped) {
        this.gzipped = gzipped;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public String getCharset() {
        return charset;
    }

    public Charset charset() {
        return (charset == null) ? StandardCharsets.US_ASCII : Charset.forName(charset);
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }


    public List<String> headerNames() throws UnsupportedEncodingException {
        List<Header> headers = headers();
        ArrayList<String> names = new ArrayList<>(headers.size());
        for (Header header : headers) {
            names.add(header.name);
        }
        return names;
    }

    public List<Header> headers() throws UnsupportedEncodingException {
        ArrayList<Header> headers = new ArrayList<Header>();

        if (getUsername() != null) {
            String authToken = authentication();
            String value = "Basic " + authToken;
            headers.add(new Header("Authorization", value));
        }

        if (getCharset() != null) {
            headers.add(new Header("Accept-Charset", getCharset()));
        }

        if (isGzipped()) {
            headers.add(Header.GZIP_ENCODING);
        }

        if (getContentType() != null) {
            headers.add(new Header("Content-Type", getContentType()));
        }

        if (isKeepAlive()) {
            headers.add(Header.KEEP_ALIVE);
        }
        if (getUserAgent() != null) {
            headers.add(new Header("User-Agent", getUserAgent()));
        }

        return headers;
    }

    private String authentication() throws UnsupportedEncodingException {
        return Base64.encode(getUsername() + ":" + getPassword(), charset());
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * The time used to pause on receiving a 429 status and having hit rate limits.
     * Default value is 10 seconds
     *
     * @return
     */
    public long getRateLimitPauseMillis() {
        return rateLimitPauseMillis;
    }

    public void setRateLimitPauseMillis(long rateLimitPauseMillis) {
        this.rateLimitPauseMillis = rateLimitPauseMillis;
    }

    /**
     * @return Number of times to attempt an analysis in the face of on a 429 or 'connection' issue before giving up. Default value is 10.
     */
    public int getMaximumAttempts() {
        return maximumAttempts;
    }

    public void setMaximumAttempts(int maximumAttempts) {
        this.maximumAttempts = maximumAttempts;
    }


    public String fullPath(String path) {
        return service + path;
    }

    @Override
    public String toString() {
        return "ClientSettings{" +
                "username='" + username + '\'' +
                ", password='##############'" +
                ", service='" + service + '\'' +
                ", readTimeout=" + readTimeout +
                ", connectTimeout=" + connectTimeout +
                ", gzipped=" + gzipped +
                ", keepAlive=" + keepAlive +
                ", charset='" + charset + '\'' +
                ", contentType='" + contentType + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", rateLimitPauseMillis=" + rateLimitPauseMillis +
                ", maximumAttempts=" + maximumAttempts +
                '}';
    }


}
