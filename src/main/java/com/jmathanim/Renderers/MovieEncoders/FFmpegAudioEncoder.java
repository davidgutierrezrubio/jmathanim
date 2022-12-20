/*
 * Copyright (C) 2022 David Gutierrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.jmathanim.Renderers.MovieEncoders;

import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.jmathanim.JMathAnimScene;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 * A class that handles calls to ffmpeg executable to add sounds to the created
 * movie
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class FFmpegAudioEncoder {

    private final JMathAnimConfig config;
    String dir;
    boolean conversionOk;

    public FFmpegAudioEncoder(JMathAnimConfig config) {
        this.config = config;
        try {
            dir = config.getOutputDir().getCanonicalPath() + File.separatorChar;
        } catch (IOException ex) {
            Logger.getLogger(FFmpegAudioEncoder.class.getName()).log(Level.SEVERE, null, ex);
        }
        conversionOk = true;
    }

    /**
     * Do a postprocessing call to ffmpeg executable to add sounds
     *
     * @param soundItems An ArrayList with sound items
     */
    public void processSounds(ArrayList<SoundItem> soundItems) {
         if (soundItems.isEmpty()) {
            return;
        }
        File ffmpegExecFile = new File(config.getFfmpegBinExecutable());
        if (!ffmpegExecFile.exists()) {
            JMathAnimScene.logger.warn("ffmpeg executable not found. Sounds will not be processed.");
            JMathAnimScene.logger.warn("Set the ffmpeg executable location with config.setFfmpegExecutable in the setupSketch method");
            return;
        }
        String tempVideoFileName = "";
        String finalOutputFileName = "";
        String tempAudioFileName = config.getOutputFileName();
        try {
            //Create temporary files
            finalOutputFileName = config.getSaveFilePath().getName();
            tempVideoFileName = "_" + finalOutputFileName;
            final File tmpFile = new File(dir + tempVideoFileName);
            //Copy output file to temp
            FileUtils.copyFile(config.getSaveFilePath(), tmpFile);
            FileUtils.forceDeleteOnExit(tmpFile);

            //Create 2 temporary wav audio files that will be deleted
            File sound1 = new File(dir + tempAudioFileName + "0.wav");
            File sound2 = new File(dir + tempAudioFileName + "1.wav");
            FileUtils.forceDeleteOnExit(sound1);
            FileUtils.forceDeleteOnExit(sound2);

            //Ok, prepare to encode
            //First, encode first audio file within tempSoundName1.wav
            JMathAnimScene.logger.debug("Processing sound: [1/" + soundItems.size() + "]: ");
            runFirstFfmpegCommand(soundItems.get(0), tempAudioFileName + "1.wav");

            //Now run a loop where encoding tempSoundName1+sound(1) to tempSoundName0
            //                              tempSoundName0+sound(2) to tempSoundName1
            //                              tempSoundName1+sound(3) to tempSoundName0...
            int index = 1;
            for (int i = 1; i < soundItems.size(); i++) {
                JMathAnimScene.logger.debug("Processing sound: [" + (i + 1) + "/" + soundItems.size() + "]");
                runIntermediateFfmpegCommand(soundItems.get(i), tempAudioFileName + index + ".wav", tempAudioFileName + (1 - index) + ".wav");
                index = 1 - index;
            }

            //And finally, do the final encoding
            //mixing tempVideoFile, tempAudioFile to finalVideoFile
            JMathAnimScene.logger.debug("Joining sounds and video...");
            runFinalFFmpegCommand(tempVideoFileName, tempAudioFileName + index + ".wav", finalOutputFileName);
            if (conversionOk) {
                JMathAnimScene.logger.info("Sounds added succesfully");
            } else {
                JMathAnimScene.logger.error("Unexpected error converting. At least one of the ffmpeg calls returned error.");
            }

        } catch (IOException ex) {
            Logger.getLogger(FFmpegAudioEncoder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(FFmpegAudioEncoder.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void runFirstFfmpegCommand(SoundItem soundItem, String outputName) throws IOException, InterruptedException {
        double pitch = Math.round(soundItem.getPitch() * 100) / 100d;
        final String cmd = config.getFfmpegBinExecutable()
                + " -y -loglevel quiet"
                + " -i " + soundItem.getPath()
                + getFilterComplex(0, pitch, soundItem.getTimeStamp(), false)
                + dir + outputName;
        JMathAnimScene.logger.debug(cmd);
        runFfmpegProcess(cmd);
    }

    private void runIntermediateFfmpegCommand(SoundItem soundItem, String inputName, String outputName) throws IOException, InterruptedException {
        inputName = ("".equals(inputName) ? "" : " -i " + dir + inputName);
        String soundPath = soundItem.getPath();
        double pitch = Math.round(soundItem.getPitch() * 100) / 100d;
        final String cmd = config.getFfmpegBinExecutable()
                + " -y -loglevel quiet"
                + inputName
                + " -i " + soundPath
                + getFilterComplex(1, pitch, soundItem.getTimeStamp(), true)
                + dir + outputName;
        JMathAnimScene.logger.debug(cmd);
        runFfmpegProcess(cmd);
    }

    private String getFilterComplex(int sourceId, double pitch, long delay, boolean shouldMix) {
        String filter = " -filter_complex \"";
        String src = sourceId + ":0";
        String dst;

        //Pitch
        if (pitch != 1) {
            //Resample to 44100hz
            dst = "resampled";
            filter += "[" + src + "]aresample=44100[" + dst + "];";
            src = dst;
            filter += "[" + src + "]asetrate=44100*" + pitch + ",atempo=1/1[" + dst + "];";
            src = dst;
        }
        //Delay
        dst = "delayed";
        filter += "[" + src + "]adelay=" + delay + "|" + delay + "[" + dst + "];";
        src = dst;

        if (shouldMix) {//If true, there are 2 input audio sources and should be mixed
            //mix the audio sources
            dst = "mixin";
            filter += "[" + src + "]amix=inputs=2:duration=longest[" + dst + "];";//Adds the sound at the specified time stamp
            src = dst;
        }
        //Volume filter
        dst = "mixout";
        filter += "[" + src + "]volume=6.0201dB[" + dst + "]";//This filter normalizes the volume

        //Close filter and map
        filter += "\" -map [" + dst + "] ";

        return filter;
    }

    private void runFinalFFmpegCommand(String tempVideoName, String tempSoundName, String outputName) throws IOException, InterruptedException {
        String cmd = config.getFfmpegBinExecutable()
                + " -y -loglevel quiet"
                + " -i " + dir + tempVideoName
                + " -i " + dir + tempSoundName
                + " " + dir + outputName;
        JMathAnimScene.logger.debug(cmd);
        runFfmpegProcess(cmd);
    }

    private void runFfmpegProcess(final String cmd) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(cmd);
        process.waitFor();
        //This return value gives problems as it differs from OS
//        if (process.exitValue() != 0) {
//            conversionOk = false;
//        }
    }
}
