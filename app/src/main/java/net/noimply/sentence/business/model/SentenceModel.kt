package net.noimply.sentence.business.model

data class SentenceModel(
    val sentenceEnglish: String,
    val sentenceKorea: String
) {
    companion object {
        fun dummy(): MutableList<SentenceModel> {
            return mutableListOf<SentenceModel>(
                SentenceModel(
                    sentenceEnglish = "I'm glad to hear it.",
                    sentenceKorea = "그것을 들으니 좋네요."
                ),
                SentenceModel(
                    sentenceEnglish = "Things will work out all right.",
                    sentenceKorea = "일이 잘 될 것입니다."
                ),
                SentenceModel(
                    sentenceEnglish = "I wish I could.",
                    sentenceKorea = "내가 할 수 있으면 좋겠어요."
                ),
                SentenceModel(
                    sentenceEnglish = "My room needs to be cleaned, please.",
                    sentenceKorea = "제 방을 청소해 주세요."
                ),
                SentenceModel(
                    sentenceEnglish = "Today is a big day.",
                    sentenceKorea = "오늘은 참 중요한 날이에요."
                )
            )
        }
    }
}