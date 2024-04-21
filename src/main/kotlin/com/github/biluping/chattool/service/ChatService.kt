package com.github.biluping.chattool.service

import com.github.biluping.chattool.panel.message.AbstractMessagePanel
import com.github.biluping.chattool.store.ChatToolAppStore
import com.github.biluping.chattool.toolkit.notifyWarn
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.theokanning.openai.completion.chat.ChatCompletionRequest
import com.theokanning.openai.completion.chat.ChatMessage
import com.theokanning.openai.service.OpenAiService
import io.reactivex.disposables.Disposable

@Service(Service.Level.PROJECT)
class ChatService {

    private val chatToolAppStore = service<ChatToolAppStore>()
    private var openAiService: OpenAiService? = null
    var changeBtnVisibleFun: ((Boolean) -> Unit)? = null
    @Volatile
    private var isFinish = true
    @Volatile
    private var disposable: Disposable? = null

    init {
        resetOpenAiService()
    }

    fun getIsFinish(): Boolean {
        return isFinish
    }

    fun disposable() {
        disposable?.dispose()
        isFinish = true
    }

    fun resetOpenAiService() {
        openAiService?.shutdownExecutor()
        openAiService = null
        if (!chatToolAppStore.state.token.isNullOrBlank()) {
            openAiService = OpenAiService(chatToolAppStore.state.token)
        }
    }

    fun sendMessage(chatMessageList: List<ChatMessage>, abstractMessagePanel: AbstractMessagePanel) {
        isFinish = false
        changeBtnVisibleFun!!(false)
        Thread {
            val chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo")
                .messages(chatMessageList)
                .temperature(0.4)
                .topP(0.95)
                .logitBias(HashMap())
                .build()

            disposable = openAiService!!.streamChatCompletion(chatCompletionRequest).doOnNext { chunk ->
                chunk.choices.forEach {
                    abstractMessagePanel.offerToQueue(it.message.content ?: "")
                }
            }.doOnError {
                abstractMessagePanel.offerToQueue(it.message, true)
                changeBtnVisibleFun!!(true)
                isFinish = true
            }.doOnComplete {
                abstractMessagePanel.offerToQueue("", true)
                changeBtnVisibleFun!!(true)
                isFinish = true
            }.subscribe()

        }.start()
    }
}

fun checkChatRequestIng(project: Project): Boolean {
    val isFinish = project.service<ChatService>().getIsFinish()
    if (!isFinish) {
        notifyWarn("请等待当前请求完成")
    }
    return isFinish
}