package com.creatokids.hajwithibraheem.Services.STT;

/**
 * Created by Amr Winter on 20/01/2018.
 */

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;

import static com.creatokids.hajwithibraheem.Global.MethodFactory.logMessage;


/**
 * Continuously records audio and notifies the {@link VoiceRecorder.Callback} when voice (or any
 * sound) is heard.
 *
 * <p>The recorded audio format is always {@link AudioFormat#ENCODING_PCM_16BIT} and
 * {@link AudioFormat#CHANNEL_IN_MONO}. This class will automatically pick the right sample rate
 * for the device. Use {@link #getSampleRate()} to get the selected value.</p>
 */
public class VoiceRecorder implements Serializable {

    @NonNull
    public static String TAG = "stt .. VoiceRecorder";

    // All possible sample rates between min sample rate = 4000 & max sample rate = 192000. From Wikipedia
//    private static final int[] SAMPLE_RATE_CANDIDATES = new int[]{4000, 8000, 11025, 16000, 22050,
//            32000, 37800, 44056, 44100, 47250, 48000, 50000, 50400, 88200, 96000, 176400, 192000};
    private static final int[] SAMPLE_RATE_CANDIDATES = new int[]{16000, 11025, 22050, 44100, 48000};

    private static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    // TODO: 22/02/2018 Threshold
    private static final int AMPLITUDE_THRESHOLD = 1500;
//    private static final int AMPLITUDE_THRESHOLD = 3500;

    private static final int SPEECH_TIMEOUT_MILLIS = 3000;
    private static final int MAX_SPEECH_LENGTH_MILLIS = 30 * 1000;

    public static abstract class Callback {

        /**
         * Called when the recorder starts hearing voice.
         */
        public void onVoiceStart() {
            logMessage(TAG + "callback", "the recorder starts hearing voice.");
        }

        /**
         * Called when the recorder is hearing voice.
         *
         * @param data The audio data in {@link AudioFormat#ENCODING_PCM_16BIT}.
         * @param size The size of the actual data in {@code data}.
         */
        public void onVoice(byte[] data, int size) {
            logMessage(TAG + "callback", "the recorder is hearing voice.");
        }

        /**
         * Called when the recorder stops hearing voice.
         */
        public void onVoiceEnd() {
            logMessage(TAG + "callback", "the recorder stops hearing voice.");

        }
    }

    @NonNull
    private final Callback mCallback;

    @Nullable
    private AudioRecord mAudioRecord;

    @Nullable
    private Thread mThread;

    @Nullable
    private static byte[] mBuffer;

    private final Object mLock = new Object();

    /** The timestamp of the last time that voice is heard. */
    private long mLastVoiceHeardMillis = Long.MAX_VALUE;

    /** The timestamp when the current voice is started. */
    private long mVoiceStartedMillis;

    private boolean mAudioRecordIsStopped = false;

    public VoiceRecorder(@NonNull Callback callback) {
        mCallback = callback;
    }

    public void start(String from) {
        logMessage(TAG + ", StartThread", "Start(): " + from);
        // Stop recording if it is currently ongoing.
        stop("Start(): " + from);
        // FIXME: 25/02/2018 Delete the if condition
//        if (mAudioRecord == null){
            // Try to create a new recording session.
            mAudioRecord = createAudioRecord();
//        }
        if (mAudioRecord == null) {
            logMessage(TAG + ", StartThread", "***Cannot instantiate VoiceRecorder");
            throw new RuntimeException("**Cannot instantiate VoiceRecorder");

        }
        // Start recording.
        logMessage(TAG + ", StartThread", "Start recording.");

        mAudioRecord.startRecording();
        // Start processing the captured audio.
        logMessage(TAG + ", StartThread", "Start processing the captured audio.");
        mAudioRecordIsStopped = false;
        // FIXME: 25/02/2018 Delete the if condition
//        if (mThread == null)
            mThread = new Thread(new ProcessVoice());
        mThread.start();
    }

    /**
     * Stops recording audio.
     */
    public void stop(String from) {
        logMessage(TAG + ", StopThread", "Stops recording audio., stop() from: " + from);

        synchronized (mLock) {
            dismiss();
            if (mThread != null) {
                mThread.interrupt();
                // FIXME: 25/02/2018 comment Recording Thread equals null
                mThread = null;
            }
            if (mAudioRecord != null) {
                mAudioRecord.stop();
                mAudioRecord.release();
                // FIXME: 25/02/2018 comment mAudio equals null
                mAudioRecord = null;
            }
            mBuffer = null;
        mAudioRecordIsStopped = true;
        }
    }

