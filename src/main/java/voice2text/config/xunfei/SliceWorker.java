/**
 * 文件名: SliceWorker.java
 * 版权：Copyright 2017-2022 CMCC All Rights Reserved.
 * 描述:
 */
package voice2text.config.xunfei;

/**
 * @description:
 * @author: Shenshuaihu
 * @version: 1.0
 * @data: 2019-04-27 23:20
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import com.iflytek.msp.cpdb.lfasr.event.Event;
import com.iflytek.msp.cpdb.lfasr.exception.SliceException;
import com.iflytek.msp.cpdb.lfasr.file.ChannelFileReader;
import com.iflytek.msp.cpdb.lfasr.model.EventType;
import com.iflytek.msp.cpdb.lfasr.model.FileSlice;
import com.iflytek.msp.cpdb.lfasr.model.UploadParams;
import com.iflytek.msp.cpdb.lfasr.util.DictUtil;
import java.util.HashMap;

public class SliceWorker {
    private EventHandler eventHandler;
    private DictUtil aTOz = new DictUtil("aaaaaaaaa`");
    private HashMap<String, String> hm;
    private UploadParams params;

    public SliceWorker(UploadParams params, long file_piece_size, boolean isResume, HashMap<String, String> hm) {
        this.hm = hm;
        this.params = params;
        this.eventHandler = new EventHandler(params, file_piece_size, isResume);
    }

    public void sliceFile(ChannelFileReader reader) throws SliceException {
        if (reader == null) {
            throw new SliceException("{\"ok\":\"-1\", \"err_no\":\"26404\", \"failed\":\"转写上传文件读取错误!\", \"data\":\"\"}");
        } else {
            try {
                while(reader.read() != -1) {
                    String slice_id = this.aTOz.getNextString();
                    if (!this.hm.containsKey(slice_id)) {
                        this.eventHandler.modifySliceHM(slice_id, false);
                        Event event = new Event(EventType.LFASR_FILE_DATA_CONTENT, this.params);
                        event.setFileSlice(new FileSlice(slice_id, reader.getArray()));
                        this.eventHandler.addEvent(event);
                    }
                }

            } catch (Exception var4) {
                throw new SliceException("{\"ok\":\"-1\", \"err_no\":\"26404\", \"failed\":\"转写上传文件读取错误!\", \"data\":\"\"}");
            }
        }
    }

    public void setFileEnd() {
        Event event = new Event(EventType.LFASR_FILE_DATA_END, this.params);
        this.eventHandler.addEvent(event);
    }

    public EventHandler getEventHandler() {
        return this.eventHandler;
    }
}
