package com.sample.sample1

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.URL

class MainActivity : AppCompatActivity() {
    private var adapter = SampleArrayAdapter(this, arrayListOf<User>())
    var userList = listOf<User>()
    private var filteredUserList = listOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView.adapter = adapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val user = filteredUserList[position]
            val bitmap = adapter.imageCache[user.imageLink]
            val intent = DetailsActivity.newIntent(this, user, bitmap?.get(1))
            startActivity(intent)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                showUsers(newText)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean { return false }
        })

        searchView.setOnClickListener {
            searchView.isIconified = false
        }

        val searchEditText = searchView.findViewById<EditText>(android.support.v7.appcompat.R.id.search_src_text)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                if (after == 0) showUsers(null)
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable) { }
        })

        spinner.visibility = View.VISIBLE
        UserListAsyncTask(WeakReference(this))
    }

    fun showUsers(text: String?) {
        filteredUserList = if (text == null) userList else userList.filter { it.name.toUpperCase().contains(text.toUpperCase()) }
        adapter.update(filteredUserList)
    }
}

class UserListAsyncTask(private val weakActivity: WeakReference<MainActivity>) : AsyncTask<Void, Void, List<User>>() {
    val link = "http://api.stackexchange.com/2.2/answers?order=desc&sort=activity&site=stackoverflow"

    init {
        execute()
    }

    override fun doInBackground(vararg params: Void?): List<User> {
        val response: String? = try {
            URL(link)
                .openStream()
                .bufferedReader()
                .use { it.readText() }
        } catch (e: IOException) {
            Log.d("SAMPLE1", e.message ?: "Unknown error")
            null
        }

        response?.let  {
            val json = try {
                JSONObject(it)
                    .getJSONArray("items")
            } catch (e: JSONException) {
                Log.d("SAMPLE1", e.message ?: "Unknown error")
                null
            }

            json?.let { array ->
                val userList = ArrayList<User>()

                for (i in 0 until array.length()) {
                    try {
                        val owner = array.getJSONObject(i).getJSONObject("owner")
                        val lastActivity = array.getJSONObject(i).getLong("last_activity_date")
                        val userId = owner.getLong("user_id")
                        val name = owner.getString("display_name")
                        val imageLink = owner.getString("profile_image")
                        val reputation = owner.getInt("reputation")
                        val user = User(userId,
                            Html.fromHtml(name).toString(),
                            lastActivity,
                            imageLink,
                            reputation)
                        userList.add(user)
                    } catch (e: JSONException) {
                        Log.d("SAMPLE1", e.message ?: "Unknown error")
                    }
                }

                return userList.sortedBy { user -> user.name.toUpperCase() }
            }
        }

        return ArrayList<User>()
    }

    override fun onPostExecute(result: List<User>) {
        super.onPostExecute(result)
        val `this` = weakActivity.get() ?: return
        `this`.userList = result
        `this`.showUsers(null)
        `this`.spinner.visibility = View.GONE
    }
}