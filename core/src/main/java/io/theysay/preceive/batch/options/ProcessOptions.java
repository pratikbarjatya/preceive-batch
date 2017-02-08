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

package io.theysay.preceive.batch.options;

import io.theysay.preceive.batch.api.EndPointProcessor;
import io.theysay.preceive.batch.cmdline.Action;
import io.theysay.preceive.batch.cmdline.ActionLibrary;

public class ProcessOptions extends ActionLibrary.StaticFields {

    public ProcessOptions() {
        super(CORE);
    }

    public static Action.Property<Integer> THREADS = Action.integer(
        "threads", 1, "The number of concurrent threads/connections during batch processing.");

    public static Action.Property<Integer> BACKLOG = Action.integer(
        "backlog", 100, "The maximum number of items to be held in memory during batch processing.");

    public static EndPointsAction ENDPOINTS = new EndPointsAction(
        "endpoints",
        "The PreCeive REST API endpoints to call.\n" +
        "Each endpoint is of the form [field in result json]=[api path][?level=document|sentence]\n" +
        "e.g. -endpoints document.sentiment=/v1/sentiment?level=document sentence.sentiment=v1/sentiment?level=sentence");

    public static EndPointProcessor newProcessor() {
        return new EndPointProcessor(
                ClientOptions.getClient(),
                DataOptions.getDataFormat(),
                ENDPOINTS.get()
        );
    }
}
