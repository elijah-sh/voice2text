/**
 * 文件名: UploadWorker.java
 * 版权：Copyright 2017-2022 CMCC All Rights Reserved.
 * 描述:
 */
package voice2text.config.xunfei;

/**
 * @description:
 * @author: Shenshuaihu
 * @version: 1.0
 * @data: 2019-04-26 12:00
 */

import com.alibaba.fastjson.JSON;
import com.iflytek.msp.cpdb.lfasr.exception.LfasrException;
import com.iflytek.msp.cpdb.lfasr.file.AudioLength;
import com.iflytek.msp.cpdb.lfasr.file.ChannelFileReader;
import com.iflytek.msp.cpdb.lfasr.file.LocalPersistenceFile;
import com.iflytek.msp.cpdb.lfasr.model.LfasrType;
import com.iflytek.msp.cpdb.lfasr.model.LocalPersistenceMeta;
import com.iflytek.msp.cpdb.lfasr.model.Message;
import com.iflytek.msp.cpdb.lfasr.model.Signature;
import com.iflytek.msp.cpdb.lfasr.model.UploadParams;
import com.iflytek.msp.cpdb.lfasr.util.VersionUtil;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import voice2text.api.LfasrClientImp;

public class UploadWorker {
    public static final long FILE_UPLOAD_MAXSIZE = 524288000L;
    private UploadParams upParams;
    private int file_piece_size;
    private static final Logger LOGGER = Logger.getLogger(UploadWorker.class);

    public UploadWorker(Signature signature, File file, LfasrType lfasr_type, int file_piece_size, HashMap<String, String> params) {
        long file_len = file.length();
        int flag = (int)file_len % file_piece_size;
        int slice_num = flag == 0 ? (int)file_len / file_piece_size : (int)file_len / file_piece_size + 1;
        this.upParams = new UploadParams(signature, file, lfasr_type, params);
        this.upParams.setSliceNum(slice_num);
        this.upParams.setClientVersion(VersionUtil.GetVersion());
        this.file_piece_size = file_piece_size;
    }

    public Message upload() throws LfasrException {
        Message message = null;
        String task_id = "";
        if (!this.upParams.getLfasrType().isSupportedAudios(this.upParams.getFile())) {
            LOGGER.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "UploadWorker", this.upParams.getTaskId(), "", "(-1) ms", "{\"ok\":\"-1\", \"err_no\":\"26402\", \"failed\":\"转写上传文件类型不支持错误!\", \"data\":\"\"}"));
            throw new LfasrException("{\"ok\":\"-1\", \"err_no\":\"26402\", \"failed\":\"转写上传文件类型不支持错误!\", \"data\":\"\"}");
        } else {
            ChannelFileReader fr = null;
            Thread hbt = null;

            Message var13;
            try {
                fr = new ChannelFileReader(this.upParams.getFile(), this.file_piece_size);
                long file_len = fr.getFileLength();
                if (file_len > 524288000L) {
                    LOGGER.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "UploadWorker", this.upParams.getTaskId(), "", "(-1) ms", "{\"ok\":\"-1\", \"err_no\":\"26403\", \"failed\":\"转写本地文件上传超过限定大小500M!\", \"data\":\"\"}"));
                    throw new LfasrException("{\"ok\":\"-1\", \"err_no\":\"26403\", \"failed\":\"转写本地文件上传超过限定大小500M!\", \"data\":\"\"}");
                }

                Double check_length = AudioLength.getLength(this.upParams.getFile().getAbsolutePath());
                this.upParams.setCheckLength(check_length);
                HttpWorker hw = new HttpWorker();
                String result = hw.prepare(this.upParams);

                try {
                    message = (Message)JSON.parseObject(result, Message.class);
                } catch (Exception var25) {
                    LOGGER.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "UploadWorker", this.upParams.getTaskId(), "", "(-1) ms", "{\"ok\":\"-1\", \"err_no\":\"26502\", \"failed\":\"转写预处理接口错误!\", \"data\":\"\"}"), var25);
                    throw new LfasrException("{\"ok\":\"-1\", \"err_no\":\"26502\", \"failed\":\"转写预处理接口错误!\", \"data\":\"\"}");
                }

                if (message.getOk() == 0 && !StringUtils.isEmpty(message.getData())) {
                    LOGGER.info(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "UploadWorker", this.upParams.getTaskId(), "", "(-1) ms", "prepare ok"));
                    task_id = message.getData();
                    LOGGER.info(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "UploadWorker", task_id, "", "(-1) ms", "get task_id:%s" + task_id));
                    this.upParams.setTaskId(task_id);
                    this.metaPersistency(this.upParams);
                    UploadThread ut = new UploadThread(this.upParams, this.file_piece_size, fr);
                    hbt = new Thread(ut);
                    hbt.start();
                    try {
                        if (!((String)this.upParams.getParams().get("not_wait")).equalsIgnoreCase("true")) {
                            hbt.join();
                            return message;
                        }
                    } catch (Exception var24) {
                        hbt.join();
                    }

                    return message;
                }

                LOGGER.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "UploadWorker", this.upParams.getTaskId(), "", "(-1) ms", "prepare error:" + message.getFailed()));
                fr.close();
                var13 = message;
            } catch (LfasrException var26) {
                LOGGER.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "UploadWorker", this.upParams.getTaskId(), "", "(-1) ms", "upload error"), var26);
                throw var26;
            } catch (Exception var27) {
                LOGGER.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "UploadWorker", this.upParams.getTaskId(), "", "(-1) ms", "upload error"), var27);
                throw new LfasrException("{\"ok\":\"-1\", \"err_no\":\"26503\", \"failed\":\"转写上传文件接口错误!\", \"data\":\"\"}");
            } finally {
                if (fr != null && (hbt == null || !hbt.isAlive())) {
                    try {
                        fr.close();
                    } catch (IOException var23) {
                        LOGGER.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "UploadWorker", this.upParams.getTaskId(), "", "(-1) ms", "close io error"), var23);
                    }
                }

            }

            return var13;
        }
    }

    private void metaPersistency(UploadParams upParams) throws LfasrException {
        try {
            LocalPersistenceMeta lpm = new LocalPersistenceMeta();
            lpm.setAppId(upParams.getSignature().getAppID());
            lpm.setSecretKey(upParams.getSignature().getSecretKey());
            lpm.setSigna(upParams.getSignature().getSigna());
            lpm.setTs(upParams.getSignature().getTs());
            lpm.setLfasrType(upParams.getLfasrType().getValue());
            lpm.setFilePieceSize(this.file_piece_size);
            lpm.setTaskId(upParams.getTaskId());
            lpm.setFile(upParams.getFile().getAbsolutePath());
            lpm.setParams(upParams.getParams());
            String jsonStr = JSON.toJSONString(lpm);
            String fileName = LfasrClientImp.SERV_STORE_PATH_VAL + "/" + upParams.getTaskId() + ".dat";
            LocalPersistenceFile.writeNIO(fileName, jsonStr);
            LOGGER.info(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "UploadWorker", upParams.getTaskId(), "", "(-1) ms", "write meta info success, store file:" + fileName));
        } catch (LfasrException var5) {
            LOGGER.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "UploadWorker", upParams.getTaskId(), "", "(-1) ms", "write meta info error"), var5);
            throw new LfasrException("{\"ok\":\"-1\", \"err_no\":\"26301\", \"failed\":\"转写断点续传持久化文件读写错误!\", \"data\":\"\"}");
        }
    }
}

