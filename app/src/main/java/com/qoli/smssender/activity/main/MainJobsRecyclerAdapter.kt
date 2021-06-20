package com.qoli.smssender.activity.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hjq.toast.ToastUtils
import com.qoli.smssender.R
import com.qoli.smssender.entity.JobEntity

class MainJobsRecyclerAdapter(var dataSet: List<JobEntity>) :
    RecyclerView.Adapter<MainJobsRecyclerAdapter.ViewHolder>() {

    fun updateData(dataSet: List<JobEntity>) {
        this.dataSet = dataSet
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.titleView)
        val descriptionView: TextView = view.findViewById(R.id.descriptionView)
        val goButton: ImageButton = view.findViewById(R.id.goButton)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.jobs_row, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView.text = dataSet[position].jobTitle
        viewHolder.descriptionView.text = dataSet[position].jobMode.toString()

        viewHolder.itemView.setOnClickListener {
            ToastUtils.show(position.toString())
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
