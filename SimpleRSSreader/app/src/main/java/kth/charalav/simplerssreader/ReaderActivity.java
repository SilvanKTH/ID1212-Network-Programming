package kth.charalav.simplerssreader;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * The rss feed with all the titles of news displayed.
 * The UI is responsive by introducing asyncTask.
 */
public class ReaderActivity extends AppCompatActivity {
    ListView lvRss;
    ArrayList<String> titles;
    ArrayList<String> links;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        setupActivity();
    }

    private void setupActivity(){
        Bundle bundle = getIntent().getExtras();
        String urlText;
        assert bundle != null;
        urlText = bundle.getString("urlText"); //here is the urlText variable from MainActivity

        lvRss = findViewById(R.id.lvRss);
        titles = new ArrayList<>();
        links = new ArrayList<>();

        /**
         * The method for clicking in a specific title, and redirecting to the browser
         */
        lvRss.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Uri uri = Uri.parse(links.get(i));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        new rssProcess().execute(urlText); //execute the asyncTask
    }

    public InputStream inputStream(URL url){
        try {
            return url.openConnection().getInputStream(); //start reading from the rss url
        } catch (IOException ioe){
            return null;
        }
    }

    private class rssProcess extends AsyncTask<String, Void, Exception>{
        ProgressDialog progressDialog = new ProgressDialog(ReaderActivity.this);
        Exception exception = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading..."); //while retrieving rss feed show this msg
            progressDialog.show();
        }

        @Override
        protected Exception doInBackground(String... urlText) {
            try{
                URL url = new URL(urlText[0]);

                XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
                xmlPullParserFactory.setNamespaceAware(false);
                XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();
                xmlPullParser.setInput(inputStream(url), "UTF_8");

                boolean inItemTag = false;
                int eventType = xmlPullParser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT){
                    if (eventType == XmlPullParser.START_TAG){
                        if (xmlPullParser.getName().equalsIgnoreCase("item")){
                            inItemTag = true;
                        } else if (xmlPullParser.getName().equalsIgnoreCase("title")){
                            if (inItemTag){
                                titles.add(xmlPullParser.nextText());
                            }
                        } else if (xmlPullParser.getName().equalsIgnoreCase("link")){
                            if (inItemTag){
                                links.add(xmlPullParser.nextText());
                            }
                        }
                    } else if (eventType == XmlPullParser.END_TAG && xmlPullParser.getName().equalsIgnoreCase("item")) {
                        inItemTag = false;
                    }
                    eventType = xmlPullParser.next();
                }
            } catch (RuntimeException | XmlPullParserException | IOException e){
                exception = e;
            }

            return exception;
        }

        @Override
        protected void onPostExecute(Exception e) {
            super.onPostExecute(e);
            progressDialog.dismiss(); //dismiss loading message

            if (links.isEmpty()){
                AlertDialog.Builder builder = new AlertDialog.Builder(ReaderActivity.this);
                builder.setMessage("Nothing to show! Press OK to dismiss!")
                        .setTitle("RSS reading error")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(ReaderActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ReaderActivity.this, android.R.layout.simple_list_item_1, titles);
                lvRss.setAdapter(arrayAdapter);
            }
        }
    }
}

