package me.synology.hajubal.coins.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.controller.dto.ResponseEntityDto;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@ControllerAdvice
public class CommonControllerAdvice {
    private final MessageSource messageSource;

//    /**
//     * 관리가 가능한 오류가 발생한 경우 오류 응답
//     *
//     * @param model
//     * @param exception
//     * @param request
//     * @param response
//     * @param locale
//     * @return
//     */
//    @ExceptionHandler(value = IniHubHomeException.class)
//    @ResponseBody
//    public Object handleIniHubException(Model model, IniHubHomeException exception
//            , HttpServletRequest request, HttpServletResponse response, Locale locale) {
//
//        log.info(exception.getMessage(), exception);
//
//        if (request.getHeader("accept").contains("application/json")){
//            return ResponseEntityDto.failResponse(Optional.ofNullable(messageSource.getMessage(exception.getMessage(), null, HomeErrorCode.SERVER_ERROR.message(), Locale.getDefault())));
//        } else {
//            switch (exception.getStatus()) {
//                case NOT_FOUND:
//                    return new ModelAndView("error/404");
//                case BAD_REQUEST:
//                    return new ModelAndView("error/400");
//                case INTERNAL_SERVER_ERROR:
//                    return new ModelAndView("error/500");
//                case FORBIDDEN:
//                    return new ModelAndView("error/403");
//                default:
//                    return new ModelAndView("error/500");
//            }
//        }
//    }

    @ExceptionHandler(value = NoResourceFoundException.class)
    @ResponseBody
    public Object resourceNotFoundException(Model model, NoResourceFoundException exception) {
        log.info(exception.getMessage(), exception);

        return new ModelAndView("error/404");
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    @ResponseBody
    public Object resourceNotFoundException(Model model, AccessDeniedException exception) {
        log.info(exception.getMessage(), exception);

        return new ModelAndView("error/403");
    }



    @ExceptionHandler(value = {NoSuchAlgorithmException.class, IOException.class})
    @ResponseBody
    public Object handleIniHubAlgIoException(Model model, Exception exception
            , HttpServletRequest request, HttpServletResponse response, Locale locale) {

        log.error(exception.getMessage(), exception);

        if (request.getHeader("accept").contains("application/json"))
            return ResponseEntityDto.failResponse(Optional.of(exception.getMessage()));
        else
            return new ModelAndView("error/500");
    }

    @ExceptionHandler(value = {BindException.class, ServletRequestBindingException.class})
    @ResponseBody
    public Object handleIniHubBindException(Model model, BindException exception, Locale locale, HttpServletRequest request) {

        BindingResult bindingResult = exception.getBindingResult();
        log.info(bindingResult.getModel().toString());

        if (request.getHeader("accept").contains("application/json")){
            List<String> errors = bindingResult.getFieldErrors().stream().map(e -> (e.getField() + ":" + e.getDefaultMessage())).toList();
            return ResponseEntityDto.failResponse(Optional.of(String.join(",", errors)));
        } else
            return new ModelAndView("error/400");
    }

    /**
     * 관리되지 않는 오류 발생시, 오류 화면으로 이동.
     * @param model
     * @param exception
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public String handleException(Model model, Exception exception) {
        log.error(exception.getMessage(), exception);
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("error", exception.getMessage());
        return "error/500";
    }

    @ModelAttribute("version")
    String applicationVersion() {
        try {
            String version = getClass().getPackage().getImplementationVersion();

            log.debug("Application version: {}", version);

            return version;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return "2.0.0";
    }
}
