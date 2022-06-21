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
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.text.DecimalFormat;
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

    public FFmpegAudioEncoder(JMathAnimConfig config) {
        this.config = config;
        try {
            dir = config.getOutputDir().getCanonicalPath() + File.separatorChar;
        } catch (IOException ex) {
            Logger.getLogger(FFmpegAudioEncoder.class.getName()).log(Level.SEVERE, null, ex);
        }
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

            //Create 2 temporary flac audio files that will be deleted
            File sound1 = new File(dir + tempAudioFileName + "0.flac");
            File sound2 = new File(dir + tempAudioFileName + "1.flac");
            FileUtils.forceDeleteOnExit(sound1);
            FileUtils.forceDeleteOnExit(sound2);

            //Ok, prepare to encode
            //First, encode first audio file within tempSoundName1.flac
            JMathAnimScene.logger.info("Processing sound: [1/" + soundItems.size() + "]: ");
            runFirstFfmpegCommand(soundItems.get(0), tempAudioFileName + "1.flac");

            //Now run a loop where encoding tempSoundName1+sound(1) to tempSoundName0
            //                              tempSoundName0+sound(2) to tempSoundName1
            //                              tempSoundName1+sound(3) to tempSoundName0...
            int index = 1;
            for (int i = 1; i < soundItems.size(); i++) {
                JMathAnimScene.logger.info("Processing sound: [" + (i + 1) + "/" + soundItems.size() + "]:");
                runIntermediateFfmpegCommand(soundItems.get(i), tempAudioFileName + index + ".flac", tempAudioFileName + (1 - index) + ".flac");
                index = 1 - index;
            }

            //And finally, do the final encoding
            //mixing tempVideoFile, tempAudioFile to finalVideoFile
           
            JMathAnimScene.logger.info("Joining everything...");
            runFinalFFmpegCommand(tempVideoFileName, tempAudioFileName + index + ".flac", finalOutputFileName);

        } catch (IOException ex) {
            Logger.getLogger(FFmpegAudioEncoder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(FFmpegAudioEncoder.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void runFirstFfmpegCommand(SoundItem soundItem, String outputName) throws IOException, InterruptedException {
         double pitch = Math.round(soundItem.getPitch() * 100) / 100d;
        final String cmd = config.getFfmpegBinDir() + "ffmpeg.exe -y -loglevel quiet"
                + " -i " + soundItem.getPath()
                + " -filter_complex \"[0:0]asetrate=44100*" + pitch + ",atempo=1/1[pitched];"
                + "[pitched]adelay=" + soundItem.getTimeStamp() + "|" + soundItem.getTimeStamp() + "[mixout]\" -map [mixout]"
                + " -c:a flac " + dir + outputName;
        JMathAnimScene.logger.debug(cmd);
        Runtime.getRuntime().exec(cmd).waitFor();
    }

    private void runIntermediateFfmpegCommand(SoundItem soundItem, String inputName, String outputName) throws IOException, InterruptedException {
        inputName = ("".equals(inputName) ? "" : " -i " + dir + inputName);
        String soundPath = soundItem.getPath();
        double pitch = Math.round(soundItem.getPitch() * 100) / 100d;
        final String cmd = config.getFfmpegBinDir()
                + "ffmpeg.exe -y -loglevel quiet"
                + inputName
                + " -i " + soundPath
                + " -filter_complex "
                + "\"[1:0]asetrate=44100*" + pitch + ",atempo=1/1[pitched];"
                + "[pitched]adelay=" + soundItem.getTimeStamp() + "|" + soundItem.getTimeStamp() + "[delayed];[delayed][0:0]amix=inputs=2:duration=longest[mixin];"//Adds the sound at the specified time stamp
                + "[mixin]volume=6.0201dB[mixout]\""//This filter normalizes the volume
                + " -map [mixout]"
                + " -c:a flac " + dir + outputName;
        JMathAnimScene.logger.debug(cmd);

        Runtime.getRuntime().exec(cmd).waitFor();
    }

    private void runFinalFFmpegCommand(String tempVideoName, String tempSoundName, String outputName) throws IOException, InterruptedException {
        String cmd = "";
        cmd = config.getFfmpegBinDir() + "ffmpeg.exe -y -loglevel quiet"
                + " -i " + dir + tempVideoName
                + " -i " + dir + tempSoundName
                + " " + dir + outputName;
        JMathAnimScene.logger.debug(cmd);
        Runtime.getRuntime().exec(cmd).waitFor();
    }

}
