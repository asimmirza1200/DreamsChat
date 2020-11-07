package com.dreams.chat.viewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dreams.chat.R;
import com.dreams.chat.activities.ImageViewerActivity;
import com.dreams.chat.adapters.SubmittedPicsAdapter;
import com.dreams.chat.interfaces.OnMessageItemClick;
import com.dreams.chat.models.AttachmentTypes;
import com.dreams.chat.models.Message;
import com.dreams.chat.models.SendRecipeChatModel;
import com.dreams.chat.models.User;
import com.dreams.chat.utils.Helper;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecipeViewHolder extends BaseMessageViewHolder {
    private final RelativeLayout rlHeader;
    private final ImageView user_image;
    TextView tvName, tvDate, tvType, tvContent, tvCommentsCount, tvLikeCount;
        ImageView  ivComments, ivLike, ivShare;
        RecyclerView rvSubmittedPics;
    private LinearLayout backGround;

    public RecipeViewHolder(View itemView, OnMessageItemClick itemClickListener, ArrayList<Message> messages) {
        super(itemView, itemClickListener, messages);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvType = itemView.findViewById(R.id.tvType);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvCommentsCount = itemView.findViewById(R.id.tvCommentsCount);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            backGround = itemView.findViewById(R.id.backGround);
            rlHeader = itemView.findViewById(R.id.rlHeader);
            user_image = itemView.findViewById(R.id.ivProfilePicture);

            rvSubmittedPics = itemView.findViewById(R.id.rvSubmittedPics);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemClick(false);
                    return true;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(true);
                }
            });

    }

    @Override
    public void setData(final Message message, int position, HashMap<String, User> myUsers, ArrayList<User> myUsersList) {
        super.setData(message, position, myUsers, myUsersList);

        if (isMine()) {
            backGround.setBackgroundResource(R.drawable.shape_incoming_message);
            senderName.setVisibility(View.GONE);
            senderName.setTextColor(context.getResources().getColor(R.color.textColorWhite));
            rlHeader.setVisibility(View.GONE);
            SendRecipeChatModel sendRecipeChatModel=new Gson().fromJson(message.getBody(), SendRecipeChatModel.class);
            rvSubmittedPics.setAdapter( new SubmittedPicsAdapter(context,sendRecipeChatModel.getPost_recipe_list()));

            tvContent.setText(sendRecipeChatModel.getContent());
//            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//            lp.setMargins(10, 0, 0, 0);
//            image.setLayoutParams(lp);
        } else {
            backGround.setBackgroundResource(R.drawable.shape_outgoing_message);
//            senderName.setVisibility(View.VISIBLE);
            senderName.setTextColor(context.getResources().getColor(R.color.textColor4));
            rlHeader.setVisibility(View.VISIBLE);
            try {
                Picasso.get()
                        .load(myUsers.get(message.getSenderId()).getImage())
                        .tag(context)
                        .placeholder(R.drawable.ic_avatar)
                        .error(R.drawable.ic_avatar)
                        .into(user_image);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //cardView.setCardBackgroundColor(ContextCompat.getColor(context, message.isSelected() ? R.color.colorPrimary : R.color.colorBgLight));
        //  ll.setBackgroundColor(message.isSelected() ? ContextCompat.getColor(context, R.color.colorPrimary) : isMine() ? Color.WHITE : ContextCompat.getColor(context, R.color.colorBgLight));



    }

    }