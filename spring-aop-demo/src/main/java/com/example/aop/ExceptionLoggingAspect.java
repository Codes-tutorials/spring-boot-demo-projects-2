package com.example.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExceptionLoggingAspect {
  private static final Logger log = LoggerFactory.getLogger(ExceptionLoggingAspect.class);

  @AfterThrowing(pointcut = "execution(* com.example..*Service.*(..))", throwing = "ex")
  public void logServiceException(JoinPoint jp, Throwable ex) {
    log.error("{} threw {}", jp.getSignature(), ex.toString());
  }
}

