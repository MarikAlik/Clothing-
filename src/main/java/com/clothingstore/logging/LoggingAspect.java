package com.clothingstore.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.clothingstore.controller..*(..))")
    public void logBefore(JoinPoint joinPoint) {
        if (logger.isInfoEnabled()) {
            logger.info("Вызов метода: {}", joinPoint.getSignature().toShortString());
        }
    }

    @AfterReturning(pointcut = "execution(* com.clothingstore.controller..*(..))",
            returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        if (logger.isInfoEnabled()) {
            String methodName = joinPoint.getSignature().getName();
            logger.info("Метод {} завершился успешно. Результат: {}", methodName, result);
        }
    }

    @AfterThrowing(pointcut = "execution(* com.clothingstore..*(..))", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        if (logger.isErrorEnabled()) {
            String methodName = joinPoint.getSignature().getName();
            logger.error("Ошибка в методе {}: {}", methodName, ex.toString(), ex);
        }
    }
}
