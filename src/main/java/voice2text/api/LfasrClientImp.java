/**
 * 文件名: LfasrClientImp.java
 * 版权：Copyright 2017-2022 CMCC All Rights Reserved.
 * 描述:
 */
package voice2text.api;

/**
 * @description:
 * @author: Shenshuaihu
 * @version: 1.0
 * @data: 2019-04-06 21:20
 */

import com.alibaba.fastjson.JSON;
import com.iflytek.msp.cpdb.lfasr.client.LfasrClient;
import com.iflytek.msp.cpdb.lfasr.exception.ConfigException;
import com.iflytek.msp.cpdb.lfasr.exception.LfasrException;
import com.iflytek.msp.cpdb.lfasr.file.LocalPersistenceFile;
import com.iflytek.msp.cpdb.lfasr.model.LfasrType;
import com.iflytek.msp.cpdb.lfasr.model.Message;
import com.iflytek.msp.cpdb.lfasr.model.Signature;
import com.iflytek.msp.cpdb.lfasr.model.UploadParams;
import com.iflytek.msp.cpdb.lfasr.util.FileUtil;
import com.iflytek.msp.cpdb.lfasr.util.StringUtil;
import com.iflytek.msp.cpdb.lfasr.util.VersionUtil;
import com.iflytek.msp.cpdb.lfasr.worker.ResumeWorker;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
//import voice2text.config.xunfei.HttpWorker;
import voice2text.config.xunfei.HttpWorker;
import voice2text.config.xunfei.PropConfig;
import voice2text.config.xunfei.UploadWorker;
import voice2text.entity.SysConfig;
import voice2text.service.SysConfigService;

@Component
public  class LfasrClientImp  {
    private static final Logger LOGGER = Logger.getLogger(LfasrClientImp.class);
    private static String POPERTIES_FILEPATH = "config.properties";
    private static String POPERTIES_APP_ID_TG = "app_id";
    private static String POPERTIES_SERCET_KEY_TG = "secret_key";
    private static String POPERTIES_LFASR_HOST_TG = "lfasr_host";
    private static String POPERTIES_FILE_PIECE_SIZE_TG = "file_piece_size";
    private static String POPERTIES_STORE_PATH_TG = "store_path";
    private static String SERV_APP_ID_VAL = "";
    private static String SERV_SECRET_KEY_VAL = "";
    public static String SERV_LFASR_HOST_VAL = "";
    private static int SERV_FILE_PIECE_SIZE_VAL = 262144;
    public static String SERV_STORE_PATH_VAL = "";
    private static String err_msg = null;

    private static SysConfigService sysConfigService;

    @Autowired
    public void setSysConfigService(SysConfigService sysConfigService) {
        LfasrClientImp.sysConfigService = sysConfigService;
    }
    //  static {
    public void getCpnfig() {
          Map<String, String> poperties = new HashMap();
          List<SysConfig> configs = new ArrayList<>();
          configs = sysConfigService.getSysConfigData();

          for (SysConfig sysConfig : configs) {
              poperties.put(sysConfig.getKey(), sysConfig.getValue());
          }
          /*try {
           // poperties = PropConfig.LoadPoperties(POPERTIES_FILEPATH);

        } catch (Exception var3) {
            err_msg = "{\"ok\":\"-1\", \"err_no\":\"26100\", \"failed\":\"读取数据库config配置文件错误!\", \"data\":\"\"}";
        }*/

        SERV_APP_ID_VAL = (String)((Map)poperties).get(POPERTIES_APP_ID_TG);
        SERV_SECRET_KEY_VAL = (String)((Map)poperties).get(POPERTIES_SERCET_KEY_TG);
        if ((StringUtil.isEmpty(SERV_APP_ID_VAL) || StringUtil.isEmpty(SERV_SECRET_KEY_VAL)) && StringUtil.isEmpty(err_msg)) {
            err_msg = "{\"ok\":\"-1\", \"err_no\":\"26101\", \"failed\":\"转写配置文件app_id/secret_key为空!\", \"data\":\"\"}";
        }

        SERV_LFASR_HOST_VAL = (String)((Map)poperties).get(POPERTIES_LFASR_HOST_TG);
        if (StringUtil.isEmpty(SERV_LFASR_HOST_VAL) && StringUtil.isEmpty(err_msg)) {
            err_msg = "{\"ok\":\"-1\", \"err_no\":\"26102\", \"failed\":\"转写配置文件lfasr_host错误!\", \"data\":\"\"}";
        }

        try {
            SERV_FILE_PIECE_SIZE_VAL = Integer.parseInt((String)((Map)poperties).get(POPERTIES_FILE_PIECE_SIZE_TG));
        } catch (NumberFormatException var5) {
            if (StringUtil.isEmpty(err_msg)) {
                err_msg = "{\"ok\":\"-1\", \"err_no\":\"26103\", \"failed\":\"转写配置文件file_piece_size错误!\", \"data\":\"\"}";
            }
        }

        if (SERV_FILE_PIECE_SIZE_VAL < 131072 || SERV_FILE_PIECE_SIZE_VAL > 10485760) {
            err_msg = "{\"ok\":\"-1\", \"err_no\":\"26104\", \"failed\":\"转写配置文件file_piece_size建议设置128K~10M之间!\", \"data\":\"\"}";
        }

        SERV_STORE_PATH_VAL = (String)((Map)poperties).get(POPERTIES_STORE_PATH_TG);
        if (StringUtil.isEmpty(SERV_STORE_PATH_VAL)) {
            if (StringUtil.isEmpty(err_msg)) {
                err_msg = "{\"ok\":\"-1\", \"err_no\":\"26105\", \"failed\":\"转写配置文件store_path错误，或目录不可读写!\", \"data\":\"\"}";
            }
        } else {
            String file_test = SERV_STORE_PATH_VAL + "/test.dat";

            try {
                LocalPersistenceFile.writeNIO(file_test, "test");
                LocalPersistenceFile.deleteFile(new File(file_test));
            } catch (LfasrException var4) {
                if (StringUtil.isEmpty(err_msg)) {
                    err_msg = "{\"ok\":\"-1\", \"err_no\":\"26105\", \"failed\":\"转写配置文件store_path错误，或目录不可读写!\", \"data\":\"\"}";
                }
            }
        }

    }

