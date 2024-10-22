package com.znhst.xtzb.dataModel

data class ZBDeviceInfo(
    val deviceSerial: String?,
    val deviceType: String?,
    val deviceName: String?,
    val deviceIp: String?,
    val deviceStatus: Int?,
    val cameraNo: Int?
)

data class ZBDeviceCategory (
    val type: Int,
    val name: String,
)