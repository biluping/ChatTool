package com.github.biluping.chattool.store

import com.github.biluping.chattool.domain.bo.RolePromptBO
import com.intellij.openapi.components.*
import com.intellij.util.xmlb.Converter
import com.intellij.util.xmlb.annotations.OptionTag
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

@Service(Service.Level.APP)
@State(name = "ChatToolAppStore", storages = [Storage("ChatToolAppStore.xml")])
class ChatToolAppStore: SimplePersistentStateComponent<ChatToolAppState>(ChatToolAppState())

class ChatToolAppState: BaseState() {
    var token by string()

    @get:OptionTag(converter = RolePromptBOConverter::class)
    var rolePromptList by list<RolePromptBO>()
}

class RolePromptBOConverter: Converter<MutableList<RolePromptBO>>() {
    override fun toString(value: MutableList<RolePromptBO>): String {
        return Json.encodeToString(ListSerializer(RolePromptBO.serializer()), value)
    }

    override fun fromString(value: String): MutableList<RolePromptBO> {
        return Json.decodeFromString(ListSerializer(RolePromptBO.serializer()), value).toMutableList()
    }
}