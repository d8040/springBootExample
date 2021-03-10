package com.example.untact.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.untact.dto.Member;
import com.example.untact.dto.ResultData;
import com.example.untact.service.MemberService;
import com.example.untact.util.Util;

@Controller
public class AdmMemberController {

	@Autowired
	private MemberService memberService;

	@RequestMapping("/adm/member/list")
	public String showList(HttpServletRequest req, @RequestParam(defaultValue = "1") int boardId, String searchKeywordType, String searchKeyword, @RequestParam(defaultValue = "1") int page) {
		if (searchKeywordType != null) {
			searchKeywordType = searchKeywordType.trim();
		}

		if (searchKeywordType == null || searchKeywordType.length() == 0) {
			searchKeywordType = "name";
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

		List<Member> members = memberService.getForPrintMembers(searchKeywordType, searchKeyword, page, itemsInAPage);

		req.setAttribute("members", members);

		return "adm/member/list";
	}

	@RequestMapping("/adm/member/join")
	public String showJoin() {
		return "adm/member/join";
	}

	//관리자 로그인
	@RequestMapping("/adm/member/login")
	public String showLogin() {
		return "adm/member/login";
	}

	//	게시물 추가
	@RequestMapping("/adm/member/doJoin")
	@ResponseBody
	public String doAdd(@RequestParam Map<String, Object> param) {

		if (param.get("loginId") == null) {
			return Util.msgAndBack("loginId을 입력해주세요");
		}

		Member existingMember = memberService.getMemberByLoginId((String) param.get("loginId"));

		if (existingMember != null) {
			return Util.msgAndBack(String.format("%s (은)는 이미 사용중인 아이디 입니다.", param.get("loginId")));
		}

		if (param.get("loginPw") == null) {
			return Util.msgAndBack("loginPw를 입력해주세요");
		}

		if (param.get("name") == null) {
			return Util.msgAndBack("name을 입력해주세요");
		}

		if (param.get("nickname") == null) {
			return Util.msgAndBack("nickname를 입력해주세요");
		}

		if (param.get("cellphoneNo") == null) {
			return Util.msgAndBack("cellphoneNo를 입력해주세요");
		}

		if (param.get("email") == null) {
			return Util.msgAndBack("email을 입력해주세요");
		}

		memberService.join(param);

		String msg = String.format("%s님 환영합니다.", param.get("nickname"));
		String redirectUrl = Util.ifEmpty((String) param.get("redirectUrl"), "../member/login");

		return Util.msgAndReplace(msg, redirectUrl);
	}

	@RequestMapping("/adm/member/doLogin")
	@ResponseBody
	public String doLogin(String loginId, String loginPw, String redirectUrl, HttpSession session) {

		if (session.getAttribute("loginedMemberId") != null) {
			return Util.msgAndBack("이미 로그인되어 있습니다.");
		}

		if (loginId == null) {
			return Util.msgAndBack("아이디를 입력해주세요.");
		}

		Member existingMember = memberService.getMemberByLoginId(loginId);

		if (existingMember == null) {
			return Util.msgAndBack("일치하는 아이디가 존재하지 않습니다.");
		}

		if (loginPw == null) {
			return Util.msgAndBack("비밀번호를 입력해주세요.");
		}

		if (existingMember.getLoginPw().equals(loginPw) == false) {
			return Util.msgAndBack("비밀번호가 일치하지 않습니다.");
		}

		if (memberService.isAdmin(existingMember) == false) {
			return Util.msgAndBack("관리자만 접근할 수 있는 페이지 입니다.");
		}

		session.setAttribute("loginedMemberId", existingMember.getId());

		String msg = String.format("%s님 환영합니다.", existingMember.getNickname());

		redirectUrl = Util.ifEmpty(redirectUrl, "../home/main");

		return Util.msgAndReplace(msg, redirectUrl);
	}

	@RequestMapping("/adm/member/doLogout")
	@ResponseBody
	public String doLogout(String loginId, String loginPw, HttpSession session) {

		if (session.getAttribute("loginedMemberId") == null) {
			return Util.msgAndBack("이미 로그아웃 상태입니다.");
		}

		session.removeAttribute("loginedMemberId");

		return Util.msgAndReplace("로그아웃 되었습니다.", "../member/login");
	}

	@RequestMapping("/adm/member/doModify")
	@ResponseBody
	public ResultData doModify(@RequestParam Map<String, Object> param, HttpServletRequest req) {

		if (param.isEmpty()) {
			return new ResultData("F-2", "수정할 정보를 입력해 주세요.");
		}

		int loginedMemberId = (int) req.getAttribute("loginedMemberId");
		param.put("id", loginedMemberId);

		return memberService.modifyMember(param);
	}
}
