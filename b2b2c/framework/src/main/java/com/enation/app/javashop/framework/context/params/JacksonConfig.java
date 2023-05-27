package com.enation.app.javashop.framework.context.params;

import com.enation.app.javashop.framework.database.WebPage;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;

/**
 * 自定义json格式转换
 * @author kingapex
 * @version 1.0
 * @since 7.2.1
 * 2020/6/14
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customJackson() {
        return new Jackson2ObjectMapperBuilderCustomizer() {
            @Override
            public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {

                //定义Long型的Json 值转换，转换为String型
                //为了适配javascript 不支持Long型的精度
                jacksonObjectMapperBuilder.serializerByType(Long.class,new JsonSerializer(){
                    @Override
                    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                        //如果是Page对象则不转换为String型，而是返回Long类型（为了防止前端分页功能报错）
                        if (WebPage.class.equals(gen.getCurrentValue().getClass())) {
                            gen.writeNumber(String.valueOf(value));
                        } else {
                            gen.writeString(String.valueOf(value));
                        }
                    }
                });
            }

        };
    }

}
