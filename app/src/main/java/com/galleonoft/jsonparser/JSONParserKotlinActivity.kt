package com.galleonoft.jsonparser

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.SimpleAdapter
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class JSONParserKotlinActivity : AppCompatActivity() {

    lateinit var Lv_developers: ListView
    lateinit var Pbar: ProgressBar
    internal var developersList = ArrayList<HashMap<String, String>>()


    // Checking Internet is available or not
    private val isNetworkConnected: Boolean
        get() = (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo != null


    companion object {
        var GetPostJSONApi = "http://galleonsoft.com/api-demo/json-rest-api-example.php"
    }

    // Show BackButton on Actionbar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jsonparser_kotlin)

        // Show BackButton and Set custom Title on Actionbar
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = "JSON Parser Kotlin Example"
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)
        }

        // findViewById and set view id
        Pbar = findViewById(R.id.Pbar)
        Lv_developers = findViewById(R.id.Lv_developers)

        if (isNetworkConnected) {
            // Call AsyncTask for getting developer list from server (JSON Api)
            getDeveloper().execute()
        } else {
            Toast.makeText(applicationContext, "No Internet Connection Yet!", Toast.LENGTH_SHORT).show()
        }

    }


    @SuppressLint("StaticFieldLeak")
    inner class getDeveloper : AsyncTask<Void, Void, String>() {

        override fun onPreExecute() {
            // Show Progressbar for batter UI
            Pbar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg voids: Void): String {

            // Here is post Json api example
            val sendParams = HashMap<String, String>()
            // Send parameters and value on JSON api
            sendParams["Name"] = "JigarPatel"

            // Only Get JSON api send HashMap null see below comment example
            // return ApiGetPostHelper.SendParams(GetPostJSONApi, null);
            // Send the HttpPostRequest by HttpURLConnection and receive a Results in return string
            return ApiGetPostHelper.SendParams(GetPostJSONApi, sendParams)
        }

        override fun onPostExecute(results: String?) {
            // Hide Progressbar
            Pbar.visibility = View.GONE

            if (results != null) {
                // See Response in Logcat for understand JSON Results and make DeveloperList
                Log.i("onPostExecute: ", results)
            }

            try {
                // Create JSONObject from string response if your response start from Array [ then create JSONArray
                val rootJsonObject = JSONObject(results)
                val isSucess = rootJsonObject.getString("success")
                if (isSucess == "1") {
                    val developerArray = rootJsonObject.getString("developers")

                    val mJsonArray = JSONArray(developerArray)
                    for (i in 0 until mJsonArray.length()) {
                        // Get single JSON object node
                        val sObject = mJsonArray.get(i).toString()
                        val mItemObject = JSONObject(sObject)
                        // Get String value from json object
                        val Name = mItemObject.getString("Name")
                        val Age = mItemObject.getString("Age")
                        val City = mItemObject.getString("City")

                        // hash map for single jsonObject you can create model.
                        val mHash = HashMap<String, String>()
                        // adding each child node to HashMap key => value/data
                        // Now I'm adding some extra text in value
                        mHash["Name"] = "Name: $Name"
                        mHash["Age"] = "Age: $Age"
                        mHash["City"] = "City: $City"
                        // Adding HashMap pair list into developer list
                        developersList.add(mHash)
                    }

                    // This is simple Adapter (android widget) for ListView
                    val simpleAdapter = SimpleAdapter(
                            applicationContext, developersList,
                            R.layout.simple_listview_item,
                            // Add String[] name same as HashMap Key
                            arrayOf("Name", "Age", "City"),
                            intArrayOf(R.id.tv_name, R.id.tv_age, R.id.tv_city))

                    Lv_developers.adapter = simpleAdapter


                } else {
                    Toast.makeText(applicationContext, "No developers found!", Toast.LENGTH_SHORT).show()
                }


            } catch (e: JSONException) {
                Toast.makeText(applicationContext, "Something wrong. Try Again!", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }

        }
    }


}
