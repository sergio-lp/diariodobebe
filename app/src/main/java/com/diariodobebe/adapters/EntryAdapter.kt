package com.diariodobebe.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.diariodobebe.R
import com.diariodobebe.models.*
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

        val entryLine: FrameLayout = view.findViewById(R.id.layout_entry_line)
        val entryPic: ImageView = view.findViewById(R.id.img_activity_type)
        val entryType: TextView = view.findViewById(R.id.tv_entry_type)
        val entryTime: TextView = view.findViewById(R.id.tv_entry_time)
        val entryDescription: TextView = view.findViewById(R.id.tv_entry_description)
        val entryValue: TextView = view.findViewById(R.id.tv_entry_value)
        val entryComment: TextView = view.findViewById(R.id.tv_entry_comment)

        val entryDetailsLayout: LinearLayout = view.findViewById(R.id.ll_entry_details_comment)

        val entryBreastDetailsLayout: LinearLayout = view.findViewById(R.id.feeding_entry_details)
        val entryLeftBreastTime: TextView = view.findViewById(R.id.tv_left_breast_time)
        val entryRightBreast: TextView = view.findViewById(R.id.tv_right_breast_time)

        val entryHealthDetailsLayout: LinearLayout = view.findViewById(R.id.health_entry_details)
        val entryHealthCondition: TextView = view.findViewById(R.id.tv_health_entry_condition)
        val entryHealthMed: TextView = view.findViewById(R.id.tv_health_entry_med)
        val entryHealthMood: TextView = view.findViewById(R.id.tv_health_entry_mood)
        val entryHealthQuantity: TextView = view.findViewById(R.id.tv_health_entry_quantity)
        val entryHealthSymptoms: TextView = view.findViewById(R.id.tv_health_entry_symptoms)
        val entryHealthTemperature: TextView = view.findViewById(R.id.tv_health_entry_temperature)

        val expandImage: ImageView = view.findViewById(R.id.img_entry_details)
        val detailsComment: LinearLayout = view.findViewById(R.id.ll_comment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryVH {
        return EntryVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_activity_entry, parent, false)
        )
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
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
    }

    override fun onBindViewHolder(holder: EntryVH, position: Int) {
        var entry = entryList[position]
        val ctx = holder.entryBreastDetailsLayout.context

        val df = SimpleDateFormat.getDateInstance(SimpleDateFormat.DATE_FIELD)
        df.timeZone = TimeZone.getTimeZone("UTC")
        val cal = Calendar.getInstance()
        cal.timeInMillis = entry.date!!

        val strHour: String =
            if (cal.get(Calendar.HOUR_OF_DAY) > 9) cal.get(Calendar.HOUR_OF_DAY)
                .toString() else "0${cal.get(Calendar.HOUR_OF_DAY)}"
        val strMinute: String =
            if (cal.get(Calendar.MINUTE) > 9) cal.get(Calendar.MINUTE)
                .toString() else "0${cal.get(Calendar.MINUTE)}"

        holder.entryTime.text =
            ctx.getString(R.string.date_time_template, df.format(entry.date), strHour, strMinute)

        holder.root.setOnClickListener {
            holder.state = toggleDetailsView(entry, holder, holder.state)
        }

        when (entry.type) {
            Entry.EntryType.ENTRY_FEEDING -> {
                entry = entry as Feeding

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
                holder.entryDescription.text = ctx.getString(R.string.sleep_duration)
                holder.entryValue.text = ctx.getString(R.string.duration_template, durationMin)
                holder.entryComment.text = entry.comment

                holder.entryPic.setImageDrawable(
                    ContextCompat.getDrawable(
                        ctx,
                        R.drawable.ic_baby_feeding
                    )
                )
            }
            Entry.EntryType.ENTRY_SLEEP -> {
                entry = entry as Sleep
                holder.entryType.text = ctx.getString(R.string.sleep)
                holder.entryValue.text = entry.duration.toString()
                holder.entryComment.text = entry.comment

                holder.entryPic.setImageDrawable(
                    ContextCompat.getDrawable(
                        ctx,
                        R.drawable.ic_sleep
                    )
                )
            }
            Entry.EntryType.ENTRY_DIAPER -> {
                entry = entry as Diaper

                holder.entryType.text = ctx.getString(R.string.diaper)
                holder.entryPic.setImageDrawable(
                    ContextCompat.getDrawable(
                        ctx,
                        R.drawable.ic_diaper
                    )
                )
                holder.entryComment.text = entry.comment

                holder.entryValue.text = when (entry.state) {
                    Diaper.STATE_DRY -> {
                        ctx.getString(R.string.diaper_dry)
                    }
                    Diaper.STATE_PEE -> {
                        ctx.getString(R.string.diaper_pee)
                    }
                    else -> {
                        ctx.getString(R.string.diaper_poop)
                    }
                }
            }
            Entry.EntryType.ENTRY_HEALTH -> {
                entry = entry as Health

                holder.entryType.text = ctx.getString(R.string.health)
                holder.entryPic.setImageDrawable(
                    ContextCompat.getDrawable(
                        ctx,
                        R.drawable.ic_health
                    )
                )
                holder.entryComment.text = entry.comment

                holder.entryHealthCondition.text = entry.healthEvent
                holder.entryHealthMed.text = entry.medication
                holder.entryHealthQuantity.text = entry.medAmount.toString()
                holder.entryHealthTemperature.text = entry.temperature.toString()

                var symptoms = ""
                if (!entry.symptoms.isNullOrEmpty()) {
                    entry.symptoms!!.forEach {
                        symptoms += it + "\n"
                    }
                }

                holder.entryHealthSymptoms.text = symptoms

                holder.entryHealthMood.text = when (entry.mood) {
                    Health.MOOD_BAD -> ctx.getString(R.string.bad_mood)
                    Health.MOOD_NORMAL -> ctx.getString(R.string.normal_mood)
                    Health.MOOD_GOOD -> ctx.getString(R.string.good_mood)
                    else -> ctx.getString(R.string.error_short)
                }
            }
            Entry.EntryType.ENTRY_EVENT -> {
                entry = entry as Event

                holder.entryType.text = ctx.getString(R.string.event)
                holder.entryPic.setImageDrawable(
                    ContextCompat.getDrawable(
                        ctx,
                        R.drawable.ic_activity
                    )
                )
            }
        }

        when (holder.itemViewType) {
            Constants.VIEW_TYPE_TOP -> {
                holder.entryLine.background =
                    ContextCompat.getDrawable(ctx, R.drawable.line_top_end)
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
    }

    private fun toggleDetailsView(e: Entry, holder: EntryVH, status: Boolean): Boolean {
        val visibility = if (!status) View.VISIBLE else View.GONE

        androidx.transition.TransitionManager.beginDelayedTransition(
            holder.root,
            androidx.transition.AutoTransition()
        )
        if (e.type == Entry.EntryType.ENTRY_FEEDING) {
            val entry = e as Feeding

            if (!entry.comment.isNullOrBlank()) {
                holder.detailsComment.visibility = visibility
            }
            holder.entryDetailsLayout.visibility = visibility
            if (entry.feedingType == Feeding.FeedingType.FEEDING_BREAST) {
                holder.entryBreastDetailsLayout.visibility = visibility
            }

        } else if (e.type == Entry.EntryType.ENTRY_HEALTH) {
            val entry = e as Health
            if (!entry.comment.isNullOrBlank()) {
                holder.detailsComment.visibility = visibility
            }
            holder.entryDetailsLayout.visibility = visibility
            holder.entryHealthDetailsLayout.visibility = visibility
        }

        return !status
    }

    override fun getItemCount(): Int {
        return entryList.size
    }
}