/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package com.xebialabs.xlrelease.flowdock.plugin;

import com.xebialabs.deployit.engine.spi.event.DeployitEventListener;
import com.xebialabs.xlrelease.domain.status.ReleaseStatus;
import com.xebialabs.xlrelease.flowdock.plugin.exception.FlowdockException;
import com.xebialabs.xlrelease.flowdock.plugin.exception.FlowdockNotConfiguredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.xebialabs.xlrelease.api.XLReleaseServiceHolder;
import com.xebialabs.xlrelease.api.v1.TemplateApi;
import com.xebialabs.xlrelease.api.v1.forms.StartRelease;
import com.xebialabs.xlrelease.domain.Release;
import com.xebialabs.xlrelease.domain.events.ReleaseExecutedEvent;
import nl.javadude.t2bus.Subscribe;

import java.util.List;

/**
 * Created by ankurtrivedi on 02/05/16.
 */
@DeployitEventListener
public class FlowdockReleaseListener {
    Logger logger = LoggerFactory.getLogger(FlowdockListener.class);

    public FlowdockReleaseListener() {
    }

    @Subscribe
    public void receiveReleaseEvent(ReleaseExecutedEvent event) {
        Release release = XLReleaseServiceHolder.getReleaseApi().getRelease(event.getReleaseId());
        FlowdockRepositoryService flowdockRepositoryService = new FlowdockRepositoryService();

        ReleaseStatus releaseStatus = release.getStatus();
        String templateName = XLReleaseServiceHolder.getTemplateApi().getTemplate(release.getOriginTemplateId()).getTitle();

        try {

            // Get flowdock properties
            List<FlowdockConfiguration> flowdockConfigurations = flowdockRepositoryService.getFlowdockConfigurations();

            for (FlowdockConfiguration flowdockConfiguration : flowdockConfigurations) {
                if (flowdockConfiguration.isEnabled() && flowdockConfiguration.getTemplateName().equalsIgnoreCase(templateName)) {
                    // Send message to flowdock
                    logger.debug("FlowdockReleaseListener: Ready to send Team Inbox Notification");
                    FlowdockApi api = new FlowdockApi(flowdockConfiguration);
                    TeamInboxMessage msg = TeamInboxMessage.fromAuditableDeployitEvent(release);
                    ChatMessage chatMessage = ChatMessage.fromAuditableDeployitEvent(release);


                    if (releaseStatus.hasBeenStarted() && releaseStatus.isActive()) {
                        if (releaseStatus.equals(ReleaseStatus.FAILED)) {

                            api.pushTeamInboxMessage(msg);
                            api.pushChatMessage(chatMessage);

                        } else if (releaseStatus.equals(ReleaseStatus.COMPLETED)) {
                            api.pushTeamInboxMessage(msg);

                        } else if (releaseStatus.equals(ReleaseStatus.ABORTED)) {
                            api.pushTeamInboxMessage(msg);

                        }

                    }
                }
            }
            } catch (FlowdockNotConfiguredException e) {
            // Do nothing, as Flowdock is not yet configured.
        } catch (FlowdockException e) {
            e.printStackTrace();
        }


        }

}
