package com.dreams.chat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dreams.chat.R;
import com.dreams.chat.activities.CallListActivity;
import com.dreams.chat.activities.MainActivity;
import com.dreams.chat.models.User;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommentsListAdapter extends RecyclerView.Adapter<CommentsListAdapter.ViewHolder> implements Filterable {
    private Context context;
    private ArrayList<Comments> myUsers;
    private ArrayList<Comments> itemsFiltered;

    public CommentsListAdapter(Context context, ArrayList<Comments> myUsers) {
        this.context = context;
        itemsFiltered = myUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.popup_list_item,
                viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final Comments user = itemsFiltered.get(i);

        if (user.getImage() != null && !user.getImage().isEmpty()) {

            Picasso.get()
                    .load(user.getImage())
                    .tag(this)
                    .placeholder(R.drawable.ic_avatar)
                    .error(R.drawable.ic_avatar)
                    .into(viewHolder.userImage);
            viewHolder.myProgressBar.setVisibility(View.GONE);
        }
        viewHolder.userName.setText(user.getId());
        viewHolder.status.setText(user.getComments());



    }

    @Override
    public int getItemCount() {
        return itemsFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString();

                ArrayList<Comments> filtered = new ArrayList<>();

                if (query.isEmpty()) {
                    filtered = myUsers;
                } else {
                    for (Comments user : myUsers) {
                        if (user.getComments().toLowerCase().contains(query.toLowerCase())) {
                            filtered.add(user);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.count = filtered.size();
                results.values = filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                itemsFiltered = (ArrayList<Comments>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView userImage;
        private ImageView audioCall;
        private ImageView videoCall;
        private TextView userName;
        private TextView status;
        private ProgressBar myProgressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.userImage);
            userName = itemView.findViewById(R.id.userName);
            status = itemView.findViewById(R.id.status);
            audioCall = itemView.findViewById(R.id.audioCall);
            videoCall = itemView.findViewById(R.id.videoCall);
            myProgressBar = itemView.findViewById(R.id.progressBar);

        }
    }
}
