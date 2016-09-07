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

package io.theysay.preceive.batch.sources;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class SourceIterator<T> implements Iterator<T> {
    private T next;
    private Source<T> enclosed;

    public SourceIterator(Source<T> enclosed) {
        this.enclosed = enclosed;
        advance();
    }


    @Override
    public boolean hasNext() {
        return this.next != null;
    }

    @Override
    public T next() {
        if (!hasNext())
            throw new NoSuchElementException();
        T result = next;
        advance();
        return result;
    }

    protected T advance() {
        try {
            this.next = enclosed.next();
        } catch (Exception e) {
            this.next = null;
        }
        return this.next;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
