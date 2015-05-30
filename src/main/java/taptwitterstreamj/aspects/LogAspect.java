package taptwitterstreamj.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import javax.inject.Named;

@Slf4j
@Aspect
@Named
public class LogAspect {

    @Pointcut("@annotation(taptwitterstreamj.aspects.Log)")
    public void methodsToLog() {
    }

    @Around("methodsToLog()")
    public Object logMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info(String.format("Executing method=%s", joinPoint.getSignature()));
        log.info(String.format("With parameters=%s", joinPoint.getArgs()));
        Object value = joinPoint.proceed();
        log.info(String.format("Finished running method=%s", joinPoint.getSignature()));
        return value;
    }

}
