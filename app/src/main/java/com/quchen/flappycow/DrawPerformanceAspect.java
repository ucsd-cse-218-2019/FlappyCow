package com.quchen.flappycow;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.HashMap;
import java.util.Map;

@Aspect
public class DrawPerformanceAspect {
    private static final int MILLISECONDS_TO_RESET = 1000;
    private static final String POINTCUT = "execution(* *.draw(..))";

    private long lastReset = System.currentTimeMillis();
    private Map<String, Integer> drawCount = new HashMap<>();

    @After(POINTCUT)
    public void drawAdvice(JoinPoint joinPoint) {
        this.checkResetAndPrint();

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();

        int hashcode = 0;
        Object target = joinPoint.getTarget();
        if (target != null) {
            hashcode = target.hashCode();
        }

//        System.out.println(String.format("=========== drawing class: %s, method: %s, hashcode: %s", className, methodName, hashcode));

        String key = className + ":" + hashcode;
        incrementCount(key);
    }

    private void incrementCount(String key) {
        Integer count = drawCount.get(key);
        if (count == null) {
            drawCount.put(key, 1);
        } else {
            drawCount.put(key, count + 1);
        }
    }

    private void checkResetAndPrint() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - this.lastReset > MILLISECONDS_TO_RESET) {
            System.out.println(" =============== draws per " + MILLISECONDS_TO_RESET + "ms: =================");
            System.out.println(this.drawCount);
            this.drawCount.clear();
            this.lastReset = currentTime;
        }
    }
}
