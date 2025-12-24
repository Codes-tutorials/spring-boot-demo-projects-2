package com.example.crud.aopdemo;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
  private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

  @Around("execution(* com.example..*Controller.*(..)) || execution(* com.example..*Service.*(..))")
  public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
    long start = System.currentTimeMillis();
    try {
      Object result = pjp.proceed();
      long elapsed = System.currentTimeMillis() - start;
      log.info("{} took {} ms", pjp.getSignature(), elapsed);
      return result;
    } catch (Throwable t) {
      long elapsed = System.currentTimeMillis() - start;
      log.error("{} failed after {} ms: {}", pjp.getSignature(), elapsed, t.getMessage());
      throw t;
    }
  }
}

