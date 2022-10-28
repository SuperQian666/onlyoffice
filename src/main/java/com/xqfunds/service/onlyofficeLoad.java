package com.xqfunds.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * @author whisper
 * @date 2022/10/27
 **/
@Controller
public class onlyofficeLoad {

    @Value("${files.filePath}")
    private String filePath;

    @RequestMapping("/onlyofficeLoad")
    @ResponseBody
    public void onlyofficeLoad(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.reset();
        String fileName = request.getParameter("file");
        fileName = URLEncoder.encode(fileName, "UTF-8");
        fileName = fileName.replaceAll("\\+", "%20");
        File file = new File(filePath + "/" + fileName);
        InputStream inputStream = new FileInputStream(file);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"; " + "filename*=UTF-8''" + fileName);
        response.setCharacterEncoding("UTF-8");
        //注意类型一定要是octet-stream
        response.setContentType("application/octet-stream");
        response.setContentLength(inputStream.available());
        OutputStream sos = response.getOutputStream();
        byte[] bytes = new byte[1024 * 512];
        int i = -1;
        while ((i = inputStream.read(bytes)) != -1){
            sos.write(bytes, 0, i);
        }
        sos.flush();
        //StreamUtils.closeStream(inputStream);
        inputStream.close();
    }

}