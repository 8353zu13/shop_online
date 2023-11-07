package com.soft2242.shop.common.constant;

/**
 * 数据库中公共字段：create_time、update_time、delete_flag；
 * 这些公共字段的值可以的数据插入时自动填充
 */
public interface Constant {
    //    创建时间
    String CREATE_TIME = "createtime";
    //    更新时间
    String UPDATE_TIME = "updatetime";
    //    逻辑删除
    String DELETE_FLAG = "deleteflag";
}