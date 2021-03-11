package com.example.untact.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.untact.dto.Member;
import com.example.untact.dto.ResultData;
import com.example.untact.service.MemberService;
import com.example.untact.util.Util;

@Controller
public class AdmMemberController extends BaseController {

	@Autowired
	private MemberService memberService;

	@GetMapping("/adm/member/getLoginIdDup")
	@ResponseBody
	public ResultData getLoginIdDup(String loginId) {
		if (loginId == null) {
			return new ResultData("F-5", "아이디를 입력해주세요.");
		}

		if (Util.allNumberString(loginId)) {
			return new ResultData("F-3", "아이디는 숫자만으로 구성될 수 없습니다.");
		}

		if (Util.startsWithNumberString(loginId)) {
			return new ResultData("F-4", "아이디는 숫자로 시작할 수 없습니다.");
		}

		if (loginId.length() < 5) {
			return new ResultData("F-5", "아이디는 5자 이상으로 입력해주세요.");
		}

		if (loginId.length() > 20) {
			return new ResultData("F-6", "아이디는 20자 이하로 입력해주세요.");
		}

		if (Util.isStandardLoginIdString(loginId) == false) {
			return new ResultData("F-1", "아이디는 영문소문자와 숫자의 조합으로 구성되어야 합니다.");
		}

		Member existingMember = memberService.getMemberByLoginId(loginId);

		if (existingMember != null) {
			return new ResultData("F-2", String.format("%s(은)는 이미 사용중인 아이디 입니다.", loginId));
		}

		return new ResultData("S-1", String.format("%s(은)는 사용가능한 아이디 입니다.", loginId), "loginId", loginId);
	}

	@GetMapping("/adm/member/list")
	public String showList(HttpServletRequest req, @RequestParam(defaultValue = "1") int boardId, String searchKeywordType, String searchKeyword, @RequestParam(defaultValue = "1") int page, @RequestParam Map<String, Object> param) {
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

		List<Member> members = memberService.getForPrintMembers(searchKeywordType, searchKeyword, page, itemsInAPage, param);

		req.setAttribute("members", members);

		return "adm/member/list";
	}

	@GetMapping("/adm/member/join")
	public String showJoin() {
		return "adm/member/join";
	}

	// 관리자 로그인
	@GetMapping("/adm/member/login")
	public String showLogin() {
		return "adm/member/login";
	}

	// 게시물 추가
	@PostMapping("/adm/member/doJoin")
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

	@PostMapping("/adm/member/doLogin")
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

	@GetMapping("/adm/member/doLogout")
	@ResponseBody
	public String doLogout(String loginId, String loginPw, HttpSession session) {

		if (session.getAttribute("loginedMemberId") == null) {
			return Util.msgAndBack("이미 로그아웃 상태입니다.");
		}

		session.removeAttribute("loginedMemberId");

		return Util.msgAndReplace("로그아웃 되었습니다.", "../member/login");
	}

	@GetMapping("/adm/member/modify")
	public String showModify(Integer id, HttpServletRequest req) {
		if (id == null) {
			return msgAndBack(req, "id를 입력해주세요.");
		}

		Member member = memberService.getForPrintMember(id);

		req.setAttribute("member", member);

		if (member == null) {
			return msgAndBack(req, "존재하지 않는 회원번호 입니다.");
		}

		return "adm/member/modify";
	}

	@PostMapping("/adm/member/doModify")
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
