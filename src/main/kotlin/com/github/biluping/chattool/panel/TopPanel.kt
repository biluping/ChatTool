package com.github.biluping.chattool.panel

import com.github.biluping.chattool.domain.enums.IconEnum
import com.github.biluping.chattool.panel.setting.ChatToolSettingDialog
import com.github.biluping.chattool.service.ChatService
import com.github.biluping.chattool.store.ChatToolAppStore
import com.github.biluping.chattool.store.ChatToolProjectStore
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.CollectionComboBoxModel
import com.intellij.ui.components.IconLabelButton
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.JPanel

@Service(Service.Level.PROJECT)
class TopPanel(private val project: Project) : JPanel(BorderLayout()) {

    private val chatService = project.service<ChatService>()
    private val rolePromptList = service<ChatToolAppStore>().state.rolePromptList
    private val chatToolProjectStore = project.service<ChatToolProjectStore>()
    private val systemRoleSelect: ComboBox<String> = ComboBox(rolePromptList.map { it.roleName }.toTypedArray())

    init {
        systemRoleSelect.selectedItem = chatToolProjectStore.state.selectedRoleName
        systemRoleSelect.addActionListener {
            chatToolProjectStore.state.selectedRoleName = systemRoleSelect.selectedItem as String
        }
        add(systemRoleSelect, BorderLayout.WEST)
        add(createActionPanel(), BorderLayout.EAST)
    }

    private fun createActionPanel(): JPanel {
        val jPanel = JPanel(FlowLayout())
        jPanel.add(IconLabelButton(IconEnum.SETTING.getIcon()) {
            val dialog = ChatToolSettingDialog()
            if (dialog.showAndGet()) {
                dialog.applyBinding()
                chatService.resetOpenAiService()
            }
            updateRoleSelect()
        })
        jPanel.add(IconLabelButton(IconEnum.NEW_CHAT.getIcon()) { project.service<CenterPanel>().newChat() })
        return jPanel
    }

    private fun updateRoleSelect() {
        systemRoleSelect.model = CollectionComboBoxModel(rolePromptList.map { it.roleName })
        systemRoleSelect.selectedItem = chatToolProjectStore.state.selectedRoleName
    }

    fun getCurrentPrompt(): String? {
        if (systemRoleSelect.selectedItem == null) {
            return null
        }
        rolePromptList.first { it.roleName == systemRoleSelect.selectedItem }.let {
            return it.prompt
        }
    }
}