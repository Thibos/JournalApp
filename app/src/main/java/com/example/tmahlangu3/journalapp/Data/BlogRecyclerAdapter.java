package com.example.tmahlangu3.journalapp.Data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tmahlangu3.journalapp.Model.Blog;
import com.example.tmahlangu3.journalapp.R;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Blog> blocList;

    public BlogRecyclerAdapter(Context context, List<Blog> blocList) {
        this.context = context;
        this.blocList = blocList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_row,parent,false);

        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Blog blog = blocList.get(position);
        String imageUrl=null;
        holder.title.setText(blog.getTitle());
        holder.desc.setText((CharSequence) blog.getDesc());
        java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
        String formattedDate = dateFormat.format(new Date(Long.valueOf(blog.timestamp)).getTime());
        holder.timestamp.setText(formattedDate);
        imageUrl = blog.getImage();

        //TODO use picaso to laod image
        Picasso.get().load(imageUrl).into(holder.image);

    }

    @Override
    public int getItemCount() {
        return blocList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public  TextView desc;
        public ImageView image;
        public TextView timestamp;
        String userid;
        public ViewHolder(View itemView,Context ctxt) {
            super(itemView);
            context = ctxt;
            title =(TextView)itemView.findViewById(R.id.postTitleList);
            desc =(TextView)itemView.findViewById(R.id.postTextList);
            timestamp= (TextView)itemView.findViewById(R.id.timestampList);
            image =(ImageView)itemView.findViewById(R.id.postImageList);
            userid=null;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //we go to the next activity
                }
            });


        }
    }
}
