import org.infai.ses.senergy.operators.Stream;

public class Operator {

    public static void main(String[] args) {
        try{
            Stream stream  = new Stream();
<<<<<<< HEAD
            Processing processing = new Processing();
            stream.start(processing);
=======
            PreProcessing preProcessing = new PreProcessing();
            stream.start(preProcessing);
>>>>>>> 65f2c2b55537bd7ab215da26fa2a1af295c95db7
        } catch (Exception e ){
            System.out.println(e);
        }
    }
}