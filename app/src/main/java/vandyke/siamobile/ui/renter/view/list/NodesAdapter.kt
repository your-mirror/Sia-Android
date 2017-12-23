/*
 * Copyright (c) 2017 Nicholas van Dyke
 *
 * This file is subject to the terms and conditions defined in 'LICENSE.md'
 */

package vandyke.siamobile.ui.renter.view.list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import vandyke.siamobile.R
import vandyke.siamobile.data.local.Dir
import vandyke.siamobile.data.local.File
import vandyke.siamobile.data.local.Node
import vandyke.siamobile.ui.renter.viewmodel.RenterViewModel

class NodesAdapter(val viewModel: RenterViewModel) : RecyclerView.Adapter<NodeHolder>() {

    private val DIR = 0
    private val FILE = 1

    private var nodes = listOf<Node>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NodeHolder {
        if (viewType == DIR) {
            val holder = DirHolder(LayoutInflater.from(parent.context).inflate(R.layout.holder_renter_dir, parent, false))
            return holder
        } else {
            val holder = FileHolder(LayoutInflater.from(parent.context).inflate(R.layout.holder_renter_file, parent, false))
            return holder
        }
    }

    override fun onBindViewHolder(holder: NodeHolder, position: Int) {
        if (holder is DirHolder)
            holder.bind(nodes[position] as Dir, viewModel)
        else if (holder is FileHolder)
            holder.bind(nodes[position] as File, viewModel)
    }

    override fun getItemViewType(position: Int) = if (nodes[position] is Dir) DIR else FILE

    override fun getItemCount() = nodes.size

    fun display(nodes: List<Node>) {
        this.nodes = nodes
        notifyDataSetChanged()
    }
}