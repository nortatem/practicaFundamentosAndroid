package io.keepcoding.eh_ho.data.posts

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import io.keepcoding.eh_ho.LoadingDialogFragment
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.*
import io.keepcoding.eh_ho.inflate
import io.keepcoding.eh_ho.topics.TAG_LOADING_DIALOG
import kotlinx.android.synthetic.main.fragment_create_post.*
import kotlinx.android.synthetic.main.fragment_create_topic.*
import kotlinx.android.synthetic.main.fragment_create_topic.container
import kotlinx.android.synthetic.main.fragment_create_topic.inputContent
import java.lang.IllegalArgumentException

const val TAG_LOADING_DIALOG = "loading_dialog"

class CreatingPostFragment : Fragment() {

    var interactionListener: CreatePostInteractionListener? = null
    val loadingDialogFragment: LoadingDialogFragment by lazy {
        val message = getString(R.string.label_creating_post)
        LoadingDialogFragment.newInstance(message)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is CreatePostInteractionListener)
            this.interactionListener = context
        else
            throw IllegalArgumentException("Context doesn't implement ${CreatePostInteractionListener::class.java.canonicalName}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_create_post)


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_create_topic, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_send -> createPost()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun createPost() {
        if (isFormValid()) {
            postPost()
        } else {
            showErrors()
        }
    }

    private fun postPost() {
        enableLoadingDialog()
        val model = CreatePostModel(
            inputTitlePost.text.toString(),
            inputContentPost.text.toString()
        )

        context?.let {
            PostsRepo.addPost(
                it.applicationContext,
                model,
                {
                    enableLoadingDialog(false)
                    interactionListener?.onPostCreated()
                },
                {
                    enableLoadingDialog(false)
                    handleError(it)
                }
            )
        }
    }

    private fun enableLoadingDialog(enabled: Boolean = true) {
        if (enabled)
            loadingDialogFragment.show(childFragmentManager, TAG_LOADING_DIALOG)
        else
            loadingDialogFragment.dismiss()
    }

    private fun handleError(error: RequestError) {
        val message =
            if (error.messageResId != null)
                getString(error.messageResId)
            else error.message ?: getString(R.string.error_default)

        Snackbar.make(container, message, Snackbar.LENGTH_LONG).show()
    }

    private fun showErrors() {
        if (inputTitle.text.isEmpty())
            inputTitle.error = getString(R.string.error_empty)
        if (inputContent.text.isEmpty())
            inputContent.error = getString(R.string.error_empty)
    }

    private fun isFormValid() = inputTitlePost.text.isNotEmpty() && inputContentPost.text.isNotEmpty()

    interface CreatePostInteractionListener {
        fun onPostCreated()
    }
}