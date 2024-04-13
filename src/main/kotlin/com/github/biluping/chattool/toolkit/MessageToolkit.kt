package com.github.biluping.chattool.toolkit

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications

// 通知工具类
private fun notify(message: String, notificationType: NotificationType) {
    val notification = Notification("chat-tool", message, notificationType)
    Notifications.Bus.notify(notification)
}

fun notifyWarn(message: String) {
    notify(message, NotificationType.WARNING)
}

fun notifyInfo(message: String) {
    notify(message, NotificationType.INFORMATION)
}
