package taptwitterstreamj.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Slf4j
@Aspect
public class LogAspect {

    @Pointcut("@annotation(taptwitterstreamj.aspects.Log)")
    public void methodsToLog() {
    }

    @Around("methodsToLog()")
    public void logMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info(String.format("Executing method=%s", joinPoint.getSignature()));
        joinPoint.proceed();
        log.info(String.format("Finished running method=%s", joinPoint.getSignature()));
    }

}
