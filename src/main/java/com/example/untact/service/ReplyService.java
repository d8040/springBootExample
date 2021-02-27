package com.example.untact.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.untact.dao.ReplyDao;
import com.example.untact.dto.Reply;

@Service
public class ReplyService {
    @Autowired
    private ReplyDao replyDao;

    public List<Reply> getForPrintReplies(String relTypeCode, int relId) {
	return replyDao.getForPrintReplies(relTypeCode, relId);
    }
    
}
