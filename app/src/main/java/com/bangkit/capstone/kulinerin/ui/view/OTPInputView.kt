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
            val otpFields = arrayOf(etOtp1, etOtp2, etOtp3, etOtp4)

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
                binding.etOtp4.text.toString()
    }

    fun setOtp(otp: String) {
        binding.etOtp1.setText(otp.getOrNull(0)?.toString())
        binding.etOtp2.setText(otp.getOrNull(1)?.toString())
        binding.etOtp3.setText(otp.getOrNull(2)?.toString())
        binding.etOtp4.setText(otp.getOrNull(3)?.toString())
    }

    fun clearOtp() {
        binding.etOtp1.text.clear()
        binding.etOtp2.text.clear()
        binding.etOtp3.text.clear()
        binding.etOtp4.text.clear()
    }
}