import org.infai.ses.senergy.operators.Message;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FeatureExtraction {

    ArrayList<Double> feature;
    ArrayList<Double> motionSensors;
    JSONObject m;
    String value;
    int amountOfMotionSensors;

    JSONObject jsonRequest = new JSONObject();


    public FeatureExtraction(int MotionSensors) {

         /* [Late night, Morning, Noon, Afternoon, Evening, Night,
            Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday,
            Weekday, Weekend,
            M001ON, M002ON, ... , M031OFF, MO32OFF,
            TotalTriggeredMotionSensors]*/

        feature = new ArrayList<>();
        amountOfMotionSensors = MotionSensors;

    }

    public JSONObject run(Stack<JSONObject> segment, LocalDateTime startTime, Boolean training) throws IOException {

        feature.clear();
        motionSensors = new ArrayList<>(Collections.nCopies(amountOfMotionSensors * 2, 0.0)); /*[M001ON, M002ON, ... , M031OFF, MO32OFF]*/

        // building featureSegment
        {
            for (int i = 0; i < segment.size(); i++) {
                m = segment.pop();
                value = m.getString("level");
                if (i == 0) {

                    LocalDateTime time = startTime;

                    /*DayTimeFeature*/
                    switch (time.getHour()) {
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                            feature.addAll(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0, 1.0));
                            break;
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                        case 10:
                        case 11:
                            feature.addAll(Arrays.asList(1.0, 0.0, 0.0, 0.0, 0.0, 0.0));
                            break;
                        case 12:
                        case 13:
                        case 14:
                            feature.addAll(Arrays.asList(0.0, 1.0, 0.0, 0.0, 0.0, 0.0));
                            break;
                        case 15:
                        case 16:
                        case 17:
                            feature.addAll(Arrays.asList(0.0, 0.0, 1.0, 0.0, 0.0, 0.0));
                            break;
                        case 18:
                        case 19:
                        case 20:
                        case 21:
                            feature.addAll(Arrays.asList(0.0, 0.0, 0.0, 1.0, 0.0, 0.0));
                            break;
                        case 22:
                        case 23:
                        case 0:
                            feature.addAll(Arrays.asList(0.0, 0.0, 0.0, 0.0, 1.0, 0.0));
                            break;
                        default:
                            feature.addAll(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
                    }
                }

                if ((value.equals("ON") || value.equals("on")) && motionSensors.get(0) == 0.0) {
                    motionSensors.set(0, 1.0);
                } else if ((value.equals("OFF") || value.equals("off")) && motionSensors.get(1 + amountOfMotionSensors - 1) == 0.0) {
                    motionSensors.set(amountOfMotionSensors, 1.0);
                }
            }

            feature.addAll(motionSensors);

            // add time feature
            Date date = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());
            feature.add((double) date.getTime());
        }

        // http request to the server with the featureSegment and set answer to solution
        jsonRequest.put("feature", feature);
        jsonRequest.put("training", training);
        return jsonRequest;
    }
}


