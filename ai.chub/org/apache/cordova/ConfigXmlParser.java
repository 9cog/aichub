package org.apache.cordova;

import android.content.Context;
import androidx.core.app.NotificationCompat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;
import okhttp3.HttpUrl;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class ConfigXmlParser {
    private static String DEFAULT_HOSTNAME = "localhost";
    private static String SCHEME_HTTP = "http";
    private static String SCHEME_HTTPS = "https";
    private static String TAG = "ConfigXmlParser";
    private String contentSrc;
    private String launchUrl;
    private CordovaPreferences prefs = new CordovaPreferences();
    private ArrayList<PluginEntry> pluginEntries = new ArrayList<>(20);
    boolean insideFeature = false;
    String service = HttpUrl.FRAGMENT_ENCODE_SET;
    String pluginClass = HttpUrl.FRAGMENT_ENCODE_SET;
    String paramType = HttpUrl.FRAGMENT_ENCODE_SET;
    boolean onload = false;

    public CordovaPreferences getPreferences() {
        return this.prefs;
    }

    public ArrayList<PluginEntry> getPluginEntries() {
        return this.pluginEntries;
    }

    public String getLaunchUrl() {
        if (this.launchUrl == null) {
            setStartUrl(this.contentSrc);
        }
        return this.launchUrl;
    }

    public void parse(Context action) {
        int identifier = action.getResources().getIdentifier("config", "xml", action.getClass().getPackage().getName());
        if (identifier == 0 && (identifier = action.getResources().getIdentifier("config", "xml", action.getPackageName())) == 0) {
            LOG.e(TAG, "res/xml/config.xml is missing!");
            return;
        }
        this.pluginEntries.add(new PluginEntry(AllowListPlugin.PLUGIN_NAME, "org.apache.cordova.AllowListPlugin", true));
        parse(action.getResources().getXml(identifier));
    }

    public void parse(XmlPullParser xml) {
        int i = -1;
        while (i != 1) {
            if (i == 2) {
                handleStartTag(xml);
            } else if (i == 3) {
                handleEndTag(xml);
            }
            try {
                i = xml.next();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e2) {
                e2.printStackTrace();
            }
        }
    }

    public void handleStartTag(XmlPullParser xml) {
        String name = xml.getName();
        if (name.equals("feature")) {
            this.insideFeature = true;
            this.service = xml.getAttributeValue(null, "name");
        } else if (this.insideFeature && name.equals("param")) {
            String attributeValue = xml.getAttributeValue(null, "name");
            this.paramType = attributeValue;
            if (attributeValue.equals(NotificationCompat.CATEGORY_SERVICE)) {
                this.service = xml.getAttributeValue(null, "value");
            } else if (this.paramType.equals("package") || this.paramType.equals("android-package")) {
                this.pluginClass = xml.getAttributeValue(null, "value");
            } else if (this.paramType.equals("onload")) {
                this.onload = "true".equals(xml.getAttributeValue(null, "value"));
            }
        } else if (name.equals("preference")) {
            this.prefs.set(xml.getAttributeValue(null, "name").toLowerCase(Locale.ENGLISH), xml.getAttributeValue(null, "value"));
        } else if (name.equals("content")) {
            String attributeValue2 = xml.getAttributeValue(null, "src");
            if (attributeValue2 != null) {
                this.contentSrc = attributeValue2;
            } else {
                this.contentSrc = "index.html";
            }
        }
    }

    public void handleEndTag(XmlPullParser xml) {
        if (xml.getName().equals("feature")) {
            this.pluginEntries.add(new PluginEntry(this.service, this.pluginClass, this.onload));
            this.service = HttpUrl.FRAGMENT_ENCODE_SET;
            this.pluginClass = HttpUrl.FRAGMENT_ENCODE_SET;
            this.insideFeature = false;
            this.onload = false;
        }
    }

    private String getLaunchUrlPrefix() {
        if (this.prefs.getBoolean("AndroidInsecureFileModeEnabled", false)) {
            return "file:///android_asset/www/";
        }
        String lowerCase = this.prefs.getString("scheme", SCHEME_HTTPS).toLowerCase();
        String string = this.prefs.getString("hostname", DEFAULT_HOSTNAME);
        if (!lowerCase.contentEquals(SCHEME_HTTP) && !lowerCase.contentEquals(SCHEME_HTTPS)) {
            String str = TAG;
            LOG.d(str, "The provided scheme \"" + lowerCase + "\" is not valid. Defaulting to \"" + SCHEME_HTTPS + "\". (Valid Options=" + SCHEME_HTTP + "," + SCHEME_HTTPS + ")");
            lowerCase = SCHEME_HTTPS;
        }
        return lowerCase + "://" + string + '/';
    }

    private void setStartUrl(String src) {
        if (Pattern.compile("^[a-z-]+://").matcher(src).find()) {
            this.launchUrl = src;
            return;
        }
        String launchUrlPrefix = getLaunchUrlPrefix();
        if (src.charAt(0) == '/') {
            src = src.substring(1);
        }
        this.launchUrl = launchUrlPrefix + src;
    }
}
