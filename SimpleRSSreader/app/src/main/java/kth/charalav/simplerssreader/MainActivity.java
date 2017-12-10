package kth.charalav.simplerssreader;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * The main activity of the app.
 * Simply type a new url rss feed.
 */
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupActivity();
    }

    private void setupActivity(){
        final Button button = findViewById(R.id.startReader);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(v.getContext().CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                //check for internet connection
                if (networkInfo != null && networkInfo.isConnected()) {
                    EditText urlEditText = findViewById(R.id.urlInput);
                    //create a new intent in order to move to the ReaderActivity class
                    Intent intent = new Intent(MainActivity.this, ReaderActivity.class);
                    intent.putExtra("urlText", urlEditText.getText().toString()); //send the urlText
                    startActivity(intent);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("No internet connection!")
                            .setTitle("Failure");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }
}