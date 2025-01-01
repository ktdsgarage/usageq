// com.ktds.krater.query.aspect.RepositoryMetricsAspect
package com.ktds.krater.query.aspect;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RepositoryMetricsAspect {
    private final MeterRegistry meterRegistry;

    @Around("execution(* com.ktds.krater.*.repository.UsageRepository.findByUserId(..))")
    public Object measureQueryTime(ProceedingJoinPoint joinPoint) throws Throwable {
        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            Object result = joinPoint.proceed();
            meterRegistry.counter("db.query.total").increment();
            sample.stop(meterRegistry.timer("db.query.time"));
            return result;
        } catch (Exception e) {
            meterRegistry.counter("db.query.errors").increment();
            throw e;
        }
    }
}