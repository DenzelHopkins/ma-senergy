import org.infai.ses.senergy.operators.Message;
import org.json.JSONObject;

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

    public List<Object> sensorEventBased(Stack<JSONObject> segment, JSONObject json) {
        if (segment.size() < windowSize) {
            segment.add(json);
            return Arrays.asList(false, segment);
        } else if (segment.size() == windowSize) {
            return Arrays.asList(true, segment);
        }
        return Arrays.asList(false, segment);
    }
}
