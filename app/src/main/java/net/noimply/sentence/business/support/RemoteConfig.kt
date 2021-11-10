package net.noimply.sentence.business.support

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import net.noimply.sentence.business.AppConstants
import net.noimply.sentence.business.model.AppNoticeModel
import net.noimply.sentence.business.model.AppVersionModel
import net.noimply.sentence.business.model.SentenceModel
import org.json.JSONArray
import org.json.JSONObject

internal object RemoteConfig {
    fun initialize(callback: (success: Boolean) -> Unit) {
        Firebase.remoteConfig.apply {
            setConfigSettingsAsync(remoteConfigSettings {
                minimumFetchIntervalInSeconds = 3600
            })
        }

        Firebase.remoteConfig
            .fetchAndActivate()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true)
                    Logger.info { "remote-config -> fetchAndActivate: isSuccessful" }
                } else {
                    callback(false)
                    Logger.error { "remote-config -> fetchAndActivate: ${it}" }
                }
            }
    }

    val appVersionModel: AppVersionModel
        get() {
            return try {
                val json = Firebase.remoteConfig.getString("appVersion")
                val obj = JSONObject(json)
                val marketUrl = obj.toStringValue("marketUrl")
                AppVersionModel(
                    versionCode = obj.toIntValue("versionCode"),
                    versionName = obj.toStringValue("versionName"),
                    description = obj.toStringValue("description"),
                    marketUrl = if (marketUrl.isNotEmpty()) marketUrl else AppConstants.MARKET_URL
                )
            } catch (e: Exception) {
                AppVersionModel(
                    versionCode = 0,
                    versionName = "0.0.0",
                    description = "",
                    marketUrl = AppConstants.MARKET_URL
                )
            }
        }

    val appNoticeModel: AppNoticeModel
        get() {
            return try {
                val json = Firebase.remoteConfig.getString("appNotice")
                val obj = JSONObject(json)
                AppNoticeModel(
                    noticeID = obj.toStringValue("noticeID"),
                    noticeMessage = obj.toStringValue("noticeMessage")
                )
            } catch (e: Exception) {
                AppNoticeModel(
                    noticeID = "",
                    noticeMessage = ""
                )
            }
        }

    val sentences: MutableList<SentenceModel>
        get() {
            var sentences: MutableList<SentenceModel> = mutableListOf()
            try {
                val json = Firebase.remoteConfig.getString("sentences")
                if (json.isNotEmpty()) {
                    JSONArray(json).until {
                        sentences.add(
                            SentenceModel(
                                sentenceEnglish = it.toStringValue("sentenceEnglish"),
                                sentenceKorea = it.toStringValue("sentenceKorea")
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                Logger.error(throwable = e) {
                    "RemoteConfig -> sentences"
                }
            }
            if (sentences.isEmpty()) {
                sentences = SentenceModel.dummy()
            }
            return sentences
        }
}