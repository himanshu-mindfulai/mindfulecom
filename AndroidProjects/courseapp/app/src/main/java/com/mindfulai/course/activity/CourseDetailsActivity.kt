package com.mindfulai.course.activity

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.mindfulai.course.CustomClass.SPData
import com.mindfulai.course.CustomClass.ServerURL
import com.mindfulai.course.R
import com.mindfulai.course.adapter.ChapterAdapterLive
import com.mindfulai.course.adapter.ChapterAdapterRecorded
import com.mindfulai.course.pojo.ChapterModelBase
import com.mindfulai.course.pojo.ChapterModelData
import com.mindfulai.course.pojo.CourseModelData
import okhttp3.OkHttpClient
import okhttp3.Request

class CourseDetailsActivity : AppCompatActivity() {


    private lateinit var course: CourseModelData
    private lateinit var chapterModelArrayList: ArrayList<ChapterModelData>
     lateinit var spData: SPData
    lateinit var rvChapters: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_details)
        supportActionBar?.hide()
        course = intent.getSerializableExtra("course") as CourseModelData
        spData = SPData(this)
        val tvName = findViewById<TextView>(R.id.tv_name)
        val tvCategory = findViewById<TextView>(R.id.tv_category)
        val tvTutor = findViewById<TextView>(R.id.tv_tutor)
        val tvInstitute = findViewById<TextView>(R.id.tv_institute)
        val tvDescription = findViewById<TextView>(R.id.tv_description)
        val tvFees = findViewById<TextView>(R.id.tv_fees)
        val tvType = findViewById<TextView>(R.id.tv_type)
        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            finish()
        }
        tvName.text = course.name
        tvCategory.text = course.category.name
        tvTutor.text = course.manager.full_name
        tvInstitute.text = course.owner.full_name
        tvDescription.text = course.description
        tvFees.text = "\u20b9 ${course.price}"
        tvType.text = course.type
        rvChapters = findViewById(R.id.rv_chapters)
        rvChapters.layoutManager = LinearLayoutManager(this)
        val FINAL_URL = ServerURL.ALL_CHAPTER_URL + course._id
        GetChapters().execute(FINAL_URL)
    }

   @SuppressLint("StaticFieldLeak")
   inner class GetChapters : AsyncTask<String,Void,ArrayList<ChapterModelData>>(){
        override fun doInBackground(vararg strings: String?): ArrayList<ChapterModelData> {
            Log.e("TAG", "doInBackground:serviceId " + strings[0])
            val client = OkHttpClient()
            val request = Request.Builder()
                    .url(strings[0]!!)
                    .get()
                    .addHeader("token", spData.token)
                    .build()
            val response = client.newCall(request).execute()
            val jsondata = response.body!!.string()
            chapterModelArrayList = Gson().fromJson(jsondata, ChapterModelBase::class.java).data
            return chapterModelArrayList
        }

        override fun onPostExecute(result: ArrayList<ChapterModelData>) {
            super.onPostExecute(result)
            try {
                val chapterAdapter = ChapterAdapterLive(this@CourseDetailsActivity, result)
                rvChapters.setAdapter(chapterAdapter)
                chapterAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                Toast.makeText(this@CourseDetailsActivity, "No chapter found", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
                finish()
            }
        }
    }
}