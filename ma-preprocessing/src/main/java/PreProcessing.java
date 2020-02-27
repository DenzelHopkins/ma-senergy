import org.infai.seits.sepl.operators.Config;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.infai.seits.sepl.operators.Message;
import org.infai.seits.sepl.operators.OperatorInterface;

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

    protected JSONObject result;

    public PreProcessing() {
        segment = new Stack<>();
        windowSize = 120;
        amountOfMotionSensors = 3;
        jsonRequest = new JSONObject();
        extraction = new FeatureExtraction(amountOfMotionSensors);
    }

    @Override
    public void run(Message message) {

        System.out.println("In Run Method!");

        try {
            System.out.println("This is valueOne " + message.getInput("valueOne").getString());
            System.out.println("This is TimeStampOne " + message.getInput("timestampOne").getString());

            RequestHandler request = new RequestHandler();
            request.sayHello();

            // "1971-01-15T00:00:00+02:00"
            Stack<LocalDateTime> times = new Stack<>();
            times.add(LocalDateTime.parse(message.getInput("timestampOne").getString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME));
//            times.add(LocalDateTime.parse(message.getInput("timestampTwo").getString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME));
//            times.add(LocalDateTime.parse(message.getInput("timestampThree").getString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME));

            while (!times.isEmpty()) {
                if (time.toString().length() > 0) {
                    break;
                }
                time = times.pop();
            }

            System.out.println("This is the segment time" + time.toString());

            // Building Segments
            if (segment.size() == 0) {
                segment.add(message);
                startTime = time;
            } else if (Duration.between(startTime, time).getSeconds() > windowSize) {
                try {
                    result = extraction.run(segment, startTime);

                    String resultTime = result.getJSONObject("activityDiscovery").get("time").toString();
                    String resultData = result.getJSONObject("activityDiscovery").get("data").toString();
                    String resultLabel = result.getJSONObject("activityRecognition").get("label").toString();

                    ObjectMapper mapper = new ObjectMapper();
                    ArrayList dataArray = mapper.readValue(resultData, ArrayList.class);

//                    message.output("resultTime", resultTime);
//                    message.output("resultData", resultData);
//                    message.output("resultLabel", resultLabel);

                    System.out.println(resultTime);
                    System.out.println(dataArray);
                    System.out.println(resultLabel);

                } catch (IOException e) {
                    System.err.println("Could not extract segment!");
                    System.err.println("Skipping this message...");
                    System.err.println(e.getMessage());
                    e.printStackTrace();
                }
                System.out.println(startTime);
                segment.clear();
                segment.add(message);
                startTime = time;
            } else {
                segment.add(message);
            }
        } catch (Exception e) {
            System.err.println("Could not build segment!");
            System.err.println("Skipping this message...");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void config(Message message) {
        message.addInput("valueOne");
//        message.addInput("valueTwo");
//        message.addInput("valueThree");
       message.addInput("timestampOne");
//        message.addInput("timestampTwo");
//        message.addInput("timestampThree");
//        message.addInput("deviceOne");
//        message.addInput("deviceTwo");
//        message.addInput("deviceThree");
    }
}

