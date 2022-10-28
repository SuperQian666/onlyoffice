package com.xqfunds.util;

import org.apache.el.parser.Token;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @author whisper
 * @date 2022/10/26
 **/
public class FileUtil {


    public static void saveFile(InputStream in, String outPath) throws Exception {
        OutputStream out = new FileOutputStream(outPath);
        IOUtils.copy(in, out);
    }

    //path 以 / 结尾
    public static void downloadFile(String name, String path, HttpServletResponse response) throws IOException {
        String storagePath = path + name;
        File file = new File(storagePath);
        String fileName = file.getName();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        InputStream in = new BufferedInputStream(new FileInputStream(file));

        response.reset();
        response.setCharacterEncoding("UTF-8");
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        //浏览器文件大小
        response.addHeader("Content-Length", String.valueOf(file.length()));
        response.setContentType("application/octet-stream");

        OutputStream out = new BufferedOutputStream(response.getOutputStream());
        while (in.available() != 0) {
            byte[] buffer = new byte[in.available()];
            in.read(buffer);
            out.write(buffer);
        }
        out.flush();
        in.close();

    }

    public static void callBackSaveDocument(JSONObject jsonObj, String filePath, HttpServletRequest request, HttpServletResponse response) throws IOException{
        String downloadUri =(String) jsonObj.get("url");
        System.out.println("源文档地址：" + downloadUri);
        String fileName = request.getParameter("fileName");
        System.out.println("下载文件名：" + fileName);
        URL url = new URL(downloadUri);
        HttpURLConnection connection =(HttpURLConnection)url.openConnection();
        InputStream inputStream = connection.getInputStream();

        File savedFile = new File(filePath + fileName);
        try(FileOutputStream out = new FileOutputStream(savedFile)) {
            int read;
            final byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
        }
        inputStream.close();
        connection.disconnect();
    }



}