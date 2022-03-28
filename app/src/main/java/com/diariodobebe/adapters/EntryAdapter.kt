package com.diariodobebe.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.diariodobebe.R
import com.diariodobebe.models.*
import java.util.*

class EntryAdapter(private val entryList: MutableList<Entry>) :
    RecyclerView.Adapter<EntryAdapter.EntryVH>() {
    private val VIEW_TYPE_TOP = 0
    private val VIEW_TYPE_MIDDLE = 1
    private val VIEW_TYPE_BOTTOM = 2

    class EntryVH(view: View) : RecyclerView.ViewHolder(view) {
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

        val expandDetailsView: RelativeLayout = view.findViewById(R.id.ll_details_show)
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
                VIEW_TYPE_TOP
            }
            entryList.size - 1 -> {
                VIEW_TYPE_BOTTOM
            }
            else -> {
                VIEW_TYPE_MIDDLE
            }
        }
    }

    override fun onBindViewHolder(holder: EntryVH, position: Int) {
        val entry = entryList[position]
        val context = holder.entryComment.context

        when (holder.itemViewType) {
            VIEW_TYPE_TOP -> {
                holder.entryLine.background =
                    ContextCompat.getDrawable(context, R.drawable.line_top_end)
            }
            VIEW_TYPE_MIDDLE -> {
                holder.entryLine.background =
                    ContextCompat.getDrawable(context, R.drawable.line_bg_middle)
            }
            VIEW_TYPE_BOTTOM -> {
                holder.entryLine.background =
                    ContextCompat.getDrawable(context, R.drawable.line_bottom_end)
            }
        }

        if (entry.hasDetails == true) {
            if (entry.comment != null) {
                holder.entryComment.text = entry.comment.toString()
            }
            holder.expandDetailsView.visibility = View.VISIBLE
        }

        when (entry.type) {
            Entry.ENTRY_SLEEP -> {
                holder.entryType.text = context.getString(R.string.sleep)
                holder.entryTime.text = context.getString(
                    R.string.entry_time,
                    (entry as Sleep).time.toString(),
                    (entry as Sleep).timeEnd.toString()
                )
                holder.entryDescription.text = context.getString(R.string.sleep_duration)
                holder.entryPic.setImageResource(R.drawable.ic_sleep)
            }
            Entry.ENTRY_FEEDING -> {
                holder.entryType.text = context.getString(R.string.feeding)
                holder.entryTime.text = context.getString(
                    R.string.entry_time,
                    (entry as Feeding).time.toString(),
                    (entry as Feeding).timeEnd.toString()
                )
                holder.entryDescription.text = when ((entry as Feeding).feedingType) {
                    Feeding.FEEDING_BOTTLE -> context.getString(R.string.feeding_bottle)
                    Feeding.FEEDING_BREAST -> context.getString(R.string.breast)
                    Feeding.FEEDING_FOOD -> context.getString(R.string.food)
                    else -> context.getString(R.string.feeding)
                }
                holder.entryPic.setImageResource(R.drawable.ic_baby_feeding)

                if (entry.hasDetails == true && (entry as Feeding).leftBreastTime != null) {
                    holder.entryBreastDetailsLayout.visibility = View.VISIBLE
                    //TODO: Set each breast time
                }
            }
            Entry.ENTRY_HEALTH -> {
                entryList.add(entry as HealthEvent)
            }
            Entry.ENTRY_MEASUREMENT -> {
                entryList.add(entry as Measurement)
            }
        }
    }

    override fun getItemCount(): Int {
        return entryList.size
    }
}