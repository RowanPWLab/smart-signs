/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.androidthings.assistant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.androidthings.assistant.EmbeddedAssistant.ConversationCallback;
import com.example.androidthings.assistant.EmbeddedAssistant.RequestCallback;
import com.google.android.things.contrib.driver.button.Button;
import com.google.android.things.contrib.driver.voicehat.Max98357A;
import com.google.android.things.contrib.driver.voicehat.VoiceHat;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;
import com.google.assistant.embedded.v1alpha2.SpeechRecognitionResult;
import com.google.auth.oauth2.UserCredentials;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.ThreadLocalRandom;



public class AssistantActivity extends Activity implements Button.OnButtonEventListener {
    private static final String TAG = AssistantActivity.class.getSimpleName();

    // Peripheral and drivers constants.
    private static final int BUTTON_DEBOUNCE_DELAY_MS = 20;
    // Default on using the Voice Hat on Raspberry Pi 3.
    private static final boolean USE_VOICEHAT_I2S_DAC = false;

    // Audio constants.
    private static final String PREF_CURRENT_VOLUME = "current_volume";
    private static final int SAMPLE_RATE = 16000;
    private static final int DEFAULT_VOLUME = 100;

    // Assistant SDK constants.
    private static final String DEVICE_MODEL_ID = "PLACEHOLDER";
    private static final String DEVICE_INSTANCE_ID = "PLACEHOLDER";
    private static final String LANGUAGE_CODE = "en-US";

    // Hardware peripherals.
    private Button mButton;
    private Button idleButton;
    private android.widget.Button mButtonWidget;
    private android.widget.Button mButtonWidgetNav;
    private android.widget.Button idleButtonWidget;
    private Gpio mLed;
    private Max98357A mDac;

    // For Timeout
    // added 10/16/2018; updated 10/18/2018
    //variables need to be static so we can access them in EmbeddedAssistant activity to restart the countdown
    static int TIME_OUT = 40000; //Time to launch the another activity in ms
    static Handler startIdleStateHandler = new Handler();    //handles the runnable that starts idle state after specified time
    static Runnable delayedRunnable;   //code that runs when ready to start idle state after TIME_OUT ms

    // Other
    private Handler mMainHandler;

    // List & adapter to store and display the history of Assistant Requests.
    private EmbeddedAssistant mEmbeddedAssistant;
    private ArrayList<String> mAssistantRequests = new ArrayList<>();
    private ArrayAdapter<String> mAssistantRequestsAdapter;

