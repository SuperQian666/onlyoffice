package com.xqfunds.controller;


import com.xqfunds.util.FileUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * @author whisper
 * @date 2022/10/26
 **/

@Controller
public class FileController {

    @Value("${files.filePath}")
    private String filePath;

    @Value("${files.docservice.url.site}")
    private String officeUrl;

    @Value("${files.docservice.url.command}")
    private String officeCommand;


    @RequestMapping("/save")
    public void saveDocument(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //处理编辑回调逻辑
        callBack(request, response);
    }


    /**
     * 处理在线编辑文档的逻辑
     * @param request
     * @param response
     * @throws IOException
     */
    private void callBack(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter writer = null;
        JSONObject jsonObj = null;
        System.out.println("===saveeditedfile------------");

        try {
            writer = response.getWriter();
            Scanner scanner = new Scanner(request.getInputStream()).useDelimiter("\\A");
            String body = scanner.hasNext() ? scanner.next() : "";
            jsonObj = (JSONObject) new JSONParser().parse(body);
            System.out.println(jsonObj);
            System.out.println("===saveeditedfile:" + jsonObj.get("status"));
	            /*
	                0 - no document with the key identifier could be found,
	                1 - document is being edited,
	                2 - document is ready for saving,
	                3 - document saving error has occurred,
	                4 - document is closed with no changes,
	                6 - document is being edited, but the current document state is saved,
	                7 - error has occurred while force saving the document.
	             * */
            if ((long) jsonObj.get("status") == 2) {
                FileUtil.callBackSaveDocument(jsonObj, filePath, request, response);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}