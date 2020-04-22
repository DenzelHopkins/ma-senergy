import org.infai.ses.senergy.operators.Message;
import org.junit.Test;
import java.util.List;

public class TestPreprocessing {

    public void run() throws Exception {
        PreProcessing pre = new PreProcessing();
        List<Message> messages = TestMessageProvider.getTestMesssagesSet();
        int data_size = messages.size();
        for (int i = 0; i < data_size; i++) {
            Message m = messages.get(i);
            pre.configMessage(m);
            pre.run(m);
        }
    }

    @Test
    public void Test() throws Exception {
        run();
    }
}
