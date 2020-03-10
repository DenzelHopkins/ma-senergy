import org.infai.seits.sepl.operators.Message;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FeatureExtraction {

    ArrayList<Double> feature;
    ArrayList<Double> motionSensors;
    Message m;
    String valueOne;
    String valueTwo;
    String valueThree;
    int amountOfMotionSensors;
    int triggeredMotionSensors;

    RequestHandler requestHandler = new RequestHandler();
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

    public JSONObject run(Stack<Message> segment, LocalDateTime startTime) throws IOException {

        feature.clear();
        motionSensors = new ArrayList<>(Collections.nCopies(amountOfMotionSensors * 2, 0.0)); /*[M001ON, M002ON, ... , M031OFF, MO32OFF]*/
        triggeredMotionSensors = 0;

        // building featureSegment
        {
            for (int i = 0; i < segment.size(); i++) {
                m = segment.pop();
                valueOne = m.getInput("valueOne").getString();
                valueTwo = m.getInput("valueTwo").getString();
//                valueThree = m.getInput("valueThree").getString();

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

                    /*DayOfWeek*/
                    switch (time.getDayOfWeek()) {
                        case MONDAY:
                            feature.addAll(Arrays.asList(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
                            break;
                        case TUESDAY:
                            feature.addAll(Arrays.asList(0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0));
                            break;
                        case WEDNESDAY:
                            feature.addAll(Arrays.asList(0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0));
                            break;
                        case THURSDAY:
                            feature.addAll(Arrays.asList(0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0));
                            break;
                        case FRIDAY:
                            feature.addAll(Arrays.asList(0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0));
                            break;
                        case SATURDAY:
                            feature.addAll(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0));
                            break;
                        case SUNDAY:
                            feature.addAll(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0));
                            break;
                        default:
                            System.out.println("--------------------NO WEEKDAY SET ");
                            feature.addAll(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
                    }

                    /*WeekendOrNot*/
                    switch (time.getDayOfWeek()) {
                        case MONDAY:
                        case TUESDAY:
                        case WEDNESDAY:
                        case THURSDAY:
                            feature.addAll(Arrays.asList(1.0, 0.0));
                            break;
                        case FRIDAY:
                        case SATURDAY:
                        case SUNDAY:
                            feature.addAll(Arrays.asList(0.0, 1.0));
                            break;
                        default:
                            System.out.println("--------------------NO WEEKEND SET ");
                            feature.addAll(Arrays.asList(0.0, 0.0));
                    }

                }

                if ((valueOne.equals("ON") || valueOne.equals("on")) && motionSensors.get(0) == 0.0) {
                    motionSensors.set(0, 1.0);
                    triggeredMotionSensors++;
                } else if ((valueOne.equals("OFF") || valueOne.equals("off")) && motionSensors.get(1 + amountOfMotionSensors - 1) == 0.0) {
                    motionSensors.set(1 + amountOfMotionSensors - 1, 1.0);
                    triggeredMotionSensors++;
                }

                if ((valueTwo.equals("ON") || valueTwo.equals("on")) && motionSensors.get(2 - 1) == 0.0) {
                    motionSensors.set(2 - 1, 1.0);
                    triggeredMotionSensors++;
                } else if ((valueTwo.equals("OFF") || valueTwo.equals("on")) && motionSensors.get(2 + amountOfMotionSensors - 1) == 0.0) {
                    motionSensors.set(2 + amountOfMotionSensors - 1, 1.0);
                    triggeredMotionSensors++;
                }

//                if ((valueThree.equals("ON") || valueThree.equals("on")) && motionSensors.get(3 - 1) == 0.0) {
//                    motionSensors.set(3 - 1, 1.0);
//                    triggeredMotionSensors++;
//                } else if ((valueThree.equals("OFF") || valueThree.equals("on")) && motionSensors.get(3 + amountOfMotionSensors - 1) == 0.0) {
//                    motionSensors.set(3 + amountOfMotionSensors - 1, 1.0);
//                    triggeredMotionSensors++;
//                }

            }

            feature.addAll(motionSensors);
            //feature.add(triggeredMotionSensors); wie normalisieren?
            // mehr features???

            // add time feature
            Date date = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());
            feature.add((double) date.getTime());
        }

        // http request to the server with the featureSegment and set answer to solution
        jsonRequest.put("feature", feature);
        return requestHandler.postSegment(jsonRequest);
    }
}


