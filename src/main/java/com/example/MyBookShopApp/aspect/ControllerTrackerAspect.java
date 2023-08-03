package com.example.MyBookShopApp.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Aspect
@Component
public class ControllerTrackerAspect {

    @Around(value = "within(com.example.MyBookShopApp.controllers.* && !com.example.MyBookShopApp.controllers.GlobalControllerAdvice) && !@annotation(com.example.MyBookShopApp.aspect.annotations.NoLogging)")
    public Object controllerAccessProceedAdvice(ProceedingJoinPoint proceedingJoinPoint) {
        Date startPoint = new Date();
        log.info("Accessing: " + proceedingJoinPoint.toShortString() + " started at " + startPoint);
        Object result;
        try {
            result = proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        Date endPoint = new Date();
        log.info(proceedingJoinPoint.toShortString() + " successfully processed in " +
                (endPoint.getTime() - startPoint.getTime()) + " ms at " + endPoint);
        return result;
    }

    @Before(value = "@annotation(com.example.MyBookShopApp.aspect.annotations.ControllerParamsCatch)")
    public void controllerParamsAdvice(JoinPoint joinPoint) {
        List<String> params = new ArrayList<>();
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof Model || arg == null) {
                continue;
            }
            params.add(arg.toString());
        }
        log.info(joinPoint.getSourceLocation().getWithinType().getSimpleName() + " got params " + params);
    }

    @Pointcut(value = "@annotation(com.example.MyBookShopApp.aspect.annotations.ControllerResponseCatch)")
    public void controllerResponsePointcut() {
    }

    @AfterReturning(value = "controllerResponsePointcut()", returning = "response")
    public void controllerResponseAdvice(JoinPoint joinPoint, Object response) {
        log.info(joinPoint.getSourceLocation().getWithinType().getSimpleName() + " returned ResponseBody: " + response);
    }
}
