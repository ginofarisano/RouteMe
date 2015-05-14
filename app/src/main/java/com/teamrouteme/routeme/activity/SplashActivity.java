/*
 *  Copyright (c) 2014, Parse, LLC. All rights reserved.
 *
 *  You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 *  copy, modify, and distribute this software in source code or binary form for use
 *  in connection with the web services and APIs provided by Parse.
 *
 *  As with any software that integrates with the Parse platform, your use of
 *  this software is subject to the Parse Terms of Service
 *  [https://www.parse.com/about/terms]. This copyright notice shall be
 *  included in all copies or substantial portions of the software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 *  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 *  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 *  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.teamrouteme.routeme.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;


import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;
import com.teamrouteme.routeme.R;



/**
 * Shows the user profile. This simple activity can function regardless of whether the user
 * is currently logged in.
 */
public class SplashActivity extends Activity {

    private static final int LOGIN_REQUEST = 0;

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;


    private ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_activity);


        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                currentUser = ParseUser.getCurrentUser();

                if (currentUser != null) {
                    showProfileLoggedIn();
                    SplashActivity.this.finish();
                } else {
                    showProfileLoggedOut();
                }

            }
        }, SPLASH_DISPLAY_LENGTH);

    }

    /**
     * go to home activity
     */
    private void showProfileLoggedIn() {

        Intent intent = new Intent(this, HomeActivity.class);

        //for sending data to the activity home
        //intent.putExtra(EXTRA_MESSAGE, message);

        startActivity(intent);
    }

    /**
     * go to login activity
     */
    private void showProfileLoggedOut() {

        ParseLoginBuilder loginBuilder = new ParseLoginBuilder(
                SplashActivity.this);
        startActivityForResult(loginBuilder.build(), LOGIN_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        currentUser = ParseUser.getCurrentUser();

        if (currentUser != null) {
            showProfileLoggedIn();
        }

        SplashActivity.this.finish();

    }
}
