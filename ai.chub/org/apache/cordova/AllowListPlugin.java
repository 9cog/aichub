package org.apache.cordova;

import android.content.Context;
import androidx.webkit.ProxyConfig;
import org.xmlpull.v1.XmlPullParser;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class AllowListPlugin extends CordovaPlugin {
    protected static final String LOG_TAG = "CordovaAllowListPlugin";
    public static final String PLUGIN_NAME = "CordovaAllowListPlugin";
    private AllowList allowedIntents;
    private AllowList allowedNavigations;
    private AllowList allowedRequests;

    public AllowListPlugin() {
    }

    public AllowListPlugin(Context context) {
        this(new AllowList(), new AllowList(), null);
        new CustomConfigXmlParser().parse(context);
    }

    public AllowListPlugin(XmlPullParser xmlParser) {
        this(new AllowList(), new AllowList(), null);
        new CustomConfigXmlParser().parse(xmlParser);
    }

    public AllowListPlugin(AllowList allowedNavigations, AllowList allowedIntents, AllowList allowedRequests) {
        if (allowedRequests == null) {
            allowedRequests = new AllowList();
            allowedRequests.addAllowListEntry("file:///*", false);
            allowedRequests.addAllowListEntry("data:*", false);
        }
        this.allowedNavigations = allowedNavigations;
        this.allowedIntents = allowedIntents;
        this.allowedRequests = allowedRequests;
    }

    @Override // org.apache.cordova.CordovaPlugin
    public void pluginInitialize() {
        if (this.allowedNavigations == null) {
            this.allowedNavigations = new AllowList();
            this.allowedIntents = new AllowList();
            this.allowedRequests = new AllowList();
            new CustomConfigXmlParser().parse(this.webView.getContext());
        }
    }

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    private class CustomConfigXmlParser extends ConfigXmlParser {
        private CordovaPreferences prefs;

        @Override // org.apache.cordova.ConfigXmlParser
        public void handleEndTag(XmlPullParser xml) {
        }

        private CustomConfigXmlParser() {
            this.prefs = new CordovaPreferences();
        }

        @Override // org.apache.cordova.ConfigXmlParser
        public void handleStartTag(XmlPullParser xml) {
            String attributeValue;
            String name = xml.getName();
            boolean z = false;
            if (name.equals("content")) {
                AllowListPlugin.this.allowedNavigations.addAllowListEntry(xml.getAttributeValue(null, "src"), false);
            } else if (name.equals("allow-navigation")) {
                String attributeValue2 = xml.getAttributeValue(null, "href");
                if (ProxyConfig.MATCH_ALL_SCHEMES.equals(attributeValue2)) {
                    AllowListPlugin.this.allowedNavigations.addAllowListEntry("http://*/*", false);
                    AllowListPlugin.this.allowedNavigations.addAllowListEntry("https://*/*", false);
                    AllowListPlugin.this.allowedNavigations.addAllowListEntry("data:*", false);
                    return;
                }
                AllowListPlugin.this.allowedNavigations.addAllowListEntry(attributeValue2, false);
            } else if (name.equals("allow-intent")) {
                AllowListPlugin.this.allowedIntents.addAllowListEntry(xml.getAttributeValue(null, "href"), false);
            } else if (!name.equals("access") || (attributeValue = xml.getAttributeValue(null, "origin")) == null) {
            } else {
                if (ProxyConfig.MATCH_ALL_SCHEMES.equals(attributeValue)) {
                    AllowListPlugin.this.allowedRequests.addAllowListEntry("http://*/*", false);
                    AllowListPlugin.this.allowedRequests.addAllowListEntry("https://*/*", false);
                    return;
                }
                String attributeValue3 = xml.getAttributeValue(null, "subdomains");
                AllowList allowList = AllowListPlugin.this.allowedRequests;
                if (attributeValue3 != null && attributeValue3.compareToIgnoreCase("true") == 0) {
                    z = true;
                }
                allowList.addAllowListEntry(attributeValue, z);
            }
        }
    }

    @Override // org.apache.cordova.CordovaPlugin
    public Boolean shouldAllowNavigation(String url) {
        return this.allowedNavigations.isUrlAllowListed(url) ? true : null;
    }

    @Override // org.apache.cordova.CordovaPlugin
    public Boolean shouldAllowRequest(String url) {
        return (Boolean.TRUE.equals(shouldAllowNavigation(url)) || this.allowedRequests.isUrlAllowListed(url)) ? true : null;
    }

    @Override // org.apache.cordova.CordovaPlugin
    public Boolean shouldOpenExternalUrl(String url) {
        return this.allowedIntents.isUrlAllowListed(url) ? true : null;
    }

    public AllowList getAllowedNavigations() {
        return this.allowedNavigations;
    }

    public void setAllowedNavigations(AllowList allowedNavigations) {
        this.allowedNavigations = allowedNavigations;
    }

    public AllowList getAllowedIntents() {
        return this.allowedIntents;
    }

    public void setAllowedIntents(AllowList allowedIntents) {
        this.allowedIntents = allowedIntents;
    }

    public AllowList getAllowedRequests() {
        return this.allowedRequests;
    }

    public void setAllowedRequests(AllowList allowedRequests) {
        this.allowedRequests = allowedRequests;
    }
}
