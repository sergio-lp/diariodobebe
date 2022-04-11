package com.diariodobebe.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.diariodobebe.R
import com.diariodobebe.ui.add_baby_activity.AddBabyActivity
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
        val intent = Intent(this, AddBabyActivity::class.java)
        intent.putExtra(HOME_DISABLE, true)
        startActivity(intent)
        finish()
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        onDonePressed(currentFragment)
    }

    companion object {
        const val HOME_DISABLE = "displayUpAsHomeDisabled"
    }

}