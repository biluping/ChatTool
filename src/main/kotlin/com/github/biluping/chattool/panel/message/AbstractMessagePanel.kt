package com.github.biluping.chattool.panel.message

import com.github.biluping.chattool.domain.enums.IconEnum
import com.github.biluping.chattool.panel.CenterPanel
import com.github.biluping.chattool.service.checkChatRequestIng
import com.github.biluping.chattool.toolkit.notifyInfo
import com.intellij.openapi.components.service
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.project.Project
import com.intellij.ui.JBColor
import com.intellij.ui.components.IconLabelButton
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.util.IconUtil
import com.intellij.util.ui.JBUI
import com.theokanning.openai.completion.chat.ChatMessageRole
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.Theme
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.Font
import java.awt.datatransfer.StringSelection
import java.util.concurrent.LinkedBlockingQueue
import javax.swing.*

abstract class AbstractMessagePanel(private val project: Project) {

    private var mainPanel: JPanel? = null
    private val messagePanel = JPanel(VerticalLayout(0))
    private var syntaxTextArea: RSyntaxTextArea? = null
    private var textArea: JTextArea? = null
    private val messageQueue = LinkedBlockingQueue<Char>()
    private val text = StringBuilder()
    protected val centerPanel = project.service<CenterPanel>()

    fun getPanel(): JPanel {
        if (mainPanel != null) return mainPanel!!
        mainPanel = JPanel(BorderLayout()).apply {
            background = getBgColor()
            border = JBUI.Borders.empty(JBUI.scale(6), 0)

            JPanel(BorderLayout()).also {
                it.isOpaque = false
                it.add(createAvatarPanel(), BorderLayout.WEST)
                it.add(createActionPanel(), BorderLayout.EAST)
                add(it, BorderLayout.NORTH)
            }

            messagePanel.also {
                it.border = JBUI.Borders.emptyLeft(5)
                it.isOpaque = false
                add(it, BorderLayout.CENTER)
            }
            startPaintMessage()
        }
        return mainPanel!!
    }

    fun offerToQueue(txt: String?, isFinish: Boolean = false) {
        for (c in txt ?: "") {
            text.append(c)
            messageQueue.offer(c)
        }
        if (isFinish) {
            messageQueue.offer(Char(0))
            return
        }
    }

    private fun startPaintMessage() {
        Thread {
            var currStr = ""
            while (true) {
                Thread.sleep(2)
                val char = messageQueue.take()
                if (char == Char(0)) {
                    break
                }
                SwingUtilities.invokeLater {
                    currStr += char
                    if (currStr.matches(Regex(""".*\n+```[a-z]*\n$""", RegexOption.DOT_MATCHES_ALL))) {
                        if (syntaxTextArea == null) {
                            textArea?.text = currStr.replace(Regex("""\n+```[a-z]*\n+"""), "")
                            textArea = null
                            val language = currStr.substring(currStr.lastIndexOf("`") + 1, currStr.length - 1)
                            syntaxTextArea = RSyntaxTextArea()
                            syntaxTextArea!!.lineWrap = true
                            syntaxTextArea!!.isEditable = false
                            syntaxTextArea!!.border = JBUI.Borders.emptyBottom(5)
                            syntaxTextArea!!.syntaxEditingStyle = "text/$language"
                            syntaxTextArea!!.font =
                                Font(syntaxTextArea!!.font.name, syntaxTextArea!!.font.style, 10)
                            if (JBColor.isBright().not()) {
                                Theme.load(javaClass.getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/dark.xml"))
                                    .apply(syntaxTextArea)
                            }
                            messagePanel.add(syntaxTextArea)
                            messagePanel.revalidate()
                            messagePanel.repaint()
                        } else {
                            syntaxTextArea!!.text = currStr.replace(Regex("\n+```\n$"), "")
                            syntaxTextArea = null
                        }
                        currStr = ""
                        return@invokeLater
//                        continue
                    }
                    if (syntaxTextArea != null) {
                        syntaxTextArea!!.append(char.toString())
                    } else {
                        if ((textArea == null)) {
                            textArea = JTextArea()
                            textArea!!.lineWrap = true
                            textArea!!.isOpaque = false
                            textArea!!.isEditable = false
                            textArea!!.border = JBUI.Borders.emptyBottom(5)
                            textArea!!.font = Font(textArea!!.font.name, textArea!!.font.style, 12)
                            messagePanel.add(textArea)
                            messagePanel.revalidate()
                            messagePanel.repaint()
                            if (char == '\n') return@invokeLater
                        }
                        // todo 代码快结束的 \n
                        textArea!!.append(char.toString())
                    }
                }
            }
        }.start()
    }

    private fun createAvatarPanel(): JPanel {
        val jPanel = JPanel(FlowLayout(FlowLayout.LEFT)).apply { isOpaque = false }
        JBLabel(IconUtil.scale(getAvatarIcon(), jPanel, 0.8f)).apply {
            isOpaque = false
            jPanel.add(this)
        }
        JBLabel(getUsername()).apply {
            isOpaque = false
            jPanel.add(this)
        }
        return jPanel
    }

    private fun createActionPanel(): JPanel {
        val jPanel = JPanel(FlowLayout()).apply { isOpaque = false }
        getExtraActions().forEach {
            jPanel.add(it)
        }
        IconLabelButton(IconEnum.DELETE.getIcon()) {
            if (!checkChatRequestIng(project)) {
                return@IconLabelButton
            }
            centerPanel.removeMessage(this)
        }.apply {
            jPanel.add(this)
        }
        IconLabelButton(IconEnum.COPY.getIcon()) {
            if (!checkChatRequestIng(project)) {
                return@IconLabelButton
            }
            CopyPasteManager.getInstance().setContents(StringSelection(text.toString()))
            notifyInfo("Copied successfully")
        }.apply {
            jPanel.add(this)
        }
        return jPanel
    }

    fun getText(): String {
        return text.toString()
    }

    fun reset() {
        text.clear()
        syntaxTextArea = null
        textArea = null
        SwingUtilities.invokeLater {
            messagePanel.removeAll()
            messagePanel.revalidate()
            messagePanel.repaint()
            startPaintMessage()
        }
    }

    protected abstract fun getBgColor(): JBColor
    protected abstract fun getAvatarIcon(): Icon
    protected abstract fun getUsername(): String
    abstract fun getRole(): ChatMessageRole
    protected abstract fun getExtraActions(): List<JComponent>

}