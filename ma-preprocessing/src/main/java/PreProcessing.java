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
        amountOfMotionSensors = 2;
        jsonRequest = new JSONObject();
        extraction = new FeatureExtraction(amountOfMotionSensors);
    }

    @Override
    public void run(Message message) {

        System.out.println("In Run Method!");
        System.out.println("This is the message: " + message.getMessageString());
        System.out.println("This is the value of device One: " + message.getInput("valueOne").getString());
        System.out.println("This is the time of device One: " + message.getInput("timestampOne").getString());
        System.out.println("This is the value of device Two: " + message.getInput("valueTwo").getString());
        System.out.println("This is the time of device Two: " + message.getInput("timestampTwo").getString());
//        System.out.println("This is the value of device Three: " + message.getInput("valueThree").getString());
//        System.out.println("This is the time of device Three: " + message.getInput("timestampThree").getString());

        try {
            time = LocalDateTime.parse(message.getInput("timestampOne").getString(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
            message.output("result", time.toString());

            // Building Segments
            if (segment.size() == 0) {
                segment.add(message);
                startTime = time;
                System.out.println("----------------Segment begins----------------");
                System.out.println("This is the StartTime: " + startTime.toString());
            } else if (segment.size() == windowSize - 1) {
                segment.add(message);
                try {
                    result = extraction.run(segment, startTime);
                    String resultData = result.getJSONObject("activityDiscovery").get("data").toString();
                    ObjectMapper mapper = new ObjectMapper();
                    ArrayList dataArray = mapper.readValue(resultData, ArrayList.class);
                    System.out.println("SolutionTime: " + startTime.toString());
                    System.out.println("SolutionData: " + dataArray.toString());
                } catch (IOException e) {
                    System.out.println("Could not extract segment!");
                    System.out.println("Skipping this message...");
                    System.out.println(e);
                }
                segment.clear();
                System.out.println("----------------Segment ends----------------");
            } else {
                segment.add(message);
            }
        } catch (Exception e) {
            System.out.println("Could not build segment!");
            System.out.println("Skipping this message...");
            System.out.println(e);
        }

    }

    @Override
    public void config(Message message) {
        message.addInput("valueOne");
        message.addInput("valueTwo");
//        message.addInput("valueThree");
        message.addInput("timestampOne");
        message.addInput("timestampTwo");
//        message.addInput("timestampThree");

    }
}

