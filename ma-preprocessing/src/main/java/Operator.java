import org.infai.ses.senergy.operators.Stream;

public class Operator {

    public static void main(String[] args) {
        try{
            Stream stream  = new Stream();
            PreProcessing preProcessing = new PreProcessing();
            System.out.println("In Main Method!");
            stream.start(preProcessing);
        } catch (Exception e ){
            System.out.println(e);
        }
    }
}