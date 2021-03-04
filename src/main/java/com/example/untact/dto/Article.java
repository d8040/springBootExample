package com.example.untact.dto;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article {
    private int id;
    private String regDate;
    private String updateDate;
    private int boardId;
    private int memberId;
    private String title;
    private String body;

    private String extra__writer;
    private String extra__boardName;
    private String extra__thumbImg;

    private Map<String, Object> extra;

    public Map<String, Object> getExtraNoNull() {
	if (extra == null) {
	    extra = new HashMap<String, Object>();
	}
	return extra;
    }
}
