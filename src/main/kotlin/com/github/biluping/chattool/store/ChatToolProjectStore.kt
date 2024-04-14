package com.github.biluping.chattool.store

import com.github.biluping.chattool.MyBundle
import com.intellij.openapi.components.*

@Service(Service.Level.PROJECT)
@State(name = "ChatToolAppStore")
class ChatToolProjectStore: SimplePersistentStateComponent<ChatToolProjectState>(ChatToolProjectState())

class ChatToolProjectState : BaseState() {

    var selectedRoleName by string(MyBundle.message("default"))
}