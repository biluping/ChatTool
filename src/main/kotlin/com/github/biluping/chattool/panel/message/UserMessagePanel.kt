package com.github.biluping.chattool.panel.message

import com.github.biluping.chattool.domain.enums.IconEnum
import com.intellij.openapi.project.Project
import com.intellij.ui.JBColor
import com.theokanning.openai.completion.chat.ChatMessageRole
import javax.swing.JComponent

class UserMessagePanel(project: Project): AbstractMessagePanel(project) {
    override fun getBgColor() = JBColor(0xF7F7F7, 0x3C3F41)
    override fun getAvatarIcon() = IconEnum.KOALA.getIcon()
    override fun getUsername() = "CafeBabe"
    override fun getRole() = ChatMessageRole.USER
    override fun getExtraActions() = emptyList<JComponent>()
}