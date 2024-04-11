package com.github.biluping.chattool.toolWindow

import com.github.biluping.chattool.panel.BottomPanel
import com.github.biluping.chattool.panel.CenterPanel
import com.github.biluping.chattool.panel.TopPanel
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import java.awt.BorderLayout


class ChatToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val chatToolWindow = ChatToolWindow(project)
        val content = ContentFactory.getInstance().createContent(chatToolWindow.getContent(), null, false)
        toolWindow.contentManager.addContent(content)
    }

    class ChatToolWindow(project: Project) {

        private val topPanel = project.service<TopPanel>()
        private val centerPanel = project.service<CenterPanel>()
        private val bottomPanel = project.service<BottomPanel>()

        fun getContent() = JBPanel<JBPanel<*>>(BorderLayout()).apply {
            add(topPanel, BorderLayout.NORTH)
            add(centerPanel, BorderLayout.CENTER)
            add(bottomPanel, BorderLayout.SOUTH)
        }
    }
}
