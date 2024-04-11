package com.github.biluping.chattool.panel

import com.github.biluping.chattool.domain.enums.IconEnum
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.IconLabelButton
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.JPanel

@Service(Service.Level.PROJECT)
class TopPanel(project: Project) : JPanel(BorderLayout()) {

    private val systemRoleSelect: ComboBox<String> = ComboBox(arrayOf("默认角色", "扎娃工程师", "前端工程师"))

    init {
        add(systemRoleSelect, BorderLayout.WEST)
        add(createActionPanel(), BorderLayout.EAST)
    }

    private fun createActionPanel(): JPanel {
        val jPanel = JPanel(FlowLayout())
        jPanel.add(IconLabelButton(IconEnum.SETTING.getIcon(), {}))
        jPanel.add(IconLabelButton(IconEnum.NEW_CHAT.getIcon(), {}))
        return jPanel
    }

}