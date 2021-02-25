package com.example.untact.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.untact.dto.Article;
import com.example.untact.dto.ResultData;
import com.example.untact.service.ArticleService;

@Controller
public class UsrArticleController {

	@Autowired
	private ArticleService articleService;

	//	게시물 상세
	@RequestMapping("/usr/article/detail")
	@ResponseBody
	public Article showDetail(int id) {
		Article article = articleService.getArticle(id);

		return article;
	}

	//	게시물 리스트
	@RequestMapping("/usr/article/list")
	@ResponseBody
	public List<Article> showList(String searchKeywordType, String searchKeyword) {
		if ( searchKeywordType != null ) {
			searchKeywordType = searchKeywordType.trim();
		}
		
		if ( searchKeywordType == null || searchKeywordType.length() == 0 ) {
			searchKeywordType = "titleAndBody";
		}
		if ( searchKeyword != null && searchKeyword.length() == 0) {
			searchKeyword = null;
		}
		
		if ( searchKeyword != null ) {
			searchKeyword = searchKeyword.trim();
		}
		
		return articleService.getArticles(searchKeywordType,searchKeyword);
	}

	//	게시물 추가
	@RequestMapping("/usr/article/doAdd")
	@ResponseBody
	public ResultData doAdd(@RequestParam Map<String, Object> param) {
		
		if (param.get("title") == null) {
			return new ResultData("F-1", "title을 입력해주세요");
		}
		if (param.get("body") == null) {
			return new ResultData("F-1", "body를 입력해주세요");
		}

		return articleService.addArticle(param);
	}

	//	게시물 삭제
	@RequestMapping("/usr/article/doDelete")
	@ResponseBody
	public ResultData doDelete(Integer id) {
		if (id == null) {
			return new ResultData("F-1", "id을 입력해주세요");
		}

		Article article = articleService.getArticle(id);
		if (article == null) {
			return new ResultData("F-1", "해당 게시물이 존재하지 않습니다.");
		}
		
		return articleService.deleteArticle(id);
	}

	//	게시물 수정
	@RequestMapping("/usr/article/doModify")
	@ResponseBody
	public ResultData doModify(Integer id, String title, String body) {
		Article article = articleService.getArticle(id);

		if (id == null) {
			return new ResultData("F-1", "id을 입력해주세요");
		}
		if (title == null) {
			return new ResultData("F-1", "title을 입력해주세요");
		}
		if (body == null) {
			return new ResultData("F-1", "body를 입력해주세요");
		}
		
		if (article == null) {
			return new ResultData("F-1", "해당 게시물이 존재하지 않습니다.");
		}
		return articleService.modifyArticle(id, title, body);
	}
}
