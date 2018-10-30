package com.imooc.utils;

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
//用来试验
public class FfmpegText {

    private String ffmpegEXE;

    FfmpegText(String ffmpegEXE){
        this.ffmpegEXE = ffmpegEXE;
    }

    public void convertor(String videoInputPath,String videoOutputPath) throws IOException {
        //转换视频格式的命令
        //ffmpeg -i input.mp4 output.avi
        List<String> command = new ArrayList<>();
        command.add(ffmpegEXE);
        command.add("-i");
        command.add(videoInputPath);
        command.add("-y");
        command.add(videoOutputPath);

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

    public static void main(String[] ages){
//        FfmpegText ffmpeg = new FfmpegText("H:\\wx_user_resource\\ffmpeg\\bin\\ffmpeg.exe");
//        String videoInputPath = "H:\\wx_user_resource\\181010C6WC4T37R4\\video\\wxbfa57d19f942cacf.o6zAJs6Od5T4TNeyZIjw_RxohzMc.9mottQ0bOsRw93252f62074c772fb6c7d2eb2985dead.mp4";
//        String videoOutputPath = "H:\\wx_user_resource\\181010C6WC4T37R4\\video\\视频.avi";
//        try {
//            ffmpeg.convertor(videoInputPath,videoOutputPath);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        File file = new File("H:\\wx_user_resource\\181010C6WC4T37R4\\video\\cover");

        if(!file.exists()){
            file.mkdirs();
        }
    }
}
