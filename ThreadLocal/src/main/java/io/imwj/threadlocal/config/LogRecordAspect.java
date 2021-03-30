package io.imwj.threadlocal.config;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;

/**
 * @author langao_q
 * @since 2021-03-26 16:20
 */
@Slf4j
@Aspect
@Configuration
public class LogRecordAspect {

    @Autowired
    private HttpServletRequest request;
    /**
     * 配置切入点表达式
     */
    @Pointcut("execution(* io.imwj.threadlocal.controller..*(..))")
    public void webData() {
    }

    @Around(value = "webData()")
    public Object aroundWebData(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long millis = System.currentTimeMillis();
        Object result = null;
        Exception ex = null;
        try {
            result = proceedingJoinPoint.proceed();
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            handleLog(proceedingJoinPoint, ex, result, millis);
        }
        return result;
    }

    /**
     * 处理日志
     */
    protected void handleLog(final JoinPoint joinPoint, final Exception e, final Object resultData, long millis) {
        try {
            //请求参数
            String params = JSONUtil.toJsonStr(request.getParameterMap());
            // 返回参数
            String resStr = JSONUtil.toJsonStr(resultData);
            //请求地址
            String url = request.getRequestURI();
            // 设置方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            // 设置请求方式
            String method = request.getMethod();
            long ms = System.currentTimeMillis() - millis;
            String info = "【" + method + "】,耗时:[" + ms + "],url:【" + url + "】," +
                    "method:[" + className + "." + methodName + "()],param:" + params + ",";
            //返回结果
            if (e != null) {
                info += "Exception:[" + e.getMessage() + "]";
                log.info(info);
            } else {
                info += "resultData:" + (resStr.length() > 600 ? "数据太长不打印" : resStr);
                log.info(info);
            }
        } catch (Exception exp) {
            // 记录本地异常日志
            log.error("异常信息:{}", exp.getMessage());
            exp.printStackTrace();
        }
    }
}