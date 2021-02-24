package com.example.untact.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.untact.dto.Article;

@Controller
public class UsrArticleController {

	private List<Article> articles;
	
	public UsrArticleController() {
		articles = new ArrayList<>();
		
		articles.add(new Article(1, "2020-12-12 12:12:12", "제목1", "내용1"));		
		articles.add(new Article(2, "2020-12-12 12:12:12", "제목2", "내용2"));

	}

//	게시물 상세
	@RequestMapping("/usr/article/detail")
	@ResponseBody
	public Article showDetail(int id) {

		return articles.get(id - 1);
	}

//	게시물 리스트
	@RequestMapping("/usr/article/list")
	@ResponseBody
	public List<Article> showList() {
		return articles;
	}
	
//	게시물 삭제
	@RequestMapping("/usr/article/doDelete")
	@ResponseBody
	public Map<String, Object> doDelete(int id) {
		boolean deleteArticleRs = deleteArticle(id);
		
		Map<String, Object> rs = new HashMap<>();
		
		if (deleteArticleRs) {
			rs.put("resultCode", "S-1");
			rs.put("msg", "삭제 되었습니다.");
		}else {
			rs.put("resultCode", "F-1");
			rs.put("msg", "해당 게시물은 존재하지 않습니다.");
		}
		
		rs.put("id", id);
		
		return rs;
	}

	private boolean deleteArticle(int id) {
		
		for (Article article : articles ) {
			if (article.getId() == id) {
				articles.remove(article);
				
				return true;
			}
		}
		
		return false;
	}
	
//	게시물 수정
	@RequestMapping("/usr/article/doModify")
	@ResponseBody
	public Map<String, Object> doModify(int id, String title, String body) {
		
		Article selArticle = null;
		
		for ( Article article : articles ) {
			if (article.getId()==id) {
				selArticle = article; 
				break;
			}
		}
		
		Map<String, Object> rs = new HashMap<>();
		
		if (selArticle == null) {
			rs.put("resultCode", "F-1");
			rs.put("msg", String.format("%d번 게시물은 존재하지 않습니다.", id));
			return rs;
		}
		
		selArticle.setTitle(title);
		selArticle.setBody(body);
		
		rs.put("resultCode", "S-1");
		rs.put("msg", String.format("%d번 게시물이 수정되었습니다.", id));
		rs.put("id", id);
		
		return rs;
	}
}
