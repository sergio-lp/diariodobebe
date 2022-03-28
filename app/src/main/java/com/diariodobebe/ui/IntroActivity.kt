package com.diariodobebe.ui

import android.os.Bundle
import android.os.PersistableBundle
import com.diariodobebe.R
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroFragment

class IntroActivity : AppIntro() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        addSlide(
            AppIntroFragment.createInstance(
                title = getString(R.string.welcome),
                description = getString(R.string.welcome_description),
                R.drawable.ic_bottle
            )
        )

        addSlide(AppIntroFragment.createInstance(
            title = getString(R.string.lets_go),
            description = getString(R.string.app_features)
        ))
    }

}