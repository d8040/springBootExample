package com.example.untact.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.untact.dto.Article;

@Mapper
public interface ArticleDao {
    public Article getArticle(@Param(value = "id") int id);
    public List<Article> getArticles(@Param(value = "searchKeywordType") String searchKeywordType, @Param(value = "searchKeyword") String searchKeyword);
    public void addArticle(Map<String, Object> param);
    public void deleteArticle(@Param(value = "id") int id);
    public void modifyArticle(@Param(value = "id") int id, @Param(value = "title") String title, @Param(value = "body") String body);
}
