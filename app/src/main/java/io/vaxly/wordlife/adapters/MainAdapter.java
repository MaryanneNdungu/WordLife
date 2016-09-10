package io.vaxly.wordlife.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.like.LikeButton;

import java.util.ArrayList;
import java.util.List;

import io.vaxly.wordlife.R;
import io.vaxly.wordlife.model.RssItem;
import io.vaxly.wordlife.view.PlayerActivity;


/**
 * Created by benson on 3/14/16.
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private List<RssItem> list;
    private Context context;

    public MainAdapter(Context context, List<RssItem> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell, parent, false);
        return new ViewHolder(view, context, list);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final RssItem post = list.get(position);

        String pubDate = post.getPubDate();
        pubDate = pubDate.substring(0, 17);

        holder.title.setText(post.getTitle());
        holder.pubdate.setText(pubDate);
        holder.desc.setText(post.getDescription());
        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "liked "+ post.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //Setup Views
        public TextView title;
        public TextView pubdate;
        public TextView desc;
        public LikeButton likeButton;

        List<RssItem> itemPosts = new ArrayList<RssItem>();
        Context ctx;

        public ViewHolder(final View itemView, Context ctx, List<RssItem> itemPosts) {

            super(itemView);
            this.itemPosts = itemPosts;
            this.ctx = ctx;
            itemView.setOnClickListener(this);


            title = (TextView) itemView.findViewById(R.id.title_from_address);
            pubdate = (TextView) itemView.findViewById(R.id.title_weight);
            desc = (TextView) itemView.findViewById(R.id.pubDate);
            likeButton = (LikeButton) itemView.findViewById(R.id.heart_button);

        }



        @Override
        public void onClick(View view) {


            int position = getAdapterPosition();
            RssItem clickRssItem = this.itemPosts.get(position);

            Intent intent = new Intent(this.ctx, PlayerActivity.class);
            intent.putExtra("title", clickRssItem.getTitle());
            intent.putExtra("link",   clickRssItem.getLink());
            intent.putExtra("verse",   clickRssItem.getDescription());

            ctx.startActivity(intent);

        }
    }


}