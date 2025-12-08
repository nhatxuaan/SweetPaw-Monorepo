package com.example.sweetpawapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.sweetpawapp.R;
import com.example.sweetpawapp.data.model.chat.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages;

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message msg = messages.get(position);

        if (msg.getSenderModel().equals("User")) {
            holder.userContainer.setVisibility(View.VISIBLE);
            holder.shopContainer.setVisibility(View.GONE);
            holder.tvUser.setText(msg.getContent());
        } else {
            holder.userContainer.setVisibility(View.GONE);
            holder.shopContainer.setVisibility(View.VISIBLE);
            holder.tvShop.setText(msg.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {

        LinearLayout userContainer, shopContainer;
        TextView tvUser, tvShop;

        public MessageViewHolder(View itemView) {
            super(itemView);
            userContainer = itemView.findViewById(R.id.message_user_container);
            shopContainer = itemView.findViewById(R.id.message_shop_container);

            tvUser = itemView.findViewById(R.id.tv_message_user);
            tvShop = itemView.findViewById(R.id.tv_message_shop);
        }
    }
}
