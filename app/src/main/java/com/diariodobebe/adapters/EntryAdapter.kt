package com.diariodobebe.adapters

import android.content.Intent
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.diariodobebe.R
import com.diariodobebe.models.*
import com.diariodobebe.ui.entry_activities.picture_activity.PictureActivity
import java.text.SimpleDateFormat
import java.util.*

class EntryAdapter(private val entryList: MutableList<Entry>) :
    RecyclerView.Adapter<EntryAdapter.EntryVH>() {

    private object Constants {
        const val VIEW_TYPE_TOP = 0
        const val VIEW_TYPE_MIDDLE = 1
        const val VIEW_TYPE_BOTTOM = 2
    }

    class EntryVH(view: View) : RecyclerView.ViewHolder(view) {
        val root: ViewGroup = view.findViewById(R.id.root)

        var state: Boolean = false

        val llDetailsShow: RelativeLayout = view.findViewById(R.id.ll_details_show)

        val entryGroupDateTv: TextView = view.findViewById(R.id.tv_entry_day)

        val viewPicButton: Button = view.findViewById(R.id.btn_show_pic)

        val entryLine: FrameLayout = view.findViewById(R.id.layout_entry_line)
        val entryPic: ImageView = view.findViewById(R.id.img_activity_type)
        val entryType: TextView = view.findViewById(R.id.tv_entry_type)
        val entryTime: TextView = view.findViewById(R.id.tv_entry_time)
        val entryDescription: TextView = view.findViewById(R.id.tv_entry_description)
        val entryValue: TextView = view.findViewById(R.id.tv_entry_value)
        val entryComment: TextView = view.findViewById(R.id.tv_entry_comment)

        val entryDetailsLayout: LinearLayout = view.findViewById(R.id.ll_entry_details)

        val entryDiaperDetails: LinearLayout = view.findViewById(R.id.ll_diaper_details)
        val entryDiaperBrand: TextView = view.findViewById(R.id.tv_diaper_brand)

        val entryActivityDescription: LinearLayout = view.findViewById(R.id.ll_activity_details)
        val entryActivityDetails: TextView = view.findViewById(R.id.tv_activity_details)

        val entryBreastDetailsLayout: LinearLayout = view.findViewById(R.id.feeding_entry_details)
        val entryLeftBreastTime: TextView = view.findViewById(R.id.tv_left_breast_time)
        val entryRightBreast: TextView = view.findViewById(R.id.tv_right_breast_time)
        val entryFoodType: TextView = view.findViewById(R.id.tv_feeding_food_type)

        val entryHealthDetailsLayout: LinearLayout = view.findViewById(R.id.health_entry_details)
        val entryHealthCondition: TextView = view.findViewById(R.id.tv_health_entry_condition)
        val entryHealthMed: TextView = view.findViewById(R.id.tv_health_entry_med)
        val entryHealthMood: TextView = view.findViewById(R.id.tv_health_entry_mood)
        val entryHealthQuantity: TextView = view.findViewById(R.id.tv_health_entry_quantity)
        val entryHealthSymptoms: TextView = view.findViewById(R.id.tv_health_entry_symptoms)
        val entryHealthTemperature: TextView = view.findViewById(R.id.tv_health_entry_temperature)

        val entryCommentLayout: LinearLayout = view.findViewById(R.id.ll_comment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryVH {
        return EntryVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_activity_entry, parent, false)
        )
    }

    override fun getItemViewType(position: Int): Int {
        val viewType: Int = when (position) {
            0 -> {
                Constants.VIEW_TYPE_TOP
            }
            entryList.size - 1 -> {
                Constants.VIEW_TYPE_BOTTOM
            }
            else -> {
                Constants.VIEW_TYPE_MIDDLE
            }
        }

        return viewType
    }

    override fun onBindViewHolder(holder: EntryVH, position: Int) {
        var entry = entryList[position]
        val ctx = holder.entryBreastDetailsLayout.context

        val df = SimpleDateFormat.getDateInstance(SimpleDateFormat.DATE_FIELD)
        val cal = Calendar.getInstance()
        df.timeZone = TimeZone.getTimeZone("UTC")
        cal.timeZone = TimeZone.getTimeZone("UTC")
        cal.timeInMillis = entry.date!!

        val strHour: String =
            if (cal.get(Calendar.HOUR_OF_DAY) > 9) cal.get(Calendar.HOUR_OF_DAY)
                .toString() else "0${cal.get(Calendar.HOUR_OF_DAY)}"
        val strMinute: String =
            if (cal.get(Calendar.MINUTE) > 9) cal.get(Calendar.MINUTE)
                .toString() else "0${cal.get(Calendar.MINUTE)}"

        holder.entryTime.text =
            ctx.getString(R.string.date_time_template, df.format(entry.date), strHour, strMinute)

        if (hasDetails(entry, holder)) {
            holder.root.setOnClickListener {
                holder.state = toggleDetails(holder, holder.state)
            }
        } else {
            holder.llDetailsShow.visibility = View.GONE
        }

        holder.entryComment.text = if (!entry.comment.isNullOrBlank()) {
            entry.comment
        } else {
            ctx.getString(R.string.empty_comment)
        }

        when (entry.type) {
            Entry.EntryType.ENTRY_PICTURE -> {
                entry = entry as Photo

                holder.entryType.text = ctx.getString(R.string.photo)
                holder.entryValue.text = ctx.getString(R.string.click_for_info)
                holder.entryDescription.text = ""
                holder.entryPic.setImageDrawable(
                    ContextCompat.getDrawable(
                        ctx,
                        R.drawable.ic_camera_add
                    )
                )

                holder.viewPicButton.setOnClickListener {
                    val intent = Intent(holder.root.context, PictureActivity::class.java)
                    intent.putExtra(Photo.EXTRA_HAS_PHOTO, (entry as Photo).path != null)
                    intent.putExtra(Photo.EXTRA_PATH, (entry as Photo).path)
                    intent.putExtra(Photo.EXTRA_PHOTO, entry)
                    holder.root.context.startActivity(intent)
                }

            }
            Entry.EntryType.ENTRY_HEALTH -> {
                entry = entry as Health

                holder.entryType.text = ctx.getString(R.string.health)
                holder.entryDescription.text = ""
                holder.entryPic.setImageDrawable(
                    ContextCompat.getDrawable(
                        ctx,
                        R.drawable.ic_health_add
                    )
                )

                val moodString = when (entry.mood) {
                    Health.MOOD_BAD -> ctx.getString(R.string.bad_mood)
                    Health.MOOD_NORMAL -> ctx.getString(R.string.normal_mood)
                    Health.MOOD_GOOD -> ctx.getString(R.string.good_mood)
                    else -> ctx.getString(R.string.not_selected)
                }

                holder.entryDescription.text = HtmlCompat.fromHtml(
                    ctx.getString(R.string.health_mood, moodString),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )

                val dfHealth = SimpleDateFormat.getDateTimeInstance(
                    SimpleDateFormat.DATE_FIELD,
                    SimpleDateFormat.SHORT
                )
                dfHealth.timeZone = TimeZone.getTimeZone("UTC")
                val healthCal = Calendar.getInstance()
                cal.timeZone = TimeZone.getTimeZone("UTC")
                healthCal.timeInMillis = entry.medTime ?: -1


                if (!entry.medication.isNullOrBlank()) {
                    holder.entryHealthMed.visibility = View.VISIBLE
                    if (healthCal.timeInMillis != (-1).toLong()) {
                        holder.entryHealthMed.text =
                            HtmlCompat.fromHtml(
                                ctx.getString(
                                    R.string.health_medication,
                                    entry.medication ?: ctx.getString(R.string.not_informed),
                                    ctx.getString(R.string.at_time),
                                    dfHealth.format(healthCal.time)
                                ),
                                HtmlCompat.FROM_HTML_MODE_LEGACY
                            )
                    } else {
                        holder.entryHealthMed.text =
                            HtmlCompat.fromHtml(
                                ctx.getString(
                                    R.string.health_medication,
                                    entry.medication ?: ctx.getString(R.string.not_informed),
                                    "", ""
                                ),
                                HtmlCompat.FROM_HTML_MODE_LEGACY
                            )
                    }
                } else {
                    holder.entryHealthMed.visibility = View.GONE
                }

                healthCal.timeInMillis = entry.vitalsTime ?: -1

                if (entry.temperature != null) {
                    holder.entryHealthTemperature.visibility = View.VISIBLE
                    if (healthCal.timeInMillis != (-1).toLong()) {
                        holder.entryHealthTemperature.text =
                            HtmlCompat.fromHtml(
                                ctx.getString(
                                    R.string.health_temperature,
                                    entry.temperature.toString(),
                                    ctx.getString(R.string.at_time),
                                    dfHealth.format(healthCal.timeInMillis)
                                ), HtmlCompat.FROM_HTML_MODE_LEGACY
                            )
                    } else {
                        holder.entryHealthTemperature.text =
                            HtmlCompat.fromHtml(
                                ctx.getString(
                                    R.string.health_temperature,
                                    entry.temperature.toString(),
                                    "", ""
                                ), HtmlCompat.FROM_HTML_MODE_LEGACY
                            )
                    }
                } else {
                    holder.entryHealthTemperature.visibility = View.GONE
                }

                holder.entryHealthCondition.text =
                    HtmlCompat.fromHtml(
                        ctx.getString(R.string.health_condition, entry.healthEvent),
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )

                if (entry.medAmount != null) {
                    holder.entryHealthQuantity.visibility = View.VISIBLE
                    holder.entryHealthQuantity.text =
                        HtmlCompat.fromHtml(
                            ctx.getString(
                                R.string.health_med_quantity,
                                entry.medAmount.toString()
                            ), HtmlCompat.FROM_HTML_MODE_LEGACY
                        )
                } else {
                    holder.entryHealthQuantity.visibility = View.GONE
                }

                var symptoms = ""
                if (!entry.symptoms.isNullOrEmpty()) {
                    entry.symptoms!!.forEachIndexed { index, symptom ->
                        symptoms += if ((entry as Health).symptoms!!.lastIndex == index) {
                            "$symptom."
                        } else {
                            "$symptom, "
                        }
                    }
                }

                holder.entryHealthSymptoms.text = HtmlCompat.fromHtml(
                    ctx.getString(R.string.health_symptoms, symptoms),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )

                holder.entryHealthMood.text = HtmlCompat.fromHtml(
                    ctx.getString(R.string.health_mood, moodString),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            }
            Entry.EntryType.ENTRY_FEEDING -> {
                entry = entry as Feeding

                holder.entryPic.setImageDrawable(
                    ContextCompat.getDrawable(
                        ctx,
                        R.drawable.ic_bottle
                    )
                )

                holder.entryFoodType.text =
                    HtmlCompat.fromHtml(
                        ctx.getString(R.string.food_type_template, entry.foodType),
                        HtmlCompat.FROM_HTML_MODE_COMPACT
                    )


                holder.entryType.text = when (entry.feedingType) {
                    Feeding.FeedingType.FEEDING_BREAST -> {
                        ctx.getString(R.string.breast)
                    }
                    Feeding.FeedingType.FEEDING_BOTTLE -> {
                        ctx.getString(R.string.feeding_bottle)
                    }
                    else -> {
                        ctx.getString(R.string.food)
                    }
                }

                when (entry.feedingType) {
                    Feeding.FeedingType.FEEDING_BOTTLE -> {
                        holder.entryDescription.text = ctx.getString(R.string.milliliters)
                        holder.entryPic.setImageDrawable(
                            ContextCompat.getDrawable(
                                ctx,
                                R.drawable.ic_bottle
                            )
                        )
                        holder.entryValue.text =
                            ctx.getString(R.string.milliliters_template, entry.milliliters)
                    }
                    Feeding.FeedingType.FEEDING_BREAST -> {
                        holder.entryPic.setImageDrawable(
                            ContextCompat.getDrawable(
                                ctx,
                                R.drawable.breastfeeding
                            )
                        )
                        if (entry.rightBreastTime == null) {
                            entry.rightBreastTime = 0
                        }

                        if (entry.leftBreastTime == null) {
                            entry.leftBreastTime = 0
                        }
                        val durationMin = entry.leftBreastTime!! + entry.rightBreastTime!!
                        holder.entryLeftBreastTime.text = HtmlCompat.fromHtml(
                            ctx.getString(
                                R.string.breast_left_time,
                                entry.leftBreastTime.toString()
                            ), HtmlCompat.FROM_HTML_MODE_LEGACY
                        )
                        holder.entryRightBreast.text = HtmlCompat.fromHtml(
                            ctx.getString(
                                R.string.breast_right_time,
                                entry.rightBreastTime.toString()
                            ), HtmlCompat.FROM_HTML_MODE_COMPACT
                        )

                        holder.entryDescription.text = ctx.getString(R.string.sleep_duration)
                        holder.entryValue.text =
                            ctx.getString(R.string.duration_template, durationMin)

                    }
                    else -> {
                        holder.entryPic.setImageDrawable(
                            ContextCompat.getDrawable(
                                ctx,
                                R.drawable.ic_food
                            )
                        )
                        holder.entryValue.text =
                            ctx.getString(R.string.long_text_template, entry.foodType?.take(8))
                    }
                }
            }
            Entry.EntryType.ENTRY_SLEEP -> {
                entry = entry as Sleep
                holder.entryType.text = ctx.getString(R.string.sleep)
                holder.entryDescription.text = ctx.getString(R.string.sleep_duration)
                holder.entryValue.text = ctx.getString(R.string.duration_template, entry.duration)

                holder.entryPic.setImageDrawable(
                    ContextCompat.getDrawable(
                        ctx,
                        R.drawable.ic_sleep_add
                    )
                )
            }
            Entry.EntryType.ENTRY_DIAPER -> {
                entry = entry as Diaper

                holder.entryType.text = ctx.getString(R.string.diaper)
                holder.entryDescription.text = ctx.getString(R.string.diaper_state).replace(":", "")
                holder.entryPic.setImageDrawable(
                    ContextCompat.getDrawable(
                        ctx,
                        R.drawable.baby_diaper_change
                    )
                )

                holder.entryValue.text = when (entry.state) {
                    Diaper.STATE_DRY -> {
                        ctx.getString(R.string.diaper_dry)
                    }
                    Diaper.STATE_PEE -> {
                        ctx.getString(R.string.diaper_pee)
                    }
                    Diaper.STATE_POOP -> {
                        ctx.getString(R.string.diaper_poop)
                    }
                    Diaper.STATE_BOTH ->
                        ctx.getString(R.string.diaper_both)
                    else -> {
                        ctx.getString(R.string.error_short)
                    }
                }
            }
            Entry.EntryType.ENTRY_EVENT -> {
                entry = entry as Event

                holder.entryType.text = ctx.getString(R.string.event)
                holder.entryValue.text = ctx.getString(R.string.see_details_for_more_info)
                holder.entryDescription.text = ""
                holder.entryActivityDetails.text = entry.activityType
                holder.entryPic.setImageDrawable(
                    ContextCompat.getDrawable(
                        ctx,
                        R.drawable.ic_toy
                    )
                )
            }
            Entry.EntryType.ENTRY_MEASUREMENT -> {
                entry = entry as Measure

                holder.entryType.text = ctx.getString(R.string.measure)
                holder.entryDescription.text = ctx.getString(R.string.height_weight)
                holder.entryValue.text =
                    ctx.getString(R.string.measure_template, entry.height, entry.weight)
                holder.entryPic.setImageDrawable(
                    ContextCompat.getDrawable(
                        ctx,
                        R.drawable.ic_measure
                    )
                )

            }
        }

        when (holder.itemViewType) {
            Constants.VIEW_TYPE_TOP -> {
                holder.entryLine.background =
                    ContextCompat.getDrawable(ctx, R.drawable.line_top_end)
                (holder.entryLine.layoutParams as ConstraintLayout.LayoutParams).topMargin = 8
            }
            Constants.VIEW_TYPE_MIDDLE -> {
                holder.entryLine.background =
                    ContextCompat.getDrawable(ctx, R.drawable.line_bg_middle)
            }
            Constants.VIEW_TYPE_BOTTOM -> {
                holder.entryLine.background =
                    ContextCompat.getDrawable(ctx, R.drawable.line_bottom_end)
            }
        }

        val dfDayMonth = SimpleDateFormat.getDateInstance(SimpleDateFormat.DEFAULT)
        dfDayMonth.timeZone = TimeZone.getTimeZone("UTC")
        if (position - 1 > -1) {
            val prevDate = entryList[position - 1].date
            val prevCal = Calendar.getInstance()
            prevCal.timeZone = TimeZone.getTimeZone("UTC")
            prevCal.timeInMillis = prevDate ?: 0
            val previousDay = prevCal.get(Calendar.DAY_OF_MONTH)
            if (cal.get(Calendar.DAY_OF_MONTH) != previousDay) {
                holder.entryGroupDateTv.text = dfDayMonth.format(cal.time)
                holder.entryGroupDateTv.visibility = View.VISIBLE
            }
        } else {
            holder.entryGroupDateTv.text = dfDayMonth.format(cal.time)
            holder.entryGroupDateTv.visibility = View.VISIBLE
        }
    }

    private fun hasDetails(e: Entry, holder: EntryVH): Boolean {
        androidx.transition.TransitionManager.beginDelayedTransition(
            holder.root,
            androidx.transition.AutoTransition()
        )

        var hasDetails = false

        if (!e.comment.isNullOrEmpty()) {
            hasDetails = true
        }

        when (e.type) {
            Entry.EntryType.ENTRY_SLEEP -> {
                holder.entryDiaperDetails.visibility = View.GONE
                holder.entryFoodType.visibility = View.GONE
                holder.viewPicButton.visibility = View.GONE
                holder.entryHealthDetailsLayout.visibility = View.GONE
                holder.entryActivityDetails.visibility = View.GONE
                holder.entryBreastDetailsLayout.visibility = View.GONE
            }
            Entry.EntryType.ENTRY_MEASUREMENT -> {
                holder.entryDiaperDetails.visibility = View.GONE
                holder.entryFoodType.visibility = View.GONE
                holder.entryHealthDetailsLayout.visibility = View.GONE
                holder.entryActivityDetails.visibility = View.GONE
                holder.viewPicButton.visibility = View.GONE
                holder.entryBreastDetailsLayout.visibility = View.GONE
            }
            Entry.EntryType.ENTRY_FEEDING -> {
                val entry = e as Feeding

                holder.viewPicButton.visibility = View.GONE

                if (!entry.foodType.isNullOrEmpty()) {
                    holder.entryFoodType.visibility = View.VISIBLE
                    holder.entryFoodType.text = e.foodType
                    holder.entryBreastDetailsLayout.visibility = View.GONE
                    holder.entryHealthDetailsLayout.visibility = View.GONE
                    holder.entryDiaperDetails.visibility = View.GONE
                    holder.entryActivityDetails.visibility = View.GONE
                    hasDetails = true
                }

                if (entry.feedingType == Feeding.FeedingType.FEEDING_BREAST) {
                    holder.entryBreastDetailsLayout.visibility = View.VISIBLE
                    holder.entryHealthDetailsLayout.visibility = View.GONE
                    holder.entryDiaperDetails.visibility = View.GONE
                    holder.entryFoodType.visibility = View.GONE
                    holder.entryActivityDetails.visibility = View.GONE
                    hasDetails = true
                }
            }
            Entry.EntryType.ENTRY_DIAPER -> {
                val entry = e as Diaper

                holder.viewPicButton.visibility = View.GONE

                if (!entry.diaperBrand.isNullOrEmpty()) {
                    holder.entryDiaperBrand.text = entry.diaperBrand
                    holder.entryDiaperDetails.visibility = View.VISIBLE
                    holder.entryFoodType.visibility = View.GONE
                    holder.entryHealthDetailsLayout.visibility = View.GONE
                    holder.entryActivityDetails.visibility = View.GONE
                    holder.entryBreastDetailsLayout.visibility = View.GONE
                    hasDetails = true
                }
            }
            Entry.EntryType.ENTRY_EVENT -> {
                val entry = e as Event

                holder.viewPicButton.visibility = View.GONE

                if (!entry.activityType.isNullOrEmpty()) {
                    holder.entryActivityDetails.text = entry.activityType
                    holder.entryActivityDescription.visibility = View.VISIBLE
                    holder.entryFoodType.visibility = View.GONE
                    holder.entryHealthDetailsLayout.visibility = View.GONE
                    holder.entryDiaperDetails.visibility = View.GONE
                    holder.entryBreastDetailsLayout.visibility = View.GONE
                    hasDetails = true
                }
            }
            Entry.EntryType.ENTRY_HEALTH -> {
                val entry = e as Health

                holder.viewPicButton.visibility = View.GONE

                if (!entry.symptoms.isNullOrEmpty()) {
                    holder.entryHealthSymptoms.visibility = View.VISIBLE
                }

                if (!entry.medication.isNullOrEmpty()) {
                    holder.entryHealthMed.visibility = View.VISIBLE
                }

                if (entry.medAmount != null) {
                    holder.entryHealthQuantity.visibility = View.VISIBLE
                }

                if (entry.temperature != null) {
                    holder.entryHealthTemperature.visibility = View.VISIBLE
                }

                if (entry.mood != null) {
                    holder.entryHealthMood.visibility = View.VISIBLE
                }

                holder.entryHealthDetailsLayout.visibility = View.VISIBLE
                holder.entryFoodType.visibility = View.GONE
                holder.entryDiaperDetails.visibility = View.GONE
                holder.entryActivityDetails.visibility = View.GONE
                holder.entryBreastDetailsLayout.visibility = View.GONE

                hasDetails = true
            }
            Entry.EntryType.ENTRY_PICTURE -> {
                holder.viewPicButton.visibility = View.VISIBLE
                holder.entryActivityDetails.visibility = View.GONE
                holder.entryFoodType.visibility = View.GONE
                holder.entryHealthDetailsLayout.visibility = View.GONE
                holder.entryDiaperDetails.visibility = View.GONE
                holder.entryBreastDetailsLayout.visibility = View.GONE
                hasDetails = true
            }
        }

        return hasDetails
    }

    private fun toggleDetails(holder: EntryVH, status: Boolean): Boolean {
        val visibility = if (!status) View.VISIBLE else View.GONE

        TransitionManager.beginDelayedTransition(holder.root, AutoTransition())

        holder.entryDetailsLayout.visibility = visibility
        holder.entryCommentLayout.visibility = visibility

        return !status
    }

    override fun getItemCount(): Int {
        return entryList.size
    }
}