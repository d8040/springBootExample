package com.example.untact.dto;

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
	private String memberId;
	private String title; 
	private String body;
}
