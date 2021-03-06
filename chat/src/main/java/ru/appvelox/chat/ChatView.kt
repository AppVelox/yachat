package ru.appvelox.chat

import android.content.Context
import android.util.AttributeSet
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.appvelox.chat.common.*
import ru.appvelox.chat.model.Author
import ru.appvelox.chat.model.Message

/**
 * Component for displaying messages
 */
class ChatView(context: Context, attributeSet: AttributeSet) : RecyclerView(context, attributeSet) {

    private var adapter: MessageAdapter =
        CommonMessageAdapter(CommonAppearance(context), CommonBehaviour())

    private val swipeToReplyCallback = SwipeToReplyCallback()

    fun setOnItemClickListener(listener: OnMessageClickListener?) {
        adapter.onItemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: OnMessageLongClickListener?) {
        adapter.onItemLongClickListener = listener
    }

    fun setOnReplyClickListener(listener: OnReplyClickListener?) {
        adapter.onReplyClickListener = listener
    }

    fun setOnSwipeActionListener(listener: OnSwipeActionListener?) {
        swipeToReplyCallback.listener = listener
    }

    fun setLoadMoreListener(listener: LoadMoreListener?) {
        adapter.loadMoreListener = listener
    }

    fun setOnAvatarClickListener(listener: OnAvatarClickListener?) {
        adapter.onAvatarClickListener = listener
    }

    fun setOnMessageSelectedListener(listener: OnMessageSelectedListener?) {
        adapter.onMessageSelectedListener = listener
    }

    fun setOnImageClickListener(listener: OnImageClickListener?) {
        adapter.onImageClickListener = listener
    }

