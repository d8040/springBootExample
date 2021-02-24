package com.example.untact.dto;

import lombok.Data;

@Data
public class Article {
	private int id;
	private String regDate; 
	private String updateDate;
	private String title; 
	private String body;
}
