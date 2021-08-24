package com.ventulus.elasticsearchproject.model;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.*;

import javax.persistence.Id;
import java.util.Date;

@Document(indexName = "article")
@Data
@Setting(settingPath = "/settings/setting.json")
@Mapping(mappingPath = "/settings/mapping.json")
public class Article {

    @Id
    private String id;
    private String title;
    private String content;

//    @Field(type = FieldType.Date)
//    private Date log_date;
//
//    @Field(type = FieldType.Text)
//    private String longtype_text;

    @Field(type = FieldType.Long)
    private Long price;

}
