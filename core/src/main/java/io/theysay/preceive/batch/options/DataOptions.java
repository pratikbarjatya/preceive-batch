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

import io.theysay.preceive.batch.api.DataFormat;
import io.theysay.preceive.batch.cmdline.Action;
import io.theysay.preceive.batch.cmdline.ActionLibrary;
import io.theysay.preceive.batch.utils.DataPath;

public class DataOptions extends ActionLibrary.StaticFields {

    public static Action.Property<DataPath> SOURCE_DATA_PATH = new Action.Property<>("copy-input-data-as", DataPath.NONE, "The field under which to store the original submitted data in the result. ");
    public static Action.Property<DataPath> TEXT_FIELD = new Action.Property<>("input-text", new DataPath("text"), "The field in the input data that contains the core text content to be processed.");
    public static Action.Property<DataPath> ID_FIELD = new Action.Property<>("input-id", new DataPath("id"), "The field in the input data that represents the id of each text. By default, an auto-generated ID will be used.");

    public DataOptions() {
        super(CORE);
    }

    public static DataFormat getDataFormat() {
        return new DataFormat(ID_FIELD.get(), TEXT_FIELD.get(), SOURCE_DATA_PATH.get());
    }
}
