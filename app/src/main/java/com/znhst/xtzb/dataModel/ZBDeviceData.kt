package com.znhst.xtzb.dataModel

data class EZDeviceInfo(
    val deviceSerial: String?,
    val deviceType: String?,
    val deviceName: String?,
    val deviceIp: String?,
    val deviceStatus: Int?,
    val cameraNo: Int?
)

data class EZDeviceCategory(
    val type: Int,
    val name: String,
    val displayName: String
)

data class FreezerInfo(
    val deviceNo: String,
    val deviceAlias: String,
)

data class SmokeAlarmInfo(
    val deviceNo: String,
    val deviceAlias: String,
)