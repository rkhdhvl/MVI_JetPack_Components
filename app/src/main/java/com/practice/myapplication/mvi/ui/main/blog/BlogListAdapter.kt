package com.practice.myapplication.mvi.ui.main.blog

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.practice.myapplication.R
import com.practice.myapplication.mvi.models.BlogPost
import com.practice.myapplication.mvi.util.DateUtils
import com.practice.myapplication.mvi.util.GenericViewHolder
import kotlinx.android.synthetic.main.fragment_view_blog.view.*
import kotlinx.android.synthetic.main.fragment_view_blog.view.blog_author
import kotlinx.android.synthetic.main.fragment_view_blog.view.blog_image
import kotlinx.android.synthetic.main.fragment_view_blog.view.blog_title
import kotlinx.android.synthetic.main.fragment_view_blog.view.blog_update_date
import kotlinx.android.synthetic.main.layout_blog_list_item.view.*


class BlogListAdapter(
    private val requestManager: RequestManager,
    private val interaction: Interaction? = null
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TAG: String = "AppDebug"
    private val NO_MORE_RESULTS = -1
    private val BLOG_ITEM = 0
    private val NO_MORE_RESULTS_BLOG_MARKER = BlogPost(
        NO_MORE_RESULTS,
        "" ,
        "",
        "",
        "",
        0,
        ""
    )

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BlogPost>() {

        override fun areItemsTheSame(oldItem: BlogPost, newItem: BlogPost): Boolean {
            return oldItem.pk == newItem.pk
        }

        override fun areContentsTheSame(oldItem: BlogPost, newItem: BlogPost): Boolean {
            return oldItem == newItem
        }

    }
    private val differ =
        AsyncListDiffer(
            BlogRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when(viewType){

            NO_MORE_RESULTS ->{
                Log.e(TAG, "onCreateViewHolder: No more results...")
                return GenericViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.layout_no_more_results,
                        parent,
                        false
                    )
                )
            }

            BLOG_ITEM ->{
                return BlogViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.layout_blog_list_item,
                        parent,
                        false
                    ),
                    interaction = interaction,
                    requestManager = requestManager
                )
            }
            else -> {
                return BlogViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.layout_blog_list_item,
                        parent,
                        false
                    ),
                    interaction = interaction,
                    requestManager = requestManager
                )
            }
        }
    }

    internal inner class BlogRecyclerChangeCallback(
        private val adapter: BlogListAdapter
    ) : ListUpdateCallback {

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapter.notifyItemRangeChanged(position, count, payload)
        }

        override fun onInserted(position: Int, count: Int) {
            adapter.notifyItemRangeChanged(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapter.notifyDataSetChanged()
        }

        override fun onRemoved(position: Int, count: Int) {
            adapter.notifyDataSetChanged()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BlogViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(differ.currentList.get(position).pk > -1){
            return BLOG_ITEM
        }
        return differ.currentList.get(position).pk
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(blogList: List<BlogPost>?, isQueryExhausted: Boolean){
        val newList = blogList?.toMutableList()
        if (isQueryExhausted)
            newList?.add(NO_MORE_RESULTS_BLOG_MARKER)
        differ.submitList(newList)
    }

    class BlogViewHolder
    constructor(
        itemView: View,
        val requestManager: RequestManager,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: BlogPost) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }

            requestManager
                .load(item.image)
                .transition(withCrossFade())
                .into(itemView.blog_image)
            itemView.blog_title.text = item.title
            itemView.blog_author.text = item.username
            itemView.blog_update_date.text = DateUtils.convertLongToStringDate(item.date_updated)
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: BlogPost)
    }
}


/*
class BlogListAdapter(
    private val interaction: Interaction? = null,
    private val requestManager: RequestManager
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TAG: String = "AppDebug"
    private val NO_MORE_RESULTS = -1
    private val BLOG_ITEM = 0
    private val NO_MORE_RESULTS_BLOG_MARKER = BlogPost(
        NO_MORE_RESULTS,
        "",
        "",
        "",
        "",
        0,
        ""
    )

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BlogPost>() {

        override fun areItemsTheSame(oldItem: BlogPost, newItem: BlogPost): Boolean {
            return oldItem.pk == newItem.pk
        }

        override fun areContentsTheSame(oldItem: BlogPost, newItem: BlogPost): Boolean {
            return oldItem == newItem
        }

    }

    // for determining which type of viewholder to display
    override fun getItemViewType(position: Int): Int {
        if (differ.currentList.get(position).pk > -1) {
            return BLOG_ITEM
        }
        return differ.currentList.get(position).pk // this will be the case for -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when (viewType) {
            NO_MORE_RESULTS -> {
                return GenericViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.layout_no_more_results,
                        parent,
                        false
                    )
                )
            }

            BLOG_ITEM -> {
                return BlogViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.layout_blog_list_item,
                        parent,
                        false
                    ),
                    interaction = interaction,
                    requestManager = requestManager
                )
            }

            else -> {
                return BlogViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.layout_blog_list_item,
                        parent,
                        false
                    ),
                    interaction = interaction,
                    requestManager = requestManager
                )
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BlogViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<BlogPost>, isQueryExhausted: Boolean) {
        val newlist = list?.toMutableList()
        if (isQueryExhausted) {
            newlist?.add(NO_MORE_RESULTS_BLOG_MARKER)
        }
        differ.submitList(list)
    }

    class BlogViewHolder
    constructor(
        itemView: View,
        val requestManager: RequestManager,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: BlogPost) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }

            requestManager
                .load(item.image)
                .transition(withCrossFade())
                .into(itemView.blog_image)
            itemView.blog_title.text = item.title
            itemView.blog_author.text = item.username
            itemView.blog_update_date.text = DateUtils.convertLongToStringDate(item.date_updated)
        }

    }

    interface Interaction {
        fun onItemSelected(position: Int, item: BlogPost)
    }

    private val differ =
        AsyncListDiffer(
            BlogRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class BlogRecyclerChangeCallback(
        private val adapter: BlogListAdapter
    ) : ListUpdateCallback {
        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapter.notifyItemRangeChanged(position, count, payload)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapter.notifyDataSetChanged()
        }

        override fun onInserted(position: Int, count: Int) {
            adapter.notifyItemRangeChanged(position, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            adapter.notifyDataSetChanged()
        }

    }
}

*/
