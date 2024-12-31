package com.trend.ozitre.config;

import com.trend.ozitre.service.RepeatingEnrollmentService;
import com.trend.ozitre.service.RepeatingExpenseService;
import com.trend.ozitre.service.RepeatingPackageService;
import com.trend.ozitre.util.AutowiringSpringBeanJobFactory;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class QuartzConfig {

    @Bean
    public JobDetail repeatingEnrollmentJobDetail() {
        return JobBuilder.newJob(RepeatingEnrollmentService.class)
                .withIdentity("repeatingEnrollmentJob")
                .storeDurably()
                .build();
    }

    @Bean
    public JobDetail repeatingPackageJobDetail() {
        return JobBuilder.newJob(RepeatingPackageService.class)
                .withIdentity("repeatingPackageJob")
                .storeDurably()
                .build();
    }

    @Bean
    public JobDetail repeatingExpenseJobDetail() {
        return JobBuilder.newJob(RepeatingExpenseService.class)
                .withIdentity("repeatingExpenseJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Scheduler scheduler(AutowiringSpringBeanJobFactory jobFactory) throws Exception {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setJobFactory(jobFactory);
        schedulerFactoryBean.setJobDetails(repeatingEnrollmentJobDetail(), repeatingPackageJobDetail(), repeatingExpenseJobDetail());
        schedulerFactoryBean.setTriggers(enrollmentCronTrigger(), packageCronTrigger(), expenseCronTrigger());
        schedulerFactoryBean.afterPropertiesSet();
        schedulerFactoryBean.getScheduler().start();
        return schedulerFactoryBean.getScheduler();
    }

    @Bean
    public AutowiringSpringBeanJobFactory jobFactory(ApplicationContext applicationContext) {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public CronTrigger enrollmentCronTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(repeatingEnrollmentJobDetail())
                .withIdentity("repeatingEnrollmentTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 00 07 * * ?"))
                .build();
    }

    @Bean
    public CronTrigger expenseCronTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(repeatingExpenseJobDetail())
                .withIdentity("repeatingExpenseTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 45 06 * * ?"))
                .build();
    }

    @Bean
    public CronTrigger packageCronTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(repeatingPackageJobDetail())
                .withIdentity("repeatingPackageTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 1/3 * ?"))
                .build();
    }
}
