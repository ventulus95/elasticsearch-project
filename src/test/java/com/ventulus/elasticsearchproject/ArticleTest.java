package com.ventulus.elasticsearchproject;


import com.ventulus.elasticsearchproject.model.Article;
import com.ventulus.elasticsearchproject.model.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ArticleTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Test
    @DisplayName("엘라스틱에 재대로 전달되는지?")
    public void elastic_transfer(){
        for (int i = 0; i <5; i++) {
            Article article = new Article();
            article.setId(String.valueOf(i));
            article.setPrice(12134L);
            article.setContent("이럴수가! 크렇게 쉬울수가?");
            article.setTitle("엘라스틱서치가 이렇게 쉽다며?");
            articleRepository.save(article);
        }
    }
}
