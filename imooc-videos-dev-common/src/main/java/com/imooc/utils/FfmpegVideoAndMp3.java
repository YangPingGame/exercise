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
public class FfmpegVideoAndMp3 {

    private static final Logger LOGGER = LoggerFactory.getLogger(FfmpegVideoAndMp3.class);

    private String ffmpegEXE;

    private static final String TemporaryName = "text";

    public FfmpegVideoAndMp3(String ffmpegEXE){
        this.ffmpegEXE = ffmpegEXE;
    }

    /**
     * 清除视频原先音轨
     */
    private String clearCommand(String videoInputPath) throws IOException {
        int start = videoInputPath.lastIndexOf("/");
        int end = videoInputPath.lastIndexOf(".");
        String vdieoName = videoInputPath.substring(start+1,end);
        String TemporaryUrl= videoInputPath.replace(vdieoName,TemporaryName);
        //将源文件的背景音频去掉
        //ffmpeg -i /path/to/input.mp4 -c:v copy -an /path/to/input-no-audio.mp4
        List<String> clearCommand = new ArrayList<>();
        clearCommand.add(ffmpegEXE);

        clearCommand.add("-i");
        clearCommand.add(videoInputPath);
        clearCommand.add("-c:v");
        clearCommand.add("copy");
        clearCommand.add("-an");
        clearCommand.add(TemporaryUrl);

        ProcessBuilder builder = new ProcessBuilder(clearCommand);
        //执行命令
        Process process = builder.start();
        //执行命令时会产生两种临时文件，会极大的占用线程资源
        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        this.ioClose(br,inputStreamReader,errorStream);
        return TemporaryUrl;
    }

    /**
     * 为视频添加新的音轨
     */
    public void converter(String videoInputPath,String mp3InputPath,
                          double seconds,String videoOutputPath) throws IOException {
        //转换视频格式的命令
        //ffmpeg.exe -i input.mp4 -i ***.mp3 -t 7 -y 新视频.mp4
        List<String> command = new ArrayList<>();
        command.add(ffmpegEXE);

        command.add("-i");
        command.add(clearCommand(videoInputPath));

        command.add("-i");
        command.add(mp3InputPath);

        command.add("-t");
        command.add(String.valueOf(seconds));//音频时长

        command.add("-y");//表示覆盖
        command.add(videoOutputPath);
        ProcessBuilder builder = new ProcessBuilder(command);
        //执行命令
        Process process = builder.start();
        //执行命令时会产生两种临时文件，会极大的占用线程资源
        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

       this.ioClose(br,inputStreamReader,errorStream);
        //截取获得临时文件目录
        String url = videoInputPath.substring(0,videoInputPath.lastIndexOf("/"));
        //清除临时文件
        delete(new File(url));
    }

    /**
     * 将视频转换为mp4格式,并从临时文件中取出
     * @param videoUrl
     */
    public void videoMp4(String videoUrl,String videoOutputPath) throws IOException {
            //转换视频格式的命令
            //ffmpeg.exe -i input.mp4 -i ***.mp3 -t 7 -y 新视频.mp4
            List<String> command = new ArrayList<>();
            command.add(ffmpegEXE);

            command.add("-i");
            command.add(videoUrl);

            command.add("-y");
            command.add(videoOutputPath);

            ProcessBuilder builder = new ProcessBuilder(command);
            //执行命令
            Process process = builder.start();
            //执行命令时会产生两种临时文件，会极大的占用线程资源
            InputStream errorStream = process.getErrorStream();
            InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
            BufferedReader br = new BufferedReader(inputStreamReader);

            this.ioClose(br,inputStreamReader,errorStream);
            //截取获得临时文件目录
            String url = videoUrl.substring(0,videoUrl.lastIndexOf("/"));
            //清除临时文件
            delete(new File(url));
    }
    /**
     * 关闭错误临时文件流
     * @param br
     * @param inputStreamReader
     * @param errorStream
     * @throws IOException
     */
    private void ioClose(BufferedReader br,InputStreamReader inputStreamReader,InputStream errorStream) throws IOException {
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

    /**
     * 删除多余文件夹
     * @param file
     */
    private static void delete(File file) {
        if(!file.exists())return;
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            if(files[i].isDirectory()){
                delete(files[i]);
            }
            files[i].delete();
        }
        LOGGER.info("陈余文件夹删除成功|");
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
