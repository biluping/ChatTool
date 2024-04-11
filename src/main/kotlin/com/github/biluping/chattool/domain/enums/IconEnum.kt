package com.github.biluping.chattool.domain.enums

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

enum class IconEnum(private val path: String) {
    USSR("/icons/user.svg"),
    ROBOT("/icons/robot.svg"),
    SEND("/icons/send.svg"),
    KOALA("/icons/koala.svg"),
    NEW_CHAT("/icons/new_chat.svg"),
    SETTING("/icons/setting.svg"),
    RETRY("/icons/retry.svg"),
    DELETE("/icons/delete.svg"),
    COPY("/icons/copy.svg")
    ;

    fun getIcon(): Icon {
        return IconLoader.getIcon(path, USSR::class.java)
    }
}