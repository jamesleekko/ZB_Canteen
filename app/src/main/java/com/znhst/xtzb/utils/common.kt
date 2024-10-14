package com.znhst.xtzb.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun calculateDpValue(radius: Dp): Int {
    // 使用 LocalDensity 来获取 dp 到 px 的转换率
    val density = LocalDensity.current

    // 将 Dp 转换为 px 并计算
    return with(density) {
        val radiusPx = radius.toPx() // 将 dp 转换为 px
        val result = radiusPx / 1.41  // 进行除法运算
        result.toInt()  // 取整
    }
}