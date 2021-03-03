package com.example.untact.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.example.untact.dto.Article;
import com.example.untact.dto.Board;
import com.example.untact.dto.ResultData;
import com.example.untact.service.ArticleService;
import com.example.untact.util.Util;

@Controller
public class AdmArticleController extends BaseController {

    @Autowired
    private ArticleService articleService;

    //	게시물 상세
    @RequestMapping("/adm/article/detail")
    @ResponseBody
    public ResultData showDetail(Integer id) {
	if (id == null) {
	    return new ResultData("F-1", "id를 입력해 주세요.");
	}

	Article article = articleService.getForPrintArticle(id);

	if (article == null) {
	    return new ResultData("F-1", "존재하지 않는 게시물 입니다.");
	}

	return new ResultData("S-1", "성공", "article", article);
    }

    //	게시물 리스트
    @RequestMapping("/adm/article/list")
    public String showList(HttpServletRequest req, @RequestParam(defaultValue = "1") int boardId, String searchKeywordType, String searchKeyword, @RequestParam(defaultValue = "1") int page) {

	Board board = articleService.getBoard(boardId);

	if (board == null) {
	    return msgAndBack(req, "존재하지 않는 게시판 입니다.");
	}

	if (searchKeywordType != null) {
	    searchKeywordType = searchKeywordType.trim();
	}

	if (searchKeywordType == null || searchKeywordType.length() == 0) {
	    searchKeywordType = "titleAndBody";
	}
	if (searchKeyword != null && searchKeyword.length() == 0) {
	    searchKeyword = null;
	}

	if (searchKeyword != null) {
	    searchKeyword = searchKeyword.trim();
	}

	if (searchKeyword == null) {
	    searchKeywordType = null;
	}

	int itemsInAPage = 20;

	List<Article> articles = articleService.getForPrintArticles(boardId, searchKeywordType, searchKeyword, page, itemsInAPage);

	req.setAttribute("articles", articles);

	return "adm/article/list";
    }

    //	댓글 추가
    @RequestMapping("/adm/article/doAddReply")
    @ResponseBody
    public ResultData doAddReply(@RequestParam Map<String, Object> param, HttpServletRequest req) {
	int loginedMemberId = (int) req.getAttribute("loginedMemberId");

	if (param.get("body") == null) {
	    return new ResultData("F-1", "body를 입력해주세요");
	}

	if (param.get("articleId") == null) {
	    return new ResultData("F-1", "articleId을 입력해주세요");
	}

	param.put("memberId", loginedMemberId);

	return articleService.addReply(param);
    }

    //    게시물 추가 페이지
    @RequestMapping("/adm/article/add")
    public String showAdd(@RequestParam Map<String, Object> param, HttpServletRequest req) {
	return "/adm/article/add";
    }
    
    //	게시물 추가
    @RequestMapping("/adm/article/doAdd")
    @ResponseBody
    public ResultData doAdd(@RequestParam Map<String, Object> param, HttpServletRequest req, MultipartRequest multipartRequest) {
	Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
	
	if (true) {
	    return new ResultData("S-1", "테스트", "fileMap.keySet", fileMap.keySet());
	}
	
	int loginedMemberId = (int) req.getAttribute("loginedMemberId");

	if (param.get("title") == null) {
	    return new ResultData("F-1", "title을 입력해주세요");
	}
	if (param.get("body") == null) {
	    return new ResultData("F-1", "body를 입력해주세요");
	}

	param.put("memberId", loginedMemberId);

	return articleService.addArticle(param);
    }

    //	게시물 삭제
    @RequestMapping("/adm/article/doDelete")
    @ResponseBody
    public ResultData doDelete(Integer id, HttpServletRequest req) {
	int loginedMemberId = (int) req.getAttribute("loginedMemberId");

	if (id == null) {
	    return new ResultData("F-1", "id을 입력해주세요");
	}

	Article article = articleService.getArticle(id);
	if (article == null) {
	    return new ResultData("F-1", "해당 게시물이 존재하지 않습니다.");
	}

	ResultData actorCanDeleteRd = articleService.getActorCanDeleteRd(article, loginedMemberId);

	if (actorCanDeleteRd.isFail()) {
	    return actorCanDeleteRd;
	}

	return articleService.deleteArticle(id);
    }

    //	게시물 수정
    @RequestMapping("/adm/article/doModify")
    @ResponseBody
    public ResultData doModify(Integer id, String title, String body, HttpServletRequest req) {
	int loginedMemberId = (int) req.getAttribute("loginedMemberId");
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

	ResultData actorCanModifyRd = articleService.getActorCanModifyRd(article, loginedMemberId);

	if (actorCanModifyRd.isFail()) {
	    return actorCanModifyRd;
	}

	return articleService.modifyArticle(id, title, body);
    }
}
