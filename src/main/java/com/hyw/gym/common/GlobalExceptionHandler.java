package com.hyw.gym.common;
// @author 黄元威

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 全局异常处理：统一返回 Result 结构，不向客户端泄露堆栈
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // 参数缺失：@RequestParam 必填但没传 → 400
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public Result<Void> handleMissingParam(MissingServletRequestParameterException e) {
        log.warn("参数缺失: {}", e.getMessage());
        return Result.error(400, "参数缺失：" + e.getParameterName());
    }

    // 参数类型错误：如把 "abc" 传给 double 参数 → 400
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public Result<Void> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.warn("参数类型错误: {}", e.getMessage());
        return Result.error(400, "参数类型错误：" + e.getName());
    }

    // JSON body 解析失败：@RequestBody 传的不是合法 JSON → 400
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public Result<Void> handleNotReadable(HttpMessageNotReadableException e) {
        log.warn("请求体解析失败: {}", e.getMessage());
        return Result.error(400, "请求体格式错误");
    }

    // 业务运行时异常：返回异常信息（如"未登录""用户不存在"），不泄露堆栈
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public Result<Void> handleRuntime(RuntimeException e) {
        String msg = (e.getMessage() == null || e.getMessage().isEmpty())
                ? "请求处理失败" : e.getMessage();
        log.warn("业务异常: {}", msg);
        return Result.error(400, msg);
    }

    // 兜底：未预期异常 → 500，不向客户端泄露堆栈
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(500, "系统繁忙，请稍后重试");
    }
}
