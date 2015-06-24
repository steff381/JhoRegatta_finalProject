package aad.finalproject.jhoregatta;


import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

public class AudioVoiceManager extends MainActivity {

    private static final String LOGTAG = "AudioManager";

    //media player used by all tracks
    private MediaPlayer voicePlayer;


    private ArrayList<String> audioFiles;

    //current context
    private Context myContext;

    //delay set in instance
    private String fileName;

    //tone generator for when time is less than 11 seconds
    private ToneGenerator toneGenerator;

    // handler for scheduling runnable tasks
    private Handler handlerFlagAlarm;
    private Handler handlerCountdownAlarm;
    private Handler handlerThirtySecondAlarm;
    private Handler handlerFortySecondAlarm;
    private Handler handlerMillisStartDelay;

    // runnables to be scheduled or played
    private Runnable runnableVoice;
    private Runnable runnableAudioFileAssignment;
    private Runnable runnableToneFortySecondWarning;
    private Runnable runnableSwitchToCountdown;
    private Runnable runnableSwitchToThirty;

    public AudioVoiceManager(Context context) {
        voicePlayer = new MediaPlayer();
        this.myContext = context;
        wireAudioFiles();

    }

    private void wireAudioFiles() {
        // create an array list with the file names for the audio tracks to play
        audioFiles = new ArrayList<>();
        audioFiles.add("fem_perf_20_class_up.wav");
        audioFiles.add("fem_perf_20_prep_up_v2.wav");
        audioFiles.add("fem_perf_20_prep_down_v2.wav");
        audioFiles.add("fem_perf_20_class_down_v2.wav");
//        audioFiles.add("fem_30_sec_warning_on_mark_v2.wav");

        // create a tone generator with full volume that plays music
        toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

        // instances of the used handlers
        handlerFlagAlarm = new Handler();
        handlerCountdownAlarm = new Handler();
        handlerThirtySecondAlarm = new Handler();
        handlerFortySecondAlarm = new Handler();
        handlerMillisStartDelay = new Handler();


        // RUNNABLE SECTION
        runnableSwitchToCountdown = new Runnable() {
            @Override
            public void run() {
                // switch the audio file to that of the count down voice
                mediaAssignmentHandler("fem_ten_sec_countdown_horn_v2.wav");
                voicePlayer.start();
            }
        };

        runnableVoice = new Runnable() {
            @Override
            public void run() {
                // play the selected audio file
                voicePlayer.start();
            }
        };

        runnableAudioFileAssignment = new Runnable() {
            @Override
            public void run() {
                // switch file is to be played
                mediaAssignmentHandler(fileName);
            }
        };

        runnableToneFortySecondWarning = new Runnable() {
            @Override
            public void run() {
                // 40 second warning tone
                toneGenerator.startTone(ToneGenerator.TONE_CDMA_MED_S_X4, 2000);
            }
        };

        runnableSwitchToThirty = new Runnable() {
            @Override
            public void run() {
                // switch the audio file the 30 second warning
                mediaAssignmentHandler("fem_30_sec_warning_on_mark_v7.wav");
                voicePlayer.start();
                handlerMillisStartDelay.postDelayed(runnableAudioFileAssignment, 6000);
            }
        };
    }

    public void playAudioByCase(int caseNum, double timerLengthInSeconds) {

        long delay = 700;
        // convert seconds to millis
        long timerLengthInMillis =  (long) timerLengthInSeconds * 1000;

        // get the audio file name from the array
        fileName = audioFiles.get((caseNum));

        // wait 500ms before assigning files, this will prevent the audio event from
        handlerMillisStartDelay.postDelayed(runnableAudioFileAssignment, delay);

        // overriding any audio already playing.
        scheduleTasks(timerLengthInMillis, caseNum);

    }

    private void scheduleTasks(long timerLengthInMillis, int caseNum) {
        long audioForty = 40000;
        long audioThirtyFour = 35000;
        long audioTwenty = 20000;
        long audioTen = 10000;
        //check if there is enough time for a 20 second warning

        if (timerLengthInMillis > 40999 && caseNum == 3) {
            Log.i(LOGTAG, "40 second contingency");
            //schedule the 40 second warning
            handlerFortySecondAlarm.postDelayed(runnableToneFortySecondWarning,
                    (timerLengthInMillis - audioForty));
            handlerThirtySecondAlarm.postDelayed(runnableSwitchToThirty,
                    (timerLengthInMillis - audioThirtyFour));
            //schedule the timer task for the 20 second warning voice
            handlerFlagAlarm.postDelayed(runnableVoice, (timerLengthInMillis - audioTwenty));
            //schedule the 10 second count down voice
            handlerCountdownAlarm.postDelayed(runnableSwitchToCountdown,
                    (timerLengthInMillis - audioTen));
        } else if (timerLengthInMillis > 20999) {
            Log.i(LOGTAG, "20 second contingency");
            //schedule the timer task for the 20 second warning voice
            handlerFlagAlarm.postDelayed(runnableVoice, (timerLengthInMillis - audioTwenty));
            //schedule the 10 second count down voice
            handlerCountdownAlarm.postDelayed(runnableSwitchToCountdown,
                    (timerLengthInMillis - audioTen));
        } else if (timerLengthInMillis > 10999) {
            Log.i(LOGTAG, "10 second contingency");
            //schedule the 10 second count down voice
            handlerCountdownAlarm.postDelayed(runnableSwitchToCountdown,
                    (timerLengthInMillis - audioTen));
        }
    }

    private void mediaAssignmentHandler(String fileName) {
        // handle the media player and source
        try {
            // attempt to reset the media player. If it hasn't been set then whatever...
            try {
                voicePlayer.stop();
                voicePlayer.reset();
                Log.i(LOGTAG, "mediaAssingmentHandler: Stopped and reset VoicePlayer");
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            //assign the audio file to the ADF
            AssetFileDescriptor afd = myContext.getAssets().openFd(fileName);
            Log.i(LOGTAG, " File played is " + fileName);
            //set the players data source elements
            voicePlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            voicePlayer.prepare(); // prepare the media player for playing
        } catch (IOException e) {
            e.printStackTrace(); //could be tricky!
            System.exit(-1); // exit out
        }
    }

    public void audioFullStop() {
        //if the timer is playing the countdown audio, stop it
        if (voicePlayer.isPlaying()) {
            Log.i(LOGTAG, "Media Player is Playing");
            voicePlayer.stop(); // stop the audio
            voicePlayer.reset(); //reset to original state
            //re-assign media player properties to adf
        }

        //try to cancel the post delay schedules. If null then do nothing
        try {
            handlerFlagAlarm.removeCallbacks(runnableVoice);
            handlerCountdownAlarm.removeCallbacks(runnableSwitchToCountdown);
            handlerThirtySecondAlarm.removeCallbacks(runnableSwitchToThirty);
            handlerFortySecondAlarm.removeCallbacks(runnableToneFortySecondWarning);
            handlerMillisStartDelay.removeCallbacks(runnableAudioFileAssignment);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
