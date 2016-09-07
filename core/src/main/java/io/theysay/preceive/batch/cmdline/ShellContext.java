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

package io.theysay.preceive.batch.cmdline;

import io.theysay.preceive.batch.utils.Classes;

import java.io.*;
import java.util.*;

public class ShellContext implements Closeable, Flushable {

    private Map<String, Action> actions = new LinkedHashMap<>();
    private PrintWriter out;


    public ShellContext() {
        this(system());
    }

    public ShellContext(Writer writer) {
        this(new PrintWriter(writer, true));
    }

    public ShellContext(PrintWriter writer) {
        this.out = writer;
        register(new Help());
        List<ActionLibrary> libraries = Classes.loadServices(ActionLibrary.class);
        Collections.sort(libraries, ActionLibrary.PRIORITY);
        for (ActionLibrary library : libraries) {
            library.install(this);
        }
    }

    public ShellContext register(Class aClass) {

        Collection<Action> actions = Classes.getStaticFinals(aClass, Action.class, true);
        for (Action action : actions) {
            register(action);
        }
        return this;
    }


    public ShellContext register(Action action) {
        this.actions.put(action.getKey(), action);
        return this;
    }

    public Action get(String key) {
        Action action = actions.get(key);
        if (action == null) {
            throw new NoSuchElementException("No action registered for " + key);
        } else {
            return action;
        }
    }


    public Collection<Action> actions() {
        return actions.values();
    }

    private LinkedList<String> arguments = new LinkedList<>();

    public ShellContext push(String... args) {
        return push(Arrays.asList(args));
    }

    public ShellContext push(List<String> arguments) {
        this.arguments.addAll(0, arguments);
        return this;
    }

    public boolean hasNextArgument() {
        return hasNext() && !arguments.peek().startsWith("-");
    }

    public String nextArgument(String argumentName) {
        if (!hasNextArgument()) throw new NoSuchElementException(argumentName + " is not specified");
        String arg = arguments.removeFirst();
        if (arg.equalsIgnoreCase("null")) arg = null;
        return arg;
    }

    public boolean hasNextCommand() {
        return hasNext() && arguments.peek().startsWith("-");
    }


    public boolean hasNext() {
        return !arguments.isEmpty();
    }

    public String next() {
        return arguments.removeFirst();
    }

    public String[] arguments() {
        ArrayList<String> tmp = new ArrayList<>();
        while (hasNextArgument()) {
            tmp.add(next());
        }
        return tmp.toArray(new String[tmp.size()]);
    }


    public void execute(String... args) throws Exception {
        push(args);
        execute();
    }


    public void execute() throws Exception {
        while (hasNextCommand()) {
            String cmd = next();
            cmd = cmd.substring(1);
            get(cmd).execute(this);
        }
        if (hasNext()) {
            String[] arguments = arguments();
            String error = "The following arguments were unused: Did you mean -" + arguments[0];
            throw new IllegalStateException(error);
        }
    }

    public void println(String text) {
        out.println(text);
    }

    @Override
    public void close() {
        out.close();
    }

    @Override
    public void flush() {
        out.flush();
    }

    /**
     * returns a runnable that every time it is invoked will print the toString() of the provided object
     *
     * @param o to print toString()
     * @return
     */
    public Runnable printer(final Object o) {
        return new Runnable() {
            @Override
            public void run() {
                println(o.toString());
            }
        };
    }


    public static PrintWriter system() {
        Console console = System.console();
        if (console != null)
            return console.writer();
        else
            return new PrintWriter(System.out, true);
    }
}
