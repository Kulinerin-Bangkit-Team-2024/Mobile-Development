package com.bangkit.capstone.kulinerin.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.bangkit.capstone.kulinerin.R
import com.bangkit.capstone.kulinerin.databinding.ViewProfileBinding

class ProfileEmailView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr) {
    private val binding: ViewProfileBinding =
        ViewProfileBinding.inflate(LayoutInflater.from(context), this)

    init {
        binding.tvProfile.apply {
            hint = context.getString(R.string.email)
            setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_email,
                0,
                0,
                0
            )
        }
    }
}