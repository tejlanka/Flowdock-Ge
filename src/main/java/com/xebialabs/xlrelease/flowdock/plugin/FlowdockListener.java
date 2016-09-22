/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package com.xebialabs.xlrelease.flowdock.plugin;

import com.xebialabs.deployit.engine.spi.event.AuditableDeployitEvent;
import com.xebialabs.deployit.engine.spi.event.CiBaseEvent;
import com.xebialabs.deployit.engine.spi.event.DeployitEventListener;
import com.xebialabs.xlrelease.domain.Phase;
import com.xebialabs.xlrelease.domain.Release;
import com.xebialabs.deployit.plugin.api.reflect.Type;
import com.xebialabs.deployit.plugin.api.udm.ConfigurationItem;
import com.xebialabs.xlrelease.api.XLReleaseServiceHolder;
import com.xebialabs.xlrelease.domain.ActivityLogEntry;
import com.xebialabs.xlrelease.domain.Task;
import com.xebialabs.xlrelease.domain.status.PhaseStatus;
import com.xebialabs.xlrelease.domain.status.ReleaseStatus;
import com.xebialabs.xlrelease.domain.status.TaskStatus;
import com.xebialabs.xlrelease.flowdock.plugin.exception.FlowdockException;
import com.xebialabs.xlrelease.flowdock.plugin.exception.FlowdockNotConfiguredException;
import nl.javadude.t2bus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.PathParam;
import java.util.List;


/**
 * Created by jdewinne on 2/4/15.
 */
@DeployitEventListener
public class FlowdockListener {

    Logger logger = LoggerFactory.getLogger(FlowdockListener.class);

    public FlowdockListener() {
    }

