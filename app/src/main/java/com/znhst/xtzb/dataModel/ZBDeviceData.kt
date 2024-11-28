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

data class TempHumiInfo(
    val deviceNo: String,
    val deviceAlias: String,
)

data class DoorInfo(
    val doorSN: String,
    val doorGuid: String,
    val doorName: String,
    val doorStatus: Int,
    val lineWay: String,
)

enum class TempHumiCategory(val value: String) {
    LAB("lab"),
    CANTEEN("canteen");

    override fun toString(): String = value
}