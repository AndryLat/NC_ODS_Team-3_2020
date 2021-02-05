package com.netcracker.odstc.logviewer.serverconnection.managers;

import com.netcracker.odstc.logviewer.dao.EAVObjectDAO;
import com.netcracker.odstc.logviewer.models.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Component
@EnableScheduling
public class JobScheduleManager implements SchedulingConfigurer {
    private final Logger logger = LogManager.getLogger(JobScheduleManager.class.getName());

    private final ScheduledExecutorService scheduledExecutorService;

    private final ServerManager serverManager;
    private final EAVObjectDAO eavObjectDAO;

    public JobScheduleManager(ServerManager serverManager, EAVObjectDAO eavObjectDAO) {
        this.serverManager = serverManager;
        this.eavObjectDAO = eavObjectDAO;

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        logger.info("Starting job runner");
        Config configInstance = eavObjectDAO.getObjectById(BigInteger.ZERO, Config.class);
        Config.setInstance(configInstance);

        scheduledTaskRegistrar.setScheduler(scheduledExecutorService);
        logger.info("Start job creating");
        logger.info("Starting Poll job");
        scheduledTaskRegistrar.addTriggerTask(serverManager::getLogsFromAllServers, triggerContext -> {
            Calendar nextExecutionTime = new GregorianCalendar();
            Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
            nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
            nextExecutionTime.add(Calendar.MILLISECOND, (int) configInstance.getChangesPollingPeriod());
            return nextExecutionTime.getTime();
        });
        logger.info("Poll job Started");

        logger.info("Starting Activity validation job");
        scheduledTaskRegistrar.addTriggerTask(serverManager::revalidateServers, triggerContext -> {
            Calendar nextExecutionTime = new GregorianCalendar();
            Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
            nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
            nextExecutionTime.add(Calendar.MILLISECOND, (int) configInstance.getActivityPollingPeriod());
            return nextExecutionTime.getTime();
        });
        logger.info("Activity validation job started");
        logger.info("Jobs created");
    }
}
