package com.yeastar.untils;

import com.musicg.fingerprint.FingerprintSimilarityComputer;
import com.musicg.wave.Wave;

import java.io.File;

/**
 * @program: SwebTest
 * @description: audio utils
 * @author: huangjx@yeastar.com
 * @create: 2021/04/01
 */
public class AudioUtils {

    /**
     * 录音文件相识度比较
     * @param wav1
     * @param wav2
     * @return  float
     */
    public static float compareWav(String wav1,String wav2){
        float similarity =0.0f;
        String path = System.getProperty("user.dir")+ File.separator+"src"+File.separator+"test"+File.separator+"resources"+File.separator+"testFile"+File.separator+"wav"+File.separator;
        Wave w1 = new Wave(path+wav1);
        Wave w2 = new Wave(path+wav2);

        FingerprintSimilarityComputer fingerprint =
                new FingerprintSimilarityComputer(w1.getFingerprint(), w2.getFingerprint());
        similarity = fingerprint.getFingerprintsSimilarity().getSimilarity();
        System.out.println("Similarity "+fingerprint.getFingerprintsSimilarity().getSimilarity()*100+"%");
        return similarity;
    }
}
