package com.imooc.mapper;

import com.imooc.pojo.SearchRecords;
import com.imooc.utils.MyMapper;

import java.util.List;

public interface SearchRecordsMapper extends MyMapper<SearchRecords> {

     List<String> getHotWords();
}