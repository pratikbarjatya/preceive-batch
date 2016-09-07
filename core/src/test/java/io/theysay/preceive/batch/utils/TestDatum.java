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

import org.junit.Assert;
import org.junit.Test;

public class TestDatum {

    static String JSON_EXAMPLE_1 =
            "[{\"head\":\"Barack\",\"headIndex\":0,\"start\":0,\"end\":0,\"sentence\":\"Barack Obama is President of the United States .\",\"sentenceHtml\":\"Barack Obama is President of the United States .\",\"text\":\"Barack\",\"namedEntityTypes\":[\"PEOPLE\"]},{\"head\":\"Obama\",\"headIndex\":1,\"start\":0,\"end\":1,\"sentence\":\"Barack Obama is President of the United States .\",\"sentenceHtml\":\"Barack Obama is President of the United States .\",\"text\":\"Barack Obama\",\"namedEntityTypes\":[\"PEOPLE\"]},{\"head\":\"President\",\"headIndex\":3,\"start\":3,\"end\":7,\"sentence\":\"Barack Obama is President of the United States .\",\"sentenceHtml\":\"Barack Obama is President of the United States .\",\"text\":\"President of the United States\",\"namedEntityTypes\":[\"PEOPLE\"]},{\"head\":\"United\",\"headIndex\":6,\"start\":6,\"end\":6,\"sentence\":\"Barack Obama is President of the United States .\",\"sentenceHtml\":\"Barack Obama is President of the United States .\",\"text\":\"United\",\"namedEntityTypes\":[\"LOCATION.COUNTRY\"]},{\"head\":\"States\",\"headIndex\":7,\"start\":5,\"end\":7,\"sentence\":\"Barack Obama is President of the United States .\",\"sentenceHtml\":\"Barack Obama is President of the United States .\",\"text\":\"the United States\",\"namedEntityTypes\":[\"LOCATION.COUNTRY\"]}]";

    static String JSON_EXAMPLE_2 =
            "{\"document\":{\"sentiment\":{\"label\":\"NEUTRAL\",\"positive\":0.0,\"negative\":0.0,\"neutral\":1.0,\"confidence\":0.844},\"wordCount\":9,\"emotion\":[{\"dimension\":\"anger1D\",\"score\":0.0},{\"dimension\":\"calm2D\",\"score\":0.0},{\"dimension\":\"fear1D\",\"score\":0.0},{\"dimension\":\"happy2D\",\"score\":0.0},{\"dimension\":\"like2D\",\"score\":0.0},{\"dimension\":\"shame1D\",\"score\":0.0},{\"dimension\":\"sure2D\",\"score\":0.0},{\"dimension\":\"surprise1D\",\"score\":0.0}]},\"sentence\":[{\"sentenceIndex\":0,\"start\":0,\"end\":8,\"text\":\"Barack Obama is President of the United States .\",\"sentiment\":{\"label\":\"NEUTRAL\",\"positive\":0.0,\"negative\":0.0,\"neutral\":1.0,\"confidence\":0.844},\"emotion\":[{\"dimension\":\"anger1D\",\"score\":0.0},{\"dimension\":\"calm2D\",\"score\":0.0},{\"dimension\":\"fear1D\",\"score\":0.0},{\"dimension\":\"happy2D\",\"score\":0.0},{\"dimension\":\"like2D\",\"score\":0.0},{\"dimension\":\"shame1D\",\"score\":0.0},{\"dimension\":\"sure2D\",\"score\":0.0},{\"dimension\":\"surprise1D\",\"score\":0.0}]}],\"entity\":[{\"start\":0,\"end\":1,\"sentence\":\"Barack Obama is President of the United States .\",\"sentenceHtml\":\" <span class=\\\"entityMention\\\">Barack Obama</span> is President of the United States .\",\"text\":\"Barack Obama\",\"headNoun\":\"Obama\",\"headNounIndex\":1,\"salience\":1.0,\"sentiment\":{\"label\":\"NEUTRAL\",\"positive\":0.0,\"negative\":0.0,\"neutral\":1.0,\"confidence\":0.914}},{\"start\":3,\"end\":3,\"sentence\":\"Barack Obama is President of the United States .\",\"sentenceHtml\":\"Barack Obama is <span class=\\\"entityMention\\\">President</span> of the United States .\",\"text\":\"President\",\"headNoun\":\"President\",\"headNounIndex\":3,\"salience\":1.0,\"sentiment\":{\"label\":\"NEUTRAL\",\"positive\":0.0,\"negative\":0.0,\"neutral\":1.0,\"confidence\":0.914}},{\"start\":6,\"end\":7,\"sentence\":\"Barack Obama is President of the United States .\",\"sentenceHtml\":\"Barack Obama is President of the <span class=\\\"entityMention\\\">United States</span> .\",\"text\":\"United States\",\"headNoun\":\"States\",\"headNounIndex\":7,\"salience\":0.7,\"sentiment\":{\"label\":\"NEUTRAL\",\"positive\":0.0,\"negative\":0.0,\"neutral\":1.0,\"confidence\":0.898}}]}";


    @Test
    public void testExtractionMethods() throws Exception {
        Datum[] entities = JsonUtils.readArray(JSON_EXAMPLE_1);
        Datum entity = entities[0];
        Assert.assertEquals(0, entity.asNumber("start"));
        Assert.assertEquals("Barack", entity.asString("head"));
        Assert.assertNull(entity.asDatum("head")); // No 'object' at path
        Assert.assertNull(entity.asNumber("head")); // No number at path
        Assert.assertEquals("PEOPLE", entity.asStringArray("namedEntityTypes")[0]);
        Assert.assertNull(entity.asStringArray("head")); // No array at path
    }

    @Test
    public void testPathExtractionMethods() throws Exception {
        Datum response = JsonUtils.readItem(JSON_EXAMPLE_2);
        Assert.assertEquals(0.0, response.asNumber("document", "sentiment", "positive"));
        Assert.assertEquals("NEUTRAL", response.asString("document", "sentiment", "label"));
        Assert.assertEquals(0, response.asDataArray("sentence")[0].asNumber("sentenceIndex"));
        // Check that no NPEs occur
        Assert.assertNull(response.asDataArray("document", "sentiment", "label"));
        Assert.assertNull(response.asDataArray("document", "foobar", "label"));


    }

    @Test
    public void testPathSetMethods() throws Exception {
        Datum response = new Datum();
        String[] path = {"document", "sentiment", "label"};
        String[] path2 = {"document", "sentiment", "types"};
        String[] path3 = {"document", "sentiment", "nested"};
        response.set(path, "POSITIVE");
        response.set(path2, new String[]{"sausages", "eggs", "ham"});
        response.set(path3, new Datum[]{new Datum("key", "value")});
        Datum document = response.asDatum(path[0]);
        Datum sentiment = document.asDatum(path[1]);
        String label = sentiment.asString(path[2]);
        Assert.assertEquals("POSITIVE", response.asString(path));
        Assert.assertEquals("POSITIVE", label);
        Assert.assertEquals("sausages", response.asStringArray(path2)[0]);
        Assert.assertEquals("value", response.asDataArray(path3)[0].asString("key"));
        Assert.assertEquals(1, response.keySet().size());
        Assert.assertEquals(1, document.keySet().size());
        Assert.assertEquals(3, sentiment.keySet().size());


    }
}
