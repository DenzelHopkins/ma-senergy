import com.fasterxml.jackson.databind.ObjectMapper;
import org.infai.ses.senergy.operators.Message;
import org.infai.ses.senergy.operators.OperatorInterface;
import org.json.JSONObject;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PreProcessing implements OperatorInterface {

    protected Stack<Message> segment;
    protected int windowSize;
    protected LocalDateTime startTime;
    protected int amountOfMotionSensors;
    protected JSONObject jsonRequest;

    protected LocalDateTime time;
    protected FeatureExtraction extraction;
    protected Segmentation segmentation;
    protected List answer;

    protected JSONObject result;


    public PreProcessing() {
        segment = new Stack<>();
        windowSize = 9;
        amountOfMotionSensors = 2;
        jsonRequest = new JSONObject();
        extraction = new FeatureExtraction(amountOfMotionSensors);
        segmentation = new Segmentation(windowSize);
    }

    @Override
    public void run(Message message) {

        System.out.println("In Run Method!");
        System.out.println("This is the message: " + message.getMessageString());
        System.out.println("This is the value of device One: " + message.getInput("value").getString());
        System.out.println("This is the time of device One: " + message.getInput("time").getString());

        try {
            message.output("result", time.toString());
            time = LocalDateTime.parse(message.getInput("time").getString(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
            System.out.println("This is the current segment size: " + segment.size());
            answer = segmentation.sensorEventBased(segment, message);
            segment = (Stack) answer.get(1);
            if ((Boolean)answer.get(0)){
                try {
                    result = extraction.run(segment, startTime);
                    String resultData = result.getJSONObject("activityDiscovery").get("data").toString();
                    ObjectMapper mapper = new ObjectMapper();
                    ArrayList dataArray = mapper.readValue(resultData, ArrayList.class);
                    System.out.println("SolutionTime: " + startTime.toString());
                    System.out.println("SolutionData: " + dataArray.toString());

                    segment.clear();
                    segment.add(message);
                    startTime = time;
                    System.out.println("StartTime of the segment: "+ startTime.toString());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println("Could not build segment!");
            System.out.println("Skipping this message...");
            System.out.println(e);
        }

    }

    @Override
    public void configMessage(Message message) {
        message.addInput("value");
        message.addInput("time");
    }
}

