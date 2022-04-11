package com.diariodobebe.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.diariodobebe.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SymptomAdapter(private val symptoms: MutableList<String>) :
    RecyclerView.Adapter<SymptomAdapter.SymptomVH>() {


    class SymptomVH(view: View) : RecyclerView.ViewHolder(view) {
        val tvSymptomId: TextView = view.findViewById(R.id.tv_symptom_id)
        val tvSymptomText: TextView = view.findViewById(R.id.tv_symptom_text)
        val root: ViewGroup = view.findViewById(R.id.root)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymptomVH {
        return SymptomVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_symptom, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SymptomVH, position: Int) {
        val symptom = symptoms[position]

        holder.tvSymptomId.text = (position + 1).toString()
        holder.tvSymptomText.text =
            symptom

        holder.root.setOnClickListener {
            if (symptom != holder.root.context.getString(R.string.no_symptom_added)) {
                MaterialAlertDialogBuilder(holder.root.context)
                    .setTitle(holder.root.context.getString(R.string.delete_confirmation_title))
                    .setMessage(holder.root.context.getString(R.string.delete_confirmation_message))
                    .setPositiveButton(holder.root.context.getString(R.string.yes)) { _, _ ->
                        val index = symptoms.indexOf(symptom)
                        if (symptoms.size > 1) {
                            symptoms.remove(symptom)
                            this.notifyDataSetChanged()
                        } else {
                            symptoms[index] =
                                holder.root.context.getString(R.string.no_symptom_added)
                            this.notifyDataSetChanged()
                        }
                    }
                    .setNegativeButton(holder.root.context.getString(R.string.no)) { dialog, _ ->
                        dialog.dismiss()
                    }.create().show()
            }
        }
    }

    override fun getItemCount(): Int {
        return symptoms.size
    }
}