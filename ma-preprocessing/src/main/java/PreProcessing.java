import com.fasterxml.jackson.databind.ObjectMapper;
import org.infai.ses.senergy.operators.Message;
import org.infai.ses.senergy.operators.OperatorInterface;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PreProcessing implements OperatorInterface {

    protected int windowSize;
    protected int amountOfMotionSensors;

    protected String timeToParse;
    protected LocalDateTime time;
    protected LocalDateTime segmentTime;
    protected Segmentation segmentation;
    protected List answer;
    protected Stack<JSONObject> segment;
    protected Integer trainingDuration;
    protected Integer trainingCounter;
    protected Boolean training;
    protected FeatureExtraction extraction;
    protected RequestHandler requestHandler;
    protected JSONObject jsonRequest;
    protected JSONObject activities;


    public PreProcessing() {
        segment = new Stack<>();
        windowSize = 3;
        amountOfMotionSensors = 2;
        training = true;
        trainingDuration = 50;
        trainingCounter = 0;
        segmentation = new Segmentation(windowSize);
        extraction = new FeatureExtraction(amountOfMotionSensors);
        requestHandler = new RequestHandler();
        jsonRequest = new JSONObject();
    }

    @Override
    public void run(Message message) {

        // System.out.println("This is the message: " + message.getMessageString());

        org.json.simple.JSONArray jsonArray = message.getValue("inputs");
        org.json.simple.JSONObject inputs = (org.json.simple.JSONObject) jsonArray.get(0);
        org.json.simple.JSONObject value = (org.json.simple.JSONObject) inputs.get("value");
        org.json.simple.JSONObject tamper = (org.json.simple.JSONObject) value.get("tamper");

        String level = tamper.get("level").toString();
        String updateTime = tamper.get("updateTime").toString();
        String device_id = inputs.get("device_id").toString();

        JSONObject json = new JSONObject().put("level", level).put("updateTime", updateTime).put("device_id", device_id);
        // System.out.println("This is the json-object: " + json.toString());

        try {
            message.output("result", "result");

            /* Get time of the message */
            timeToParse = json.getString("updateTime");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            time = LocalDateTime.parse(timeToParse, formatter);

            /* Check if segment size is null or not to set segmentTime */
            if (segment.size() == 0){
                segmentTime = time;
                System.out.println("This is the current segment size: " + segment.size());
            }

            /* Training or not */
            trainingCounter += 1;
            if (trainingCounter > trainingDuration) {
                training = false;
            }

            /* Use segmentation approach */
            answer = segmentation.sensorEventBased(segment, json);
            segment = (Stack) answer.get(1);

            /* If segment is ready to analyse extract features and send to server*/
            if ((Boolean)answer.get(0)){
                try {
                    /* Extract features */
                    jsonRequest = extraction.run(segment, segmentTime, training);

                    System.out.println("This is the jsonRequest to the server: " + jsonRequest.toString());

                    /* Send dataPoint to the server */
                    activities = requestHandler.analyseDataPoint(jsonRequest);

                    /* Recognized and discovered activity */
                    System.out.println("This is the recognized activity: " +
                            activities.getString("recognizedActivity") +
                            (" and this is the discoveredActivity: ") +
                            activities.getString("discoveredActivity")
                    );

                    /* Clear the segment, add current message and set startTime of the new segment */
                    segment.clear();
                    segment.add(json);
                    segmentTime = time;

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
        message.addInput("level");
        message.addInput("updateTime");
    }
}

