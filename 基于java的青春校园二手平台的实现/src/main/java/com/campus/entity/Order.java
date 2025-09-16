package com.campus.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 * 
 * @author Campus Team
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("`order`")
public class Order {
    
    /**
     * 订单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 买家ID
     */
    private Long buyerId;
    
    /**
     * 卖家ID
     */
    private Long sellerId;
    
    /**
     * 商品标题
     */
    private String productTitle;
    
    /**
     * 商品图片
     */
    private String productImage;
    
    /**
     * 交易价格
     */
    private BigDecimal price;
    
    /**
     * 交易方式：0-线下，1-线上
     */
    private Integer tradeType;
    
    /**
     * 收货地址
     */
    private String deliveryAddress;
    
    /**
     * 联系电话
     */
    private String contactPhone;
    
    /**
     * 订单状态：0-待确认，1-已确认，2-交易中，3-已完成，4-已取消
     */
    private Integer status;
    
    /**
     * 备注
     */
    private String remark;
    
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
     * 订单状态枚举
     */
    public enum Status {
        PENDING(0, "待确认"),
        CONFIRMED(1, "已确认"),
        TRADING(2, "交易中"),
        COMPLETED(3, "已完成"),
        CANCELLED(4, "已取消");
        
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
        ONLINE(1, "线上交易");
        
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