package com.github.biluping.chattool.panel

import com.github.biluping.chattool.domain.enums.IconEnum
import com.github.biluping.chattool.panel.message.GptMessagePanel
import com.github.biluping.chattool.panel.message.UserMessagePanel
import com.github.biluping.chattool.service.ChatService
import com.github.biluping.chattool.store.ChatToolAppStore
import com.github.biluping.chattool.toolkit.notifyWarn
import com.intellij.icons.AllIcons
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextArea
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.Cursor
import java.awt.FlowLayout
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPanel
import javax.swing.SwingUtilities

@Service(Service.Level.PROJECT)
class BottomPanel(private val project: Project) : JPanel(BorderLayout()) {

    private val chatToolAppStore = service<ChatToolAppStore>()
    private val centerPanel = project.service<CenterPanel>()
    private val chatService = project.service<ChatService>()

    private val jbTextArea = JBTextArea().apply {
        border = JBUI.Borders.empty(JBUI.scale(8), JBUI.scale(4))
        emptyText.text = "Type your message here..."
        lineWrap = true
        isOpaque = false
        addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (!e.isShiftDown && e.keyCode == KeyEvent.VK_ENTER) {
                    doSubmit()
                }
            }
        })
    }
    private val sendBtn = JBLabel(IconEnum.SEND.getIcon()).apply {
        cursor = Cursor(Cursor.HAND_CURSOR)
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    doSubmit()
                }
            }
        })
    }
    private val stopBtn = JBLabel(AllIcons.Actions.Suspend).apply {
        isVisible = false
        cursor = Cursor(Cursor.HAND_CURSOR)
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    chatService.disposable()
                    changeBtnVisible(true)
                }
            }
        })
    }

    init {
        add(jbTextArea, BorderLayout.CENTER)
        add(createBtnPanel(), BorderLayout.EAST)
        chatService.changeBtnVisibleFun = { changeBtnVisible(it) }
    }

    private fun createBtnPanel(): JPanel {
        val jPanel = JPanel(FlowLayout())
        jPanel.add(sendBtn)
        jPanel.add(stopBtn)
        return jPanel
    }

    private fun doSubmit() {
        if (!chatService.getIsFinish()) {
            notifyWarn("请等待当前请求结束")
            return
        }
        if (jbTextArea.text.isBlank()) {
            notifyWarn("请输入的问题")
            return
        }
        if (chatToolAppStore.state.token.isNullOrBlank()) {
            notifyWarn("请先填写 token")
            return
        }
        UserMessagePanel(project).apply {
            // 如果当前编辑器有选中代码，则将选中的代码加入到提问中
            FileEditorManager.getInstance(project).selectedTextEditor?.apply {
                val selectedText = this.selectionModel.selectedText
                val file = FileDocumentManager.getInstance().getFile(this.document)
                if (!selectedText.isNullOrBlank() && file != null) {
                     val selectContent = "下面对来自 %s 的代码片段进行提问\n```%s\n%s\n```\n".format(
                        file.name,
                        file.extension,
                        selectedText,
                    )
                    offerToQueue(selectContent)
                }
            }
            offerToQueue(jbTextArea.text)
            centerPanel.addMessage(this)
        }
        GptMessagePanel(project).apply {
            centerPanel.addMessage(this)
            chatService.sendMessage(centerPanel.getChatMessageList(), this)
        }
        SwingUtilities.invokeLater {
            jbTextArea.text = null
        }
    }

    private fun changeBtnVisible(isFinish: Boolean) {
        SwingUtilities.invokeLater {
            sendBtn.isVisible = isFinish
            stopBtn.isVisible = !isFinish
        }
    }

}