package com.campus.secondhand.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品实体类
 *
 * @author campus-secondhand
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("products")
public class Product {

    /**
     * 商品ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品标题
     */
    private String title;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    @TableField(exist = false)
    private String categoryName;

    /**
     * 卖家ID
     */
    private Long sellerId;

    /**
     * 卖家信息
     */
    @TableField(exist = false)
    private User seller;

    /**
     * 商品图片，JSON数组格式
     */
    private String images;

    /**
     * 图片列表
     */
    @TableField(exist = false)
    private List<String> imageList;

    /**
     * 处理前端发送的images数组
     * 将images数组映射到imageList字段
     */
    @JsonProperty("images")
    public void setImagesFromArray(List<String> images) {
        this.imageList = images;
    }

    /**
     * 返回images数组给前端
     */
    @JsonProperty("images")
    public List<String> getImagesAsArray() {
        return this.imageList;
    }

    /**
     * 处理前端发送的tags数组
     * 将tags数组映射到tagList字段
     */
    @JsonProperty("tags")
    public void setTagsFromArray(List<String> tags) {
        this.tagList = tags;
    }

    /**
     * 返回tags数组给前端
     */
    @JsonProperty("tags")
    public List<String> getTagsAsArray() {
        return this.tagList;
    }

    /**
     * 商品状态：available-可售，reserved-已预定，sold-已售出，unavailable-已下架
     */
    private String status;

    /**
     * 审核状态：0-待审核，1-审核通过，2-审核拒绝
     */
    private Integer auditStatus;

    /**
     * 审核意见
     */
    private String auditReason;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;

    /**
     * 审核人ID
     */
    private Long auditorId;

    /**
     * 商品成色：1-全新，2-几乎全新，3-轻微使用痕迹，4-明显使用痕迹，5-重度使用
     */
    @TableField("`condition`")
    private Integer condition;

    /**
     * 交易方式：1-仅线下，2-仅线上，3-线上线下均可
     */
    private Integer tradeType;

    /**
     * 交易地点
     */
    private String tradeLocation;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 收藏次数
     */
    private Integer favoriteCount;

    /**
     * 是否置顶
     */
    private Boolean isTop;

    /**
     * 置顶到期时间
     */
    private LocalDateTime topExpireTime;

    /**
     * 是否推荐
     */
    private Boolean isRecommend;

    /**
     * 标签，JSON数组格式
     */
    private String tags;

    /**
     * 标签列表
     */
    @TableField(exist = false)
    private List<String> tagList;

    /**
     * 是否已收藏（当前用户）
     */
    @TableField(exist = false)
    private Boolean isFavorited;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;

}