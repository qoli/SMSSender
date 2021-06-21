package com.qoli.smssender.activity.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qoli.smssender.R
import com.qoli.smssender.app.AppUnits
import com.qoli.smssender.entity.JobEntity

class MainJobsRecyclerAdapter(var dataSet: List<JobEntity>, val ctx: Context) :
    RecyclerView.Adapter<MainJobsRecyclerAdapter.ViewHolder>() {

    fun updateData(dataSet: List<JobEntity>) {
        this.dataSet = dataSet
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.titleView)
        val descriptionView: TextView = view.findViewById(R.id.descriptionView)
        val goButton: ImageButton = view.findViewById(R.id.goButton)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.share_jobs_row, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView.text = dataSet[position].jobTitle
        viewHolder.descriptionView.text = dataSet[position].jobMode.toString()

        viewHolder.itemView.setOnClickListener {
            AppUnits.toJobViewByID(ctx, dataSet[position].id.toInt())
        }
    }

    override fun getItemCount() = dataSet.size

}
