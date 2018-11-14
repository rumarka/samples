package com.sample.sample1

import android.content.Context
import android.graphics.*
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.list_item_layout.view.*
import java.io.IOException
import java.io.InputStream
import java.lang.ref.WeakReference
import java.net.URL

class SampleArrayAdapter(private val context: Context, private val userList: ArrayList<User>) : BaseAdapter() {
    val imageCache = HashMap<String, Array<Bitmap?>>()

    inner class ViewHolder {
        var text: TextView? = null
        var subText: TextView? = null
        var image: ImageView? = null
        var asyncTask: UserImageAsyncTask? = null
    }

    override fun getItemId(position: Int): Long {
        return userList[position].userId
    }

    override fun getItem(position: Int): Any {
        return userList[position]
    }

    override fun getCount(): Int {
        return userList.count()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: SampleArrayAdapter.ViewHolder
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_layout, null)
            holder = ViewHolder()
            holder.text = view.textView
            holder.subText = view.subTextView
            holder.image = view.imageView
            view.tag = holder
        }
        else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val user = userList[position]
        holder.text!!.text = user.name
        holder.subText!!.text = if (user.reputation < 100) "Newbie" else if (user.reputation > 1000) "Expert" else "Member"

        val image = imageCache[user.imageLink]
        if (image == null) {
            holder.image!!.setImageResource(R.mipmap.ic_launcher_round)
            holder.asyncTask?.cancel(false)
            holder.asyncTask = UserImageAsyncTask(WeakReference(holder.image!!), user.imageLink, imageCache)
        }
        else {
            holder.image!!.setImageBitmap(image[0])
        }

        return view
    }

    fun update(list: List<User>) {
        userList.clear()
        userList.addAll(list)
        notifyDataSetChanged()
    }
}

class UserImageAsyncTask(private val weakImageView: WeakReference<ImageView>, private val imageLink: String, private val cache: HashMap<String, Array<Bitmap?>>) : AsyncTask<Void, Void, Array<Bitmap?>>() {
    init {
        execute()
    }

    override fun doInBackground(vararg params: Void?): Array<Bitmap?> {
        val response: InputStream? = try {
            URL(imageLink).openStream()
        } catch (e: IOException) {
            Log.d("SAMPLE1", e.message ?: "Unknown error")
            null
        }

        response?.let {
            val bitmap = BitmapFactory.decodeStream(it)
            val width = bitmap.width
            val height = bitmap.height
            val bitmapRounded = Bitmap.createBitmap(width, height, bitmap.config)
            val canvas = Canvas(bitmapRounded)
            val paint = Paint()
            paint.isAntiAlias = true
            paint.shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            canvas.drawRoundRect(
                RectF(0.0f, 0.0f, width.toFloat(), height.toFloat()),
                (width / 2).toFloat(),
                (height / 2).toFloat(),
                paint
            )
            val matrix = Matrix()
            matrix.postScale(80f / bitmapRounded.width, 80f / bitmapRounded.height)
            val newBitmap = Bitmap.createBitmap(bitmapRounded, 0, 0, bitmapRounded.width, bitmapRounded.height, matrix, true)

            return arrayOf(newBitmap, bitmap)
        }

        return arrayOf(null, null)
    }

    override fun onPostExecute(result: Array<Bitmap?>) {
        super.onPostExecute(result)
        cache[imageLink] = result
        weakImageView.get()?.setImageBitmap(result[0])
    }
}