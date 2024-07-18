import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Main {

    private static final String API_KEY = "579b464db66ec23bdd000001c63027fd0bd149b676d7ff8d14e3b92f";
    private static final String BASE_URL = "https://api.data.gov.in/resource/3b01bcb8-0b14-4abf-b6f2-c1bfd384ba69?api-key=%s&format=json&offset=0";

    public static void main(String[] args) {
        String state = "Karnataka";
        String city = "Chennai"; // Bengaluru, Patna, Delhi, Hyderabad
        String url = String.format(BASE_URL, API_KEY);

        boolean forState = false;

        if (forState) {
            url = url + "&filters[state]=" + state;
        } else {
            url = url + "&filters[city]=" + city;
        }

        showAQI(url);
    }

    public static void showAQI(String url) {
        HttpResponse httpResponse = sendHttpReq(url);

        if (httpResponse != null && httpResponse.getBody() != null) {
            JSONObject jsonObject = new JSONObject(httpResponse.getBody());
            JSONArray allRecords = jsonObject.getJSONArray("records");

            try {
                JSONObject firstRecord = (JSONObject) allRecords.get(0);
                System.out.println("*************** Air Pollution Level *********************");
                System.out.println("City :" + firstRecord.getString("city"));
                System.out.println("State :" + firstRecord.getString("state"));
                System.out.println("On :" + firstRecord.getString("last_update"));
                System.out.println("Min Pollution :" + firstRecord.getString("pollutant_min"));
                System.out.println("Average Pollution :" + firstRecord.getString("pollutant_avg"));
                System.out.println("Max Pollution :" + firstRecord.getString("pollutant_max"));
                System.out.println("*****************************");
            } catch (Exception e) {
                System.out.println("Please enter city/state name correctly");
            }
        } else {
            System.out.println("Error fetching data from API");
        }
    }

    private static HttpResponse sendHttpReq(String url) {
        HttpResponse responseObj = null;
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Content-Type", "application/json");

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                    response.append(System.lineSeparator());
                }
                in.close();

                String out = response.toString().trim();
                Map<String, String> headers = new HashMap<>();
                for (String header : con.getHeaderFields().keySet()) {
                    headers.put(header, con.getHeaderField(header));
                }
                responseObj = new HttpResponse(out, headers, responseCode);
            }
            con.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseObj;
    }
}

class HttpResponse {
    private final String body;
    private final Map<String, String> headers;
    private final int responseCode;

    public HttpResponse(String body, Map<String, String> headers, int responseCode) {
        this.body = body;
        this.headers = headers;
        this.responseCode = responseCode;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public int getResponseCode() {
        return responseCode;
    }
}

