package io.keepcoding.eh_ho.data.posts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.Post
import io.keepcoding.eh_ho.data.UserRepo
import io.keepcoding.eh_ho.isFirsTimeCreated
import io.keepcoding.eh_ho.login.LoginActivity

const val TRANSACTION_CREATE_POST = "create_post"
const val EXTRA_TOPIC_ID = "TOPIC_ID"

class PostsActivity : AppCompatActivity(),PostsFragment.PostInteractionListener, CreatingPostFragment.CreatePostInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_posts)
        if (isFirsTimeCreated(savedInstanceState)){
            val id = intent.getStringExtra(EXTRA_TOPIC_ID)
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, PostsFragment.newInstance(id))
                .commit()
        }

    }


    override fun onCreatePost() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer,CreatingPostFragment())
            .addToBackStack(TRANSACTION_CREATE_POST)
            .commit()
    }


    override fun onShowPostsP(post: Post) {}


    override fun onPostCreated() {
        supportFragmentManager.popBackStack()
    }


    override fun onLogout() {
        UserRepo.logout(this.applicationContext)
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}





