package utility;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Captor {

    // Create a stream to hold the output
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    PrintStream currentPrintStream = new PrintStream(byteArrayOutputStream);

    PrintStream originalPrintStream = System.out;

    public void start() {
        originalPrintStream = System.out;
        System.setOut(currentPrintStream);
    }

    public String stop() {
        System.out.flush();
        System.setOut(originalPrintStream);
        return byteArrayOutputStream.toString();
    }

}
