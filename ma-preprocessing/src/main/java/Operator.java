import org.infai.ses.senergy.operators.Stream;

public class Operator {

    public static void main(String[] args) {
        try{
            Stream stream  = new Stream();
            Processing processing = new Processing();
            stream.start(processing);
        } catch (Exception e ){
            System.out.println(e);
        }
    }
}