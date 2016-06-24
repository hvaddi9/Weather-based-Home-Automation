package com.vaddi.hemanth.myhome;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        if (mViewPager != null) {
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(mViewPager);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class LEDFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        SeekBar ledBrightness;
        ToggleButton ledToggle;
        RelativeLayout ledLayout;
        int ledBrightnessValue = 0;
        boolean ledChangedAtDevice = false;

        public LEDFragment() {
        }

        public static LEDFragment newInstance(int sectionNumber) {
            LEDFragment fragment = new LEDFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_led, container, false);
            final Firebase myLED = new Firebase("https://miniprojecthello.firebaseio.com/led");
            myLED.keepSynced(true);
            ledLayout = (RelativeLayout) rootView.findViewById(R.id.layoutLED);
            ledToggle = (ToggleButton) rootView.findViewById(R.id.ledToggle);
            ledBrightness = (SeekBar) rootView.findViewById(R.id.ledBrightness);
            ledBrightness.setMax(0);
            ledBrightness.setMax(255);

            ledToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    LEDProperties ledProperties;
                    if (isChecked) {
                        ledProperties = new LEDProperties(ledBrightnessValue, true);
                        ledLayout.setBackgroundColor(Color.parseColor("#fec59d"));
                    } else {
                        ledProperties = new LEDProperties(ledBrightnessValue, false);
                        ledLayout.setBackgroundColor(Color.parseColor("#d8d3d3"));
                    }
                    myLED.child("1").setValue(ledProperties);
                }
            });
            ledBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(ledChangedAtDevice) {
                        ledBrightnessValue = progress;
                        HashMap<String, Object> newLEDBrightnessValue = new HashMap<>();
                        newLEDBrightnessValue.put("brightness", ledBrightnessValue);
                        myLED.child("1").updateChildren(newLEDBrightnessValue);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    ledChangedAtDevice = true;
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    ledChangedAtDevice = false;
                    Toast.makeText(getActivity(), "seek bar progress:" + ledBrightnessValue,
                            Toast.LENGTH_SHORT).show();
                }
            });

            myLED.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    ledChangedAtDevice = false;
                    LEDProperties led = dataSnapshot.getValue(LEDProperties.class);
                    Log.d("Led_added", dataSnapshot + "");
                    ledToggle.setChecked(led.getStatus());
                    ledBrightness.setProgress(led.getBrightness());
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    ledChangedAtDevice = true;
                    LEDProperties led = dataSnapshot.getValue(LEDProperties.class);
                    Log.d("Led_changed", dataSnapshot + "");
                    ledToggle.setChecked(led.getStatus());
                    ledBrightness.setProgress(led.getBrightness());
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
            return rootView;
        }
    }

    public static class FanFragment extends Fragment {
        SeekBar fanSpeed;
        ToggleButton fanToggle;
        RelativeLayout fanLayout;
        int fanSpeedValue = 0;
        boolean fanChangedAtDevice = false;
        private static final String ARG_SECTION_NUMBER = "section_number";

        public FanFragment() {
        }

        public static FanFragment newInstance(int sectionNumber) {
            FanFragment fragment = new FanFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_fan, container, false);
            final Firebase myFan = new Firebase("https://miniprojecthello.firebaseio.com/fan");
            fanLayout = (RelativeLayout) rootView.findViewById(R.id.layoutFan);
            fanToggle = (ToggleButton) rootView.findViewById(R.id.fanToggle);
            fanSpeed = (SeekBar) rootView.findViewById(R.id.fanSpeed);

            fanToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    FanProperties fanProperties;
                    if (isChecked) {
                        fanProperties = new FanProperties(fanSpeedValue, true);
                        fanLayout.setBackgroundColor(Color.parseColor("#fec59d"));
                    } else {
                        fanProperties = new FanProperties(fanSpeedValue, false);
                        fanLayout.setBackgroundColor(Color.parseColor("#d8d3d3"));
                    }
                    myFan.child("1").setValue(fanProperties);
                }
            });
            fanSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(!fanChangedAtDevice){
                        Log.d("Progress",progress+"");
                        if (progress>=0 && progress<25) {
                            fanSpeed.setProgress(0);
                        }else if (progress>=25 && progress<75){
                            fanSpeed.setProgress(50);
                        }else if (progress>=75 && progress<125){
                            fanSpeed.setProgress(100);
                        }else if (progress>=125 && progress<175){
                            fanSpeed.setProgress(150);
                        }else if (progress>=175 && progress<225){
                            fanSpeed.setProgress(200);
                        }else{
                            fanSpeed.setProgress(255);
                        }
                        if(fanChangedAtDevice)
                            fanChangedAtDevice = false;
                        else
                            fanChangedAtDevice = true;
                    }
                    if(fanChangedAtDevice){
                        fanSpeedValue = progress;
                        HashMap<String, Object> newFanSpeedValue = new HashMap<>();
                        newFanSpeedValue.put("speed", fanSpeedValue);
                        myFan.child("1").updateChildren(newFanSpeedValue);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    fanChangedAtDevice = false;
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    fanChangedAtDevice = false;
                    Toast.makeText(getActivity(), "seek bar progress:" + fanSpeedValue,
                            Toast.LENGTH_SHORT).show();
                }
            });

            myFan.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    fanChangedAtDevice = false;
                    FanProperties fan = dataSnapshot.getValue(FanProperties.class);
                    Log.d("Fan", dataSnapshot + "");
                    fanToggle.setChecked(fan.getStatus());
                    fanSpeed.setProgress(fan.getSpeed());
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    fanChangedAtDevice = true;
                    FanProperties fan = dataSnapshot.getValue(FanProperties.class);
                    Log.d("Fan", dataSnapshot + "");
                    fanToggle.setChecked(fan.getStatus());
                    fanSpeed.setProgress(fan.getSpeed());
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
            return rootView;
        }
    }

    public static class StatusFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        TextView lightIntensity;
        TextView temperature;
        TextView weather;

        private static final String ARG_SECTION_NUMBER = "section_number";

        public StatusFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static StatusFragment newInstance(int sectionNumber) {
            StatusFragment fragment = new StatusFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_status, container, false);
            lightIntensity = (TextView) rootView.findViewById(R.id.lightIntensity);
            temperature = (TextView) rootView.findViewById(R.id.temperature);
            weather = (TextView) rootView.findViewById(R.id.weather);
            final Firebase myHomeStatus = new Firebase("https://miniprojecthello.firebaseio.com/homeStatus");

            myHomeStatus.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    HomeStatusProperties homeStatus = dataSnapshot.getValue(HomeStatusProperties.class);
                    lightIntensity.setText(homeStatus.getLightIntensity());
                    temperature.setText(homeStatus.getTemperature());
                    weather.setText(homeStatus.getWeather());
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
            myHomeStatus.child("1").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HomeStatusProperties homeStatus = dataSnapshot.getValue(HomeStatusProperties.class);
                    lightIntensity.setText(homeStatus.getLightIntensity());
                    temperature.setText(homeStatus.getTemperature());
                    weather.setText(homeStatus.getWeather());
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    //page 1
                    return LEDFragment.newInstance(position + 1);
                case 1:
                    //page 2
                    return FanFragment.newInstance(position + 1);
                case 2:
                    //page 3
                    return StatusFragment.newInstance(position + 1);
                default:
                    //this page does not exists
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "LED";
                case 1:
                    return "FAN";
                case 2:
                    return "STATUS";
            }
            return null;
        }
    }

}
