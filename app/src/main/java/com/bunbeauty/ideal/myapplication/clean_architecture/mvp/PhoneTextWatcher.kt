package com.bunbeauty.ideal.myapplication.clean_architecture.mvp

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText

class PhoneTextWatcher(private val phoneEditText: TextInputEditText) : TextWatcher {

    @SuppressLint("SetTextI18n")
    override fun afterTextChanged(text: Editable?) {
        var cursorPosition = phoneEditText.selectionEnd

        val numbers = text.toString().replace(Regex("\\D"), "")
        val firstGroup = numbers.take(3)
        val secondGroup = numbers.drop(3).take(3)
        val thirdGroup = numbers.drop(6).take(2)
        val fourthGroup = numbers.drop(8).take(2)

        var result = ""
        if (numbers.isNotEmpty()) {
            result += "($firstGroup"
        }

        if (secondGroup.isNotEmpty()) {
            result += ") $secondGroup"
        }

        if (thirdGroup.isNotEmpty()) {
            result += "-$thirdGroup"
        }

        if (fourthGroup.isNotEmpty()) {
            result += "-$fourthGroup"
        }

        when (cursorPosition) {
            1 -> {
                // "(" -> ""
                if (text.toString() == "(") {
                    cursorPosition--
                }

                // "0" -> "(0"
                else {
                    if (text?.first() != '(') {
                        cursorPosition++
                    }
                }
            }
            5 -> {
                if (text!![4] != ')') {
                    cursorPosition += 2
                }
            }
            6 -> {
                if (text!![5] == ' ') {
                    cursorPosition -= 2
                } else {
                    cursorPosition++
                }
            }
            10 -> {
                // "(000) 000-" -> "(000) 000"
                if (text?.last() == '-') {
                    cursorPosition--
                }

                // "(000) 0000" -> "(000) 000-0"
                else {
                    if (text!![9] != '-') {
                        cursorPosition++
                    }
                }
            }
            13 -> {
                // "(000) 000-00-" -> "(000) 000-00"
                if (text?.last() == '-') {
                    cursorPosition--
                }

                // "(000) 000-000" -> "(000) 000-00-0"
                else {
                    if (text!![12] != '-') {
                        cursorPosition++
                    }
                }
            }
            16 -> {
                // "(000) 000-00-000" -> "(000) 000-00-00"
                cursorPosition--
            }
        }

        if (text.toString() != result) {
            phoneEditText.setText(result)
            phoneEditText.setSelection(cursorPosition)
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }
}