package databasePopulation.dailyPrice;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.TimeZone;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class ScheduleListener implements ServletContextListener {

    /**
     * Method to schedule job to add daily price of stock
     */
    public void contextInitialized(ServletContextEvent arg0) {
        try {
            String exp = "0 30 16 ? * *";
            String[] timeZones = {"IST","EST"};
            SchedulerFactory factory = new StdSchedulerFactory();
            Scheduler scheduler = factory.getScheduler();
            scheduler.start();
            JobDetail addDailyPriceJob = newJob(StockUpdate.class).build();

            Trigger addDailyPriceTrigger = newTrigger()
                    .startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule(exp).inTimeZone(TimeZone.getTimeZone(timeZones[1])))
                    .build();
            scheduler.scheduleJob(addDailyPriceJob,addDailyPriceTrigger);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void contextDestroyed(ServletContextEvent arg0) {
        //
    }
}

