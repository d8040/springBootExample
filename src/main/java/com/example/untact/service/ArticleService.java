package com.example.untact.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.untact.dao.ArticleDao;
import com.example.untact.dto.Article;
import com.example.untact.dto.Board;
import com.example.untact.dto.GenFile;
import com.example.untact.dto.Member;
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

		genFileService.changeInputFileRelIds(param, id);

		return new ResultData("S-1", "게시물이 추가 되었습니다.", "id", id);
	}

	public ResultData deleteArticle(int id) {
		articleDao.deleteArticle(id);

		genFileService.deleteGenFiles("article", id);

		return new ResultData("S-1", "게시물이 삭제되었습니다.", "id", id);
	}

	public ResultData getActorCanDeleteRd(Article article, Member actor) {
		if (article.getMemberId() == actor.getId()) {
			return new ResultData("S-1", "가능합니다.");
		}

		if (memberService.isAdmin(actor)) {
			return new ResultData("S-2", "가능합니다.");
		}
		return new ResultData("F-1", "권한이 없습니다.");
	}

	public ResultData getActorCanModifyRd(Article article, Member actor) {
		return getActorCanDeleteRd(article, actor);
	}

	public Article getForPrintArticle(int id) {
		return articleDao.getForPrintArticle(id);
	}

	public List<Article> getForPrintArticles(int boardId, String searchKeywordType, String searchKeyword, int page, int itemsInAPage) {

		int limitStart = (page - 1) * itemsInAPage;
		int limitTake = itemsInAPage;

		List<Article> articles = articleDao.getForPrintArticles(boardId, searchKeywordType, searchKeyword, limitStart, limitTake);
		List<Integer> articleIds = articles.stream().map(article -> article.getId()).collect(Collectors.toList());
		Map<Integer, Map<String, GenFile>> filesMap = genFileService.getFilesMapKeyRelIdAndFileNo("article", articleIds, "common", "attachment");

		for (Article article : articles) {
			Map<String, GenFile> mapByFileNo = filesMap.get(article.getId());

			if (mapByFileNo != null) {
				article.getExtraNotNull().put("file__common__attachment", mapByFileNo);
			}
		}

		return articles;
	}

	public Board getBoard(int id) {
		return articleDao.getBoard(id);
	}

	public ResultData addReply(Map<String, Object> param) {
		articleDao.addReply(param);

		int id = Util.getAsInt(param.get("id"), 0);

		return new ResultData("S-1", "댓글이 추가 되었습니다.", "id", id);
	}

	public ResultData modifyArticle(Map<String, Object> param) {
		articleDao.modifyArticle(param);

		int id = Util.getAsInt(param.get("id"), 0);

		return new ResultData("S-1", "게시물을 수정하였습니다.", "id", id);
	}
}
