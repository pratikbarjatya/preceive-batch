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

package io.theysay.preceive.batch.plugins.excel;

import io.theysay.preceive.batch.sources.Source;
import io.theysay.preceive.batch.sources.SourceProvider;
import io.theysay.preceive.batch.utils.Datum;
import io.theysay.preceive.batch.utils.IOUtils;
import io.theysay.preceive.batch.utils.Resource;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class XLSXSourceProvider implements SourceProvider {

    @SuppressWarnings("unchecked")
    @Override
    public Source create(Resource resource, Class target) throws Exception {
        if (!target.isAssignableFrom(Datum.class)) return null;
        if (!resource.matchType(Resource.XLSX_FILE)) return null;
        InputStream input = IOUtils.inputStream(resource);
        if (input == null) return null;
        return new XSLXSource(resource, new XSSFWorkbook(input));
    }

    public static class XSLXSource implements Source<Datum> {
        private final Resource resource;
        private final XSSFWorkbook workbook;
        private final XSSFSheet sheet;
        private Iterator<Row> rows;
        private short firstColumn;
        private ArrayList<String> headers;

        public XSLXSource(Resource resource, XSSFWorkbook workbook) {
            this.resource = resource;
            this.workbook = workbook;
            String fragment = resource.getFragment();
            XSSFSheet sheet;
            if (fragment == null) {
                sheet = workbook.getSheetAt(0);
            } else {
                sheet = workbook.getSheet(fragment);
                if (sheet == null) {
                    throw new IllegalArgumentException("Unable to find Sheet named '" + fragment + "'");
                }
            }
            this.sheet = sheet;
            this.rows = this.sheet.iterator();
            if (!rows.hasNext()) {
                throw new IllegalArgumentException(sheet.getSheetName() + " is empty");
            }
            XSSFRow row = nextRow();
            this.firstColumn = row.getFirstCellNum();
            this.headers = readHeaders(row, this.firstColumn);

        }

        public XSSFRow nextRow() {
            return (XSSFRow) rows.next();
        }

        public ArrayList<String> readHeaders(XSSFRow row, short from) {
            ArrayList<String> headers = new ArrayList<>();
            short maxColIx = row.getLastCellNum();
            for (short colIx = this.firstColumn ; colIx < maxColIx ; colIx++) {
                XSSFCell cell = row.getCell(colIx);
                if (cell == null) {
                    break;
                }
                String value = asString(cell);
                if (value == null || value.isEmpty())
                    break;
                headers.add(value);
            }
            return headers;
        }

        @Override
        public Datum next() {
            while (rows.hasNext()) {
                Datum read = read(nextRow());
                if (read != null)
                    return read;
            }
            return null;
        }

        public Datum read(XSSFRow row) {

            short columnIndex = firstColumn;
            Datum datum = new Datum();
            for (String header : headers) {
                Object value = get(row.getCell(columnIndex++));
                if (value != null) {
                    datum.put(header, value);
                }
            }
            if (datum.isEmpty())
                return null;
            int rowNum = row.getRowNum();
            datum.put(Datum.AUTO_ID, resource.getName() + ":" + rowNum);
            datum.put(Datum.ROW_ID, rowNum);
            return datum;
        }

        public String asString(XSSFCell cell) {
            Object o = get(cell);
            return (o == null) ? null : o.toString();
        }

        public Object get(XSSFCell cell) {
            if (cell != null) {
                int cellType = cell.getCellType();
                switch (cellType) {
                    case Cell.CELL_TYPE_NUMERIC:
                        return cell.getNumericCellValue();
                    case Cell.CELL_TYPE_BOOLEAN:
                        return cell.getBooleanCellValue();
                    default:
                        try {
                            return cell.getStringCellValue();
                        } catch (IllegalStateException mismatch) {
                            return null;
                        }
                }
            } else {
                return null;
            }

        }
    }
}
