package com.imooc.controller.interceptor;

import com.alibaba.fastjson.JSON;
import com.imooc.controller.BasicController;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author 1819014975
 * @Title: MineInterceptor
 * @ProjectName imooc-videos-dev
 * @Description: TODO
 * @date 2018/10/22 14:44
 */
public class MineInterceptor extends BasicController implements HandlerInterceptor  {


    @Autowired
    private RedisOperator redis;

    /**
     * 对请求之前拦截
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("userId");
        String token = request.getHeader("token");
        //判断两者是否为空
        if(StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(token)){
            String uniquerToken = redis.get(USER_REDIS_SESSION+":"+userId);
            //判断信息是否过期和
            if(StringUtils.isEmpty(uniquerToken) && StringUtils.isBlank(uniquerToken)){
                 //回话过期的情况
                returnErrorResponse(response,IMoocJSONResult.errorTokenMsg("请登录..."));
                return false;
            }else{
                if(!token.equals(uniquerToken)){
                    //账号在别的手机上的登录的情况
                    returnErrorResponse(response,IMoocJSONResult.errorTokenMsg("账号被挤出..."));
                    return false;
                }
            }
        }else{
            //没有登录的情况
            returnErrorResponse(response,IMoocJSONResult.errorTokenMsg("请登录..."));
            return false;
        }
        return true;
    }

    /**
     * 将提示传到页面端
     * @param response
     * @param result
     * @throws IOException
     */
    public void returnErrorResponse(HttpServletResponse response, IMoocJSONResult result) throws IOException {
        OutputStream out = null;
        try{
            //设置字符串编码
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out = response.getOutputStream();
            out.write(JSON.toJSONString(result).getBytes("utf-8"));
            out.flush();
        }finally {
            //关闭流
            if(out != null){
                out.close();
            }
        }
    }

    /**
     * 请求结束之后拦截，渲染视图之前
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 前端渲染结束调用
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
