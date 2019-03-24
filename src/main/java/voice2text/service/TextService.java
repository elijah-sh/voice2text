package voice2text.service;

import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import voice2text.api.LfasrSDK2Text;
import voice2text.config.PathConfig;
import voice2text.entity.Text;
import voice2text.entity.VoiceTextFile;
import voice2text.entity.WordsResult;
import voice2text.mapper.TextMapper;
import voice2text.mapper.VoiceTextFileMapper;
import voice2text.mapper.WordsResultMapper;
import voice2text.utils.FastJsonConvertUtil;
import voice2text.utils.FileUtils;
import voice2text.utils.KeyUtil;
import com.github.pagehelper.PageHelper;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: shuaihu.shen@hand-china.com
 * @Date: 2018/11/1 15:12
 * @Description:
 */
@Service
public class TextService {

    @Autowired
    private TextMapper textMapper;

    @Autowired
    private WordsResultMapper wordsResultMapper;

    @Autowired
    private VoiceTextFileMapper voiceTextFileMapper;

    @Autowired
    private PathConfig pathConfig;

    private static final String local_file = "E:/2.lfasr/";
    private static final String local_file_JSON = ".json";
    private static final String local_voice_file = "E:/2.lfasr/更新的世界.m4a";

    public String createText(String path) throws Exception {

        if (StringUtils.isEmpty(path)) {
            return "输入为空！";
        }
        FileUtils fileUtils = new FileUtils();

        String jsonString = "";
        if ("json".equals(FileUtils.getFileNameInFilePath(path))) {

            jsonString = fileUtils.ReadFile(path);
        } else {
            //jsonString = fileUtils.ReadFile(local_file + path + local_file_JSON);
             jsonString =  LfasrSDK2Text.toText(path);
        }

        List<Text> textList = FastJsonConvertUtil.jsonToList(jsonString, Text.class);

        String title = path;
        textList.stream().forEach(
                list -> {
                    list.setWordsResultSum(list.getWordsResultList().size());
                    list.setDateTime(new Date());
                    list.setTitle(title);
                }
        );

        textMapper.batchInsert(textList);
        // 存文本
        String a = textList.stream().map(e -> e.getOnebest()).collect(Collectors.joining());

        System.out.println(a);
        for (Text text : textList) {
            List<WordsResult> wordsResults = text.getWordsResultList();

            wordsResults.stream().forEach(
                    list -> {
                        list.setTextId(text.getId());
                        list.setDateTime(new Date());
                    }
            );
            wordsResultMapper.batchInsert(wordsResults);
        }

        return a;

    }

    public boolean handleFile(HttpServletRequest request, VoiceTextFile dto) {
        try {
            WebSocketServer.sendInfo("开始处理"
                    + "\n" +KeyUtil.getNowDateTime(),null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String text = "";
        try {
           // WebSocketServer.sendInfo("解析音频",null);

            text = createText(dto.getVoicePath());

            WebSocketServer.sendInfo("转换文字"
                    + "\n" +KeyUtil.getNowDateTime(),null);

            dto.setTextName(dto.getTitleName());
            String fileName[] = dto.getVoiceName().split("\\.");
            dto.setTextPath(pathConfig.getTextPath() +
                    dto.getTitleName() +
                    "_" +
                    KeyUtil.genUniqueKeySimpleId() +
                    "." +
                    "txt");
            dto.setUpdateDate(new Date());
            voiceTextFileMapper.updateByPrimaryKey(dto);

            WebSocketServer.sendInfo("保存文件"
                    + "\n" +KeyUtil.getNowDateTime(),null);

            FileUtils.saveFile(dto.getTextPath() ,text);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


        return true;
    }

    public boolean uploadFile(HttpServletRequest request, MultipartFile file, VoiceTextFile voiceFile) {

        voiceFile.setVoiceName(file.getOriginalFilename());
        String fileName[] = voiceFile.getVoiceName().split("\\.");
        voiceFile.setVoicePath(pathConfig.getVoicePath() +
                KeyUtil.genUniqueKeySimpleId() +
                "." +
                fileName[fileName.length - 1]);// 后面会将文件写入到固定的文件名中
        voiceFile.setCreateDate(new Date());
        voiceFile.setTitleName(fileName[fileName.length - 2]);
        voiceFile.setFileType(fileName[fileName.length - 1]);
        int sizeMB = Math.round(file.getSize()/1024/1024*100)/100;
        int sizeKB = Math.round(file.getSize()/1024*100)/100;
        String fileSize = "";
        if (sizeMB > 0){
            fileSize = sizeMB + "MB";
        }else {
            fileSize = sizeKB +"KB";
        }
        voiceFile.setFileSize(fileSize);
        // 存一下数据库
        voiceTextFileMapper.insert(voiceFile);
        return uploadVoiceFile(request, file, voiceFile);
    }


    /**
     * 上传文件操作
     * @param request
     * @param file
     */
    private boolean uploadVoiceFile(HttpServletRequest request, MultipartFile file, VoiceTextFile voiceFile) {
         File targetFile = new File(voiceFile.getVoicePath());
        try {
            file.transferTo(targetFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
    public PageInfo<VoiceTextFile> selectAll(HttpServletRequest request, VoiceTextFile dto,int page, int limit){
        PageHelper.startPage(page, limit);
        List<VoiceTextFile> list = voiceTextFileMapper.selectAll();
        PageInfo<VoiceTextFile> pageInfo = new PageInfo<VoiceTextFile>(list);
        return pageInfo;
    }

    public VoiceTextFile selectByPrimaryKey(HttpServletRequest request, int id){
        return voiceTextFileMapper.selectByPrimaryKey(id);
    }


    public void downloadFile(HttpServletResponse response, String path) {
        FileInputStream inputStream = null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ServletOutputStream outputStream = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        InputStream is = null;
        // 设置生成的文件名

        try {
            // 获得模板文件的输入流
            inputStream = new FileInputStream(new File(path));
            byte[] buffer = new byte[1024];
            int n;
            while((n = inputStream.read(buffer)) != -1){
                os.write(buffer,0,n);
            }
            byte[] content = os.toByteArray();
            is = new ByteArrayInputStream(content);

            String fileName[] = path.split("\\/");

            String name = fileName[fileName.length-1];
            //  String fileName[] = path.split("\\/");
            // 触发弹出浏览器下载页
            response.reset();
            response.setContentType("appllication/octet-stream");
            response.setHeader("Content-Disposition",
                    "attachment;filename="+ new String(name.getBytes("UTF-8"), "ISO-8859-1"));
            response.setContentLength(content.length);
            outputStream = response.getOutputStream();
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(outputStream);
            byte[] buff = new byte[8192];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭流
            try {
                bos.close();
                bis.close();
                outputStream.flush();
                outputStream.close();
                is.close();
                os.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