    public void killVoiceRecorder(){
        synchronized (mLock) {
            dismiss();
            if (mThread != null) {
                mThread.interrupt();
                mThread = null;
            }
            if (mAudioRecord != null) {
                // FIXME: 25/02/2018 i put the if condition
//                if (!mAudioRecordIsStopped){
                    mAudioRecord.stop();
                    mAudioRecord.release();
//                }
                mAudioRecord = null;
            }
            mBuffer = null;
        }
    }

    /**
     * Dismisses the currently ongoing utterance.
     */
    public void dismiss() {
        logMessage(TAG + ", dismiss", "Dismisses the currently ongoing utterance.");

        if (mLastVoiceHeardMillis != Long.MAX_VALUE) {
            mLastVoiceHeardMillis = Long.MAX_VALUE;
            mCallback.onVoiceEnd();
        }
    }

    /**
     * Retrieves the sample rate currently used to record audio.
     *
     * @return The sample rate of recorded audio.
     */
    public int getSampleRate() {
        if (mAudioRecord != null) {
            return mAudioRecord.getSampleRate();
        }
        return 0;
    }

    /**
     * Creates a new {@link AudioRecord}.
     *
     * @return mcq_a newly created {@link AudioRecord}, or null if it cannot be created (missing
     * permissions?).
     */
    private AudioRecord createAudioRecord() {
        logMessage(TAG + ", createAudio", "creating AudioRecord ...");

        for (int sampleRate : SAMPLE_RATE_CANDIDATES) {
            final int sizeInBytes = AudioRecord.getMinBufferSize(sampleRate, CHANNEL, ENCODING);
            if (sizeInBytes == AudioRecord.ERROR_BAD_VALUE) {
                continue;
            }
            final AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    sampleRate, CHANNEL, ENCODING, sizeInBytes);
            if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                mBuffer = new byte[sizeInBytes];
                logMessage(TAG + ", stt", "created AudioRecord ...");

                return audioRecord;

            } else {
                audioRecord.release();
            }
        }
        return null;
    }

    /**
     * Continuously processes the captured audio and notifies {@link #mCallback} of corresponding
     * events.
     */
    private class ProcessVoice implements Runnable {

        @Override
        public void run() {
            while (true) {
                synchronized (mLock) {
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                    final int size = mAudioRecord.read(mBuffer, 0, mBuffer.length);
                    final long now = System.currentTimeMillis();
                    if (isHearingVoice(mBuffer, size)) {
                        if (mLastVoiceHeardMillis == Long.MAX_VALUE) {
                            logMessage(TAG + ", isHearingVoice", "IN mLastVoiceHeardMillis == Long.MAX_VALUE");
                            mVoiceStartedMillis = now;
                            mCallback.onVoiceStart();
                        }
                        mCallback.onVoice(mBuffer, size);
                        mLastVoiceHeardMillis = now;
                        logMessage(TAG + ", isHearingVoice", "after mLastVoiceHeardMillis == Long.MAX_VALUE");

                        if (now - mVoiceStartedMillis > MAX_SPEECH_LENGTH_MILLIS) {
                            logMessage(TAG + ", isHearingVoice", "now - mVoiceStartedMillis > MAX_SPEECH_LENGTH_MILLIS");
                            end();

                        }
                    } else if (mLastVoiceHeardMillis != Long.MAX_VALUE) {
                        logMessage(TAG + ", isHearingVoice", "mLastVoiceHeardMillis != Long.MAX_VALUE");
                        mCallback.onVoice(mBuffer, size);
                        if (now - mLastVoiceHeardMillis > SPEECH_TIMEOUT_MILLIS) {
                            end();
                        }
                    }
                    else {
                        logMessage(TAG + ", isHearingVoice", "You didn't get anything");

                    }
                }
            }
        }

        private void end() {
            mLastVoiceHeardMillis = Long.MAX_VALUE;
            mCallback.onVoiceEnd();
        }

        private boolean isHearingVoice(byte[] buffer, int size) {
            for (int i = 0; i < size - 1; i += 2) {
                // The buffer has LINEAR16 in little endian.
                int s = buffer[i + 1];
                if (s < 0) s *= -1;
                s <<= 8;
                s += Math.abs(buffer[i]);
                if (s > AMPLITUDE_THRESHOLD) {
                    logMessage(TAG + ", is hearing()", "is Hearing ...");
                    return true;
                }
            }
            return false;
        }

    }

}