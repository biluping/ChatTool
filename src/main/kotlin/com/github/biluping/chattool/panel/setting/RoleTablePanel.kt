package com.github.biluping.chattool.panel.setting

import com.github.biluping.chattool.domain.bo.RolePromptBO
import com.intellij.openapi.ui.Messages
import com.intellij.ui.AddEditRemovePanel

class RoleTablePanel(rolePromptList: MutableList<RolePromptBO>): AddEditRemovePanel<RolePromptBO>(RolePromptTableModel(), rolePromptList) {

        override fun addItem(): RolePromptBO? {
            val roleName = Messages.showInputDialog("请输入角色名称", "添加角色", null, null, MyInputValidator("角色名称不能为空"))
            if (roleName.isNullOrBlank()) {
                return null
            }

            val prompt = Messages.showMultilineInputDialog(null, "请输入提示词", "添加提示词", null, null, MyInputValidator("提示词不能为空"))
            if (prompt.isNullOrBlank()) {
                return null
            }

            return RolePromptBO(roleName, prompt)
        }

        override fun removeItem(bo: RolePromptBO): Boolean {
            return true
        }

        override fun editItem(bo: RolePromptBO): RolePromptBO? {
            val roleName = Messages.showInputDialog("请输入角色名称", "编辑角色", null, bo.roleName, MyInputValidator("角色名称不能为空"))
            if (roleName.isNullOrBlank()) {
                return null
            }

            val prompt = Messages.showMultilineInputDialog(null, "请输入提示词", "编辑提示词", bo.prompt, null, MyInputValidator("提示词不能为空"))
            if (prompt.isNullOrBlank()) {
                return null
            }

            bo.roleName = roleName
            bo.prompt = prompt
            return bo
        }
    }