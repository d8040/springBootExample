package com.example.untact.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.untact.dao.ArticleDao;
import com.example.untact.dto.Article;
import com.example.untact.dto.ResultData;
import com.example.untact.util.Util;

@Service
public class ArticleService {

    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private MemberService memberService;

    public Article getArticle(int id) {
	return articleDao.getArticle(id);
    }

    public List<Article> getArticles(String searchKeywordType, String searchKeyword) {
	return articleDao.getArticles(searchKeywordType, searchKeyword);
    }

    public ResultData addArticle(Map<String, Object> param) {
	articleDao.addArticle(param);

	int id = Util.getAsInt(param.get("id"), 0);

	return new ResultData("S-1", "게시물이 추가 되었습니다.", "id", id);
    }

    public ResultData deleteArticle(int id) {
	articleDao.deleteArticle(id);

	return new ResultData("S-1", "게시물이 삭제되었습니다.", "id", id);
    }

    public ResultData modifyArticle(int id, String title, String body) {
	articleDao.modifyArticle(id, title, body);
	return new ResultData("S-1", "게시물이 수정되었습니다.", "id", id);
    }

    public ResultData getActorCanDeleteRd(Article article, int actorId) {
	if (article.getMemberId() == actorId) {
	    return new ResultData("S-1", "가능합니다.");
	}
	
	if (memberService.isAdmin(actorId)) {
	    return new ResultData("S-1", "가능합니다.");
	}
	return new ResultData("F-1", "권한이 없습니다.");
    }

    public ResultData getActorCanModifyRd(Article article, int actorId) {
	return getActorCanDeleteRd(article, actorId);
    }

}
