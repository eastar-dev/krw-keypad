package dev.eastar.moneykeypad

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class MoneyTextWatcher(private val editText: EditText) : TextWatcher {
    private var afterTextChange: Boolean = false
    var countOfNumberAfterCursor = 0

    override fun beforeTextChanged(text: CharSequence, start: Int, count: Int, after: Int) {
        if (afterTextChange)
            return

        countOfNumberAfterCursor =
            text.takeLast(editText.length() - editText.selectionEnd).count { it in '0'..'9' }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(editable: Editable) {
        if (afterTextChange)
            return
        afterTextChange = true

        val text = editable.toString().own_
        if (text != editText.text.toString()) {
            editText.setText(text)
        }

        var countOfNumberAfterCursor = countOfNumberAfterCursor
        var sel = text.indexOfLast { ch ->
            countOfNumberAfterCursor -= if (ch in '0'..'9') 1 else 0
            countOfNumberAfterCursor <= 0
        }
        if (sel > 0 && text[sel - 1] !in '0'..'9')
            sel--

        editText.setSelection(sel)
        afterTextChange = false
    }
}