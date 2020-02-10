import com.fasterxml.jackson.databind.ObjectMapper;
import org.infai.seits.sepl.operators.Message;
import org.infai.seits.sepl.operators.OperatorInterface;

import org.json.JSONObject;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class preProcessing implements OperatorInterface {

    protected Stack<Message> segment;
    protected int windowSize;
    protected LocalDateTime startTime;
    protected int amountOfMotionSensors;
    protected JSONObject jsonRequest;


    protected String time_to_parse;
    protected featureExtraction extraction;

    protected JSONObject result;

    public preProcessing() {
        segment = new Stack<>();
        windowSize = 120;
        amountOfMotionSensors = 3;
        jsonRequest = new JSONObject();
        extraction = new featureExtraction(amountOfMotionSensors);
    }

    @Override
    public void run(Message message) {

        Stack<String> times = new Stack<>();
        times.add(message.getInput("timestamp1").getString());
        times.add(message.getInput("timestamp2").getString());
        times.add(message.getInput("timestamp3").getString());

        while(!times.isEmpty()){
            if(time_to_parse.length() > 0){
                break;
            }
            time_to_parse = times.pop();
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        LocalDateTime time = LocalDateTime.parse(time_to_parse, formatter);

        // Building Segments
        {
            if (segment.size() == 0) {
                segment.add(message);
                startTime = time;
            } else if (Duration.between(startTime, time).getSeconds() > windowSize) {
                try {
                    result = extraction.run(segment, startTime);

                    String result_time = result.getJSONObject("activityDiscovery").get("time").toString();
                    String result_data = result.getJSONObject("activityDiscovery").get("data").toString();
                    String result_label = result.getJSONObject("activityRecognition").get("label").toString();

                    ObjectMapper mapper = new ObjectMapper();
                    ArrayList data_array = mapper.readValue(result_data, ArrayList.class);

                    message.output("Result_Time", result_time);
                    message.output("Result_Data", result_data);
                    message.output("Result_Label", result_label);

                    System.out.println(result_time);
                    System.out.println(data_array);
                    System.out.println(result_label);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(startTime);
                segment.clear();
                segment.add(message);
                startTime = time;
            } else {
                segment.add(message);
            }
        }
    }

    @Override
    public void config(Message message) {
        message.addInput("value1");
        message.addInput("value2");
        message.addInput("value3");
        message.addInput("timestamp1");
        message.addInput("timestamp2");
        message.addInput("timestamp3");
    }
}

