package aad.finalproject.jhoregatta;

import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private String LOGTAG = GlobalContent.logTag(this);

    Button btn;
    EditText et1, et2;
    ToneGenerator tg;
    ArrayList<Integer> tones;
    Handler handler;
    Runnable r;
    AudioVoiceManager am;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // wire buttons
        Button calculator = (Button) findViewById(R.id.btn_nav_Calculator);

        am = new AudioVoiceManager(this);




        Log.i(LOGTAG, "testing Logtag");
        // assign click listeners
        calculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DistanceCalculator.class);
                startActivity(intent);
            }
        });

        btn = (Button) findViewById(R.id.button);
        et1 = (EditText) findViewById(R.id.editText);
        et2 = (EditText) findViewById(R.id.editText2);

        btn.setVisibility(View.GONE);
        et1.setVisibility(View.GONE);
        et2.setVisibility(View.GONE);

        soundTest(false);

        mergeTest(false);
        audioTest(false);
    }

    private void mergeTest(boolean b) {
        if (b) {

            GlobalContent.unmergedBoats = new ArrayList<>();
            btn.setVisibility(View.VISIBLE);
            btn.setText("Merge");
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), MergeBoatClasses.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void audioTest(boolean b) {
        if (b) {

            am.audioFullStop();
            final int time = 11;
            final int delay = 1;

            handler = new Handler();
            r = new Runnable() {
                @Override
                public void run() {
                    am.playAudioByCase(0, time);
                }
            };
            btn.setVisibility(View.VISIBLE);
            btn.setText("Twenty Second Delay");
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    am.playAudioByCase(-1, time);
                    handler.postDelayed(r, ((time *1000) + delay));
                }
            });
        }
    }

    private void soundTest(boolean isActive) {
        if (isActive) {
            btn.setText("Play");
            btn.setVisibility(View.VISIBLE);
            et1.setVisibility(View.VISIBLE);
            et2.setVisibility(View.VISIBLE);

            makeTones();

            int volume = 100;
            int streamType = AudioManager.STREAM_MUSIC;
            tg = new ToneGenerator(streamType, volume);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tg.startTone(tones.get(Integer.parseInt(et1.getText().toString())),
                            Integer.parseInt(et2.getText().toString()));
                }
            });
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
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, Preferences.class);
                startActivity(intent);
                return true;
