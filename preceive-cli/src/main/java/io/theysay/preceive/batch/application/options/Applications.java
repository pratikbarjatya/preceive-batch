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

package io.theysay.preceive.batch.application.options;

import io.theysay.preceive.batch.api.ApiResource;
import io.theysay.preceive.batch.cmdline.Action;
import io.theysay.preceive.batch.cmdline.ActionLibrary;
import io.theysay.preceive.batch.cmdline.Macro;

public class Applications extends ActionLibrary.StaticFields {


    public Applications() {
        super(APPLICATION);
    }

    public static Action BATCH = new RunBatch();
    public static Action PROCESS_DOCUMENTS = new ProcessDocuments();
    public static Macro[] MACROS = Macro.loadMacros("classpath://macros/macros.json");

    public static Action LIST_TOPICS =
        new ResourceAction.List(ApiResource.TOPICS,
                                   "list-topic-keywords",
                                   "List Topic Keyword Entries");
    public static Action SET_TOPICS =
        new ResourceAction.Set(ApiResource.TOPICS,
                                   "set-topic-keywords",
                                   "-set-topic-keywords <xlsx file> Set Topic Keyword Entries to values in xlsx file");
    public static Action DELETE_TOPICS =
        new ResourceAction.Clear(ApiResource.TOPICS,
                                   "delete-topic-keywords",
                                   "Delete all current topic keywords.");

    public static Action LIST_NAMED_ENTITY_ASSERTIONS =
        new ResourceAction.List(ApiResource.NAMED_ENTITY_ASSERTIONS,
                                   "list-ner-assertions",
                                   "List Named Entity Assertions");

    public static Action SET_NAMED_ENTITY_ASSERTIONS =
        new ResourceAction.Set(ApiResource.NAMED_ENTITY_ASSERTIONS,
                                   "set-ner-assertions",
                                   "-set-ner-assertions <xlsx file> Set Named Entity Assertions to the values in xlsx file");

    public static Action DELETE_NAMED_ENTITY_ASSERTIONS =
        new ResourceAction.Clear(ApiResource.NAMED_ENTITY_ASSERTIONS,
                                   "delete-ner-assertions",
                                   "Delete all Named Entity Assertions.");
}
