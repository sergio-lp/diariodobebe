package com.diariodobebe.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.diariodobebe.R
import com.diariodobebe.ui.main_activity.MainActivity
import com.github.appintro.AppIntro
import com.github.appintro.AppIntro2
import com.github.appintro.AppIntroFragment

class IntroActivity : AppIntro2() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addSlide(
            AppIntroFragment.createInstance(
                title = getString(R.string.welcome),
                description = getString(R.string.welcome_description),
                R.drawable.ic_bottle,
                titleColorRes = R.color.black,
                descriptionColorRes = R.color.black,
                backgroundColorRes = R.color.material_green
            )
        )

        addSlide(
            AppIntroFragment.createInstance(
                title = getString(R.string.lets_go),
                description = getString(R.string.app_features),
                titleColorRes = R.color.black,
                descriptionColorRes = R.color.black,
                backgroundColorRes = R.color.material_red,
                imageDrawable = R.drawable.ic_baby
            )
        )

        showStatusBar(false)
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        getSharedPreferences(getString(R.string.PREFS), Context.MODE_PRIVATE).edit {
            putBoolean(getString(R.string.PREF_OPENED), true)
            commit()
        }
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}