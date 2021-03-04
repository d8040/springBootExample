package com.example.untact.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.untact.dao.ArticleDao;
import com.example.untact.dto.Article;
import com.example.untact.dto.Board;
import com.example.untact.dto.ResultData;
import com.example.untact.util.Util;

@Service
public class ArticleService {

    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private MemberService memberService;
    @Autowired
    private GenFileService genFileService;

    public Article getArticle(int id) {
	return articleDao.getArticle(id);
    }

    public List<Article> getArticles(String searchKeywordType, String searchKeyword) {
	return articleDao.getArticles(searchKeywordType, searchKeyword);
    }

    public ResultData addArticle(Map<String, Object> param) {
	articleDao.addArticle(param);

	int id = Util.getAsInt(param.get("id"), 0);

	String genFileIdsStr = Util.ifEmpty((String) param.get("genFileIdsStr"), null);

	if (genFileIdsStr != null) {
	    List<Integer> genFileIds = Util.getListDividedBy(genFileIdsStr, ",");

	    // 파일이 먼저 생성된 후에, 관련 데이터가 생성되는 경우에는, file의 relId가 일단 0으로 저장된다.
	    // 그것을 뒤늦게라도 이렇게 고처야 한다.
	    for (int genFileId : genFileIds) {
		genFileService.changeRelId(genFileId, id);
	    }
	}

	return new ResultData("S-1", "게시물이 추가 되었습니다.", "id", id);
    }

    public ResultData deleteArticle(int id) {
	articleDao.deleteArticle(id);
	
	genFileService.deleteFiles("article", id);

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

    public Article getForPrintArticle(int id) {
	return articleDao.getForPrintArticle(id);
    }

    public List<Article> getForPrintArticles(int boardId, String searchKeywordType, String searchKeyword, int page, int itemsInAPage) {

	int limitStart = (page - 1) * itemsInAPage;
	int limitTake = itemsInAPage;
	return articleDao.getForPrintArticles(boardId, searchKeywordType, searchKeyword, limitStart, limitTake);
    }

    public Board getBoard(int id) {
	return articleDao.getBoard(id);
    }

    public ResultData addReply(Map<String, Object> param) {
	articleDao.addReply(param);

	int id = Util.getAsInt(param.get("id"), 0);

	return new ResultData("S-1", "댓글이 추가 되었습니다.", "id", id);
    }

}
