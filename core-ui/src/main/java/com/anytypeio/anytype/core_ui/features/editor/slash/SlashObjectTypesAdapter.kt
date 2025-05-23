package com.anytypeio.anytype.core_ui.features.editor.slash

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anytypeio.anytype.core_ui.R
import com.anytypeio.anytype.core_ui.databinding.ItemSlashWidgetObjectTypeBinding
import com.anytypeio.anytype.core_ui.databinding.ItemSlashWidgetSelectDateBinding
import com.anytypeio.anytype.core_ui.databinding.ItemSlashWidgetStyleBinding
import com.anytypeio.anytype.core_ui.databinding.ItemSlashWidgetSubheaderBinding
import com.anytypeio.anytype.core_ui.databinding.ItemSlashWidgetSubheaderLeftBinding
import com.anytypeio.anytype.core_ui.features.editor.slash.holders.ActionMenuHolder
import com.anytypeio.anytype.core_ui.features.editor.slash.holders.DateSelectHolder
import com.anytypeio.anytype.core_ui.features.editor.slash.holders.ObjectTypeMenuHolder
import com.anytypeio.anytype.core_ui.features.editor.slash.holders.SubheaderMenuHolder
import com.anytypeio.anytype.core_ui.features.editor.slash.holders.SubheaderOnlyMenuHolder
import com.anytypeio.anytype.presentation.editor.editor.slash.SlashItem

class SlashObjectTypesAdapter(
    private var items: List<SlashItem>,
    private val clicks: (SlashItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun update(items: List<SlashItem>) {
        if (items.isEmpty()) {
            clear()
        } else {
            this.items = items
            notifyDataSetChanged()
        }
    }

    fun clear() {
        val size = items.size
        if (size > 0) {
            items = listOf()
            notifyItemRangeRemoved(0, size)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.item_slash_widget_object_type -> ObjectTypeMenuHolder(
                binding = ItemSlashWidgetObjectTypeBinding.inflate(
                    inflater, parent, false
                )
            )
            R.layout.item_slash_widget_style -> ActionMenuHolder(
                binding = ItemSlashWidgetStyleBinding.inflate(
                    inflater, parent, false
                )
            )
            R.layout.item_slash_widget_subheader -> SubheaderMenuHolder(
                binding = ItemSlashWidgetSubheaderBinding.inflate(
                    inflater, parent, false
                )
            ).apply {
                itemView.findViewById<View>(R.id.flBack).setOnClickListener {
                    clicks(SlashItem.Back)
                }
            }
            R.layout.item_slash_widget_subheader_left -> SubheaderOnlyMenuHolder(
                binding = ItemSlashWidgetSubheaderLeftBinding.inflate(
                    inflater, parent, false
                )
            )
            R.layout.item_slash_widget_select_date -> DateSelectHolder(
                binding = ItemSlashWidgetSelectDateBinding.inflate(
                    inflater, parent, false
                )
            )
            else -> throw IllegalArgumentException("Wrong viewtype:$viewType")
        }.apply {
            itemView.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    clicks(items[pos])
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ObjectTypeMenuHolder -> {
                val item = items[position] as SlashItem.ObjectType
                holder.bind(item)
            }
            is ActionMenuHolder -> {
                val item = items[position] as SlashItem.Actions
                holder.bind(item)
            }
            is SubheaderMenuHolder -> {
                val item = items[position] as SlashItem.Subheader
                holder.bind(item)
            }
            is SubheaderOnlyMenuHolder -> {
                val item = items[position] as SlashItem.Subheader
                holder.bind(item)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int = when (val item = items[position]) {
        is SlashItem.ObjectType -> R.layout.item_slash_widget_object_type
        is SlashItem.Subheader -> item.getViewType()
        is SlashItem.Actions -> R.layout.item_slash_widget_style
        is SlashItem.SelectDate -> R.layout.item_slash_widget_select_date
        else -> throw IllegalArgumentException("Wrong item type:${items[position]} for SlashObjectTypeAdapter")
    }
}

fun SlashItem.Subheader.getViewType(): Int = when (this) {
    SlashItem.Subheader.Actions -> R.layout.item_slash_widget_subheader_left
    SlashItem.Subheader.ActionsWithBack -> R.layout.item_slash_widget_subheader
    SlashItem.Subheader.Alignment -> R.layout.item_slash_widget_subheader_left
    SlashItem.Subheader.AlignmentWithBack -> R.layout.item_slash_widget_subheader
    SlashItem.Subheader.Background -> R.layout.item_slash_widget_subheader_left
    SlashItem.Subheader.BackgroundWithBack -> R.layout.item_slash_widget_subheader
    SlashItem.Subheader.Color -> R.layout.item_slash_widget_subheader_left
    SlashItem.Subheader.ColorWithBack -> R.layout.item_slash_widget_subheader
    SlashItem.Subheader.Media -> R.layout.item_slash_widget_subheader_left
    SlashItem.Subheader.MediaWithBack -> R.layout.item_slash_widget_subheader
    SlashItem.Subheader.ObjectType -> R.layout.item_slash_widget_subheader_left
    SlashItem.Subheader.ObjectTypeWithBlack -> R.layout.item_slash_widget_subheader
    SlashItem.Subheader.Other -> R.layout.item_slash_widget_subheader_left
    SlashItem.Subheader.OtherWithBack -> R.layout.item_slash_widget_subheader
    SlashItem.Subheader.Style -> R.layout.item_slash_widget_subheader_left
    SlashItem.Subheader.StyleWithBack -> R.layout.item_slash_widget_subheader
}