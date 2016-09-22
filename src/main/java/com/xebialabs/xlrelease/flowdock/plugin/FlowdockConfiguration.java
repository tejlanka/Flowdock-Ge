/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package com.xebialabs.xlrelease.flowdock.plugin;

/**
 * Created by jdewinne on 2/5/15.
 */
public class FlowdockConfiguration {

    private String apiUrl = "";
    private String apiKey = "";
    private String proxyHost = "";
    private int proxyPort = 0;
    private String templateName = "";
    private String flowToken = "";
    private String orgParamName="";
    private Boolean enabled = Boolean.FALSE;

    // Add the API token as the global config

    //organization/flowparameterized name for the flow


    public FlowdockConfiguration(String apiUrl, String apiKey, Boolean enabled, String proxyHost, int proxyPort, String templateName, String flowToken, String orgParamName) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.enabled = enabled;
        this.templateName = templateName;
        this.flowToken = flowToken;
        this.orgParamName = orgParamName;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public String getTemplateName() {
        return templateName;
    }

    public String getFlowToken() {
        return flowToken;
    }

    public String getOrgParamName() {
        return orgParamName;
    }

}
