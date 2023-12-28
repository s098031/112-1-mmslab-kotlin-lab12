package com.example.a112_1_mmslab_kotlin_lab12

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.btn_search).setOnClickListener { v: View? ->
            val URL = "https://tools-api.italkutalk.com/java/lab12"
            val request: Request = Request.Builder().url(URL).build()
            val okHttpClient: OkHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient
            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("查詢失敗", e.message!!)
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        if (response.body == null) return
                        val data: Data =
                            com.google.gson.Gson().fromJson(
                                response.body?.string(),
                                Data::class.java
                            )
                        val items =
                            arrayOfNulls<String>(
                                data.result!!.results.size
                            )
                        for (i in items.indices) {
                            items[i] = """
                                
                                列車即將進入:${data.result!!.results[i].Station}
                                列車行駛目的地:${data.result!!.results[i].Destination}
                                """.trimIndent()
                        }
                        runOnUiThread {
                            AlertDialog.Builder(this@MainActivity)
                                .setTitle("台北捷運列車到站站名").setItems(items, null).show()
                        }
                    } else {
                        Log.e("伺服器錯誤", "${response.code} ${response.message}")
                    }
                }
            })
        }
    }
}

internal class Data {
    var result: Result? = null

    internal inner class Result {
        lateinit var results: Array<Results>

        internal inner class Results {
            var Station: String? = null
            var Destination: String? = null
        }
    }
}
