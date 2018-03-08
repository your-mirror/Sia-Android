/*
 * Copyright (c) 2017 Nicholas van Dyke. All rights reserved.
 */

package com.vandyke.sia.ui.node.modules

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vandyke.sia.R
import com.vandyke.sia.data.local.Prefs
import com.vandyke.sia.ui.common.BaseFragment
import com.vandyke.sia.util.GenUtil
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_node_modules.*
import kotlinx.android.synthetic.main.holder_module.*

class NodeModulesFragment : BaseFragment() {
    override val title: String = "Node Modules"
    override val layoutResId: Int = R.layout.fragment_node_modules

    private lateinit var vm: NodeModulesViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = ModulesAdapter()
        modules_list.adapter = adapter

        vm = ViewModelProviders.of(this).get(NodeModulesViewModel::class.java)

        vm.modules.observe(this, adapter::submitList)
    }

    inner class ModulesAdapter : ListAdapter<ModuleData, ModuleHolder>(
            object : DiffUtil.ItemCallback<ModuleData>() {
                override fun areItemsTheSame(oldItem: ModuleData, newItem: ModuleData): Boolean {
                    return oldItem.type == newItem.type
                }

                override fun areContentsTheSame(oldItem: ModuleData, newItem: ModuleData): Boolean {
                    return oldItem.type == newItem.type && oldItem.on == newItem.on && oldItem.internalSize == newItem.internalSize
                }
            }) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleHolder {
            return ModuleHolder(LayoutInflater.from(parent.context).inflate(R.layout.holder_module, parent, false))
        }

        override fun onBindViewHolder(holder: ModuleHolder, position: Int) {
            holder.bind(getItem(position))
        }
    }

    inner class ModuleHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {
        override val containerView: View? = itemView

        private lateinit var module: Module

        init {
            module_switch.setOnCheckedChangeListener { buttonView, isChecked ->
                if (!isChecked) {
                    Prefs.modulesString = Prefs.modulesString.replace(module.text[0].toString(), "", true)
                } else {
                    Prefs.modulesString += module.text[0].toLowerCase()
                }
            }

            module_delete_internal.setOnClickListener { // might have to stop before deleting and then start after
                vm.deleteModule(module, true)
            }

            module_delete_external.setOnClickListener {
                vm.deleteModule(module, false)
            }

//        module_delete_cached.setOnClickListener { } TODO: allow deleting of db stuff. Should it go here?
        }

        fun bind(module: ModuleData) {
            this.module = module.type
            module_name.text = module.type.text
            module_switch.isChecked = module.on
            module_internal_size.text = GenUtil.readableFilesizeString(module.internalSize)
            module_external_size.text = GenUtil.readableFilesizeString(module.externalSize)
        }
    }
}

