package ru.webbyskytracker.usersservice.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Component
@Aspect
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(public * ru.webbyskytracker.usersservice.controller.*.*(..))")
    public void controllerLog(){}

    @Pointcut("execution(public * ru.webbyskytracker.usersservice.service.*.*(..))")
    public void serviceLog(){}

    //Срабатывает перед вызовами всех контроллеров
    @Before("controllerLog()")
    public void doBeforeController(JoinPoint joinPoint){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if(attributes != null){
            request = attributes.getRequest();
        }
        if(request != null){
            log.info("NEW REQUEST: IP: {}, URL: {}, HTTP_METHOD: {}, CONTROLLER_METHOD: {}.{}",
                    request.getRemoteAddr(),
                    request.getRequestURL().toString(),
                    request.getMethod(),
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName());
        }
    }

    //Срабатывает перед вызовами всех сервисов
    @Before("serviceLog()")
    public void doBeforeService(JoinPoint joinPoint){
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        Object[] args = joinPoint.getArgs();
        String argsString  = args.length > 0 ? Arrays.toString(args) : "METHOD HAS NO ARGUMENTS";

        log.info("RUN SERVICE: SERVICE_METHOD: {}.{}\nMETHOD ARGUMENTS: [{}],",
                className, methodName, argsString);
    }

    //Показывает в логах, что возвращается в ответе
    @AfterReturning(returning = "returnObject", pointcut = "controllerLog()")
    public void doAfterReturning(Object returnObject){
        log.info("Return value: {}", returnObject);
    }

    //Говорит об успешном завершении работы метода контроллера
    @After("controllerLog()")
    public void doAfter(JoinPoint joinPoint){
        log.info("Controller Method executed successfully: {}.{}.",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
    }

    //Данный метод, благодаря аннотации @Around, может перехватывать выполнение методов до их начала, во время и после
    @Around("controllerLog()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        //выполнение целевого метода
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;

        log.info("Execution method: {}.{}. Execution time: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                executionTime);

        return proceed;
    }

    //Говорит о выбросе исключений
    @AfterThrowing(throwing = "ex", pointcut = "controllerLog()")
    public void throwsException(JoinPoint joinPoint, Exception ex){
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        log.error("Exception in {}.{} with arguments {}. Exception message: {}",
                className, methodName, Arrays.toString(joinPoint.getArgs()), ex.getMessage());
    }

}
