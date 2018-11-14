package com.sample.sample1

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.Spanned
import android.text.format.DateUtils
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : AppCompatActivity() {
    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val bitmap = intent.getParcelableExtra<Bitmap>(INTENT_IMAGE)
        if (bitmap == null) {
            imageView.setImageResource(R.mipmap.ic_launcher)
        } else {
            imageView.setImageBitmap(bitmap)
        }

        intent.getParcelableExtra<User>(INTENT_USER)?.let {
            textView.text = getUserText(it)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getUserText(user: User): Spanned {
        val dateStr = DateUtils.getRelativeDateTimeString(this, user.lastActivity * 1000, DateUtils.DAY_IN_MILLIS,
            DateUtils.WEEK_IN_MILLIS,
            DateUtils.FORMAT_SHOW_TIME)

        @Suppress("DEPRECATION")
        return Html.fromHtml("<big>${user.name}</big><br/>" +
                "<small>Reputation: ${user.reputation}</small><br/>" +
                "<small>Last activity at $dateStr</small><br/><br/>" +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore" +
                " et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut" +
                " aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse" +
                " cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in" +
                " culpa qui officia deserunt mollit anim id est laborum.")
    }

    companion object {
        private const val INTENT_USER = "USER"
        private const val INTENT_IMAGE = "IMAGE"
        fun newIntent(context: Context, user: User, image: Bitmap?): Intent {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra(INTENT_USER, user)
            intent.putExtra(INTENT_IMAGE, image)
            return intent
        }
    }
}
