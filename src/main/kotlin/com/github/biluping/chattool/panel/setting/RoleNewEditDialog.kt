package com.github.biluping.chattool.panel.setting

import com.github.biluping.chattool.domain.bo.RolePromptBO
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.dsl.builder.*
import javax.swing.JComponent

class RoleNewEditDialog(bo: RolePromptBO? = null): DialogWrapper(true) {

    private val rolePromptBO = bo ?: RolePromptBO("", "")
    private val panel = panel {
        row("角色名称:") {
            textField()
                .align(AlignX.FILL)
                .bindText(rolePromptBO::roleName)
        }

        row("提示词:") {
            textArea()
                .align(AlignX.FILL)
                .rows(10)
                .columns(40)
                .bindText(rolePromptBO::prompt)
                .apply {
                    this.component.lineWrap = true
                }
        }
    }

    init {
        title = "Role New Or Edit"
        init()
    }

    override fun createCenterPanel(): JComponent {
        return panel
    }

    override fun doValidate(): ValidationInfo? {
        panel.apply()
        if (rolePromptBO.roleName.isBlank() || rolePromptBO.prompt.isBlank()) {
            return ValidationInfo("角色名称和提示词不能为空")
        }
        return null
    }

    fun getRolePromptBO(): RolePromptBO {
        return rolePromptBO
    }
}