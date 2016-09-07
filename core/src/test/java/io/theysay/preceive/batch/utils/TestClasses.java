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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

public class TestClasses {
    @Test
    public void testValueOf() throws Exception {
        Assert.assertEquals(10, Classes.valueOf(Integer.class, "10"));
        Assert.assertEquals(10L, Classes.valueOf(Long.class, "10"));
        Assert.assertEquals(10.0, Classes.valueOf(Double.class, "10"));
        Assert.assertEquals(10.0, Classes.valueOf(Double.class, "10"));
        Assert.assertEquals(true, Classes.valueOf(Boolean.class, "true"));
        Assert.assertEquals(false, Classes.valueOf(Boolean.class, "false"));
        Assert.assertEquals("Hello", Classes.valueOf(String.class, "Hello"));

    }

    @Test(expected = IllegalStateException.class)
    public void testValueOfNoMethods() throws Exception {
        Classes.valueOf(Object.class, "10");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfBadString() throws Exception {
        Classes.valueOf(Integer.class, "five");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStringConstructorException() throws Exception {
        Classes.valueOf(NoValueOf.class, "");
    }

    @Test
    public void testStringConstructor() throws Exception {
        NoValueOf value = (NoValueOf) Classes.valueOf(NoValueOf.class, "value");
        Assert.assertEquals("value", value.value);
    }

    @Test
    public void testServiceLoading() {
        ArrayList<TestService> testServices = Classes.loadServices(TestService.class);
        Assert.assertEquals(1, testServices.size());
        Assert.assertTrue(testServices.get(0) instanceof WorkingTestService);
    }

    @Test
    public void testStaticFieldAccess() throws Exception {
        Collection<String> strings = Classes.getStaticFinals(TestClassFieldAccess.class, String.class, false);
        Collection<String> stringWithArrays = Classes.getStaticFinals(TestClassFieldAccess.class, String.class, true);

        Collection<Date> dates = Classes.getStaticFinals(TestClassFieldAccess.class, Date.class, false);
        Collection<Date> dateArrays = Classes.getStaticFinals(TestClassFieldAccess.class, Date.class, true);

        Collection<Number> numbers = Classes.getStaticFinals(TestClassFieldAccess.class, Number.class, true);
        Collection<Number> numberArray = Classes.getStaticFinals(TestClassFieldAccess.class, Number.class, true);


        Assert.assertEquals(1, strings.size());
        Assert.assertTrue(strings.contains("public"));
        Assert.assertEquals(3, stringWithArrays.size());
        Assert.assertTrue(stringWithArrays.containsAll(Arrays.asList("public", "hello", "world")));

        Assert.assertEquals(1, dates.size());
        Assert.assertEquals(1, dateArrays.size());

        Assert.assertEquals(0, numbers.size());
        Assert.assertEquals(0, numberArray.size());

    }

    public static class NoValueOf {
        private String value;

        public NoValueOf(String value) {
            this.value = value;
            if (value.isEmpty())
                throw new IllegalArgumentException("Cannot use empty values");
        }

        public String getValue() {
            return value;
        }
    }

    public static class TestClassFieldAccess {
        public static final String PUBLIC = "public";
        public static final Date DATE = new Date();
        public static final String[] ARRAY = {"hello", "world"};

        public final String NON_STATIC = "goodbyte";
        private final String PRIVATE = "private";


    }
}
