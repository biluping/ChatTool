package com.github.biluping.chattool.domain.bo

import kotlinx.serialization.Serializable

@Serializable
data class RolePromptBO(var roleName: String, var prompt: String)