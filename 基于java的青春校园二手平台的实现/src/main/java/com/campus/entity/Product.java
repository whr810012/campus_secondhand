package com.campus.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 * 
 * @author Campus Team
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("product")
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
     * 卖家ID
     */
    private Long sellerId;
    
    /**
     * 商品图片，JSON格式存储
     */
    private String images;
    
    /**
     * 新旧程度描述
     */
    private String conditionDesc;
    
    /**
     * 交易方式：0-线下，1-线上，2-都支持
     */
    private Integer tradeType;
    
    /**
     * 交易地点
     */
    private String location;
    
    /**
     * 联系方式
     */
    private String contactInfo;
    
    /**
     * 状态：0-待审核，1-已上架，2-已下架，3-已售出，4-审核拒绝
     */
    private Integer status;
    
    /**
     * 浏览次数
     */
    private Integer viewCount;
    
    /**
     * 点赞次数
     */
    private Integer likeCount;
    
    /**
     * 审核时间
     */
    private LocalDateTime auditTime;
    
    /**
     * 审核人ID
     */
    private Long auditUserId;
    
    /**
     * 审核备注
     */
    private String auditRemark;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    
    /**
     * 逻辑删除：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
    
    /**
     * 商品状态枚举
     */
    public enum Status {
        PENDING(0, "待审核"),
        ONLINE(1, "已上架"),
        OFFLINE(2, "已下架"),
        SOLD_OUT(3, "已售出"),
        REJECTED(4, "审核拒绝");
        
        private final Integer code;
        private final String desc;
        
        Status(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }
        
        public Integer getCode() {
            return code;
        }
        
        public String getDesc() {
            return desc;
        }
    }
    
    /**
     * 交易方式枚举
     */
    public enum TradeType {
        OFFLINE(0, "线下交易"),
        ONLINE(1, "线上交易"),
        BOTH(2, "都支持");
        
        private final Integer code;
        private final String desc;
        
        TradeType(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }
        
        public Integer getCode() {
            return code;
        }
        
        public String getDesc() {
            return desc;
        }
    }
}