package net.noimply.sentence.business.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.noimply.sentence.business.model.SentenceModel
import net.noimply.sentence.business.support.RemoteConfig
import java.util.*

internal class ScreenViewModel(application: Application) : AndroidViewModel(application) {
    // region { content }
    private val _content = MutableLiveData<ResultViewModel<SentenceModel>>()
    val content: LiveData<ResultViewModel<SentenceModel>>
        get() {
            return _content
        }
    // endregion

    private val sentences: MutableList<SentenceModel> by lazy {
        RemoteConfig.sentences
    }

    fun requestSentence() {
        RemoteConfig.initialize {
            _content.value = ResultViewModel.InProgress
            _content.value = ResultViewModel.Complete(
                sentences[
                        Random().nextInt(sentences.size - 1)
                ]
            )
        }
    }
}