package com.example.deeplinkapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

public class DeeplinkActivity extends AppCompatActivity {
    public static final String TAG = "123";

    TextInputLayout webAddressLayout;
    Button goButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deeplink);
        goButton = findViewById(R.id.goButton);
        webAddressLayout = findViewById(R.id.webAddressLayout);
        goButton.setOnClickListener(v -> {
            String text = webAddressLayout.getEditText().getText().toString();
            Intent implicitIntent = new Intent();
            implicitIntent.setAction(Intent.ACTION_VIEW);
            implicitIntent.setDataAndType(Uri.parse("https://" + text), "text/plain");

            try {
                startActivity(implicitIntent);
            }
            catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Activity not found", Toast.LENGTH_SHORT).show();
            }
        });

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getDynamicLink:onFailure", e);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                sharePage();
        }
        return super.onOptionsItemSelected(item);
    }

    private void sharePage() {
        String webAddress = webAddressLayout.getEditText().getText().toString();

            DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLink(Uri.parse("https://" + webAddress))
                    .setDomainUriPrefix("https://deeplinkappthrishna.page.link")
                    // Open links with this app on Android
                    .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                    .buildDynamicLink();

            Uri dynamicLinkUri = dynamicLink.getUri();

    }
}