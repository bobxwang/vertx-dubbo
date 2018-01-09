package com.bob.vertx.facade.param;

/**
 * Created by wangxiang on 18/1/9.
 */
public class User {

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private Integer id;

    private String name;
}