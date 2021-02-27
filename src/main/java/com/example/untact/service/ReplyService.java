package com.example.untact.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.untact.dao.ReplyDao;
import com.example.untact.dto.Reply;
import com.example.untact.dto.ResultData;

@Service
public class ReplyService {
    @Autowired
    private ReplyDao replyDao;
    @Autowired
    private MemberService memberService;

    public List<Reply> getForPrintReplies(String relTypeCode, int relId) {
	return replyDao.getForPrintReplies(relTypeCode, relId);
    }

    public Reply getReply(Integer id) {
	return replyDao.getReply(id);
    }

    public ResultData getActorCanDeleteRd(Reply reply, int actorId) {
	if (reply.getMemberId() == actorId) {
	    return new ResultData("S-1", "가능합니다.");
	}
	
	if (memberService.isAdmin(actorId)) {
	    return new ResultData("S-2", "가능합니다.");
	}
	
	return new ResultData("F-1", "권한이 없습니다.");
    }

    public ResultData deleteReply(int id) {
	replyDao.deleteReply(id);
	return new ResultData("S-1", "삭제되었습니다.", "id", id);
    }
    
}