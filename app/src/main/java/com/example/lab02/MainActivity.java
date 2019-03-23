package com.example.lab02;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

     RecyclerView feed;
     EditText filter;
    private static List<FeedStruct> feedList;
    private static List<FeedStruct> feedFilter;

    static String filterText = "";

    public static final String EXTRA_TITLE =
            "com.example.lab02.extra.TITLE";
    public static final String EXTRA_LINK =
            "com.example.lab02.extra.LINK";
    public static final String EXTRA_DESCRIPTION =
            "com.example.lab02.extra.DESCRIPTION";

    private static boolean feedGotten = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button Preference = findViewById(R.id.btn_preferenc);
        filter = findViewById(R.id.filter);
        feed = findViewById(R.id.feed);

        feed.setLayoutManager(new LinearLayoutManager(this));

        filter.setText(filterText);

        filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filterFeed();
            }
        });

        Preference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PreferenceActivity.class);

                startActivity(intent);
            }
        });

        if (!feedGotten) {
            new GetFeed().execute((Void) null);
            feedGotten = true;
        } else {
                filterFeed();
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new GetFeed().execute((Void) null);
            }
        }, 0, PreferenceActivity.updateTime);
    }

    public void filterFeed() {
        if (feedList != null) {
            feedFilter = feedSet();
            for (int i = feedFilter.size() - 1; i >= 0; i--) {
                if (!feedFilter.get(i).title.contains(filter.getText().toString())) {
                    feedFilter.remove(i);

                }
            }
            feed.setAdapter(new FeedAdapter(feedFilter));
        }
    }

    //Required for the filter to function
    public List<FeedStruct> feedSet() {
        List<FeedStruct> set = new ArrayList<>();
        for (int i = 0; i < feedList.size(); i++) {
            FeedStruct item = new FeedStruct(feedList.get(i).title,
                                            feedList.get(i).link,
                                            feedList.get(i).description);
            set.add(item);
        }
        return set;
    }

   private class GetFeed extends AsyncTask<Void, Void, Boolean> {

        private String link;

        @Override
       protected void onPreExecute() {
            link = PreferenceActivity.RSSURL;
            feedList = null;
        }

        @Override
       protected Boolean doInBackground(Void... voids) {
            if (link.length() == 0)
                return false;

            try {
                if (!link.startsWith("http://") && !link.startsWith("https://"))
                    link = "http://" + link;

                URL url = new URL(link);
                InputStream inputStream = url.openConnection().getInputStream();
                feedList = parseFeed(inputStream);
                return true;
            }catch (IOException e) {
                Log.e("IO Exception", "Error", e);
            } catch (XmlPullParserException e) {
                Log.e("XmlPull", "Error", e);
            }

            return false;
        }

        @Override
       protected void onPostExecute(Boolean success) {
            if (success) {
                filterFeed();
            } else {
                Toast.makeText(MainActivity.this, "Rss feed url not valid!", Toast.LENGTH_LONG).show();
            }
        }
   }

   public List<FeedStruct> parseFeed(InputStream inputStream) throws XmlPullParserException, IOException {

        String title = null;
        String link = null;
        String description = null;
        int itemCount = 0;
        boolean isItem = false;
        boolean firstLink = false;      //First link is for some reason gotten twice on bbc news
        List<FeedStruct> items = new ArrayList<>();

        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);

            xmlPullParser.nextTag();
            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT && itemCount < PreferenceActivity.listAmount) {
                int eventType = xmlPullParser.getEventType();

                String name = xmlPullParser.getName();
                if (name == null)
                    continue;

                if (eventType == XmlPullParser.END_TAG) {
                    if (name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if (name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                }

                Log.d("MyXmlParser", "Parsing name → " + name);
                String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }

                if (name.equalsIgnoreCase("title")) {
                    title = result;
                } else if (name.equalsIgnoreCase("link")) {
                    if (!firstLink) {
                        firstLink = true;
                    } else {
                        link = result;
                    }
                } else if (name.equalsIgnoreCase("description")) {
                    description = result;
                }

                if (title != null && link != null) {
                    if (isItem) {
                        FeedStruct item = new FeedStruct(title, link, description);
                        items.add(item);
                        itemCount++;
                    }

                    title = null;
                    link = null;
                    description = null;
                    isItem = false;
                }
            }

            return items;
        } finally {
            inputStream.close();
        }
   }

    public class FeedStruct {
        public String title;
        public String link;
        public String description;

        public FeedStruct(String title, String link, String description) {
            this.title = title;
            this.link = link;
            this.description = description;
        }
    }

    public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {
        private List<FeedStruct> feedData;

        private class ViewHolder extends RecyclerView.ViewHolder {
            private View v;
            LinearLayout linearLayout;

            public ViewHolder(View v) {
                super(v);
                this.v = v;
                linearLayout = v.findViewById(R.id.linearLayout);
            }
        }

        public FeedAdapter(List<FeedStruct> feedData) {
            this.feedData = feedData;
        }



        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed, parent, false);
            ViewHolder holder = new ViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final FeedStruct feedS = feedData.get(position);
            ((TextView)holder.v.findViewById(R.id.titleText)).setText("\n" + feedS.title + "\n");    //Double \n\n to give them some space
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, ArticleActivity.class);

                    intent.putExtra(EXTRA_TITLE, feedS.title);
                    intent.putExtra(EXTRA_LINK, feedS.link);
                    intent.putExtra(EXTRA_DESCRIPTION, feedS.description);

                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return feedData.size();
        }
    }


}
