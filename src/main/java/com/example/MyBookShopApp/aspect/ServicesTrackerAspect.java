package com.example.MyBookShopApp.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ServicesTrackerAspect {

    @Pointcut(value = "@annotation(com.example.MyBookShopApp.aspect.annotations.ServiceProcessTrackable)")
    public void serviceProceedPointcut() {}

    @Around(value = "serviceProceedPointcut()")
    public Object serviceProceedAdvice(ProceedingJoinPoint proceedingJoinPoint) {
        Long startPoint = System.currentTimeMillis();
        Object result;
        try {
            result = proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        Long endPoint = System.currentTimeMillis();
        log.info(proceedingJoinPoint.toShortString() +
                " successfully finished in " +
                (endPoint - startPoint) + " ms.");
        return result;
    }
}
