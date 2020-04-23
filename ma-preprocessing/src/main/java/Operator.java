import org.infai.ses.senergy.operators.Stream;

public class Operator {

    public static void main(String[] args) {
        try{
            Stream stream  = new Stream();
            PreProcessing preProcessing = new PreProcessing();
            stream.start(preProcessing);
        } catch (Exception e ){
            System.out.println(e);
        }
    }
}