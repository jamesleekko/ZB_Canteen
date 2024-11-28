package com.znhst.xtzb.dataModel

enum class StockCategory(val chineseName: String) {
    VEGETABLE("蔬菜"),
    FRUIT("水果"),
    MEAT("肉类"),
    RICE("米饭"),
    SEASONING("调料"),
    EGG("蛋类");

    // 转换为中文名称
    fun toChinese(): String = chineseName
}

enum class StockUnit(val chineseName: String) {
    KG("千克"),
    ITEM("个");

    fun toChinese(): String = chineseName
}

data class StockItem(
    val itemId: Long,
    val name: String,
    val category: StockCategory,  // 直接用 String 映射，也可用枚举
    val unit: StockUnit,
    val description: String
)

data class Stock(
    val stockId: Long,
    val item: StockItem,
    val currentQuantity: Int,
    val lastUpdated: String,
    val deptId: Long
)

data class StockInboundRecord(
    val inboundId: Long,
    val itemId: Long,
    val quantity: Int,
    val inboundTime: String,
    val operator: String,
    val note: String?,
    val deptId: Long
)

data class StockOutboundRecord(
    val outboundId: Long,
    val itemId: Long,
    val quantity: Int,
    val reason: String,
    val outboundTime: String,
    val operator: String,
    val note: String?,
    val deptId: Long
)