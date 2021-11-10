package net.noimply.sentence.business.model

data class AppVersionModel(
    val versionCode: Int,
    val versionName: String,
    val description: String,
    val marketUrl: String
)