import org.infai.seits.sepl.operators.Message;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class Segmentation {

    protected int windowSize;
    protected String currentActivity;

    public Segmentation(int size) {
        this.windowSize = size;
        this.currentActivity = "";
    }

    public List<Object> sensorEventBased(Stack<Message> segment, Message message) {
        if (segment.size() < windowSize) {
            segment.add(message);
            return Arrays.asList(false, segment);
        } else if (segment.size() == windowSize) {
            return Arrays.asList(true, segment);
        }
        return Arrays.asList(false, segment);
    }
}
