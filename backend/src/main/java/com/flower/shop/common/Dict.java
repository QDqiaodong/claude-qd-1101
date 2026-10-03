package com.flower.shop.common;

import java.util.List;

/**
 * 花店业务词典与规则参数表：花材类别 / 花材状态、订单状态与流转、
 * 配送状态与流转、以及配送未结案态集合。
 *
 * <p>规则集中在此，便于出题时被要求调整或扩展，并在各 Service 中引用校验。</p>
 */
public final class Dict {

    private Dict() {
    }

    // ---------------- 花材 ----------------
    /** 切花 / 叶材 / 辅材 */
    public static final List<String> MATERIAL_CATEGORIES = List.of("切花", "叶材", "辅材");

    /** 在售 / 停用 */
    public static final List<String> MATERIAL_STATUS = List.of("在售", "停用");

    // ---------------- 订单 ----------------
    /** 待付款 / 已付款 / 已取消 */
    public static final List<String> ORDER_STATUS = List.of("待付款", "已付款", "已取消");

    /** 订单状态流转：待付款 → 已付款（已取消为异常终态，不计入正常流转） */
    public static final List<String> ORDER_FLOW = List.of("待付款", "已付款");

    // ---------------- 配送 ----------------
    /** 待配送 / 配送中 / 已完成 / 已取消 */
    public static final List<String> DELIVERY_STATUS = List.of("待配送", "配送中", "已完成", "已取消");

    /** 配送状态流转：待配送 → 配送中 → 已完成 */
    public static final List<String> DELIVERY_FLOW = List.of("待配送", "配送中", "已完成");

    /** 配送未结案态：同一订单只允许存在一条处于该集合中的配送单 */
    public static final List<String> DELIVERY_ACTIVE = List.of("待配送", "配送中");

    // ---------------- 取值方法 ----------------

    public static boolean isMaterialCategory(String category) {
        return category != null && MATERIAL_CATEGORIES.contains(category);
    }

    public static boolean isMaterialStatus(String status) {
        return status != null && MATERIAL_STATUS.contains(status);
    }

    public static boolean isOrderStatus(String status) {
        return status != null && ORDER_STATUS.contains(status);
    }

    public static boolean isDeliveryStatus(String status) {
        return status != null && DELIVERY_STATUS.contains(status);
    }

    /** 只允许待配送 → 配送中 → 已完成 逐级推进 */
    public static boolean canTransferDelivery(String from, String to) {
        int i = DELIVERY_FLOW.indexOf(from);
        return i >= 0 && i + 1 < DELIVERY_FLOW.size() && DELIVERY_FLOW.get(i + 1).equals(to);
    }
}
