package io.vaxly.wordlife.view;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.mingle.sweetpick.SweetSheet;

import java.util.ArrayList;
import java.util.List;

import io.vaxly.wordlife.R;
import io.vaxly.wordlife.Utils.RssReader;
import io.vaxly.wordlife.adapters.MainAdapter;
import io.vaxly.wordlife.model.RssItem;


public class MainActivity extends AppCompatActivity {


    private List<RssItem> posts = new ArrayList<>();

    private RecyclerView recycler;
    private LinearLayoutManager layoutManager;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;
    private MainAdapter postAdapter;
    private SweetSheet mSweetSheet3;
    private RelativeLayout rl;
    private DrawerLayout mDrawerLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        //myToolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rl = (RelativeLayout)findViewById(R.id.rl);


        recycler = (RecyclerView) findViewById(R.id.mainRecycler);
        mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setLayoutManager(mStaggeredLayoutManager);
        recycler.setHasFixedSize(true);
        postAdapter = new MainAdapter(this, posts);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        new GetRssFeed().execute("http://www.joncourson.com/podcast/podcast.xml");

    }

    private class GetRssFeed extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                RssReader rssReader = new RssReader(params[0]);
                for (RssItem item : rssReader.getItems()){

                    RssItem rssItem = new RssItem();
                    rssItem.setTitle(item.getTitle());
                    rssItem.setLink(item.getLink());
                    rssItem.setDescription(item.getDescription());
                    rssItem.setPubDate(item.getPubDate());
                    posts.add(rssItem);

                }


            } catch (Exception e) {
                Log.v("Error Parsing Data", e + "");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            postAdapter.notifyDataSetChanged();
            recycler.setAdapter(postAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);

    }

}

