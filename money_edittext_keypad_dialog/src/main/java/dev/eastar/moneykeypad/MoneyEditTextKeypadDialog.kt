package dev.eastar.moneykeypad

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import dev.eastar.moneykeypad.databinding.MoneyEdittextKeypadBinding

typealias MoneyEditTextKeypadCallback = (Map<String, String>) -> Unit

class MoneyEditTextKeypadDialog : AppCompatDialogFragment() {
    private var callback: MoneyEditTextKeypadCallback? = null
    lateinit var bb: MoneyEdittextKeypadBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.MoneyKeyPadTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bb = MoneyEdittextKeypadBinding.inflate(inflater, container, false)
        return bb.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onLoadOnce()
    }

    fun onLoadOnce() {
        bb.groupEditLayer.isVisible = true
        bb.editText.hint = "Money"
        bb.editText.addTextChangedListener(MoneyTextWatcher(bb.editText))
        bb.editText.doAfterTextChanged {
            fireCallback(
                RES_EDIT_VALUE to "" + bb.editText.text.toString().parseMoney,
            )
        }

        bb.clear.setOnClickListener { bb.editText.setText("") }
        bb.editText.showSoftInputOnFocus = false
        bb.close.setOnClickListener { dialog?.cancel() }
        bb.dim.setOnClickListener { dialog?.cancel() }

        bb.numbers.children.forEach { child ->
            child.setOnClickListener {
                bb.editText.text?.delete(bb.editText.selectionStart, bb.editText.selectionEnd)
                bb.editText.text?.insert(bb.editText.selectionEnd, it.tag.toString())
                sendKey(it.tag.toString())
            }
        }
        bb.confirm.setOnClickListener { onConfirm() }
        bb.delete.setOnClickListener {
            val s = bb.editText.selectionStart
            val e = bb.editText.selectionEnd

            if (bb.editText.selectionStart == bb.editText.selectionEnd && s > 0)
                bb.editText.text?.delete(s - 1, e)
            else
                bb.editText.text?.delete(s, e)
            sendKey(it.tag.toString())
        }
    }

    private fun onConfirm() {
        dismiss()
        fireCallback(
            RES_EDIT_VALUE to "" + bb.editText.text.toString().parseMoney,
            RES_STATE to RES_STATE_CONFIRM
        )
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        fireCallback(
            RES_EDIT_VALUE to "" + bb.editText.text.toString().parseMoney,
            RES_STATE to RES_STATE_CANCEL
        )
    }

    private fun sendKey(key: String) = fireCallback(
        RES_EDIT_VALUE to "" + bb.editText.text.toString().parseMoney,
        RES_STATE to RES_STATE_KEY,
        RES_CHAR to key
    )

    private fun fireCallback(vararg data: Pair<String, String>) {
        callback?.invoke(mapOf(*data))
    }

    companion object {
        //      ---------------------------------------------------------------
        const val RES_CHAR = "key"
        const val RES_EDIT_VALUE = "value"

        const val RES_STATE = "state"
        const val RES_STATE_CONFIRM = "confirm"
        const val RES_STATE_KEY = "keydown"
        const val RES_STATE_CANCEL = "cancel"

        fun newInstance(callback: MoneyEditTextKeypadCallback? = null): MoneyEditTextKeypadDialog {
            return MoneyEditTextKeypadDialog().apply {
                this.callback = callback
            }
        }
    }
}

