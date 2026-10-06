package com.example.recylerbing

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerViewRequests: RecyclerView
    private lateinit var adapter: FriendRequestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupRecyclerView()
    }

    private fun initViews() {
        recyclerViewRequests = findViewById(R.id.recyclerViewRequests)
    }

    private fun setupRecyclerView() {
        val friendArray = arrayOf(
            FriendRequest(1, "Suchismita", 4),
            FriendRequest(2, "Debanjali", 1),
            FriendRequest(3, "Sneha", 10),
            FriendRequest(4, "Rima", 19),
            FriendRequest(5, "Susmita", 54),
            FriendRequest(6, "Anupama", 33),
            FriendRequest(7, "Suchana", 57),
            FriendRequest(8, "Priti", 78),
            FriendRequest(9, "Subhashree", 77),
            FriendRequest(10, "Anushka", 112)
        )

        adapter = FriendRequestAdapter(friendArray)
        recyclerViewRequests.layoutManager = LinearLayoutManager(this)
        recyclerViewRequests.adapter = adapter
    }
}