    init {
        super.setAdapter(adapter)
        val layoutManager = MessageLayoutManager(context)
        super.setLayoutManager(layoutManager)
        addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (layoutManager.findFirstVisibleItemPosition() <= 5 && !adapter.oldDataLoading) {
                    adapter.requestPreviousMessagesFromListener()
                }
            }
        })
        val itemTouchHelper = ItemTouchHelper(swipeToReplyCallback)
        itemTouchHelper.attachToRecyclerView(this)
        swipeToReplyCallback.listener = object : OnSwipeActionListener {
            override fun onAction(textMessage: Message) {
                Toast.makeText(
                    context,
                    "Reply on textMessage #${textMessage.id}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        adapter.onReplyClickListener = object : OnReplyClickListener {
            override fun onReplyClick(textMessage: Message) {
                navigateToMessage(textMessage)
            }
        }
    }

    fun setSelectOnClick(isSelectable: Boolean) {
        fun onSelected() {
            if (adapter.selectedMessageList.isNotEmpty()) {
                adapter.onMessageSelectedListener?.onSelected(true)
            } else {
                adapter.onMessageSelectedListener?.onSelected(false)
            }
        }

        if (isSelectable) {
            adapter.onItemLongClickListener = object : OnMessageLongClickListener {
                override fun onLongClick(textMessage: Message) {
                    adapter.changeMessageSelection(textMessage)
                    onSelected()
                }
            }
            adapter.onItemClickListener = object : OnMessageClickListener {
                override fun onClick(textMessage: Message) {
                    if (adapter.selectedMessageList.isNotEmpty()) {
                        adapter.changeMessageSelection(textMessage)
                        onSelected()
                    }
                }
            }
        } else {
            adapter.onItemLongClickListener = null
            adapter.onItemClickListener = null
            eraseSelectedMessages()
        }
    }

    fun addMessage(message: Message) {
        adapter.addNewMessage(message)
        layoutManager?.scrollToPosition(adapter.getLastMessageIndex())
    }

    fun setCurrentUserId(id: String) {
        adapter.currentUserId = id
    }

    fun navigateToMessage(message: Message) {
        val scrollTo = adapter.getPositionOfMessage(message)
        layoutManager?.scrollToPosition(scrollTo)
    }

    fun addOldMessages(messages: List<Message>) {
        adapter.addOldMessages(messages)
    }

    fun deleteMessage(message: Message) {
        adapter.deleteMessage(message)
    }

    fun updateMessage(message: Message) {
        adapter.updateMessage(message)
    }

    fun eraseSelectedMessages() {
        adapter.eraseSelectedMessages()
    }

    fun deleteSelectedMessages() {
        adapter.selectedMessageList.forEach {
            deleteMessage(it)
        }
    }

    fun getSelectedMessagesText(): String {
        var text = ""
        with(adapter.selectedMessageList) {
            this.dropLast(1).forEach {
                text += it.text + "\n"
            }
            text += this.last().text
        }
        return text
    }

    fun setDefaultAvatar(avatar: Int) {
        adapter.appearance.defaultAvatar = avatar
        adapter.notifyAppearanceChanged()
    }

    fun setMessageBackgroundCornerRadius(radius: Float) {
        adapter.appearance.messageBackgroundCornerRadius = radius
        adapter.notifyAppearanceChanged()
    }

    fun setIncomingMessageBackgroundColor(color: Int) {
        adapter.appearance.incomingMessageBackgroundColor = color
        adapter.notifyAppearanceChanged()
    }

    fun setOutgoingMessageBackgroundColor(color: Int) {
        adapter.appearance.outgoingMessageBackgroundColor = color
        adapter.notifyAppearanceChanged()
    }

    fun setIncomingSelectedMessageBackgroundColor(color: Int) {
        adapter.appearance.incomingSelectedMessageBackgroundColor = color
        adapter.notifyAppearanceChanged()
    }

    fun setOutgoingSelectedMessageBackgroundColor(color: Int) {
        adapter.appearance.outgoingSelectedMessageBackgroundColor = color
        adapter.notifyAppearanceChanged()
    }

    fun setMessageTextSize(size: Float) {
        adapter.appearance.messageTextSize = size
        adapter.notifyAppearanceChanged()
    }

    fun setAuthorTextSize(size: Float) {
        adapter.appearance.authorNameSize = size
        adapter.notifyAppearanceChanged()
    }

    fun setReplyMessageTextSize(size: Float) {
        adapter.appearance.replyMessageSize = size
        adapter.notifyAppearanceChanged()
    }

    fun setReplyAuthorTextSize(size: Float) {
        adapter.appearance.replyAuthorNameSize = size
        adapter.notifyAppearanceChanged()
    }

    fun setAuthorTextColor(color: Int) {
        adapter.appearance.authorNameColor = color
        adapter.notifyAppearanceChanged()
    }

    fun setMessageTextColor(color: Int) {
        adapter.appearance.messageColor = color
        adapter.notifyAppearanceChanged()
    }

    fun setReplyAuthorTextColor(color: Int) {
        adapter.appearance.replyAuthorNameColor = color
        adapter.notifyAppearanceChanged()
    }

    fun setReplyMessageTextColor(color: Int) {
        adapter.appearance.replyMessageColor = color
        adapter.notifyAppearanceChanged()
    }

    fun setReplyLineColor(color: Int) {
        adapter.appearance.replyLineColor = color
        adapter.notifyAppearanceChanged()
    }

    fun setMaxWidth(width: Int) {
        adapter.appearance.maxMessageWidth = width
        adapter.notifyAppearanceChanged()
    }

    fun setMinWidth(width: Int) {
        adapter.appearance.minMessageWidth = width
        adapter.notifyAppearanceChanged()
    }

    fun notifyDatasetChanged() {
        adapter.notifyDataSetChanged()
    }

    fun addMessages(messages: MutableList<Message>) {
        adapter.addMessages(messages)
    }

    fun deleteMessages() {
        adapter.deleteMessages()
    }

    /**
     * Callback for showing more messages
     */
    interface LoadMoreCallback {
        fun onResult(textMessages: List<Message>)
    }

    interface LoadMoreListener {
        fun requestPreviousMessages(
            count: Int,
            alreadyLoadedMessagesCount: Int,
            callback: LoadMoreCallback
        )
    }

    interface OnMessageClickListener {
        fun onClick(textMessage: Message)
    }

    interface OnReplyClickListener {
        fun onReplyClick(textMessage: Message)
    }

    interface OnSwipeActionListener {
        fun onAction(textMessage: Message)
    }

    interface OnMessageLongClickListener {
        fun onLongClick(textMessage: Message)
    }

    interface OnAvatarClickListener {
        fun onClick(author: Author)
    }

    interface OnMessageSelectedListener {
        fun onSelected(selected: Boolean)
    }

    interface OnImageClickListener {
        fun onClick(imageUrl: String)
    }
}
