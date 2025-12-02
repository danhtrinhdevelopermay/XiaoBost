package com.optimizer.shizuku.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.optimizer.shizuku.R
import com.optimizer.shizuku.utils.OptimizationCommands

class CommandAdapter(
    private val onCommandClick: (OptimizationCommands.OptimizationCommand) -> Unit
) : ListAdapter<OptimizationCommands.OptimizationCommand, CommandAdapter.CommandViewHolder>(CommandDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommandViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_command, parent, false)
        return CommandViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommandViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class CommandViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvCommandName)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvCommandDescription)
        private val tvRootBadge: TextView = itemView.findViewById(R.id.tvRootBadge)
        private val tvWarningIndicator: TextView = itemView.findViewById(R.id.tvWarningIndicator)
        private val btnRun: MaterialButton = itemView.findViewById(R.id.btnRun)
        private val ivCommandIcon: ImageView = itemView.findViewById(R.id.ivCommandIcon)
        private val iconGlow: View = itemView.findViewById(R.id.iconGlow)

        fun bind(command: OptimizationCommands.OptimizationCommand, position: Int) {
            tvName.text = command.name
            tvDescription.text = command.description
            tvRootBadge.visibility = if (command.requiresRoot) View.VISIBLE else View.GONE
            tvWarningIndicator.visibility = if (command.warning != null) View.VISIBLE else View.GONE
            
            val categoryIcon = when (command.category) {
                OptimizationCommands.Category.PERFORMANCE -> R.drawable.ic_performance
                OptimizationCommands.Category.BATTERY -> R.drawable.ic_battery_gaming
                OptimizationCommands.Category.RAM -> R.drawable.ic_ram_gaming
                OptimizationCommands.Category.GAMING -> R.drawable.ic_gaming_controller
                OptimizationCommands.Category.GENERAL -> R.drawable.ic_settings_gaming
            }
            ivCommandIcon.setImageResource(categoryIcon)
            
            btnRun.setOnClickListener {
                it.startAnimation(AnimationUtils.loadAnimation(it.context, R.anim.button_pulse))
                onCommandClick(command)
            }
            
            itemView.startAnimation(AnimationUtils.loadAnimation(itemView.context, R.anim.item_fade_in).apply {
                startOffset = (position * 50).toLong()
            })
        }
    }

    class CommandDiffCallback : DiffUtil.ItemCallback<OptimizationCommands.OptimizationCommand>() {
        override fun areItemsTheSame(
            oldItem: OptimizationCommands.OptimizationCommand,
            newItem: OptimizationCommands.OptimizationCommand
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: OptimizationCommands.OptimizationCommand,
            newItem: OptimizationCommands.OptimizationCommand
        ): Boolean = oldItem == newItem
    }
}
