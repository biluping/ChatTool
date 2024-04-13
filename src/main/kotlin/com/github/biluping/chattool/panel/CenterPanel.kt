package com.github.biluping.chattool.panel

import com.github.biluping.chattool.panel.message.AbstractMessagePanel
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.util.ui.JBUI
import com.theokanning.openai.completion.chat.ChatMessage
import com.theokanning.openai.completion.chat.ChatMessageRole
import javax.swing.JPanel
import javax.swing.SwingUtilities

@Service(Service.Level.PROJECT)
class CenterPanel(private val project: Project) : JBScrollPane() {

    private val panel = JPanel(VerticalLayout(0))
    private val messagePanelList = mutableListOf<AbstractMessagePanel>()

    init {
        setViewportView(panel)
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED)
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER)
        border = JBUI.Borders.customLine(JBColor.border(), 1, 0, 1, 0)
    }

    fun addMessage(messagePanel: AbstractMessagePanel) {
        messagePanelList.add(messagePanel)
        SwingUtilities.invokeLater {
            panel.add(messagePanel.getPanel())
        }
    }

    fun getChatMessageList(abstractMessagePanel: AbstractMessagePanel? = null): List<ChatMessage> {
        val index = abstractMessagePanel?.let { messagePanelList.indexOf(it) } ?: (messagePanelList.size - 1)
        val chatMessageList = messagePanelList.subList(0, index).map {
            ChatMessage(it.getRole().value(), it.getText())
        }.toMutableList()
        project.service<TopPanel>().getCurrentPrompt()?.let {
            chatMessageList.add(0, ChatMessage(ChatMessageRole.SYSTEM.value(), it))
        }
        return chatMessageList
    }

    fun newChat() {
        messagePanelList.clear()
        SwingUtilities.invokeLater {
            panel.removeAll()
            panel.revalidate()
            panel.repaint()
        }
    }

    fun removeMessage(abstractMessagePanel: AbstractMessagePanel) {
        messagePanelList.remove(abstractMessagePanel)
        SwingUtilities.invokeLater {
            panel.remove(abstractMessagePanel.getPanel())
            panel.revalidate()
            panel.repaint()
        }
    }
}