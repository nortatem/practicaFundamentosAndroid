package io.keepcoding.eh_ho.data.posts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.Post
import io.keepcoding.eh_ho.data.PostsRepo
import io.keepcoding.eh_ho.inflate
import kotlinx.android.synthetic.main.fragment_posts.*
import kotlinx.android.synthetic.main.fragment_topics.swipeRefresh
import kotlinx.android.synthetic.main.fragment_topics.viewError
import kotlinx.android.synthetic.main.fragment_topics.viewLoadingFragmentTopics
import kotlinx.android.synthetic.main.view_error.*

const val ARG_ID = "arg_Id"

class PostsFragment : Fragment() {

    companion object {
        fun newInstance (topicId: String): PostsFragment {
            val argument: Bundle = Bundle()
            argument.putString(ARG_ID, topicId)

            val fragment = PostsFragment()
            fragment.arguments = argument

            return fragment
        }
    }

    var postsInteractionListener: PostInteractionListener? = null


    private val postsAdapter: PostsAdapter by lazy {
        val adapter = PostsAdapter {
            this.postsInteractionListener?.onShowPostsP(it)
        }
        adapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is PostInteractionListener)
            postsInteractionListener = context
        else
            throw IllegalArgumentException("Context doesn't implement ${PostInteractionListener::class.java.canonicalName}")

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        Log.d("MSG", arguments?.getString(ARG_ID))

    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_posts)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshSwipe()

        postsAdapter.setPosts(PostsRepo.posts)

        listPosts.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        listPosts.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        listPosts.adapter = postsAdapter


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_posts, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onResume() {
        super.onResume()
        enableLoading()
        loadPosts()
    }

    private fun loadPosts() {
        context?.let {
            PostsRepo
                .getPosts(it.applicationContext,
                    {
                        postsAdapter.setPosts(it)
                        disableLoading()
                    },
                    {
                        showViewError()
                    },
                    arguments?.getString(ARG_ID)?: ""
                )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_logout -> this.postsInteractionListener?.onLogout()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDetach() {
        super.onDetach()
        postsInteractionListener = null
    }

    interface PostInteractionListener {
        fun onCreatePost()
        fun onLogout()
        fun onShowPostsP(post: Post)
    }


    private fun enableLoading(enabled: Boolean = true) {
        if (enabled) {
            fragmentContainerPosts.visibility = View.INVISIBLE
            viewLoadingFragmentPosts.visibility = View.VISIBLE
        } else {
            fragmentContainerPosts.visibility = View.VISIBLE
            viewLoadingFragmentPosts.visibility = View.INVISIBLE
        }
    }


    private fun disableLoading(){
        val runnable = Runnable {
            Thread.sleep(2*1000)
            viewLoadingFragmentPosts.post{
                (enableLoading(false))
            }
        }
        Thread(runnable).start()
    }


    private fun showViewError (){
        fragmentContainerPosts.visibility = View.INVISIBLE
        viewLoadingFragmentTopics.visibility = View.INVISIBLE
        viewError.visibility = View.VISIBLE
    }


    private fun refreshSwipe (){
        swipeRefresh.setOnRefreshListener {
            Toast.makeText(context, "¡Actualizado!", Toast.LENGTH_SHORT).show()
            swipeRefresh.isRefreshing = false
        }

    }



}