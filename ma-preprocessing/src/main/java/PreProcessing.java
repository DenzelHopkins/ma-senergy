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

        // "1971-01-15T00:00:00+02:00"

        Stack<LocalDateTime> times = new Stack<>();
        times.add(LocalDateTime.parse(message.getInput("timestamp1").getString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        times.add(LocalDateTime.parse(message.getInput("timestamp2").getString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        times.add(LocalDateTime.parse(message.getInput("timestamp3").getString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        while(!times.isEmpty()){
            if(time.toString().length() > 0){
                break;
            }
            time = times.pop();
        }

        message.output("Time", time.toString());

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

