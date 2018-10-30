package com.imooc.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 1819014975
 * @Title: FfmpegText
 * @ProjectName imooc-videos-dev
 * @Description: TODO
 * @date 2018/10/16 14:29
 */
public class FfmpegVideoJpg {

    private static final Logger LOGGER = LoggerFactory.getLogger(FfmpegVideoJpg.class);

    private String ffmpegEXE;

    private static final String TemporaryName = "text";

    public FfmpegVideoJpg(String ffmpegEXE){
        this.ffmpegEXE = ffmpegEXE;
    }


    /**
     * 为视频添加新的音轨
     */
    public void converter(String videoInputPath,String videoJpgOutputPath) throws IOException {
        //转换视频格式的命令
        //ffmpeg.exe -ss 00:00:01 -y -i 0091820c-2511-42d2-b67e-81456421f13c.mp4 -vframes 1 new.jpg
        List<String> command = new ArrayList<>();
        command.add(ffmpegEXE);

        command.add("-ss");
        command.add("00:00:01");

        command.add("-y");
        command.add("-i");
        command.add(videoInputPath);

        command.add("-vframes");
        command.add("1");
        command.add(videoJpgOutputPath);

        ProcessBuilder builder = new ProcessBuilder(command);
        //执行命令
        Process process = builder.start();
        //执行命令时会产生两种临时文件，会极大的占用线程资源
        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);
        String line = "";
        while ((line = br.readLine()) != null){
        }
        //关闭数据流
        if(br != null){
            br.close();
        }
        if(inputStreamReader != null){
            inputStreamReader.close();
        }
        if(errorStream != null){
            errorStream.close();
        }
    }


//
//    public static void main(String[] ages){
//        FfmpegVideoAndMp3 ffmpeg = new FfmpegVideoAndMp3("H:\\wx_user_resource\\ffmpeg\\bin\\ffmpeg.exe");
//        String videoInputPath = "H:\\wx_user_resource\\181010C6WC4T37R4\\video\\temporary\\10---.mp4";
//        String mp3InputPath = "H:\\wx_user_resource\\bgm\\music.mp3";
//        String videoOutputPath = "H:\\wx_user_resource\\181010C6WC4T37R4\\video\\temporary\\text.mp4";
//        String videoOutputPath2 = "H:\\wx_user_resource\\181010C6WC4T37R4\\video\\text.mp4";
//        try {
////            ffmpeg.clearCommand(videoInputPath,videoOutputPath);
//            ffmpeg.converter(videoOutputPath,mp3InputPath,24,videoOutputPath2);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
////        File file = new File("H:\\wx_user_resource\\181010C6WC4T37R4\\video\\temporary");
////        delete(file);
//    }
}
