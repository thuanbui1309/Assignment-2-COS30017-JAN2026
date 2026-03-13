package com.example.assignment_2_cos30017_jan2026.util

import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.assignment_2_cos30017_jan2026.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.switchmaterial.SwitchMaterial

object DialogHelper {

    fun showSettingsBottomSheet(activity: AppCompatActivity) {
        val bottomSheet = BottomSheetDialog(activity)
        bottomSheet.setContentView(R.layout.dialog_settings)

        val switchDarkMode = bottomSheet.findViewById<SwitchMaterial>(R.id.switch_dark_mode)
        val rbEnglish = bottomSheet.findViewById<android.widget.RadioButton>(R.id.rb_english)
        val rbVietnamese = bottomSheet.findViewById<android.widget.RadioButton>(R.id.rb_vietnamese)

        if (switchDarkMode != null) {
            switchDarkMode.isChecked = ThemeHelper.isDarkMode(activity)
            switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
                ThemeHelper.applyDarkMode(isChecked)
                bottomSheet.dismiss()
            }
        }

        if (rbEnglish != null && rbVietnamese != null) {
            val currentLang = LocaleHelper.getLanguage(activity)
            if (currentLang == "vi") {
                rbVietnamese.isChecked = true
            } else {
                rbEnglish.isChecked = true
            }

            // Prevent recreating if they select the currently active language
            rbEnglish.setOnClickListener {
                if (currentLang != "en") {
                    LocaleHelper.setLanguage(activity, "en")
                    bottomSheet.dismiss()
                    activity.recreate()
                }
            }

            rbVietnamese.setOnClickListener {
                if (currentLang != "vi") {
                    LocaleHelper.setLanguage(activity, "vi")
                    bottomSheet.dismiss()
                    activity.recreate()
                }
            }
        }

        bottomSheet.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheetInternal = d.findViewById<android.view.View>(com.google.android.material.R.id.design_bottom_sheet)
            if (bottomSheetInternal != null) {
                val behavior = com.google.android.material.bottomsheet.BottomSheetBehavior.from(bottomSheetInternal)
                behavior.state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }

        bottomSheet.show()
    }
}
