import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;


public class RequestHandler {

    /* Address of the server */
    String uri = "http://127.0.0.1:5000/";

    /* Data for a request */
    StringEntity stringEntity;

    /* Send dataPoint to the server and analyse it */
    public JSONObject analyseDataPoint(JSONObject jsonRequest) throws IOException {
        stringEntity = new StringEntity(jsonRequest.toString());
        HttpPost post = new HttpPost(uri + "analyseDataPoint");
        post.setEntity(stringEntity);
        JSONObject result;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            try (CloseableHttpResponse response = httpClient.execute(post)) {
                result = new JSONObject(EntityUtils.toString(response.getEntity()));
            }
        }
        return result;
    }
}
