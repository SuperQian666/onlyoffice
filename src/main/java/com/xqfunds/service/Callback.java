package com.xqfunds.service;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.text.ParseException;
import java.util.Scanner;

/**
 * @author whisper
 * @date 2022/10/27
 **/
@Controller
public class Callback {

    @Value("${files.savePath}")
    private String filePath;


    @RequestMapping(value = "/generateReport", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public void generateReport(HttpServletRequest request, HttpServletResponse response){
        System.out.println("callback:===================");
        System.out.printf("request sent host: %s\n", request.getRemoteHost());
        PrintWriter writer = null;
        try{
            String body = "";
            try{
                writer = response.getWriter();
                Scanner scanner = new Scanner(request.getInputStream());
                scanner.useDelimiter("\\A");
                body = scanner.hasNext() ? scanner.next() : "";
                scanner.close();
            } catch (Exception ex) {
                writer.write("get request.getInputStream error:" + ex.getMessage());
                return;
            }
            if (body.isEmpty()) {
                writer.write("empty request.getInputStream");
                return;
            }

            JSONParser parser = new JSONParser();
            JSONObject jsonObj = (JSONObject) parser.parse(body);
            int status = Integer.parseInt(jsonObj.get("status").toString());
            System.out.println(jsonObj.toJSONString());
            int saved = 0;
            //statusAPI见下表
            if (status == 2 || status == 3 || status == 6) {
                String downloadUri = (String) jsonObj.get("url");
                boolean saveSuccessfully = true;
                try {
                    URL url = new URL(downloadUri);
                    java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                    InputStream stream = connection.getInputStream();
                    if (stream == null){
                        throw new Exception("Stream is null");
                    }



                    File fileDir = new File(filePath);
                    if (!fileDir.isDirectory()){
                        fileDir.mkdirs();
                    }
                   /* File savedFile = new File(upload_file_path + filePath);
                    if (savedFile.exists()) {
                        savedFile.delete();
                    }*/
                    try (FileOutputStream out = new FileOutputStream(fileDir)) {
                        int read;
                        final byte[] bytes = new byte[1024];
                        while ((read = stream.read(bytes)) != -1){
                            out.write(bytes, 0, read);
                        }
                        out.flush();
                    }
                    connection.disconnect();
                } catch (Exception ex){
                    saved = 1;
                    saveSuccessfully = false;
                    ex.printStackTrace();
                }
            }
            writer.write("{\"error\":" + saved + "}");
            System.out.println("保存成功");
        } catch (org.json.simple.parser.ParseException e) {
            System.out.println("保存异常");
            writer.write("{\"error\":\"-1\"}");
            e.printStackTrace();
        }
    }

}