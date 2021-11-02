package com.congta.spring.boot.web.controller;

import com.congta.spring.boot.shared.ex.OpCode;
import com.congta.spring.boot.shared.ex.OpFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Fucheng
 * created in 2021/1/23
 */
@RestControllerAdvice
public class Advice {

    private static Logger logger = LoggerFactory.getLogger(Advice.class);

    /**
     * 把值绑定到Model中，使全局@RequestMapping可以获取到该值
     * @param model request model
     */
    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("provider", "congta");
    }

    /**
     * 全局异常捕捉处理
     * @param ex 异常
     * @return { code: 100, message: '' }
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResultModel<String> errorHandler(Exception ex) {
        if (ex instanceof OpFailureException) {
            OpFailureException e = (OpFailureException) ex;
            if (!e.isPrinted()) {
                logger.error("meet exception.", ex);
            }
            return ResultModel.failure(e.getCode(), e.getMessage());
        }
        logger.error("meet exception.", ex);
        return ResultModel.failure(OpCode.SYSTEM_ERROR.getValue(),
                OpCode.SYSTEM_ERROR.getVerbose() + "，请提交反馈");
    }
}
