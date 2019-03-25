/**
 * 文件名: IndexController.java
 * 版权：Copyright 2017-2022 CMCC All Rights Reserved.
 * 描述:
 */
package voice2text.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description:
 * @author: Shenshuaihu
 * @version: 1.0
 * @data: 2019-03-19 23:04
 */
@Controller
public class IndexController {

    /**
     * 住访问页
     * @return
     */
    @RequestMapping("/")
    public String index() {
        return "voice_text_file";
    }

    /**
     * 住访问页
     * @return
     */
    @RequestMapping("/upload")
    public String upload() {
        return "upload";
    }

    @RequestMapping("/upload/lay")
    public String layUpload() {
        return "lay_upload";
    }

    @RequestMapping("/file")
    public String table() {
        return "voice_text_file";
    }
}
