package com.github.biluping.chattool.panel.setting

import com.github.biluping.chattool.store.ChatToolAppStore
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.RowLayout
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import java.awt.Dimension
import javax.swing.JPanel

class ChatToolSettingDialog : DialogWrapper(true) {

    private val chatToolAppStore = service<ChatToolAppStore>()
    private val tokenPanel = panel {
        row("Token:") {
            textField()
                .align(AlignX.FILL)
                .bindText({chatToolAppStore.state.token?:""}, {
                    println(it)
                    chatToolAppStore.state.token = it
                })
        }.layout(RowLayout.INDEPENDENT)
    }

    init {
        title = "ChatTool 设置"
        init()
    }

    override fun createCenterPanel(): JPanel {
        val roleTablePanel = RoleTablePanel(chatToolAppStore.state.rolePromptList)
        roleTablePanel.preferredSize = Dimension(500,300)

        return JPanel(VerticalLayout(10)).apply {
            add(tokenPanel)
            add(roleTablePanel)
        }
    }

    fun applyBinding() {
        tokenPanel.apply()
    }

}