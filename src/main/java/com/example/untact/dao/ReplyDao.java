package com.example.untact.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.untact.dto.Reply;

@Mapper
public interface ReplyDao {

    List<Reply> getForPrintReplies(@Param("relTypeCode") String relTypeCode, @Param("relId") int relId);

}
