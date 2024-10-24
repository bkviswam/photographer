package com.intuit.craft.photographer.aspects;

import com.intuit.craft.photographer.util.DatabaseTimeTracker;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DatabaseLoggingAspect {

    @Around("execution(* com.intuit.craft.photographer.repository..*(..))")
    public Object logDatabaseTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();  // Execute the database call

        long timeTaken = System.currentTimeMillis() - startTime;

        // Accumulate the database time in ThreadLocal storage
        DatabaseTimeTracker.addDatabaseTime(timeTaken);

        return result;
    }
}