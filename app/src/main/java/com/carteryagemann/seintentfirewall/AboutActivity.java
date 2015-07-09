package com.carteryagemann.seintentfirewall;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Simple activity which shows information about the application. The core of this application is
 * its firewall service, but this gives the user something to look at.
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Load about information
        Resources mResources = getResources();
        String appName = mResources.getString(R.string.app_name);
        String author = mResources.getString(R.string.app_author);
        String version = mResources.getString(R.string.app_version);
        String codename = mResources.getString(R.string.app_codename);
        String copyright = mResources.getString(R.string.app_copyright);

        String aboutText = "\nAuthor: " + author +
                "\nVersion: " + version + " " + codename +
                "\n\n" + copyright;

        TextView mAboutViewBig = (TextView) findViewById(R.id.aboutViewBig);
        TextView mAboutViewMedium = (TextView) findViewById(R.id.aboutViewMedium);
        mAboutViewBig.setText(appName);
        mAboutViewMedium.setText(aboutText);
    }
}