    public LfasrClientImp() throws LfasrException {
        if (!StringUtil.isEmpty(err_msg)) {
            LOGGER.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "LfasrClientImp", "", "", "(-1) ms", err_msg));
            throw new LfasrException(err_msg);
        }
    }

    public LfasrClientImp(String app_id, String secret_key) throws LfasrException {
        if (StringUtil.isEmpty(app_id) || StringUtil.isEmpty(secret_key)) {
            err_msg = "{\"ok\":\"-1\", \"err_no\":\"26101\", \"failed\":\"转写配置文件app_id/secret_key为空!\", \"data\":\"\"}";
        }

        SERV_APP_ID_VAL = app_id;
        SERV_SECRET_KEY_VAL = secret_key;
        if (!StringUtil.isEmpty(err_msg)) {
            LOGGER.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "LfasrClientImp", "", "", "(-1) ms", err_msg));
            throw new LfasrException(err_msg);
        }
    }

    public static LfasrClientImp initLfasrClient() throws LfasrException {
        return new LfasrClientImp();
    }

    public static LfasrClientImp initLfasrClient(String app_id, String secret_key) throws LfasrException {
        return new LfasrClientImp(app_id, secret_key);
    }

    public Message lfasrUpload(String local_file, LfasrType lfasr_type) throws LfasrException {
        return this.lfasrUpload(local_file, lfasr_type, (HashMap)null);
    }

    public Message lfasrUpload(String local_file, LfasrType lfasr_type, HashMap<String, String> params) throws LfasrException {
        getCpnfig();
        boolean isExist = FileUtil.isExist(local_file);
        if (!isExist) {
            LOGGER.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "LfasrClientImp", "", "", "(-1) ms", "{\"ok\":\"-1\", \"err_no\":\"26201\", \"failed\":\"转写参数上传文件不能为空或文件不存在!\", \"data\":\"\"}"));
            throw new LfasrException("{\"ok\":\"-1\", \"err_no\":\"26201\", \"failed\":\"转写参数上传文件不能为空或文件不存在!\", \"data\":\"\"}");
        } else {
            boolean isEmpty = FileUtil.isEmpty(local_file);
            if (isEmpty) {
                LOGGER.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "LfasrClientImp", "", "", "(-1) ms", "{\"ok\":\"-1\", \"err_no\":\"26201\", \"failed\":\"转写参数上传文件不能为空或文件不存在!\", \"data\":\"\"}"));
                throw new LfasrException("{\"ok\":\"-1\", \"err_no\":\"26201\", \"failed\":\"转写参数上传文件不能为空或文件不存在!\", \"data\":\"\"}");
            } else if (lfasr_type == null) {
                LOGGER.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "LfasrClientImp", "", "", "(-1) ms", "{\"ok\":\"-1\", \"err_no\":\"26202\", \"failed\":\"转写参数类型不能为空!\", \"data\":\"\"}"));
                throw new LfasrException("{\"ok\":\"-1\", \"err_no\":\"26202\", \"failed\":\"转写参数类型不能为空!\", \"data\":\"\"}");
            } else {
                Signature signature = null;

                try {
                    signature = new Signature(SERV_APP_ID_VAL, SERV_SECRET_KEY_VAL);
                } catch (Exception var8) {
                    LOGGER.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "LfasrClientImp", "", "", "(-1) ms", "{\"ok\":\"-1\", \"err_no\":\"26203\", \"failed\":\"转写参数客户端生成签名错误!\", \"data\":\"\"}"));
                    throw new LfasrException("{\"ok\":\"-1\", \"err_no\":\"26203\", \"failed\":\"转写参数客户端生成签名错误!\", \"data\":\"\"}");
                }

                UploadWorker uw = new UploadWorker(signature, new File(local_file), lfasr_type, SERV_FILE_PIECE_SIZE_VAL, params);
                return uw.upload();
            }
        }
    }

    public void lfasrResume() throws LfasrException {
        ResumeWorker rw = new ResumeWorker();
        rw.upload();
    }

    public Message lfasrGetVersion() throws LfasrException {
        try {
            UploadParams params = new UploadParams();
            params.setSignature(new Signature(SERV_APP_ID_VAL, SERV_SECRET_KEY_VAL));
            params.setClientVersion(VersionUtil.GetVersion());
            String result = (new HttpWorker()).getVersion(params);
            Message message = null;

            try {
                message = (Message)JSON.parseObject(result, Message.class);
                return message;
            } catch (Exception var5) {
                LOGGER.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "LfasrClientImp", "", "", "(-1) ms", "{\"ok\":\"-1\", \"err_no\":\"26501\", \"failed\":\"转写获取版本号接口错误!\", \"data\":\"\"}"));
                throw new LfasrException("{\"ok\":\"-1\", \"err_no\":\"26501\", \"failed\":\"转写获取版本号接口错误!\", \"data\":\"\"}");
            }
        } catch (Exception var6) {
            LOGGER.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "LfasrClientImp", "", "", "(-1) ms", "{\"ok\":\"-1\", \"err_no\":\"26203\", \"failed\":\"转写参数客户端生成签名错误!\", \"data\":\"\"}"));
            throw new LfasrException("{\"ok\":\"-1\", \"err_no\":\"26203\", \"failed\":\"转写参数客户端生成签名错误!\", \"data\":\"\"}");
        }
    }

    public Message lfasrGetProgress(String task_id) throws LfasrException {
        try {
            UploadParams params = new UploadParams();
            params.setSignature(new Signature(SERV_APP_ID_VAL, SERV_SECRET_KEY_VAL));
            params.setTaskId(task_id);
            String result = (new HttpWorker()).getProgress(params);
            Message message = null;

            try {
                message = (Message)JSON.parseObject(result, Message.class);
                return message;
            } catch (Exception var6) {
                LOGGER.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "LfasrClientImp", "", "", "(-1) ms", "{\"ok\":\"-1\", \"err_no\":\"26505\", \"failed\":\"转写获取进度接口错误!\", \"data\":\"\"}"));
                throw new LfasrException("{\"ok\":\"-1\", \"err_no\":\"26505\", \"failed\":\"转写获取进度接口错误!\", \"data\":\"\"}");
            }
        } catch (Exception var7) {
            LOGGER.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "LfasrClientImp", "", "", "(-1) ms", "{\"ok\":\"-1\", \"err_no\":\"26203\", \"failed\":\"转写参数客户端生成签名错误!\", \"data\":\"\"}"));
            throw new LfasrException("{\"ok\":\"-1\", \"err_no\":\"26203\", \"failed\":\"转写参数客户端生成签名错误!\", \"data\":\"\"}");
        }
    }

    public Message lfasrGetResult(String task_id) throws LfasrException {
        try {
            UploadParams params = new UploadParams();
            params.setSignature(new Signature(SERV_APP_ID_VAL, SERV_SECRET_KEY_VAL));
            params.setTaskId(task_id);
            String result = (new HttpWorker()).getResult(params);
            Message message = null;

            try {
                message = (Message)JSON.parseObject(result, Message.class);
                return message;
            } catch (Exception var6) {
                LOGGER.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "LfasrClientImp", "", "", "(-1) ms", "{\"ok\":\"-1\", \"err_no\":\"26506\", \"failed\":\"转写获取结果接口错误!\", \"data\":\"\"}"));
                throw new LfasrException("{\"ok\":\"-1\", \"err_no\":\"26506\", \"failed\":\"转写获取结果接口错误!\", \"data\":\"\"}");
            }
        } catch (Exception var7) {
            LOGGER.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "LfasrClientImp", "", "", "(-1) ms", "{\"ok\":\"-1\", \"err_no\":\"26203\", \"failed\":\"转写参数客户端生成签名错误!\", \"data\":\"\"}"));
            throw new LfasrException("{\"ok\":\"-1\", \"err_no\":\"26203\", \"failed\":\"转写参数客户端生成签名错误!\", \"data\":\"\"}");
        }
    }
}
