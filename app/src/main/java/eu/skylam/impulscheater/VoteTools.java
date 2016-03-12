package eu.skylam.impulscheater;

import android.content.Context;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Mannik on 28-Nov-15.
 */
public class VoteTools {
    String urlResponse;
    final String voteUrl = "http://impuls.mopa.cz/form2/index.php";
    final String refererUrl = "http://www.impuls.cz/souteze/halo-tady-impulsovi-o-milion-a-dalsi-dve-prani/";

   // List<Cookie> cookieList;
    CookieManager cm;
    Context context;
    public VoteTools(Context context)
    {
        StaticClass.context = context;
        TxtLog.appendDebug("VoteTools: constructor call");
        cm = new CookieManager();
        this.context = context;

        //just for sure
        loadUrl(refererUrl,true);
        //generate new timestamp and get the form
        urlResponse = loadUrl(voteUrl, true);
        //ping the tracker
        pingKbmg(Kbmg.getId(context), "3lnj4tug", "", "/form2/index.php");

    }

    public void vote(VoteUser voteUser) {
        TxtLog.appendDebug("VoteTools: vote called");
        if (isVotingAvailable()) {
            try {
                String result = performPostCall(voteUrl, voteUser.getAttributes(getSpamCheck(),getTelCheck()));

               // TxtLog.append("spam:"+getSpamCheck());
               // TxtLog.append("tel:"+getTelCheck());
                if (result.contains("Registrace do hry byla úspěšně provedena"))
                    TxtLog.append("Vote successful");
                else
                    TxtLog.append("Vote wasn't successful, content: " + result);

                //ping the tracker once more
                pingKbmg(Kbmg.getId(context), "3lnj4tug", "", "/form2/index.php");
                //Utils.scheduleNextVote(context, true);
            } catch (Exception e) {
                TxtLog.append("Error:" + e.toString());
                //TxtLog.appendErr(e.toString());
            }
            return;
        }
        TxtLog.append("Voting not available");
       // Utils.scheduleNextVote(context, true);
    }


    public boolean isVotingAvailable()
    {
        if (urlResponse.contains("Jméno")&& urlResponse.contains("Registrovat"))
            return true;
        else
            return false;
    }

    public String getSpamCheck()
    {
        String[] spmchkParts = urlResponse.split("name=\"spmchk\"")[0].split("\"");

        return spmchkParts[spmchkParts.length-2];
    }

    public String getTelCheck()
    {
        String[] telParts = urlResponse.split("name=\"tel_")[1].split("\"");

        return "tel_"+telParts[0];
    }

    private String loadUrl(String url, boolean newLastTimestamp)
    {
        TxtLog.appendDebug("VoteTools:loadUrl("+url+")");
        try {
            InputStream inputStream = OpenHttpConnection(url, newLastTimestamp, (url == voteUrl) ? refererUrl : "") ;
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }

            return total.toString();
        }
        catch (Exception e)
        {
            TxtLog.append("Error, err:" + e.toString());
           // TxtLog.appendErr(e.toString());
            return "";
        }
    }
    
    private void setUserAgent(HttpURLConnection httpConn)
    {
      //me
        httpConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36");
        //pc1
        //httpConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36");
        //pc2
       // httpConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0");
    }

    private InputStream OpenHttpConnection(String strURL, boolean newLastTimestamp, String referer, boolean... kbmgFormat)
            throws IOException {
        URLConnection conn = null;
        InputStream inputStream = null;
        URL url = new URL(strURL);
        conn = url.openConnection();
        conn.setReadTimeout(15000);
        conn.setConnectTimeout(15000);
        HttpURLConnection httpConn = (HttpURLConnection) conn;
        httpConn.setRequestMethod("GET");
        setUserAgent(httpConn);
        httpConn.setRequestProperty("Referer", referer);

        //add kbmg
        cm.addCookies(httpConn, kbmgValues(kbmgFormat.length > 0 && kbmgFormat[0]));


        //set new timestamp
        if (newLastTimestamp)
            Kbmg.setLast(context);

        httpConn.connect();
        try {
            //store php session
            cm.storeCookies(httpConn);
        }
        catch (Exception e)
        {
            TxtLog.append("unable to store cookies, err" + e.toString());

        }
        if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            inputStream = httpConn.getInputStream();
        }
        return inputStream;
    }

    private String  performPostCall(String requestURL,
                                    HashMap<String, String> postDataParams) {
        TxtLog.appendDebug("VoteTools:performPostCall called");
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            setUserAgent(conn);
            
            cm.setCookies(conn);

            Kbmg.setLast(context);

            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                TxtLog.append("unknown code: "+responseCode);
            }
        } catch (Exception e) {
            TxtLog.append("Error:" + e.toString());
           // TxtLog.appendErr(e.toString());
        }

        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {

        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    private List<HttpCookie> kbmgValues(boolean... kbmgFormat)
    {
        List<HttpCookie> kbmg = new ArrayList<>();
        if (kbmgFormat.length > 0 && kbmgFormat[0])
        {
            kbmg.add(new HttpCookie("kbmg", Kbmg.getId(context)));
        }
        else {
            kbmg.add(new HttpCookie("kbmg_id", Kbmg.getId(context)));
            kbmg.add(new HttpCookie("kbmg_last", String.valueOf(Kbmg.getLast(context))));
        }
        return kbmg;
    }

    private void pingKbmg(String kbmgId, String kbmgHash, String kbmgRef, String kbmgPath)
    {
        String kbmgUrl = "http://id.kbmg.cz/tracker.php?kbmg="+kbmgId+"&hash="+kbmgHash+"&ref="+kbmgRef+"&path="+kbmgPath;

        //ping it
        try {
            OpenHttpConnection(kbmgUrl, false, voteUrl,true);
           // TxtLog.append("pinged kbmg");
        }
        catch (Exception e)
        {
            TxtLog.append("failed to ping kbmg, err:" + e.toString());
        }

    }

    /*
    private void logHeaders(URLConnection conn)
    {
        Map<String, List<String>> headerFields = conn.getHeaderFields();

        Set<String> headerFieldsSet = headerFields.keySet();

        Iterator<String> hearerFieldsIter = headerFieldsSet.iterator();



        while (hearerFieldsIter.hasNext()) {



            String headerFieldKey = hearerFieldsIter.next();

            TxtLog.append("header"+headerFieldKey);


                List<String> headerFieldValue = headerFields.get(headerFieldKey);

                for (String headerValue : headerFieldValue) {
                    TxtLog.append("value"+headerValue);



            }

        }

    }
    */
}
