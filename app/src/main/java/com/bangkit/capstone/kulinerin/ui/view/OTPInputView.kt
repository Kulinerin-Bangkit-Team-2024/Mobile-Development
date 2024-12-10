package com.bangkit.capstone.kulinerin.ui.view

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import com.bangkit.capstone.kulinerin.databinding.ViewOtpInputBinding

class OTPInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: ViewOtpInputBinding =
        ViewOtpInputBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        binding.apply {
            val otpFields = arrayOf(etOtp1, etOtp2, etOtp3, etOtp4, etOtp5, etOtp6)

            otpFields.forEach { field ->
                field.inputType = InputType.TYPE_CLASS_NUMBER
                field.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if (s != null && s.length == 1) {
                            moveToNextBox(otpFields, field)
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {}
                })
            }

            otpFields[0].requestFocus()

            setOnClickListener {
                otpFields[0].requestFocus()
            }
        }
    }

    private fun moveToNextBox(otpFields: Array<EditText>, currentField: EditText) {
        val currentIndex = otpFields.indexOf(currentField)
        if (currentIndex < otpFields.size - 1) {
            otpFields[currentIndex + 1].requestFocus()
        }
    }

    fun getOtp(): String {
        return binding.etOtp1.text.toString() +
                    binding.etOtp2.text.toString() +
                    binding.etOtp3.text.toString() +
                    binding.etOtp4.text.toString() +
                    binding.etOtp5.text.toString() +
                    binding.etOtp6.text.toString()
    }

    fun setError(errorMessage: String?) {
        binding.apply {
            etOtp1.error = errorMessage
            etOtp2.error = errorMessage
            etOtp3.error = errorMessage
            etOtp4.error = errorMessage
            etOtp5.error = errorMessage
            etOtp6.error = errorMessage
        }
    }
}