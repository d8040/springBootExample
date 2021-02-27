package com.example.untact.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.untact.dto.Article;
import com.example.untact.dto.Reply;
import com.example.untact.dto.ResultData;
import com.example.untact.service.ArticleService;
import com.example.untact.service.ReplyService;

@Controller
public class UsrReplyController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private ReplyService replyService;

    //    댓글 리스트
    @RequestMapping("/usr/reply/list")
    @ResponseBody
    public ResultData showList(String relTypeCode, Integer relId) {

	if (relTypeCode == null) {
	    return new ResultData("F-1", "relTypeCode를 입력해 주세요.");
	}

	if (relId == null) {
	    return new ResultData("F-1", "relId를 입력해 주세요.");
	}

	if (relTypeCode.equals("article")) {
	    Article article = articleService.getArticle(relId);

	    if (article == null) {
		return new ResultData("F-1", "게시물이 존재하지 않습니다.");
	    }
	}

	List<Reply> replies = replyService.getForPrintReplies(relTypeCode, relId);

	return new ResultData("S-1", "성공", "replies", replies);
    }

    //    댓글 삭제
    @RequestMapping("/usr/reply/doDelete")
    @ResponseBody
    public ResultData doDelete(Integer id, HttpServletRequest req) {
	int loginedMemberId = (int) req.getAttribute("loginedMemberId");

	if (id == null) {
	    return new ResultData("F-1", "id을 입력해주세요");
	}

	Reply reply = replyService.getReply(id);
	if (reply == null) {
	    return new ResultData("F-1", "해당 게시물이 존재하지 않습니다.");
	}

	ResultData actorCanDeleteRd = replyService.getActorCanDeleteRd(reply, loginedMemberId);

	if (actorCanDeleteRd.isFail()) {
	    return actorCanDeleteRd;
	}

	return replyService.deleteReply(id);
    }

}
