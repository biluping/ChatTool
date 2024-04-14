package com.github.biluping.chattool.panel.setting

import com.github.biluping.chattool.MyBundle
import com.github.biluping.chattool.domain.bo.RolePromptBO
import com.intellij.ui.AddEditRemovePanel

class RoleTablePanel(rolePromptList: MutableList<RolePromptBO>): AddEditRemovePanel<RolePromptBO>(RolePromptTableModel(), rolePromptList) {

        override fun addItem(): RolePromptBO? {

            val dialog = RoleNewEditDialog()
            if (dialog.showAndGet()) {
                return dialog.getRolePromptBO()
            }
            return null
        }

        override fun removeItem(bo: RolePromptBO): Boolean {
            return bo.roleName != MyBundle.message("default")
        }

        override fun editItem(bo: RolePromptBO): RolePromptBO {
            val dialog = RoleNewEditDialog(bo.copy())
            if (dialog.showAndGet()) {
                bo.roleName = dialog.getRolePromptBO().roleName
                bo.prompt = dialog.getRolePromptBO().prompt
            }
            return bo
        }
    }