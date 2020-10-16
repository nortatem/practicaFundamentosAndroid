package io.keepcoding.eh_ho.topics

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.SignInModel
import io.keepcoding.eh_ho.data.Topic
import io.keepcoding.eh_ho.data.TopicsRepo
import io.keepcoding.eh_ho.data.posts.PostsActivity
import io.keepcoding.eh_ho.inflate
import kotlinx.android.synthetic.main.fragment_sign_in.*
import kotlinx.android.synthetic.main.fragment_topics.*
import kotlinx.android.synthetic.main.view_error.*

class TopicsFragment : Fragment() {

    var topicsInteractionListener: TopicsInteractionListener? = null


    private val topicsAdapter: TopicsAdapter by lazy {
        val adapter = TopicsAdapter {
            this.topicsInteractionListener?.onShowPosts(it)
        }
        adapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is TopicsInteractionListener)
            topicsInteractionListener = context
        else
            throw IllegalArgumentException("Context doesn't implement ${TopicsInteractionListener::class.java.canonicalName}")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_topics)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshSwipe()

        buttonCreate.setOnClickListener {
            this.topicsInteractionListener?.onCreateTopic()
        }

        buttonRetry.setOnClickListener{
            val intent = Intent(context, TopicsActivity::class.java)
            startActivity(intent)
        }



        topicsAdapter.setTopics(TopicsRepo.topics)

        listTopics.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        listTopics.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        listTopics.adapter = topicsAdapter



        listTopics.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    buttonCreate.hide()
                } else {
                    buttonCreate.show()
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })


    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_topics, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onResume() {
        super.onResume()
        enableLoading()
        loadTopics()
    }

    private fun loadTopics() {
        context?.let {
            TopicsRepo
                .getTopics(it.applicationContext,
                    {
                        topicsAdapter.setTopics(it)
                        disableLoading()
                    },
                    {
                        showViewError()
                        }
                )
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_logout -> this.topicsInteractionListener?.onLogout()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDetach() {
        super.onDetach()
        topicsInteractionListener = null
    }

    interface TopicsInteractionListener {
        fun onCreateTopic()
        fun onLogout()
        fun onShowPosts(topic: Topic)
    }


        private fun enableLoading(enabled: Boolean = true) {
        if (enabled) {
            fragmentContainerTopics.visibility = View.INVISIBLE
            viewLoadingFragmentTopics.visibility = View.VISIBLE
        } else {
            fragmentContainerTopics.visibility = View.VISIBLE
            viewLoadingFragmentTopics.visibility = View.INVISIBLE
        }
    }

    private fun disableLoading(){
        val runnable = Runnable {
            Thread.sleep(2*1000)
            viewLoadingFragmentTopics.post{
                (enableLoading(false))
            }
        }
            Thread(runnable).start()
    }


    private fun showViewError (){
            fragmentContainerTopics.visibility = View.INVISIBLE
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