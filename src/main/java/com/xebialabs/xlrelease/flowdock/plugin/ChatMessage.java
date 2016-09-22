/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package com.xebialabs.xlrelease.flowdock.plugin;

import com.xebialabs.deployit.plugin.api.reflect.Type;
import com.xebialabs.xlrelease.domain.Phase;
import com.xebialabs.xlrelease.domain.PlanItem;
import com.xebialabs.xlrelease.domain.Release;
import com.xebialabs.xlrelease.domain.Task;
import com.xebialabs.xlrelease.domain.status.TaskStatus;
import com.xebialabs.xlrelease.service.UserProfilesService;
import org.springframework.beans.factory.annotation.Autowired;
import com.xebialabs.xlrelease.configuration.UserProfile;
import org.springframework.stereotype.Service;


import java.io.UnsupportedEncodingException;

/**
 * Created by ankurtrivedi on 02/05/16.
 */

@Service
public class ChatMessage extends FlowdockMessage{

    public static final String XLRELEASE_RELEASE_MAIL = "xlrelease@flowdock.com";

    protected String externalUserName;
    protected String subject;
    protected String fromAddress;
    protected String source;
    protected String event;
    protected static UserProfilesService userProfilesService;

    private static ChatMessage singleton;

    @Autowired
    public ChatMessage(UserProfilesService userProfilesService) {
        this.externalUserName = "XLRelease";
        this.subject = "Message from XL Release";
        this.fromAddress = XLRELEASE_RELEASE_MAIL;
        this.source = "";
        this.userProfilesService = userProfilesService;
        register(this);
    }

    private static synchronized void register(ChatMessage chatMessage) {
        if (singleton == null) {
            singleton = chatMessage;
        }
    }

    public static ChatMessage getInstance() {
        return singleton;
    }

    public void setExternalUserName(String externalUserName) {

        this.externalUserName = externalUserName;
    }

    public void setSubject(String subject) {

        this.subject = subject;
    }

    public void setFromAddress(String fromAddress) {

        this.fromAddress = fromAddress;
    }

    public void setSource(String source) {

        this.source = source;
    }

    public void setEvent(String event) {

        this.event = event;
    }

    @Override
    public String asPostData() throws UnsupportedEncodingException {
        StringBuffer postData = new StringBuffer();
        postData.append("subject=").append(urlEncode(subject));
        postData.append("&content=").append(urlEncode(content));
        postData.append("&from_address=").append(urlEncode(fromAddress));
        postData.append("&source=").append(urlEncode(source));
        postData.append("&external_user_name=").append(urlEncode(externalUserName));
        postData.append("&tags=").append(urlEncode(tags));
        postData.append("&event=").append(urlEncode(event));
        return postData.toString();
    }

    public static ChatMessage fromAuditableDeployitEvent(PlanItem pi) {
        ChatMessage msg = getInstance();
        String content = "";

        UserProfile userProfile = userProfilesService.findByUsername(pi.getProperty("owner").toString());

        if(pi instanceof Release){
            content = "@team Release " + pi.getProperty("title") + " has status " + ((Release) pi).getStatus().value();
        }
        else if (pi instanceof Phase){
            content = "@team Phase " + pi.getProperty("title") + " has status " + ((Phase) pi).getStatus().value();

        }
        else if(pi instanceof Task) {

            Task task = (Task) pi;
            if (task.getStatus().equals(TaskStatus.IN_PROGRESS) && task.getTaskType().equals(Type.valueOf("xlrelease.Task"))) {


                content = "@" +userProfile.getFullName()+" Approval Pending for " + pi.getProperty("title");

                //content = "@"+pi.getProperty("owner")+" Approval Pending for " + pi.getProperty("title");


            }
            else {

                content = "@team Task " + pi.getProperty("title") +
                        " assigned to " + userProfile.getFullName() + " has status " + ((Task) pi).getStatus().value();
            }
        }


        msg.setContent(content);
        msg.setSubject("XL Release event");
        msg.setFromAddress(XLRELEASE_RELEASE_MAIL);
        msg.setSource("XL Release");
        msg.setTags("XL Release");
        msg.setEvent("message");

        return msg;
    }
}
