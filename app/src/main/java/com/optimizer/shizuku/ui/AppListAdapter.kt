package com.optimizer.shizuku.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.optimizer.shizuku.R
import com.optimizer.shizuku.databinding.ItemAppBinding

class AppListAdapter(
    private val onAppSelected: (packageName: String, isSelected: Boolean) -> Unit
) : ListAdapter<AppInfo, AppListAdapter.AppViewHolder>(AppDiffCallback()) {

    private val selectedItems = mutableSetOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemAppBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AppViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty() && payloads[0] == PAYLOAD_SELECTION_CHANGED) {
            holder.updateSelection(getItem(position))
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    fun selectAll() {
        selectedItems.clear()
        currentList.forEach { selectedItems.add(it.packageName) }
        notifyItemRangeChanged(0, itemCount, PAYLOAD_SELECTION_CHANGED)
    }

    fun deselectAll() {
        selectedItems.clear()
        notifyItemRangeChanged(0, itemCount, PAYLOAD_SELECTION_CHANGED)
    }

    fun getAllPackageNames(): List<String> {
        return currentList.map { it.packageName }
    }

    fun isSelected(packageName: String): Boolean {
        return selectedItems.contains(packageName)
    }

    inner class AppViewHolder(
        private val binding: ItemAppBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(appInfo: AppInfo, position: Int) {
            binding.apply {
                ivAppIcon.setImageDrawable(appInfo.icon)
                tvAppName.text = appInfo.appName
                tvPackageName.text = appInfo.packageName
                tvSystemBadge.visibility = if (appInfo.isSystemApp) View.VISIBLE else View.GONE
                
                checkbox.setOnCheckedChangeListener(null)
                checkbox.isChecked = selectedItems.contains(appInfo.packageName)
                checkbox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedItems.add(appInfo.packageName)
                    } else {
                        selectedItems.remove(appInfo.packageName)
                    }
                    onAppSelected(appInfo.packageName, isChecked)
                }
                
                root.setOnClickListener {
                    it.startAnimation(AnimationUtils.loadAnimation(it.context, R.anim.button_pulse))
                    checkbox.isChecked = !checkbox.isChecked
                }
                
                root.startAnimation(AnimationUtils.loadAnimation(root.context, R.anim.item_fade_in).apply {
                    startOffset = (position * 30).toLong()
                })
            }
        }

        fun updateSelection(appInfo: AppInfo) {
            binding.checkbox.setOnCheckedChangeListener(null)
            binding.checkbox.isChecked = selectedItems.contains(appInfo.packageName)
            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedItems.add(appInfo.packageName)
                } else {
                    selectedItems.remove(appInfo.packageName)
                }
                onAppSelected(appInfo.packageName, isChecked)
            }
        }
    }

    class AppDiffCallback : DiffUtil.ItemCallback<AppInfo>() {
        override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
            return oldItem.packageName == newItem.packageName
        }

        override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
            return oldItem.packageName == newItem.packageName && 
                   oldItem.appName == newItem.appName
        }
    }

    companion object {
        private const val PAYLOAD_SELECTION_CHANGED = "selection_changed"
    }
}
