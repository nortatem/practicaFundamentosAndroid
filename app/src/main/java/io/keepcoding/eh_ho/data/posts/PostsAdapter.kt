package io.keepcoding.eh_ho.data.posts

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.Post
import io.keepcoding.eh_ho.inflate
import kotlinx.android.synthetic.main.item_post.view.*
import kotlinx.android.synthetic.main.item_topic.view.*
import java.lang.IllegalArgumentException
import java.util.*

class PostsAdapter(val postClickListener: ((Post) -> Unit)? = null):
    RecyclerView.Adapter<PostsAdapter.PostHolder>() {

    private val posts = mutableListOf<Post>()

    private val listener: ((View) -> Unit) = {
        if (it.tag is Post) {
            postClickListener?.invoke(it.tag as Post)
        } else {
            throw IllegalArgumentException("Topic item view has not a Topic Data as a tag")
        }

    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onCreateViewHolder(list: ViewGroup, viewType: Int): PostHolder {
        val view = list.inflate(R.layout.item_post)
        return PostHolder(view)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        val post = posts[position]
        holder.post = post

    }





    fun setPosts(posts: List<Post>) {
        this.posts.clear()
        this.posts.addAll(posts)
        notifyDataSetChanged()
    }

    inner class PostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var post: Post? = null
            set(value) {
                field = value
                itemView.tag = field

                field?.let {
                    itemView.labelTitlePost.text = it.titlePost
                    val description = it.descriptionContent
                    var deleteOpenTag = description.replace("<p>","")
                    var deleteCloseTag = deleteOpenTag.replace("</p>", "")
                    itemView.descriptionContent.text = deleteCloseTag
                    itemView.datePost.text = it.date.toString()
                    setTimeOffset(it.getTimeOffsetPost())
                }
            }

        private fun setTimeOffset(timeOffset: Post.TimeOffset) {

            val quantityString = when (timeOffset.unit) {
                Calendar.YEAR -> R.plurals.years
                Calendar.MONTH -> R.plurals.months
                Calendar.DAY_OF_MONTH -> R.plurals.days
                Calendar.HOUR -> R.plurals.hours
                else -> R.plurals.minutes
            }

            if (timeOffset.amount == 0) {
                itemView.datePost.text =
                    itemView.context.resources.getString(R.string.minutes_zero)
            } else {
                itemView.datePost.text =
                    itemView.context.resources.getQuantityString(
                        quantityString,
                        timeOffset.amount,
                        timeOffset.amount
                    )
            }
        }
    }

}