    mapnavigation nav = new mapnavigation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "starting assistant demo");

        setContentView(R.layout.activity_main);
        ListView assistantRequestsListView = findViewById(R.id.assistantRequestsListView);
        mAssistantRequestsAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                        mAssistantRequests);
        mMainHandler = new Handler(getMainLooper());
        assistantRequestsListView.setAdapter(mAssistantRequestsAdapter);

        mButtonWidget = findViewById(R.id.assistantQueryButton);
        mButtonWidget.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startIdleStateHandler.removeCallbacks(delayedRunnable);   //if making request, stop the code that's waiting to start the idle activity
                try {   //this occasionally caused crashes with null object reference, so adding try-catch
                    mEmbeddedAssistant.startConversation();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        //display query suggestion on screen. Randomize between 4 options using random number generator
        //added 11/29/2018
        int randomNum = ThreadLocalRandom.current().nextInt(0, 3 + 1);
        switch(randomNum){
            case 0:
                ((TextView) findViewById(R.id.suggestionsTV)).setText("You can ask me things like \"Where is room 340?\"");
                break;
            case 1:
                ((TextView) findViewById(R.id.suggestionsTV)).setText("You can ask me things like \"What's the weather like?\"");
                break;
            case 2:
                ((TextView) findViewById(R.id.suggestionsTV)).setText("You say things like \"Give me directions to the office.\"");
                break;
            case 3:
                ((TextView) findViewById(R.id.suggestionsTV)).setText("You can ask me things like \"Where's the ECE office?\"");
                break;
            default:    //the default should never fire, but it's here just in case
                ((TextView) findViewById(R.id.suggestionsTV)).setText("You can ask me things like \"Where's the ECE office?\"");
                break;
        }

        //Handler to automatically start Idle activity after TIME_OUT number of ms
        //If activity is open for TIME_OUT ms without user making request, then IdleActivity opens
        //added 10/16/2018
        startIdleStateHandler.postDelayed(delayedRunnable = new Runnable() {
            @Override
            public void run() {
                    startActivity(new Intent(getApplicationContext(), IdleActivity.class)); //start idle state activity
                finish();
            }
        }, TIME_OUT);

        // Audio routing configuration: use default routing.
        AudioDeviceInfo audioInputDevice = null;
        AudioDeviceInfo audioOutputDevice = null;
        if (USE_VOICEHAT_I2S_DAC) {
            audioInputDevice = findAudioDevice(AudioManager.GET_DEVICES_INPUTS, AudioDeviceInfo.TYPE_USB_DEVICE);
            if (audioInputDevice == null) {
                Log.e(TAG, "failed to find I2S audio input device, using default");
            }
            audioOutputDevice = findAudioDevice(AudioManager.GET_DEVICES_OUTPUTS, AudioDeviceInfo.TYPE_BUILTIN_SPEAKER);
            if (audioOutputDevice == null) {
                Log.e(TAG, "failed to found I2S audio output device, using default");
            }
        }

        try {
            if (USE_VOICEHAT_I2S_DAC) {
                Log.i(TAG, "initializing DAC trigger");
                mDac = VoiceHat.openDac();
                mDac.setSdMode(Max98357A.SD_MODE_SHUTDOWN);

                mButton = VoiceHat.openButton();
                mLed = VoiceHat.openLed();
            } else {
                PeripheralManager pioManager = PeripheralManager.getInstance();
                mButton = new Button(BoardDefaults.getGPIOForButton(),
                    Button.LogicState.PRESSED_WHEN_LOW);
                mLed = pioManager.openGpio(BoardDefaults.getGPIOForLED());
            }

            mButton.setDebounceDelay(BUTTON_DEBOUNCE_DELAY_MS);
            mButton.setOnButtonEventListener(this);

            mLed.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLed.setActiveType(Gpio.ACTIVE_HIGH);
        } catch (IOException e) {
            Log.e(TAG, "error configuring peripherals:", e);
            return;
        } catch (Exception e){
            Log.e(TAG, "error with another exception:", e);
        }

        // Set volume from preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int initVolume = preferences.getInt(PREF_CURRENT_VOLUME, DEFAULT_VOLUME);
        Log.i(TAG, "setting audio track volume to: " + initVolume);

        UserCredentials userCredentials = null;
        try {
            userCredentials =
                    EmbeddedAssistant.generateCredentials(this, R.raw.credentials);
        } catch (IOException | JSONException e) {
            Log.e(TAG, "error getting user credentials", e);
        }
        mEmbeddedAssistant = new EmbeddedAssistant.Builder()
                .setCredentials(userCredentials)
                .setDeviceInstanceId(DEVICE_INSTANCE_ID)
                .setDeviceModelId(DEVICE_MODEL_ID)
                .setLanguageCode(LANGUAGE_CODE)
                .setAudioInputDevice(audioInputDevice)
                .setAudioOutputDevice(audioOutputDevice)
                .setAudioSampleRate(SAMPLE_RATE)
                .setAudioVolume(initVolume)
                .setDeviceModelId(DEVICE_MODEL_ID)
                .setDeviceInstanceId(DEVICE_INSTANCE_ID)
                .setLanguageCode(LANGUAGE_CODE)
                .setRequestCallback(new RequestCallback() {
                    @Override
                    public void onRequestStart() {
                        Log.i(TAG, "starting assistant request, enable microphones");
                        mButtonWidget.setText(R.string.button_listening);
                        mButtonWidget.setEnabled(false);
                    }

                    @Override
                    public void onSpeechRecognition(List<SpeechRecognitionResult> results) {
                        for (final SpeechRecognitionResult result : results) {
                            Log.i(TAG, "assistant request text: " + result.getTranscript() +
                                " stability: " + Float.toString(result.getStability()));
                            mAssistantRequestsAdapter.add(result.getTranscript());
                        }
                    }
                })
                .setConversationCallback(new ConversationCallback() {
                    @Override
                    public void onResponseStarted() {
                        super.onResponseStarted();
                        // When bus type is switched, the AudioManager needs to reset the stream volume
                        if (mDac != null) {
                            try {
                                mDac.setSdMode(Max98357A.SD_MODE_LEFT);
                            } catch (IOException e) {
                                Log.e(TAG, "error enabling DAC", e);
                            }
                        }
                    }

                    @Override
                    public void onResponseFinished() {
                        super.onResponseFinished();
                        if (mDac != null) {
                            try {
                                mDac.setSdMode(Max98357A.SD_MODE_SHUTDOWN);
                            } catch (IOException e) {
                                Log.e(TAG, "error disabling DAC", e);
                            }
                        }
                        if (mLed != null) {
                            try {
                                mLed.setValue(false);
                            } catch (IOException e) {
                                Log.e(TAG, "cannot turn off LED", e);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e(TAG, "assist error:", throwable);
                    }

                    @Override
                    public void onVolumeChanged(int percentage) {
                        Log.i(TAG, "assistant volume changed: " + percentage);
                        // Update our shared preferences
                        Editor editor = PreferenceManager
                                .getDefaultSharedPreferences(AssistantActivity.this)
                                .edit();
                        editor.putInt(PREF_CURRENT_VOLUME, percentage);
                        editor.apply();
                    }

                    @Override
                    public void onConversationFinished() {
                        Log.i(TAG, "assistant conversation finished");
                        mButtonWidget.setText(R.string.button_new_request);
                        mButtonWidget.setEnabled(true);
                    }

                    @Override
                    public void onAssistantResponse(final String response) {
                        if(!response.isEmpty()) {
                            mMainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mAssistantRequestsAdapter.add("Google Assistant: " + response);
                                }
                            });
                        }
                    }

                    public void onDeviceAction(String intentName, JSONObject parameters) {
                        if (parameters != null) {
                            Log.d(TAG, "Get device action " + intentName + " with parameters: " +
                                parameters.toString());
                        } else {
                            Log.d(TAG, "Get device action " + intentName + " with no paramete"
                                + "rs");
                        }
                        //if user asks for a room using a number:
                        if (intentName.equals("com.acme.commands.Room_Number")) {
                            try {   //get room number
                                int room = (int)parameters.getDouble("number");
                                nav.navigate(AssistantActivity.this, room);
                                Log.i(TAG,"Entered Room_Number: Room: " + room);
                                nav.startnavigation();
                                mButtonWidgetNav = findViewById(R.id.NavButton);
                                mButtonWidgetNav.setOnClickListener(new OnClickListener()  {
                                    @Override
                                    public void onClick(View view) {
                                        setContentView(R.layout.activity_main);
                                        mButtonWidget = findViewById(R.id.assistantQueryButton);
                                        mButtonWidget.setOnClickListener(new OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                startIdleStateHandler.removeCallbacks(delayedRunnable);   //if making request, stop the code that's waiting to start the idle activity
                                                mEmbeddedAssistant.startConversation();
                                            }
                                        });
                                        view.invalidate();
                                        view.requestLayout();
                                    }
                                });
                            } catch (JSONException e) {
                                Log.e(TAG, "Cannot get value of command", e);
                            }
                            try {
                                boolean turnOn = parameters.getBoolean("on");
                                mLed.setValue(turnOn);
                            } catch (JSONException e) {
                                Log.e(TAG, "Cannot get value of command", e);
                            } catch (IOException e) {
                                Log.e(TAG, "Cannot set value of LED", e);
                            }
                        //if user asks for a room by a non-numerical name, e.g. "office":
                        //added 11/29/2018
                        } else if (intentName.equals("com.acme.commands.Room_Name")) {
                            try {
                                String room = parameters.getString("name"); //get value of input
                                //actions.json converts room name into number equivalent,
                                // so String room is actually a number when it gets here in the code
                                int room_int = Integer.parseInt(room);  //turn string into int
                                nav.navigate(AssistantActivity.this, room_int); //start navigation
                                Log.d(TAG, "Looking for Room_Name: Room: " + room);
                                nav.startnavigation();
                                mButtonWidgetNav = findViewById(R.id.NavButton);
                                mButtonWidgetNav.setOnClickListener(new OnClickListener()  {
                                    @Override
                                    public void onClick(View view) {
                                        setContentView(R.layout.activity_main);
                                        mButtonWidget = findViewById(R.id.assistantQueryButton);
                                        mButtonWidget.setOnClickListener(new OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                startIdleStateHandler.removeCallbacks(delayedRunnable);   //if making request, stop the code that's waiting to start the idle activity
                                                mEmbeddedAssistant.startConversation();
                                            }
                                        });
                                        view.invalidate();
                                        view.requestLayout();
                                    }
                                });
                            } catch (JSONException e) {
                                Log.e(TAG, "Cannot get value of command", e);
                            }
                        }
                    }
                })
                .build();
        mEmbeddedAssistant.connect();

        //Once we finish setting everything up when switching from Idle to Assistant Activity, start a conversation
        startIdleStateHandler.removeCallbacks(delayedRunnable);   //if making request, stop the code that's waiting to start the idle activity
        // ^ This is not the most efficient implementation: turning runnable on then off when starting activity, but it works and is good enough for now
        mEmbeddedAssistant.startConversation();
    }

    private AudioDeviceInfo findAudioDevice(int deviceFlag, int deviceType) {
        AudioManager manager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        AudioDeviceInfo[] adis = manager.getDevices(deviceFlag);
        for (AudioDeviceInfo adi : adis) {
            if (adi.getType() == deviceType) {
                return adi;
            }
        }
        return null;
    }

    @Override
    public void onButtonEvent(Button button, boolean pressed) {
        try {
            if (mLed != null) {
                mLed.setValue(pressed);
            }
        } catch (IOException e) {
            Log.d(TAG, "error toggling LED:", e);
        }
        if (pressed) {
            mEmbeddedAssistant.startConversation();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "destroying assistant demo");
        if (mLed != null) {
            try {
                mLed.close();
            } catch (IOException e) {
                Log.w(TAG, "error closing LED", e);
            }
            mLed = null;
        }
        if (mButton != null) {
            try {
                mButton.close();
            } catch (IOException e) {
                Log.w(TAG, "error closing button", e);
            }
            mButton = null;
        }
        if (mDac != null) {
            try {
                mDac.close();
            } catch (IOException e) {
                Log.w(TAG, "error closing voice hat trigger", e);
            }
            mDac = null;
        }
        mEmbeddedAssistant.destroy();
    }

}
