package com.optimizer.shizuku.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
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
        holder.bind(getItem(position))
    }

    inner class CommandViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvCommandName)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvCommandDescription)
        private val ivRootBadge: ImageView = itemView.findViewById(R.id.ivRootBadge)
        private val btnRun: Button = itemView.findViewById(R.id.btnRun)

        fun bind(command: OptimizationCommands.OptimizationCommand) {
            tvName.text = command.name
            tvDescription.text = command.description
            ivRootBadge.visibility = if (command.requiresRoot) View.VISIBLE else View.GONE
            
            btnRun.setOnClickListener {
                onCommandClick(command)
            }
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
