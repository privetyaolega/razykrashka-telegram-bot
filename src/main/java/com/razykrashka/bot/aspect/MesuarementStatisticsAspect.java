package com.razykrashka.bot.aspect;


import com.razykrashka.bot.ui.helpers.UpdateHelper;
import com.razykrashka.bot.ui.helpers.sender.MessageManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManagerFactory;

@Aspect
@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MesuarementStatisticsAspect {

    final UpdateHelper updateHelper;
    final MessageManager messageManager;

    private SessionFactory hibernateFactory;

    public MesuarementStatisticsAspect(@Lazy UpdateHelper updateHelper, @Lazy MessageManager messageManager,
                                       EntityManagerFactory factory) {
        this.updateHelper = updateHelper;
        this.messageManager = messageManager;

        if (factory.unwrap(SessionFactory.class) == null) {
            throw new NullPointerException("factory is not a hibernate factory");
        }
        this.hibernateFactory = factory.unwrap(SessionFactory.class);
    }

    @Pointcut("execution(public void com.razykrashka.bot.service.BotExecutor.execute(*))))")
    public void updateReceivedPointcut() {
    }

    @Around("updateReceivedPointcut()")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed(joinPoint.getArgs());
        long executionTime = System.currentTimeMillis() - start;

        log.info("Measurement stat: time execution: {}, query statements amount: {}", executionTime,
                hibernateFactory.getStatistics().getQueryExecutionCount());
        return proceed;
    }
}