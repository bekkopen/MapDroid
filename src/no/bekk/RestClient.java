package no.bekk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
 
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 
import android.util.Log;
 
public class RestClient {
 
    private String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
 
    /* This is a test function which will connects to a given
     * rest service and prints it's response to Android Log with
     * labels "Praeda".
     */
    public JSONArray getEmployeeList(String url, String host, String username, String password) throws Exception
    {
    	JSONArray jsonArray = new JSONArray();
        HttpClient httpclient = new DefaultHttpClient();
 
        AuthScope as = new AuthScope(host, 443);
        UsernamePasswordCredentials upc = new UsernamePasswordCredentials(
                username, password);

        ((AbstractHttpClient) httpclient).getCredentialsProvider()
                .setCredentials(as, upc);

        BasicHttpContext localContext = new BasicHttpContext();

        BasicScheme basicAuth = new BasicScheme();
        localContext.setAttribute("preemptive-auth", basicAuth);

        HttpHost targetHost = new HttpHost(host, 443, "https");

        HttpGet httpget = new HttpGet(url); 
 
        // Execute the request
        HttpResponse response;
            response = httpclient.execute(targetHost, httpget, localContext);
            // Examine the response status
            Log.i("Praeda",response.getStatusLine().toString());
 
            // Get hold of the response entity
            HttpEntity entity = response.getEntity();
            // If the response does not enclose an entity, there is no need
            // to worry about connection release
 
            if (entity != null) {
 
                // A Simple JSON Response Read
                InputStream instream = entity.getContent();
                String result= convertStreamToString(instream);
                Log.i("Praeda",result);
 
                // A Simple JSONObject Creation
                jsonArray=new JSONArray(result);
 
//                for(int i=0;i<jsonArray.length();i++)
//                {
//                    Log.i("JsonObject: ",jsonArray.get(i).toString() + "\n");
//                }
 
                instream.close();
            }
 
 
        
            return jsonArray;
    }
 
}

