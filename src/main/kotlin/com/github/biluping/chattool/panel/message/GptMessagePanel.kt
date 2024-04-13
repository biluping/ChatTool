package com.github.biluping.chattool.panel.message

import com.github.biluping.chattool.domain.enums.IconEnum
import com.github.biluping.chattool.service.ChatService
import com.github.biluping.chattool.service.checkChatRequestIng
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.ui.JBColor
import com.intellij.ui.components.IconLabelButton
import com.theokanning.openai.completion.chat.ChatMessageRole
import javax.swing.JComponent

class GptMessagePanel(private val project: Project): AbstractMessagePanel(project) {

    private val chatService: ChatService = project.service<ChatService>()

    override fun getBgColor(): JBColor = JBColor(0xEBEBEB, 0x2d2f30)
    override fun getAvatarIcon() = IconEnum.ROBOT.getIcon()
    override fun getUsername() = "MyChat"
    override fun getRole() = ChatMessageRole.ASSISTANT
    override fun getExtraActions() = arrayListOf<JComponent>().also {
        val retryBtn = IconLabelButton(IconEnum.RETRY.getIcon()) {
            if (!checkChatRequestIng(project)) {
                return@IconLabelButton
            }
            val chatMessageList = centerPanel.getChatMessageList(this)
            super.reset()
            chatService.sendMessage(chatMessageList, this)
        }
        it.add(retryBtn)
    }
}