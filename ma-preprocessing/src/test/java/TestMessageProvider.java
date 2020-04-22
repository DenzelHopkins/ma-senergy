import org.infai.ses.senergy.operators.Builder;
import org.infai.ses.senergy.operators.Message;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestMessageProvider {

    public static List<Message> getTestMesssagesSet() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("src/test/resources/sample-data-small.json"));
        Builder builder = new Builder("1", "1");
        List<Message> messageSet = new ArrayList<>();
        JSONObject config = getConfig();
        String line;
        Message m;
        JSONObject jsonObjectRead, jsonObject;
        while ((line = br.readLine()) != null) {
            jsonObjectRead = new JSONObject(line);
            String device = jsonObjectRead.getString("device");
            JSONObject values = new JSONObject() ;
            values.put("level", jsonObjectRead.getString("value"));
            values.put("updateTime", jsonObjectRead.getString("timestamp"));
            jsonObject = new JSONObject().put("device_id", device).put("value", new JSONObject().put("tamper", values));
            m = new Message(builder.formatMessage(jsonObject.toString()));
            m.setConfig(config.toString());
            messageSet.add(m);
        }
        return messageSet;
    }

    private static JSONObject getConfig() {
        JSONObject config = new JSONObject().put("inputTopics",new JSONArray().put(new JSONObject().put("Name", "test")
                .put("FilterType", "DeviceId")
                .put("FilterValue", "1")
                .put("Mappings", new JSONArray()
                        .put(new JSONObject().put("Source", "value.tamper.level").put("Dest", "level"))
                        .put(new JSONObject().put("Source", "value.tamper.updateTime").put("Dest", "updateTime"))
                )));
        return config;
    }
}