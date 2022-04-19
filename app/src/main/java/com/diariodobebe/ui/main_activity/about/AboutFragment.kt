package com.diariodobebe.ui.main_activity.about

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.diariodobebe.R
import com.diariodobebe.databinding.FragmentAboutBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AboutFragment : Fragment() {
    private lateinit var _binding: FragmentAboutBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(layoutInflater)

        binding.supportView.setOnClickListener {
            val i = Intent(Intent.ACTION_SENDTO)
            i.type = "message/rfc822"
            i.data = Uri.parse("mailto:")
            i.putExtra(Intent.EXTRA_EMAIL, getString(R.string.support_email))
            i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_email_subject))
            startActivity(Intent.createChooser(i, getString(R.string.send_email)))
        }

        binding.creditsView.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(
                requireContext(),
                R.style.DialogTheme
            )
                .setTitle(getString(R.string.credits))
                .setMessage(
                    HtmlCompat.fromHtml(
                        "App icon (baby bottle) by <a href=\"https://elements.envato.com/user/alexdndz?utm_source=reshot&utm_medium=referral&utm_campaign=elements_reshot_icon_item_page\">@alexdndz</a> at <a href=\"https://www.reshot.com/free-svg-icons/item/baby-bottle-VJTL2RMS9X/\">Reshot</a>" +
                                "<br><br><a href=\"https://github.com/Baseflow/PhotoView\">PhotoView by Chris Banes under Apache License 2.0 - 2018</a>" +
                                "<br><br><a href=\"https://github.com/Dhaval2404/ImagePicker\">ImagePicker by Dhaval Patel under Apache License 2.0 - 2021</a>" +
                                "<br><br><a href=\"https://github.com/AppIntro/AppIntro\">App Intro by App Intro Developers under Apache License 2.0 - 2015-2020</a>" +
                                "<br><br><b>Icons by:</b><br><a href=\"https://www.flaticon.com/free-icons/baby\" title=\"baby icons\">Baby icons created by Freepik - Flaticon</a><br>" +
                                "<a href=\"https://www.flaticon.com/free-icons/camera\" title=\"camera icons\">Camera icons created by Freepik - Flaticon</a>" +
                                "<a href=\"https://www.flaticon.com/free-icons/camera\" title=\"camera icons\">Camera icons created by Ilham Fitrotul Hayat - Flaticon</a>" +
                                "<a href=\"https://www.flaticon.com/free-icons/baby\" title=\"baby icons\">Baby icons created by Flat Icons - Flaticon</a><br>" +
                                "<a href=\"https://www.flaticon.com/free-icons/ruler\" title=\"ruler icons\">Ruler icons created by Kiranshastry - Flaticon</a><br>" +
                                "<a href=\"https://www.flaticon.com/free-icons/diaper\" title=\"diaper icons\">Diaper icons created by Freepik - Flaticon</a><br>" +
                                "<a href=\"https://www.flaticon.com/free-icons/health\" title=\"health icons\">Health icons created by Freepik - Flaticon</a><br>" +
                                "<a href=\"https://www.flaticon.com/free-icons/woman\" title=\"woman icons\">Woman icons created by Freepik - Flaticon</a><br>" +
                                "<a href=\"https://www.flaticon.com/free-icons/male\" title=\"male icons\">Male icons created by bqlqn - Flaticon</a><br>" +
                                "<a href=\"https://www.flaticon.com/free-icons/sleeping\" title=\"sleeping icons\">Sleeping icons created by Freepik - Flaticon</a><br>" +
                                "<a href=\"https://www.flaticon.com/free-icons/child\" title=\"child icons\">Child icons created by Freepik - Flaticon</a><br>" +
                                "<a href=\"https://www.flaticon.com/free-icons/baby-bottle\" title=\"baby bottle icons\">Baby bottle icons created by LAFS - Flaticon</a><br>" +
                                "<a href=\"https://www.flaticon.com/free-icons/mother\" title=\"mother icons\">Mother icons created by Freepik - Flaticon</a><br>" +
                                "<a href=\"https://www.flaticon.com/free-icons/height\" title=\"height icons\">Height icons created by Freepik - Flaticon</a><br>" +
                                "<a href=\"https://www.flaticon.com/free-icons/diaper\" title=\"diaper icons\">Diaper icons created by amonrat rungreangfangsai - Flaticon</a><br>" +
                                "<a href=\"https://www.flaticon.com/free-icons/sleep\" title=\"sleep icons\">Sleep icons created by Freepik - Flaticon</a><br>" +
                                "<a href=\"https://www.flaticon.com/free-icons/toys\" title=\"toys icons\">Toys icons created by iconixar - Flaticon</a><br>" +
                                "<a href=\"https://www.flaticon.com/free-icons/diaper\" title=\"diaper icons\">Diaper icons created by Freepik - Flaticon</a><br>" +
                                "<a href=\"https://www.flaticon.com/free-icons/sleep\" title=\"sleep icons\">Sleep icons created by Fuzzee - Flaticon</a><br>" +
                                "<a href=\"https://www.flaticon.com/free-icons/drug\" title=\"drug icons\">Drug icons created by Freepik - Flaticon</a><br>" +
                                "<a href=\"https://www.flaticon.com/free-icons/neutral\" title=\"neutral icons\">Neutral icons created by Freepik - Flaticon</a><br>" +
                                "<a href=\"https://www.flaticon.com/free-icons/good\" title=\"good icons\">Good icons created by Freepik - Flaticon</a><br>" +
                                "<a href=\"https://www.flaticon.com/free-icons/bad\" title=\"bad icons\">Bad icons created by Freepik - Flaticon</a><br>" +
                                "<a href=\"https://www.flaticon.com/free-icons/heart-beat\" title=\"heart beat icons\">Heart beat icons created by kmg design - Flaticon</a>",
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                )
                .setPositiveButton(getString(R.string.OK)) { _, _ -> }
                .setCancelable(true).create()


            dialog.findViewById<TextView>(android.R.id.message)?.typeface = Typeface.MONOSPACE

            dialog.show()
        }

        return binding.root
    }

}