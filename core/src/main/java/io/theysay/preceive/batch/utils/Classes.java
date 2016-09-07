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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.*;
import java.util.*;

/**
 * Class and classpath related functionality
 */
public final class Classes {

    private Classes() {
    }

    private final static Logger _LOGGER = LoggerFactory.getLogger(Classes.class);


    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static InputStream getResource(String path) throws IOException {
        while (path.startsWith("/")) path = path.substring(1);
        InputStream inputStream = getClassLoader().getResourceAsStream(path);
        if (inputStream == null)
            throw new FileNotFoundException(path + " not found");
        return inputStream;
    }

    /**
     * Load all available implementation of the specified services
     *
     * @param service Interface or abstract class to load
     * @param <T>     Interace/Abstract Class
     * @return Collection of all providers of this service...
     */
    public static <T> ArrayList<T> loadServices(Class<T> service) {
        return loadServices(service, null);
    }

    /**
     * Load all available implementation of the specified services
     *
     * @param service     Interface or abstract class to load
     * @param <T>         Interace/Abstract Class
     * @param classLoader to use
     * @return Collection of all providers of this service...
     */
    public static synchronized <T> ArrayList<T> loadServices(Class<T> service, ClassLoader classLoader) {
        if (classLoader == null) classLoader = getClassLoader();
        ArrayList<T> results = new ArrayList<>();
        ServiceLoader<T> loader = ServiceLoader.load(service, classLoader);
        Iterator<T> iterator = loader.iterator();
        while (true) {
            if (!iterator.hasNext()) {
                return results;
            }

            try {
                T provider = iterator.next();
                if (provider != null) {
                    results.add(provider);
                    _LOGGER.info(service + ": Loaded " + provider.getClass());
                }

            } catch (ServiceConfigurationError error) {
                // If this happens - move to next. See JavaDoc for details..
                // 'To write robust code it is only necessary to catch ServiceConfigurationError when using a service iterator.'
                // Ok then
                _LOGGER.error("ConfigurationError of Provider class", error);
            }
        }
    }

    /**
     * Create instance of the specified class using either a
     * public static valueOf(String) method
     * public Single string argument constructor
     *
     * @param target class to instantiate
     * @param string to use
     * @return new instantiated value...
     * @throws IllegalArgumentException - if the string argument was not acceptable to the determined method or constructor
     * @throws IllegalStateException    - if no method or constructor was available and accessible.
     */
    @SuppressWarnings("unchecked")
    public static Object valueOf(Class target, String string) throws IllegalArgumentException, IllegalStateException {
        if (target.equals(String.class)) return string;
        try {
            Method valueOf = target.getMethod("valueOf", String.class);
            return valueOf.invoke(null, string);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            throw new IllegalArgumentException(string + " not acceptable:" + cause.getMessage(), cause);
        } catch (Exception e) {
            // Does not exist or not accessible
        }

        try {
            Constructor constructor = target.getConstructor(String.class);
            return constructor.newInstance(string);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            throw new IllegalArgumentException(string + " not acceptable:" + cause.getMessage(), cause);
        } catch (Exception e) {
            // Does not exist or not accessible
        }
        throw new IllegalStateException("Not possible to create the instance of " + target + " from " + string);
    }


    @SuppressWarnings("unchecked")
    public static <T> Collection<T> getStaticFinals(Class clazz, Class<T> targetClass, boolean includeArrays) {
        ArrayList<T> result = new ArrayList<>();

        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            Object value = getPublicStatic(field);
            if (value == null) continue;

            if (targetClass.isInstance(value)) {

                result.add((T) value);

            } else if (includeArrays && value.getClass().isArray()) {
                int length = Array.getLength(value);
                for (int i = 0 ; i < length ; i++) {
                    Object val = Array.get(value, i);
                    if (targetClass.isInstance(val)) result.add((T) val);
                }
            }
        }
        return result;
    }

    private static Object getPublicStatic(Field field) {
        int modifiers = field.getModifiers();
        if (!Modifier.isStatic(modifiers)) return null;
        if (!Modifier.isPublic(modifiers)) return null;
        try {
            return field.get(null);
        } catch (IllegalAccessException e) {
            // We have guarded against this already...
            return null;
        }
    }

}

