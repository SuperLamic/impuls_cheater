package eu.skylam.impulscheater;

import android.text.TextUtils;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class CookieManager {

    private static java.net.CookieManager msCookieManager;

    static final String COOKIES_HEADER = "Set-Cookie";


    public CookieManager() {

        msCookieManager = new java.net.CookieManager();

    }


    public void storeCookies(URLConnection conn) throws IOException {
        Map<String, List<String>> headerFields = conn.getHeaderFields();
        List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

        if(cookiesHeader != null)
        {
            for (String cookie : cookiesHeader)
            {
                msCookieManager.getCookieStore().add(null,HttpCookie.parse(cookie).get(0));
            }
        }

    }

    public void setCookies(URLConnection conn) throws IOException {
        if(msCookieManager.getCookieStore().getCookies().size() > 0)
        {
            //While joining the Cookies, use ',' or ';' as needed. Most of the server are using ';'
            conn.setRequestProperty("Cookie",
                    TextUtils.join(";", msCookieManager.getCookieStore().getCookies()));
        }
    }
    public void addCookies(URLConnection conn, List<HttpCookie> cookieList)
    {
        for (HttpCookie cookie : cookieList)
        {
            msCookieManager.getCookieStore().add(null,cookie);
        }
    }
}

