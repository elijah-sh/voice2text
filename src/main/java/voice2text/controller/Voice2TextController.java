/**
 * 文件名: Voice2TextController.java
 * 版权：Copyright 2017-2022 CMCC All Rights Reserved.
 * 描述:
 */
package voice2text.controller;

import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import voice2text.entity.Order;
import voice2text.entity.Text;
import voice2text.entity.VoiceTextFile;
import voice2text.service.OrderService;
import voice2text.service.TextService;
import voice2text.service.WebSocketServer;
import voice2text.utils.KeyUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @description:
 * @author: Shenshuaihu
 * @version: 1.0
 * @data: 2019-03-17 16:12
 */
@RestController
@Slf4j
public class Voice2TextController {

    @Autowired
    private TextService textService;


    /**
     * 上传 带有数据的Excel
     * @param request
     * @param dto
     * @return
     */
    @PostMapping(value = "/upload/file")
    public Map<String,Object> uploadFile(HttpServletRequest request, VoiceTextFile dto){
        Map<String,Object> map = new HashMap<>();
        log.info("上传的结果{}" ,dto.toString());
       String flag = textService.handleFile(request, dto);
        if ("success".equals(flag)){
            map.put("success",true);
            map.put("code",200);
        }

        return map;
    }

    @RequestMapping("/testWebSocker")
    public String testWebSocker() throws InterruptedException {
        /**
         * 1、上传音频 <br/>
         * 2、解析音频 <br/>
         * 3、转换文字 <br/>
         * 4、保存文件 <br/>
         * 5、可以下载 <br/>
         */
        int sid = 777;
        try {
            Map<String,String> map = new HashMap<>();
            WebSocketServer.sendInfo("开始处理",null);
            Thread.sleep(1300);
            WebSocketServer.sendInfo("上传音频",null);
            Thread.sleep(5300);
            WebSocketServer.sendInfo("解析音频",null);
            Thread.sleep(5300);
            WebSocketServer.sendInfo("转换文字",null);
            Thread.sleep(2300);
            WebSocketServer.sendInfo("保存文件",null);
            Thread.sleep(11300);
            WebSocketServer.sendInfo("可以下载",null);
            Thread.sleep(1300);
            WebSocketServer.sendInfo("处理完成",null);

        } catch (IOException e) {
            e.printStackTrace();
            return "error" + "testWebSocker" +"#"+e.getMessage();
        }

        return "ok";
    }

    @RequestMapping(value = "/upload/voice")
    public Map<String,Object> uploadVoice(HttpServletRequest request, MultipartFile file, VoiceTextFile dto){
        Map<String,Object> map = new HashMap<>();
        //根据解析信息 做出简单的校验
        log.info("上传的结果{}" ,file.toString());
        Boolean flag = textService.uploadFile(request,file, dto);
        if (flag){
            map.put("success",true);
            map.put("code",200);
        }
        return map;
    }

    /**
     * 表单查询
     * @param request
     * @param dto
     * @param page
     * @param limit
     * @return
     */
    @GetMapping(value = "/selectVoiceTextFile")
    public Map<String,Object> selectVoiceTextFile(HttpServletRequest request, VoiceTextFile dto, int page, int limit){

        Map<String,Object> map = new HashMap<>();
      //  {"code":0,"msg":"","count":1000,"data":[{}，{}]}
        PageInfo<VoiceTextFile> files = textService.selectAll(request,dto,page,limit);
        map.put("code",0);
        map.put("msg",0);
        map.put("count",files.getTotal());
        map.put("data",files.getList());
        return map;
    }

    /**
     * 下载
     * @param id
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/download")
    public boolean downloadFile(int id, HttpServletRequest request, HttpServletResponse response){
        textService.downloadFile(response, textService.selectByPrimaryKey(request, id).getTextPath());
        return true;
    }

    @RequestMapping("/to/text")
    public Map<String,Object> toText(int id, HttpServletRequest request) throws InterruptedException {
        Map<String,Object> map = new HashMap<>();
        String flag = "error";
        /**
         * 1、上传音频 <br/>
         * 2、解析音频 <br/>
         * 3、转换文字 <br/>
         * 4、保存文件 <br/>
         * 5、可以下载 <br/>
         */
        VoiceTextFile dto = textService.selectByPrimaryKey(request,id);

        try {
            if (dto != null && StringUtils.isNotEmpty(dto.getVoicePath())){

                String fileName[] = dto.getVoiceName().split("\\.");
                String type = fileName[fileName.length-1];

                if ("mp3".equals(type) || "m4a".equals(type)
                        || "mp4".equals(type)|| "pcm".equals(type)
                        || "wav".equals(type)|| "3gp".equals(type)
                        || "amr".equals(type)|| "wma".equals(type)) {
                    flag = textService.handleFile(request, dto);
                    if ("success".equals(flag)){
                        map.put("success",true);
                        map.put("code",200);
                    }else {
                        map.put("success",false);
                        map.put("data","音频类型有误,请检查"+flag);
                        return map;
                    }
                }else {
                    map.put("success",false);
                    map.put("data","音频类型有误,请检查");
                    return map;
                }
            }
            WebSocketServer.sendInfo("处理完成"
                    + "\n" +KeyUtil.getNowDateTime(),null);
        } catch (IOException e) {
            e.printStackTrace();
            map.put("success",false);
            map.put("data","error" + "处理失败" +"#"+e.getMessage());
            return map;
        }
            map.put("success",true);
        return map;
    }


}
