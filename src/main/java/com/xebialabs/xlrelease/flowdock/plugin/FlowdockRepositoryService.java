/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package com.xebialabs.xlrelease.flowdock.plugin;

import com.xebialabs.deployit.plugin.api.reflect.Type;
import com.xebialabs.deployit.plugin.api.udm.ConfigurationItem;
import com.xebialabs.xlrelease.api.XLReleaseServiceHolder;
import com.xebialabs.deployit.repository.SearchParameters;
import com.xebialabs.xlrelease.flowdock.plugin.exception.FlowdockNotConfiguredException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jdewinne on 2/5/15.
 */
public class FlowdockRepositoryService {

    private List<FlowdockConfiguration> flowdockConfigurations;

    public List<FlowdockConfiguration> getFlowdockConfigurations() throws FlowdockNotConfiguredException {
        if (flowdockConfigurations == null) {
            setFlowdockConfigurations();
        }

        return flowdockConfigurations;
    }

    public Boolean isFlowdockEnabled() throws FlowdockNotConfiguredException {
        if (flowdockConfigurations == null) {
            setFlowdockConfigurations();
        }

        for (FlowdockConfiguration flowdockConfiguration : flowdockConfigurations) {
            if (flowdockConfiguration.isEnabled()) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    private void setFlowdockConfigurations() throws FlowdockNotConfiguredException {
        // Get flowdock properties
        SearchParameters parameters = new SearchParameters().setType(Type.valueOf("flowdock.configuration"));
        List<ConfigurationItem> query = XLReleaseServiceHolder.getRepositoryService().listEntities(parameters);
        if (query.size() > 0) {
            flowdockConfigurations = new ArrayList<FlowdockConfiguration>();
            for (ConfigurationItem read : query) {
               // flowdockConfigurations.add(new FlowdockConfiguration((String) read.getProperty("apiUrl"), (String) read.getProperty("apiKey"), (Boolean) read.getProperty("enabled"),(String) read.getProperty("proxyHost"),(Integer) read.getProperty("proxyPort"),(String) read.getProperty("templateName"),(String) read.getProperty("flowToken"),(String) read.getProperty("orgParamName")));
               flowdockConfigurations.add(new FlowdockConfiguration((String) read.getProperty("apiUrl"), (String) read.getProperty("apiKey"), (Boolean) read.getProperty("enabled"),"",0,(String) read.getProperty("templateName"),(String) read.getProperty("flowToken"),(String) read.getProperty("orgParamName")));
            }
        } else {
            throw new FlowdockNotConfiguredException();
        }
    }


}
