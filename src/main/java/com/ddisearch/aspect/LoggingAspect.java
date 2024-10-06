package com.ddisearch.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // 使用ConcurrentHashMap来存储每个方法的调用次数
    private final ConcurrentHashMap<String, AtomicInteger> methodCallCount = new ConcurrentHashMap<>();

    @Around("execution(* com.ddisearch.controller..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object proceed = joinPoint.proceed(); // 继续执行原方法
        long executionTime = System.currentTimeMillis() - startTime;

        String methodName = joinPoint.getSignature().toShortString();

        // 更新方法调用次数
        methodCallCount.putIfAbsent(methodName, new AtomicInteger(0));
        int count = methodCallCount.get(methodName).incrementAndGet();

        // 记录执行时间和次数
        logger.info("{} executed in {} ms, called {} times.", methodName, executionTime, count);

        return proceed;
    }
}