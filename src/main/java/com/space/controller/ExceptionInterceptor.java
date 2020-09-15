package com.space.controller;

import com.space.model.errors.IllegalIdException;
import com.space.model.errors.ShipNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ExceptionInterceptor {

    @ExceptionHandler(IllegalIdException.class)
    public ModelAndView handleError400(HttpServletRequest request, Exception e)   {
        ModelAndView mv = new ModelAndView("/400");
        return mv;
    }

    @ExceptionHandler(ShipNotFoundException.class)
    public ModelAndView handleError404(HttpServletRequest request, Exception e)   {
        ModelAndView mv = new ModelAndView("/400");
        return mv;
    }
}