//            case R.id.action_ddms:
//                GlobalContent.DDMS(this);
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //go to the boats menu
    public void navigateToBoatMenu(View view){
        Intent intent = new Intent(this,BoatMenu.class);
        startActivity(intent);

        Log.i("Main Menu ", " GotoBoat ");
    }

    //go to the race menu
    public void navigateToRaceMenu(View view){
        Intent intent = new Intent(this, RaceMenu.class);
        startActivity(intent);
        Log.i("Main Menu ", " GotoRace ");
    }

    // send the user to the preferences screen
    public void navigateToPreferences(View view){
        Intent intent = new Intent(this, Preferences.class);
        startActivity(intent);

        Log.i("Main Menu ", " goto Preferences ");
    }

    private void makeTones() {
        tones = new ArrayList<>();
        tones.add(ToneGenerator.MAX_VOLUME);
        tones.add(ToneGenerator.MIN_VOLUME);
        tones.add(ToneGenerator.TONE_CDMA_ABBR_ALERT);
        tones.add(ToneGenerator.TONE_CDMA_ABBR_INTERCEPT);
        tones.add(ToneGenerator.TONE_CDMA_ABBR_REORDER);
        tones.add(ToneGenerator.TONE_CDMA_ALERT_AUTOREDIAL_LITE);
        tones.add(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
        tones.add(ToneGenerator.TONE_CDMA_ALERT_INCALL_LITE);
        tones.add(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE);
        tones.add(ToneGenerator.TONE_CDMA_ANSWER);
        tones.add(ToneGenerator.TONE_CDMA_CALLDROP_LITE);
        tones.add(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_INTERGROUP);
        tones.add(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_NORMAL);
        tones.add(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_PAT3);
        tones.add(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_PAT5);
        tones.add(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_PAT6);
        tones.add(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_PAT7);
        tones.add(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_PING_RING);
        tones.add(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_SP_PRI);
        tones.add(ToneGenerator.TONE_CDMA_CONFIRM);
        tones.add(ToneGenerator.TONE_CDMA_DIAL_TONE_LITE);
        tones.add(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK);
        tones.add(	ToneGenerator.TONE_CDMA_HIGH_L);
        tones.add(	ToneGenerator.TONE_CDMA_HIGH_PBX_L);
        tones.add(	ToneGenerator.TONE_CDMA_HIGH_PBX_SLS);
        tones.add(	ToneGenerator.TONE_CDMA_HIGH_PBX_SS);
        tones.add(	ToneGenerator.TONE_CDMA_HIGH_PBX_SSL);
        tones.add(	ToneGenerator.TONE_CDMA_HIGH_PBX_S_X4);
        tones.add(	ToneGenerator.TONE_CDMA_HIGH_SLS);
        tones.add(	ToneGenerator.TONE_CDMA_HIGH_SS);
        tones.add(	ToneGenerator.TONE_CDMA_HIGH_SSL);
        tones.add(	ToneGenerator.TONE_CDMA_HIGH_SS_2);
        tones.add(	ToneGenerator.TONE_CDMA_HIGH_S_X4);
        tones.add(	ToneGenerator.TONE_CDMA_INTERCEPT);
        tones.add(	ToneGenerator.TONE_CDMA_KEYPAD_VOLUME_KEY_LITE);
        tones.add(	ToneGenerator.TONE_CDMA_LOW_L);
        tones.add(	ToneGenerator.TONE_CDMA_LOW_PBX_L);
        tones.add(	ToneGenerator.TONE_CDMA_LOW_PBX_SLS);
        tones.add(	ToneGenerator.TONE_CDMA_LOW_PBX_SS);
        tones.add(	ToneGenerator.TONE_CDMA_LOW_PBX_SSL);
        tones.add(	ToneGenerator.TONE_CDMA_LOW_PBX_S_X4);
        tones.add(	ToneGenerator.TONE_CDMA_LOW_SLS);
        tones.add(	ToneGenerator.TONE_CDMA_LOW_SS);
        tones.add(	ToneGenerator.TONE_CDMA_LOW_SSL);
        tones.add(	ToneGenerator.TONE_CDMA_LOW_SS_2);
        tones.add(	ToneGenerator.TONE_CDMA_LOW_S_X4);
        tones.add(	ToneGenerator.TONE_CDMA_MED_L);
        tones.add(	ToneGenerator.TONE_CDMA_MED_PBX_L);
        tones.add(	ToneGenerator.TONE_CDMA_MED_PBX_SLS);
        tones.add(	ToneGenerator.TONE_CDMA_MED_PBX_SS);
        tones.add(	ToneGenerator.TONE_CDMA_MED_PBX_SSL);
        tones.add(	ToneGenerator.TONE_CDMA_MED_PBX_S_X4);
        tones.add(	ToneGenerator.TONE_CDMA_MED_SLS);
        tones.add(	ToneGenerator.TONE_CDMA_MED_SS);
        tones.add(	ToneGenerator.TONE_CDMA_MED_SSL);
        tones.add(	ToneGenerator.TONE_CDMA_MED_SS_2);
        tones.add(	ToneGenerator.TONE_CDMA_MED_S_X4);
        tones.add(	ToneGenerator.TONE_CDMA_NETWORK_BUSY);
        tones.add(	ToneGenerator.TONE_CDMA_NETWORK_BUSY_ONE_SHOT);
        tones.add(	ToneGenerator.TONE_CDMA_NETWORK_CALLWAITING);
        tones.add(	ToneGenerator.TONE_CDMA_NETWORK_USA_RINGBACK);
        tones.add(	ToneGenerator.TONE_CDMA_ONE_MIN_BEEP);
        tones.add(	ToneGenerator.TONE_CDMA_PIP);
        tones.add(	ToneGenerator.TONE_CDMA_PRESSHOLDKEY_LITE);
        tones.add(	ToneGenerator.TONE_CDMA_REORDER);
        tones.add(	ToneGenerator.TONE_CDMA_SIGNAL_OFF);
        tones.add(	ToneGenerator.TONE_CDMA_SOFT_ERROR_LITE);
        tones.add(	ToneGenerator.TONE_DTMF_0);
        tones.add(	ToneGenerator.TONE_DTMF_1);
        tones.add(	ToneGenerator.TONE_DTMF_2);
        tones.add(	ToneGenerator.TONE_DTMF_3);
        tones.add(	ToneGenerator.TONE_DTMF_4);
        tones.add(	ToneGenerator.TONE_DTMF_5);
        tones.add(	ToneGenerator.TONE_DTMF_6);
        tones.add(	ToneGenerator.TONE_DTMF_7);
        tones.add(	ToneGenerator.TONE_DTMF_8);
        tones.add(	ToneGenerator.TONE_DTMF_9);
        tones.add(	ToneGenerator.TONE_DTMF_A);
        tones.add(	ToneGenerator.TONE_DTMF_B);
        tones.add(	ToneGenerator.TONE_DTMF_C);
        tones.add(	ToneGenerator.TONE_DTMF_D);
        tones.add(	ToneGenerator.TONE_DTMF_P);
        tones.add(	ToneGenerator.TONE_DTMF_S);
        tones.add(	ToneGenerator.TONE_PROP_ACK);
        tones.add(	ToneGenerator.TONE_PROP_BEEP);
        tones.add(	ToneGenerator.TONE_PROP_BEEP2);
        tones.add(	ToneGenerator.TONE_PROP_NACK);
        tones.add(	ToneGenerator.TONE_PROP_PROMPT);
        tones.add(	ToneGenerator.TONE_SUP_BUSY);
        tones.add(	ToneGenerator.TONE_SUP_CALL_WAITING);
        tones.add(	ToneGenerator.TONE_SUP_CONFIRM);
        tones.add(	ToneGenerator.TONE_SUP_CONGESTION);
        tones.add(	ToneGenerator.TONE_SUP_CONGESTION_ABBREV);
        tones.add(	ToneGenerator.TONE_SUP_DIAL);
        tones.add(	ToneGenerator.TONE_SUP_ERROR);
        tones.add(	ToneGenerator.TONE_SUP_INTERCEPT);
        tones.add(	ToneGenerator.TONE_SUP_INTERCEPT_ABBREV);
        tones.add(	ToneGenerator.TONE_SUP_PIP);
        tones.add(	ToneGenerator.TONE_SUP_RADIO_ACK);
        tones.add(	ToneGenerator.TONE_SUP_RADIO_NOTAVAIL);
        tones.add(	ToneGenerator.TONE_SUP_RINGTONE);
    }
}
