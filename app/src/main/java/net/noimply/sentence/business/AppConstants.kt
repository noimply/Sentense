package net.noimply.sentence.business

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
}