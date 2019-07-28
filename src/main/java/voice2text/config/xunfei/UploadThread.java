/**
 * 文件名: UploadThread.java
 * 版权：Copyright 2017-2022 CMCC All Rights Reserved.
 * 描述:
 */
package voice2text.config.xunfei;

/**
 * @description:
 * @author: Shenshuaihu
 * @version: 1.0
 * @data: 2019-04-27 22:04
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import com.iflytek.msp.cpdb.lfasr.file.ChannelFileReader;
import com.iflytek.msp.cpdb.lfasr.model.UploadParams;
import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class UploadThread implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(UploadThread.class);
    private UploadParams params;
    private int file_piece_size;
    private ChannelFileReader fr;

    public UploadThread(UploadParams params, int file_piece_size, ChannelFileReader fr) {
        this.params = params;
        this.file_piece_size = file_piece_size;
        this.fr = fr;
    }

    public void run() {
        try {
            SliceWorker sw = new SliceWorker(this.params, (long)this.file_piece_size, false, new HashMap());
            sw.sliceFile(this.fr);

            while(!sw.getEventHandler().isSendAll()) {
                try {
                    Thread.sleep(500L);
                } catch (Exception var12) {
                    ;
                }
            }

            LOGGER.info(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "UploadThread", this.params.getTaskId(), "", "(-1) ms", "upload file send success, file:" + this.params.getFile().getAbsolutePath() + ", task_id:" + this.params.getTaskId()));
            sw.setFileEnd();
            sw.getEventHandler().await();
        } catch (Exception var13) {
            LOGGER.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "UploadThread", this.params.getTaskId(), "", "(-1) ms", "upload file send error, file:" + this.params.getFile().getAbsolutePath() + ", task_id:" + this.params.getTaskId()), var13);
        } finally {
            if (this.fr != null) {
                try {
                    this.fr.close();
                } catch (IOException var11) {
                    var11.printStackTrace();
                }
            }

        }

    }
}
