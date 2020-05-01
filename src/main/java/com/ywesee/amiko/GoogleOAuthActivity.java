package com.ywesee.amiko;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GoogleOAuthActivity extends AppCompatActivity {
    private TextView descriptionTextView;
    private Button syncButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_auth);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Login Google");

        GoogleOAuthActivity _this = this;
        descriptionTextView = findViewById(R.id.description_textview);
        syncButton = findViewById(R.id.sync_button);
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                SyncService.enqueueWork(_this, SyncService.class, 0, intent);
            }
        });

        Intent intent = getIntent();
        handleIntent(intent);

        IntentFilter statusIntentFilter = new IntentFilter(
                SyncService.BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        receivedSyncStatus(intent.getStringExtra("status"));
                    }
                },
                statusIntentFilter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void getAccessTokenWithCode(String code) {
        GoogleOAuthActivity _this = this;
        descriptionTextView.setText(R.string.loading);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    PersistenceManager.getShared().receivedAuthCodeFromGoogle(code);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            descriptionTextView.setText("");
                        }
                    });
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(_this)
                                    .setTitle("Error")
                                    .setMessage(e.getLocalizedMessage())
                                    .setPositiveButton(android.R.string.ok, null)
                                    .show();
                        }
                    });

                }
            }
        });

    }

    private void handleIntent(Intent intent) {
        boolean shouldAskForAuth = true;
        if (intent != null) {
            Uri uri = intent.getData();
            if (uri != null) {
                String code = uri.getQueryParameter("code");
                if (code != null) {
                    shouldAskForAuth = false;
                    getAccessTokenWithCode(code);
                }
            }
        }
        if (shouldAskForAuth && PersistenceManager.getShared().isGoogleLoggedIn()) {
            shouldAskForAuth = false;
        }
        if (shouldAskForAuth) {
            descriptionTextView.setText(R.string.redirecting_to_google);
            String url = PersistenceManager.getShared().getUrlToLoginToGoogle();

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }
    }

    protected void receivedSyncStatus(String status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                descriptionTextView.setText(status);
            }
        });
    }
}
