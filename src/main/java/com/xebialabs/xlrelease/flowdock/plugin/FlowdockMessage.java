/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package com.xebialabs.xlrelease.flowdock.plugin;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by jdewinne on 2/5/15.
 */
public abstract class FlowdockMessage {
    protected String content;
    protected String tags;

    public void setContent(String content) {
        this.content = content;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public abstract String asPostData() throws UnsupportedEncodingException;

    protected String urlEncode(String data) throws UnsupportedEncodingException {
        return data == null ? "" : URLEncoder.encode(data, "UTF-8");
    }
}
