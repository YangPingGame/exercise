package com.imooc.utils;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author 1819014975
 * @Title: MyMapper
 * @ProjectName imooc-videos-dev
 * @Description: TODO
 * @date 2018/10/9 14:55
 */
public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {
    //TODO
    //FIXME 特别注意，该接口不能被扫描到，否则会出错
}
