package vn.edu.hust.networkexamples

import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nc = cm.getNetworkCapabilities(cm.activeNetwork)
        if (nc == null) {
            Log.v("TAG", "No connection")
        } else {
            if (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.v("TAG", "WIFI connection")
            }
            if (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.v("TAG", "Mobile connection")
            }
        }

        // sendGet()
        // sendPost()


        findViewById<Button>(R.id.button_download).setOnClickListener {
            downloadFile()
        }

        try {
            val jsonString = "[{\"name\":\"John\", \"age\":20, \"gender\":\"male\"}, {\"name\":\"Peter\", \"age\":21, \"gender\":\"male\"}, {\"name\":\"July\", \"age\":19, \"gender\":\"female\"}]"
            val jArr = JSONArray(jsonString)
            repeat(jArr.length()) {
                val jObj = jArr.getJSONObject(it)
                val name = jObj.getString("name")
                val age = jObj.getInt("age")
                val gender = jObj.getString("gender")
                Log.v("TAG", "$name - $age - $gender")
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        val jsonObject = JSONObject()
        jsonObject.put("MSSV", 20204939)
        jsonObject.put("Hoten", "Nguyen Co Tuan Anh")
        val jAddress = JSONObject()
        jAddress.put("Tinh/Thanh", "Ha Noi")
        jAddress.put("Quan/Huyen", "Hoai Duc")
        jsonObject.put("Dia chi", jAddress)

        Log.v("TAG", jsonObject.toString())

        getUsers()
    }

    fun getUsers() {
        lifecycleScope.launch(Dispatchers.IO) {
            val url = URL("https://jsonplaceholder.typicode.com/users")
            val conn = url.openConnection() as HttpURLConnection

            // Get results
            Log.v("TAG", "Response code: ${conn.responseCode}")

            val reader = conn.inputStream.reader()
            val content = reader.readText()
            reader.close()

            Log.v("TAG", content)

            val jUsers = JSONArray(content)
            repeat(jUsers.length()) {
                val jUser = jUsers.getJSONObject(it)
                val id = jUser.getInt("id")
                val name = jUser.getString("name")
                val city = jUser.getJSONObject("address").getString("city")
                Log.v("TAG", "$id - $name - $city")
            }
        }
    }

    fun sendGet() {
        lifecycleScope.launch(Dispatchers.IO) {
            val url = URL("https://jsonplaceholder.typicode.com/users")
            val conn = url.openConnection() as HttpURLConnection

            // Get results
            Log.v("TAG", "Response code: ${conn.responseCode}")

            val reader = conn.inputStream.reader()
            val content = reader.readText()
            reader.close()

            Log.v("TAG", content)
        }
    }

    fun downloadFile() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Downloading")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.show()

        lifecycleScope.launch(Dispatchers.IO) {
            Log.v("TAG", "Started")

            val url = URL("https://lebavui.github.io/videos/ecard.mp4")
            val conn = url.openConnection() as HttpURLConnection

            // Get results
            Log.v("TAG", "Response code: ${conn.responseCode}")

            val inputStream = conn.inputStream
            val outputStream = openFileOutput("download.mp4", MODE_PRIVATE)

            val buffer = ByteArray(2048)

            val total = conn.contentLength
            var downloaded = 0

            while (true) {
                val len = inputStream.read(buffer)
                if (len <= 0)
                    break
                outputStream.write(buffer, 0, len)
                downloaded += len

                withContext(Dispatchers.Main) {
                    progressDialog.max = total
                    progressDialog.progress = downloaded
                }
            }

            outputStream.close()
            inputStream.close()

            Log.v("TAG", "Completed")
            progressDialog.dismiss()
        }
    }

    fun sendPost() {
        lifecycleScope.launch(Dispatchers.IO) {
            val url = URL("https://httpbin.org/post")
            val conn = url.openConnection() as HttpURLConnection

            // Send POST body
            conn.requestMethod = "POST"
            conn.doOutput = true

            val writer = conn.outputStream.writer()
            writer.write("username=admin&password=123456")
            writer.close()

            // Get results
            Log.v("TAG", "Response code: ${conn.responseCode}")

            val reader = conn.inputStream.reader()
            val content = reader.readText()
            reader.close()

            Log.v("TAG", content)
        }
    }
}