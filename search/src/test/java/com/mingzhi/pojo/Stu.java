package com.mingzhi.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;


@Document(indexName = "stu")
@Setting(shards = 3, replicas = 0)
public class Stu {
    @Id
    private Long stuId;
    @Field(store = true, type = FieldType.Keyword)
    private String sign;
    @Field(store = true)
    private String description;
    @Field(store = true)
    private Float money;
    @Field(store = true)
    private String name;
    @Field(store = true)
    private Long age;

    public static String getIndexName() {
        return Stu.class.getAnnotation(Document.class).indexName();
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getMoney() {
        return money;
    }

    public void setMoney(Float money) {
        this.money = money;
    }

    public Long getStuId() {
        return stuId;
    }

    public void setStuId(Long stuId) {
        this.stuId = stuId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Stu{" +
                "stuId=" + stuId +
                ", sign='" + sign + '\'' +
                ", description='" + description + '\'' +
                ", money=" + money +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
