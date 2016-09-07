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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Basic Enum containing Status Codes.
 * Not necessarily exhaustive but sufficient for this client's purposes.
 */
public enum StatusCode {
    Continue(100, "Continue"),
    Switching_Protocols(101, "Switching Protocols"),
    Processing(102, "Processing"),
    OK(200, "OK"),
    Created(201, "Created"),
    Accepted(202, "Accepted"),
    NonAuthoritative_Information(203, "Non-Authoritative Information"),
    NoContent(204, "No Content"),
    ResetContent(205, "Reset Content"),
    PartialContent(206, "Partial Content"),
    MultiStatus(207, "Multi-Status"),
    AlreadyReported(208, "Already Reported"),
    IMUsed(226, "IM Used"),
    MultipleChoices(300, "Multiple Choices"),
    MovedPermanently(301, "Moved Permanently"),
    Found(302, "Found"),
    SeeOther(303, "See Other"),
    NotModified(304, "Not Modified"),
    UseProxy(305, "Use Proxy"),
    TemporaryRedirect(307, "Temporary Redirect"),
    PermanentRedirect(308, "Permanent Redirect"),
    BadRequest(400, "Bad Request"),
    Unauthorized(401, "Unauthorized"),
    PaymentRequired(402, "Payment Required"),
    Forbidden(403, "Forbidden"),
    NotFound(404, "Not Found"),
    MethodNotAllowed(405, "Method Not Allowed"),
    Not_Acceptable(406, "Not Acceptable"),
    Proxy_Authentication_Required(407, "Proxy Authentication Required"),
    RequestTimeout(408, "Request Timeout"),
    Conflict(409, "Conflict"),
    Gone(410, "Gone"),
    Length_Required(411, "Length Required"),
    Precondition_Failed(412, "Precondition Failed"),
    RequestEntityTooLarge(413, "Request Entity Too large"),
    URI_Too_Long(414, "URI Too Long"),
    Unsupported_Media_Type(415, "Unsupported Media Type"),
    Range_Not_Satisfiable(416, "Range Not Satisfiable"),
    Expectation_Failed(417, "Expectation Failed"),
    Misdirected_Request(421, "Misdirected Request"),
    Unprocessable_Entity(422, "Unprocessable Entity"),
    Locked(423, "Locked"),
    FailedDependency(424, "Failed Dependency"),
    UpgradeRequired(426, "Upgrade Required"),
    PreconditionRequired(428, "Precondition Required"),
    TooManyRequests(429, "Too Many Requests"),
    RequestHeaderFieldsTooLarge(431, "Request Header Fields Too Large"),
    Unavailable_For_Legal_Reasons(451, "Unavailable For Legal Reasons"),
    InternalServerError(500, "Internal Server Error"),
    NotImplemented(501, "Not Implemented"),
    BadGateway(502, "Bad Gateway"),
    ServiceUnavailable(503, "Service Unavailable"),
    GatewayTimeout(504, "Gateway Timeout"),
    HTTPVersionNotSupported(505, "HTTP Version Not Supported"),
    VariantAlsoNegotiates(506, "Variant Also Negotiates"),
    InsufficientStorage(507, "Insufficient Storage"),
    LoopDetected(508, "Loop Detected"),
    Unassigned(509, "Unassigned"),
    NotExtended(510, "Not Extended"),
    NetworkAuthenticationRequired(511, "Network Authentication Required"),
    UNKNOWN(600, "No information about this status code");


    public final String message;
    public final int code;

    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }


    private static final Map<Integer, StatusCode> _CODE_INDEX;

    static {
        Map<Integer, StatusCode> mapping = new LinkedHashMap<>();
        StatusCode[] values = values();
        for (int i = 0; i < values.length; i++) {
            StatusCode value = values[i];
            mapping.put(value.code, value);
        }
        _CODE_INDEX = mapping;
    }

    public static StatusCode valueOf(int code) {
        StatusCode statusCode = _CODE_INDEX.get(code);
        if (statusCode == null) {
            return UNKNOWN;
        }
        return statusCode;
    }

    public boolean isOk() {
        return this.code >= 200 && this.code <= 299;
    }

    public boolean isClientError() {
        return this.code >= 400 && this.code <= 499;
    }

    public boolean isServerError() {
        return this.code >= 500 && this.code <= 599;
    }

}
