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

    String uri = "http://127.0.0.1:5000/";
    HttpPost post = new HttpPost(uri + "discovery");
    HttpGet get = new HttpGet(uri + "test");
    StringEntity stringEntity;

    public JSONObject postSegment(JSONObject jsonRequest) throws IOException {
        stringEntity = new StringEntity(jsonRequest.toString());
        post.setEntity(stringEntity);
        JSONObject result;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            CloseableHttpResponse response = httpClient.execute(post);
            try {
                result = new JSONObject(EntityUtils.toString(response.getEntity()));
            } finally {
                response.close();
            }
        } finally {
            httpClient.close();
        }
        return result;
    }

    public void sayHello() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            CloseableHttpResponse response = httpClient.execute(get);
            try {
                JSONObject result = new JSONObject(EntityUtils.toString(response.getEntity()));
                String answer = result.get("answer").toString();
                System.out.println(answer);
            } finally {
                response.close();
            }
        } finally {
            httpClient.close();
        }
    }
}