    @Subscribe
    public void sendUpdateToFlowdock(AuditableDeployitEvent event) {
        FlowdockRepositoryService flowdockRepositoryService = new FlowdockRepositoryService();
        logger.debug("Flowdock: Listener triggered Successfully");
        logger.debug("Flowdock: event.getClass() :"+ event.getClass());
        try {
            if (flowdockRepositoryService.isFlowdockEnabled()) {
                if (event instanceof CiBaseEvent) {
                    CiBaseEvent ciEvent = (CiBaseEvent) event;

                    for (ConfigurationItem ci : ciEvent.getCis()) {
                        logger.debug("Flowdock: Event type is "+ ci.getType());

                        if (ci.getType().equals(Type.valueOf("xlrelease.Release"))) {

                            logger.debug("Flowdock: Found an Release log entry");
                            Release releaseObject = (Release) ci;
                            logger.debug("Flowdock: Release ID is " + releaseObject.getId());

                            //Get the release name from the id
                            String[] output = releaseObject.getId().split("/");

                            logger.debug("Release Id: " + output[1]);

                            //Get the release from the release name
                            Release release = XLReleaseServiceHolder.getReleaseApi().getRelease("Applications/"+output[1]);

                            //Get title of template

                            String templateTitle = XLReleaseServiceHolder.getTemplateApi().getTemplate(release.getOriginTemplateId()).getTitle();

                            ReleaseStatus releaseStatus = release.getStatus();

                            // Get flowdock properties
                            List<FlowdockConfiguration> flowdockConfigurations = flowdockRepositoryService.getFlowdockConfigurations();

                            for (FlowdockConfiguration flowdockConfiguration : flowdockConfigurations) {
                                if (flowdockConfiguration.isEnabled() && flowdockConfiguration.getTemplateName().equalsIgnoreCase((templateTitle))) {
                                    // Send message to flowdock
                                    logger.debug("Flowdock: Ready to send Release Team Inbox Notification");
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

                                    logger.info("Flowdock: Release Team Inbox notification sent successfully");


                                }
                            }
                        } else if (ci.getType().equals(Type.valueOf("xlrelease.Phase"))) {

                            logger.debug("Flowdock: Found a Phase entry");
                            Phase phaseObject = (Phase) ci;
                            logger.debug("Flowdock: Phase entry is " + phaseObject.getId());

                            //Get the release name from the id
                            String[] output = phaseObject.getId().split("/");

                            logger.debug("Phase Block: Release Id: " + output[1]);

                            //Get the release from the release name
                            Phase phase = XLReleaseServiceHolder.getPhaseApi().getPhase(phaseObject.getId());

                            PhaseStatus phaseStatus = phase.getStatus();

                            //Get title of template

                            String templateTitle = XLReleaseServiceHolder.getTemplateApi().getTemplate(XLReleaseServiceHolder.getReleaseApi().getRelease("Applications/"+output[1]).getOriginTemplateId()).getTitle();

                            // Get flowdock properties
                            List<FlowdockConfiguration> flowdockConfigurations = flowdockRepositoryService.getFlowdockConfigurations();

                            for (FlowdockConfiguration flowdockConfiguration : flowdockConfigurations) {
                                if (flowdockConfiguration.isEnabled() && flowdockConfiguration.getTemplateName().equalsIgnoreCase((templateTitle))) {
                                    // Send message to flowdock
                                    logger.debug("Flowdock: Ready to send Phase Team Inbox Notification");
                                    FlowdockApi api = new FlowdockApi(flowdockConfiguration);
                                    TeamInboxMessage msg = TeamInboxMessage.fromAuditableDeployitEvent(phase);
                                    ChatMessage chatMessage = ChatMessage.fromAuditableDeployitEvent(phase);

                                    if (phaseStatus.hasBeenStarted() && phaseStatus.isActive()) {
                                        if (phaseStatus.equals(PhaseStatus.FAILED)) {

                                            //api.pushTeamInboxMessage(msg);
                                            api.pushChatMessage(chatMessage);

                                        }
                                        else if(phaseStatus.equals(PhaseStatus.PLANNED)){
                                            api.pushTeamInboxMessage(msg);
                                        }

                                        else if (phaseStatus.equals(PhaseStatus.COMPLETED)) {
                                            api.pushTeamInboxMessage(msg);

                                        } else if (phaseStatus.equals(PhaseStatus.ABORTED)) {
                                            api.pushTeamInboxMessage(msg);

                                        }

                                    }

                                }
                            }

                        } else if (ci.getType().equals(Type.valueOf("xlrelease.Task"))) {

                            logger.debug("Flowdock: Found a Task entry");
                            Task taskObject = (Task) ci;
                            logger.debug("Flowdock: Task entry is " + taskObject.getId());

                            //Get the release name from the id
                            String[] output = taskObject.getId().split("/");

                            logger.debug("Task Block: Release Id: " + output[1]);

                            //Get the release from the release name
                            Task task = XLReleaseServiceHolder.getTaskApi().getTask(taskObject.getId());

                            TaskStatus taskStatus = task.getStatus();

                            //Get title of template

                            String templateTitle = XLReleaseServiceHolder.getTemplateApi().getTemplate(XLReleaseServiceHolder.getReleaseApi().getRelease("Applications/"+output[1]).getOriginTemplateId()).getTitle();

                            // Get flowdock properties
                            List<FlowdockConfiguration> flowdockConfigurations = flowdockRepositoryService.getFlowdockConfigurations();

                            for (FlowdockConfiguration flowdockConfiguration : flowdockConfigurations) {
                                if (flowdockConfiguration.isEnabled() && flowdockConfiguration.getTemplateName().equalsIgnoreCase((templateTitle))) {
                                    // Send message to flowdock
                                    logger.debug("Flowdock: Ready to send Task Team Inbox Notification");
                                    FlowdockApi api = new FlowdockApi(flowdockConfiguration);
                                    TeamInboxMessage msg = TeamInboxMessage.fromAuditableDeployitEvent(task);
                                    ChatMessage chatMessage = ChatMessage.fromAuditableDeployitEvent(task);

                                    if (taskStatus.hasBeenStarted() && taskStatus.isActive()) {
                                        if (taskStatus.equals(TaskStatus.FAILED)) {

                                            api.pushTeamInboxMessage(msg);
                                            api.pushChatMessage(chatMessage);

                                        } else if (taskStatus.equals(TaskStatus.IN_PROGRESS)){

                                            api.pushChatMessage(chatMessage);

                                        }
                                        else if (taskStatus.equals(TaskStatus.COMPLETED)) {
                                            api.pushChatMessage(chatMessage);
                                            api.pushTeamInboxMessage(msg);

                                            if (task.getTaskType().equals(Type.valueOf("xldeploy.DeployTask"))){

                                                api.pushChatMessage(chatMessage);
                                            }

                                        } else if (taskStatus.equals(TaskStatus.ABORTED)) {
                                            api.pushChatMessage(chatMessage);

                                        }

                                    }

                                }
                            }
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
