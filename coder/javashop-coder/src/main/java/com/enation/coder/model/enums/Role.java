package com.enation.coder.model.enums;

/**
 * Created by kingapex on 23/02/2018.
 * 角色枚举
 * @author kingapex
 * @version 1.0
 * @since 6.4.0
 * 23/02/2018
 */
public enum Role {

    administrator("管理员"),commonuser("普通用户");
    private String name;


    Role(String _name){
        name=_name;
    }

    public String getName(){
        return  name;
    }
}
