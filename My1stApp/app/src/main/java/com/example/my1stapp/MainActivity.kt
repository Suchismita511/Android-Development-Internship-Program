package com.example.my1stapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.my1stapp.BuildConfig.GROQ_API_KEY
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class AIChatbotActivity : AppCompatActivity() {

    private lateinit var etPrompt: EditText
    private lateinit var btnSubmit: Button
    private lateinit var tvResponse: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etPrompt = findViewById(R.id.etPrompt)
        btnSubmit = findViewById(R.id.btnSubmit)
        tvResponse = findViewById(R.id.tvResponse)

        btnSubmit.setOnClickListener {
            val prompt = etPrompt.text.toString().trim()
            if (prompt.isNotEmpty()) {
                tvResponse.text = "Loading..."
                apiCall(prompt)
            }
        }
    }

    private fun apiCall(prompt: String) {
        val url = "https://api.groq.com/openai/v1/chat/completions"
        val apiKey = BuildConfig.GROQ_API_KEY
        val jsonBody = JSONObject().apply {
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            })
            put("model", "qwen/qwen3.8-27b")
            put("temperature", 0.6)
            put("max_completion_tokens", 2048)
            put("top_p", 0.95)
            put("stream", false)
            put("reasoning_effort", "default")
        }

        val request = object : JsonObjectRequest(
            Method.POST,
            url,
            jsonBody,
            Response.Listener { response ->
                try {
                    val answer = response
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")

                    tvResponse.text = answer
                } catch (e: Exception) {
                    tvResponse.text = "Api Error: ${e.message}"
                }
            },
            Response.ErrorListener { error ->
                tvResponse.text = "Api Error: ${error.message}"
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $GROQ_API_KEY"
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        Volley.newRequestQueue(this).add(request)
    }
}