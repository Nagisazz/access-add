package com.nagisazz.accessadd.dao.base;

import com.nagisazz.accessadd.entity.Record;
import java.util.List;

public interface RecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Record record);

    int insertSelective(Record record);

    Record selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Record record);

    int updateByPrimaryKey(Record record);

    Record selectOne(Record record);

    List<Record> selectList(Record record);

    int deleteSelective(Record record);

    int batchUpdate(List<Record> recordList);

    int batchUpdateSelective(List<Record> recordList);

    int batchInsert(List<Record> recordList);

    int batchDelete(List<Long> recordList);

    List<Record> batchSelect(List<Long> recordList);
}