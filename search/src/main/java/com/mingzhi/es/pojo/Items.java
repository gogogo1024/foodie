package com.mingzhi.es.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;


// 中文分词
@Document(indexName = "foodie-items-ik")
@Setting(shards = 3, replicas = 0)
public class Items {
    @Id
    @Field(store = true, type = FieldType.Text, index = false)
    private String itemId;
    @Field(store = true, type = FieldType.Text)
    private String itemName;
    @Field(store = true, type = FieldType.Text, index = false)
    private String imgUrl;
    @Field(store = true, type = FieldType.Integer)
    private Integer price;
    @Field(store = true, type = FieldType.Keyword)
    private String sellCounts;
    @Field(store = true)
    private String description;

    @Override
    public String toString() {
        return "Items{" +
                "itemId='" + itemId + '\'' +
                ", itemName='" + itemName + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", price=" + price +
                ", sellCounts='" + sellCounts + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getSellCounts() {
        return sellCounts;
    }

    public void setSellCounts(String sellCounts) {
        this.sellCounts = sellCounts;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
