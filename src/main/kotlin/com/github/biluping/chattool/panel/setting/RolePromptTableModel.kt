package com.github.biluping.chattool.panel.setting

import com.github.biluping.chattool.domain.bo.RolePromptBO
import com.intellij.ui.AddEditRemovePanel

class RolePromptTableModel : AddEditRemovePanel.TableModel<RolePromptBO>() {

        private val column: MutableList<String> = mutableListOf("角色", "提示词")

        override fun getColumnCount(): Int {
            return column.size
        }

        override fun getField(rolePromptBO: RolePromptBO, columnIndex: Int): Any {
            return when (columnIndex) {
                0 -> rolePromptBO.roleName
                1 -> rolePromptBO.prompt
                else -> ""
            }
        }

        override fun getColumnClass(columnIndex: Int): Class<*> {
            return String::class.java
        }

        override fun isEditable(column: Int): Boolean {
            return false
        }

        override fun setValue(value: Any, data: RolePromptBO, columnIndex: Int) {
            when (columnIndex) {
                0 -> data.roleName = value.toString()
                1 -> data.prompt = value.toString()
            }
        }

        override fun getColumnName(columnIndex: Int): String {
            return column[columnIndex]
        }
    }