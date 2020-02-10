import org.infai.seits.sepl.operators.Stream;

import java.io.IOException;

public class Operator {

    public static void main(String[] args) throws IOException {
        Stream stream  = new Stream();
        stream.start(new PreProcessing());
    }
}