package com.blackmark.emoveon.mypackages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blackmark.emoveon.R
import com.blackmark.emoveon.clases.Package
import kotlinx.android.synthetic.main.package_list.view.*

class PackageAdapter(packList : ArrayList<Package>, var listener: ClickListener) : RecyclerView.Adapter<PackageAdapter.ViewHolder>() {

    var packList : ArrayList<Package>? = null
    var viewHolder : ViewHolder? = null

    init {
        this.packList = packList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageAdapter.ViewHolder {
        val vista = LayoutInflater.from(parent?.context).inflate(R.layout.package_list, parent, false)
        viewHolder = ViewHolder(vista, listener)
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = packList?.get(position)
        holder.concept?.text = item?.concept
        holder.destiny?.text = item?.destinatary
    }

    override fun getItemCount(): Int {
        return this.packList!!.size
    }

    class ViewHolder(vista: View, listener: ClickListener) : RecyclerView.ViewHolder(vista), View.OnClickListener {
        var vista = vista
        var concept: TextView? = null
        var listener: ClickListener? = null
        var destiny: TextView? = null
        init {
            concept = vista.single_package
            destiny = vista.single_package_status
            this.listener = listener
            vista.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            this.listener?.onClick(p0!!, adapterPosition)
        }
    }
}