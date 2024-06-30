package com.ukpatel.expense.tracker.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Before("execution(* com.ukpatel.expense.tracker..*Controller.*(..))")
    public void logBeforeControllerMethods(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        log.info("{}.{} starts...", className, methodName);
    }

    @After("execution(* com.ukpatel.expense.tracker..*Controller.*(..))")
    public void logAfterControllerMethods(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        log.info("{}.{} ends...", className, methodName);
    }

    @Before("execution(* com.ukpatel.expense.tracker..*Service.*(..))")
    public void logBeforeServiceMethods(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        log.info("{}.{} starts...", className, methodName);
    }

    @After("execution(* com.ukpatel.expense.tracker..*Service.*(..))")
    public void logAfterServiceMethods(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        log.info("{}.{} ends...", className, methodName);
    }

    @AfterThrowing(value = "execution(* com.ukpatel.expense.tracker..*Service.*(..))", throwing = "exception")
    public void logAfterThrowingServiceMethods(Exception exception) {
        StackTraceElement[] stackTraceElements = exception.getStackTrace();

        // Check if stack trace elements are present
        if (stackTraceElements.length > 0) {
            // Retrieve the first element (which corresponds to where the exception was thrown)
            StackTraceElement stackTraceElement = stackTraceElements[0];
            // Print the line number, class name, and method name
            log.error("Exception thrown in main service method at line " + stackTraceElement.getLineNumber()
                    + " of class " + stackTraceElement.getClassName() + ", method " + stackTraceElement.getMethodName());
        } else {
            // If no stack trace elements are found, print a generic message
            log.error("Exception thrown in main service method, details unavailable");
        }
    }
}
