package net.noimply.sentence.business

import net.noimply.sentence.BuildConfig

//application tag name
const val TAG = "noimply-sentence"

object AppConstants {
    const val MARKET_URL = "market://details?id=net.noimply.sentence"

    val weekNamesKor = arrayOf("월", "화", "수", "목", "금", "토", "일")

    val weekNamesEng = arrayOf(
        "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"
    )

    val monthNameEng = arrayOf(
        "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"
    )

    object Action {
        const val ACTION_NEED_SERVICE_TO_LIVE = "action:need:service:live"
    }

    object PlacementID {
        val appOpen: String
            get() {
                return when (BuildConfig.X_BUILD_ALLOW_AD) {
                    true -> "ca-app-pub-7629363633288746/3261043495"
                    false -> "ca-app-pub-3940256099942544/1033173712"
                }
            }

        val appCommonLinear: String
            get() {
                return when (BuildConfig.X_BUILD_ALLOW_AD) {
                    true -> "ca-app-pub-7629363633288746/8868593392"
                    false -> "ca-app-pub-3940256099942544/6300978111"
                }
            }
    }
}