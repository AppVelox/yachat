package ru.appvelox.chat.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.avatar.view.*
import kotlinx.android.synthetic.main.item_message.view.*
import ru.appvelox.chat.MessageType
import ru.appvelox.chat.common.ChatAppearance
import ru.appvelox.chat.model.Message
import ru.appvelox.chat.utils.CircularAvatar
import ru.appvelox.chat.utils.DateFormatter

/**
 * Base ViewHolder for [Message]
 */
abstract class MessageViewHolder(
    val view: View,
    private val appearance: ChatAppearance,
    private val dateFormatter: DateFormatter
) :
    RecyclerView.ViewHolder(view) {
    var message: Message? = null
    open fun bind(
        message: Message,
        showMessageDate: Boolean,
        messageType: MessageType? = null
    ) {
        this.message = message

        updateStatusIndicator()

        view.imageViewLeftSwipeActionIcon?.imageAlpha = 0

        itemView.authorName.text = message.author.name
        itemView.message.text = message.text
        itemView.time.text = dateFormatter.formatTime(message.date)
        itemView.date.text = dateFormatter.formatDate(message.date)

        if (message.author.avatar.isNullOrEmpty()) {
            Picasso.get()
                .load(appearance.defaultAvatar)
                .transform(CircularAvatar())
                .into(itemView.avatar)
        } else {
            Picasso.get()
                .load(message.author.avatar)
                .transform(CircularAvatar())
                .into(itemView.avatar)
        }

        itemView.dateContainer.visibility = if (showMessageDate) View.VISIBLE else View.GONE
    }

    private fun updateStatusIndicator() {
        val message = message ?: return

        if (message.status == Message.Status.NONE) {
            itemView.statusIndicator.visibility = View.GONE
        }

        val icon = when (message.status) {
            Message.Status.READ -> appearance.getReadIndicatorIcon()
            Message.Status.SENT -> appearance.getSentIndicatorIcon()
            else -> appearance.getReadIndicatorIcon()
        }

        itemView.statusIndicator.setImageDrawable(icon)
    }
}