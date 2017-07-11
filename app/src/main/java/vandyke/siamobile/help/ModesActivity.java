/*
 * Copyright (c) 2017 Nicholas van Dyke
 *
 * This file is subject to the terms and conditions defined in Licensing section of the file 'README.md'
 * included in this source code package. All rights are reserved, with the exception of what is specified there.
 */

package vandyke.siamobile.help;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import vandyke.siamobile.R;

public class ModesActivity extends AppIntro {

    public static int COLD_STORAGE = 0;
    public static int REMOTE_FULL_NODE = 1;
    public static int LOCAL_FULL_NODE = 2;

    private int currentSlide;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance("Cold storage",
                "Most secure. Limited functionality - can only view receive addresses and seed. Meant for securely" +
                        " storing coins for long periods of time.",
                R.drawable.safe_image, ContextCompat.getColor(this, android.R.color.white),
                ContextCompat.getColor(this, android.R.color.black),
                ContextCompat.getColor(this, android.R.color.darker_gray)));
        addSlide(AppIntroFragment.newInstance("Remote full node",
                "Run a full node on your computer, and control it from Sia Mobile. Allows all Sia features. Some setup required.",
                R.drawable.remote_node_graphic, ContextCompat.getColor(this, android.R.color.white),
                ContextCompat.getColor(this, android.R.color.black),
                ContextCompat.getColor(this, android.R.color.darker_gray)));
        addSlide(AppIntroFragment.newInstance("Local full node",
                "Run a full node on your Android device. Completely independent. Allows all Sia features. Must " +
                        "sync Sia blockchain, which uses significant storage - about 5GB.",
                R.drawable.local_node_graphic, ContextCompat.getColor(this, android.R.color.white),
                ContextCompat.getColor(this, android.R.color.black),
                ContextCompat.getColor(this, android.R.color.darker_gray)));
        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        setDoneText("Close");
//        setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
//        showSkipButton(false);
//        setProgressButtonEnabled(false);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        switch (currentSlide) {
            case 0:
                setResult(COLD_STORAGE);
                break;
            case 1:
                setResult(REMOTE_FULL_NODE);
                break;
            case 2:
                setResult(LOCAL_FULL_NODE);
                break;
        }
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        int i;
        for (i = 0; i < getSlides().size(); i++) {
            if (newFragment == getSlides().get(i))
                break;
        }
        currentSlide = i;
        switch (currentSlide) {
            case 0:
               setSkipText("Create");
               break;
            case 1:
                setSkipText("Setup");
                break;
            case 2:
                setSkipText("Start");
                showSkipButton(true);
                break;
        }
    }
}