package com.enation.app.javashop.framework.swagger;

import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger配置基类
 * Created by kingapex on 2018/3/23.
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/3/23
 */
public abstract class AbstractSwagger2 {


    /**
     * 构建认证token参数
     * @return token参数
     */
   protected  List<Parameter>  buildParameter( ){

       ParameterBuilder tokenPar = new ParameterBuilder();
       List<Parameter> pars = new ArrayList<Parameter>();
       tokenPar.name("Authorization").description("令牌").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
       pars.add(tokenPar.build());
        return  pars;
   }

}
