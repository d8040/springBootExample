package com.example.untact.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.untact.dto.Article;

@Mapper
public interface ArticleDao {
    public Article getArticle(@Param("id") int id);
    public List<Article> getArticles(@Param("searchKeywordType") String searchKeywordType, @Param("searchKeyword") String searchKeyword);
    public void addArticle(Map<String, Object> param);
    public void deleteArticle(@Param("id") int id);
    public void modifyArticle(@Param("id") int id, @Param("title") String title, @Param("body") String body);
    public Article getForPrintArticle(@Param("id") int id);
    public List<Article> getForPrintArticles(@Param("searchKeywordType") String searchKeywordType, @Param("searchKeyword") String searchKeyword);
}
