package com.example.untact.controller;

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

    //관리자 로그인
    @RequestMapping("/adm/member/login")
    public String login() {
	return "adm/member/login";
    }

    //	게시물 추가
    @RequestMapping("/adm/member/doJoin")
    @ResponseBody
    public ResultData doAdd(@RequestParam Map<String, Object> param) {

	if (param.get("loginId") == null) {
	    return new ResultData("F-1", "loginId을 입력해주세요");
	}

	Member existingMember = memberService.getMemberByLoginId((String) param.get("loginId"));

	if (existingMember != null) {
	    return new ResultData("F-2", String.format("%s (은)는 이미 사용중인 아이디 입니다.", param.get("loginId")));
	}

	if (param.get("loginPw") == null) {
	    return new ResultData("F-1", "loginPw를 입력해주세요");
	}

	if (param.get("name") == null) {
	    return new ResultData("F-1", "name을 입력해주세요");
	}

	if (param.get("nickname") == null) {
	    return new ResultData("F-1", "nickname를 입력해주세요");
	}

	if (param.get("cellphoneNo") == null) {
	    return new ResultData("F-1", "cellphoneNo를 입력해주세요");
	}

	if (param.get("email") == null) {
	    return new ResultData("F-1", "email을 입력해주세요");
	}

	return memberService.join(param);
    }

    @RequestMapping("/adm/member/doLogin")
    @ResponseBody
    public String doLogin(String loginId, String loginPw, HttpSession session) {

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
	
	return Util.msgAndReplace(msg, "../home/main");
    }

    @RequestMapping("/adm/member/doLogout")
    @ResponseBody
    public ResultData doLogout(String loginId, String loginPw, HttpSession session) {

	if (session.getAttribute("loginedMemberId") == null) {
	    return new ResultData("F-1", "이미 로그아웃 되었습니다.");
	}

	session.removeAttribute("loginedMemberId");

	return new ResultData("S-1", "로그아웃 되었습니다.");
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
