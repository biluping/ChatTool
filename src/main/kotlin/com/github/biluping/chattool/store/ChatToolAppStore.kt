package com.github.biluping.chattool.store

import com.github.biluping.chattool.MyBundle
import com.github.biluping.chattool.domain.bo.RolePromptBO
import com.intellij.openapi.components.*
import com.intellij.util.xmlb.Converter
import com.intellij.util.xmlb.annotations.OptionTag
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

@Service(Service.Level.APP)
@State(name = "ChatToolAppStore", storages = [Storage("ChatToolAppStore.xml")])
class ChatToolAppStore: SimplePersistentStateComponent<ChatToolAppState>(ChatToolAppState()) {
    init {
        // 第一次打开时，添加一个默认角色，这里必须要有，因为第一次 rolePromptList 没内容不走反序列化逻辑
        val rolePromptList = state.rolePromptList
        if (rolePromptList.size == 0 || rolePromptList[0].roleName != "默认") {
            rolePromptList.add(0, RolePromptBO("默认", "You're an all-around assistant"))
        }
    }
}

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
        val defaultMsg = MyBundle.message("default")
        val rolePromptList = Json.decodeFromString(ListSerializer(RolePromptBO.serializer()), value).toMutableList()
        if (rolePromptList.size == 0 || rolePromptList[0].roleName != defaultMsg) {
            rolePromptList.add(0, RolePromptBO(defaultMsg, "You're an all-around assistant"))
        }
        return rolePromptList
    }
}