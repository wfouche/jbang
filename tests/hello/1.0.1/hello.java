///usr/bin/env jbang "$0" "$@" ; exit $?

import static java.lang.System.*;
import java.lang.System;
public class hello {

    public static void main(String... args) {
        out.println("Hello World, 1.0.1");
	String version = System.getProperty("jbang.app.version");

        if (version != null) {
            System.out.println("JBang App Version: " + version);
        } else {
            System.out.println("Property 'jbang.app.version' is not defined.");
        }
    }
}
