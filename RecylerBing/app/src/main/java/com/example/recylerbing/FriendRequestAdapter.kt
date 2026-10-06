package com.example.recylerbing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FriendRequestAdapter(
    private val friendArray: Array<FriendRequest>
) : RecyclerView.Adapter<FriendRequestAdapter.FriendViewHolder>() {

    class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvMutualFriends: TextView = itemView.findViewById(R.id.tvMutualFriends)
        val btnConfirm: Button = itemView.findViewById(R.id.btnConfirm)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend_request, parent, false)
        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friendArray[position]
        holder.tvName.text = friend.name
        holder.tvMutualFriends.text = "${friend.mutualFriendsCount} mutual friends"
    }

    override fun getItemCount(): Int = friendArray.size
}