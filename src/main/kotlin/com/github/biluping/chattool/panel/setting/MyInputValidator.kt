package com.github.biluping.chattool.panel.setting

import com.intellij.openapi.ui.InputValidatorEx

class MyInputValidator(private val errorMessage: String): InputValidatorEx {
            
    override fun getErrorText(inputString: String?): String? {
        return if(inputString.isNullOrEmpty()) {
            errorMessage
        } else {
            null
        }
    }
}