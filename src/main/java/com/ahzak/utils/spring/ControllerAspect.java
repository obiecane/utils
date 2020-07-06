package com.ahzak.utils.spring;//package com.jeecms.market.utils.spring;
//
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Optional;
//
//
///**
// * @author Duan Yigui
// * @date 2020-04-26
// * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
// * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的
// */
//@Component
//@Aspect
//public class ControllerAspect {
//
//    private static final String[] EMPTY_STRING_ARRAY = new String[0];
//
//    @Pointcut(value = "@annotation(requestMapping)")
//    public void requestMappingPointcut(RequestMapping requestMapping) { }
//
//    @Pointcut(value = "@annotation(getMapping)")
//    public void getMappingPointcut(GetMapping getMapping) { }
//
//    @Pointcut(value = "@annotation(postMapping)")
//    public void postMappingPointcut(PostMapping postMapping) { }
//
//    @Pointcut(value = "@annotation(putMapping)")
//    public void putMappingPointcut(PutMapping putMapping) { }
//
//    @Pointcut(value = "@annotation(patchMapping)")
//    public void patchMappingPointcut(PatchMapping patchMapping) { }
//
//    @Pointcut(value = "@annotation(deleteMapping)")
//    public void deleteMappingPointcut(DeleteMapping deleteMapping) { }
//
//
//
//
//
//
//    @Before(value = "requestMappingPointcut(requestMapping)", argNames = "joinPoint,requestMapping")
//    public void doBefore(JoinPoint joinPoint, RequestMapping requestMapping) {
//        setMapping(joinPoint, requestMapping.value());
//    }
//
//    @Before(value = "getMappingPointcut(getMapping)", argNames = "joinPoint,getMapping")
//    public void doBefore(JoinPoint joinPoint, GetMapping getMapping) {
//        setMapping(joinPoint, getMapping.value());
//    }
//
//    @Before(value = "postMappingPointcut(postMapping)", argNames = "joinPoint,postMapping")
//    public void doBefore(JoinPoint joinPoint, PostMapping postMapping) {
//        setMapping(joinPoint, postMapping.value());
//    }
//
//    @Before(value = "putMappingPointcut(putMapping)", argNames = "joinPoint,putMapping")
//    public void doBefore(JoinPoint joinPoint, PutMapping putMapping) {
//        setMapping(joinPoint, putMapping.value());
//    }
//
//    @Before(value = "deleteMappingPointcut(patchMapping)", argNames = "joinPoint,patchMapping")
//    public void doBefore(JoinPoint joinPoint, DeleteMapping patchMapping) {
//        setMapping(joinPoint, patchMapping.value());
//    }
//
//    @Before(value = "patchMappingPointcut(deleteMapping)", argNames = "joinPoint,deleteMapping")
//    public void doBefore(JoinPoint joinPoint, PatchMapping deleteMapping) {
//        setMapping(joinPoint, deleteMapping.value());
//    }
//
//
//    private void setMapping(JoinPoint joinPoint, String[] vals) {
//        final Object target = joinPoint.getTarget();
//        final RequestMapping brm = target.getClass().getAnnotation(RequestMapping.class);
//        final String[] bVals = Optional.ofNullable(brm)
//                .map(RequestMapping::value)
//                .orElse(EMPTY_STRING_ARRAY);
//
//        final String mapping = SpringMvcUtil.currModulePath()
//                + SpringMvcUtil.currContextPath()
//                + (bVals.length > 0 ? bVals[0] : "")
//                + (vals.length > 0 ? vals[0] : "");
//        SpringMvcUtil.mappingThreadLocal.set(mapping);
//    }
//
//
//}
