package com.github.biluping.chattool.panel

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import javax.swing.JPanel

@Service(Service.Level.PROJECT)
class CenterPanel(project: Project) : JPanel() {

    init {

        println(project)
    }
}