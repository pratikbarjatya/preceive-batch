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

public class DataFormat {
    private DataPath idField;
    private DataPath textField;
    private DataPath sourceDataPath;

    public DataFormat(DataPath idField, DataPath textField, DataPath sourceDataPath) {
        this.idField = idField;
        this.textField = textField;
        this.sourceDataPath = sourceDataPath;
    }

    public String getText(Datum inputData) {
        return inputData.asString(textField);
    }


    public Datum createInitialResult(Datum inputData) {
        Datum result = new Datum();
        String id = inputData.asString(idField);
        if (id == null) {
            id = inputData.asString(Datum.AUTO_ID);
        }
        result.put("id", id);
        if (!sourceDataPath.isEmpty()) {
            result.set(sourceDataPath, inputData);
        }
        return result;
    }

    public Datum addResponseToResult(Datum currentResult, Object response, DataPath dataPath) {
        currentResult.set(dataPath, response);
        return currentResult;
    }


    public Datum addError(Datum result, Exception e) {
        result.put("error", e.getMessage());
        return result;
    }
}
