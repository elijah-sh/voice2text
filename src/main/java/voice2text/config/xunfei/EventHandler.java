/**
 * 文件名: EventHandler.java
 * 版权：Copyright 2017-2022 CMCC All Rights Reserved.
 * 描述:
 */
package voice2text.config.xunfei;

/**
 * @description:
 * @author: Shenshuaihu
 * @version: 1.0
 * @data: 2019-04-27 21:02
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import com.alibaba.fastjson.JSON;
//import com.iflytek.msp.cpdb.lfasr.client.LfasrClientImp;
import com.iflytek.msp.cpdb.lfasr.event.Event;
import com.iflytek.msp.cpdb.lfasr.event.EventQueue;
import com.iflytek.msp.cpdb.lfasr.exception.LfasrException;
import com.iflytek.msp.cpdb.lfasr.file.LocalPersistenceFile;
import com.iflytek.msp.cpdb.lfasr.model.EventType;
import com.iflytek.msp.cpdb.lfasr.model.Message;
import com.iflytek.msp.cpdb.lfasr.model.UploadParams;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;
import voice2text.api.LfasrClientImp;

public class EventHandler {
    private static final Logger LOGGER = Logger.getLogger(EventHandler.class);
    private static final int LIMITED_ACCESS_FREQUENCY_ECODE = 26603;
    private UploadParams upParams;
    private long file_piece_size;
    private boolean is_resume = false;
    private EventQueue<Event> queue;
    private HashMap<String, Boolean> slice_hm = new HashMap();
    private CountDownLatch latch = new CountDownLatch(1);
    private ExecutorService exec;
    private int threadNum = 10;

    public EventHandler(UploadParams upParams, long file_piece_size, boolean is_resume) {
        this.upParams = upParams;
        this.file_piece_size = file_piece_size;
        this.is_resume = is_resume;
        this.queue = new EventQueue();
        this.exec = Executors.newFixedThreadPool(this.threadNum);
        this.start();
    }

    public void start() {
        for(int i = 0; i < this.threadNum; ++i) {
            this.exec.execute(new EventHandler.ProcessorThread());
        }

    }

    public void addEvent(Event event) {
        try {
            this.queue.put(event);
        } catch (InterruptedException var3) {
            var3.printStackTrace();
        }

    }

    private void retryEvent(Event event) {
        try {
            long activeTimeMillis = System.currentTimeMillis();
            int retryTimes = event.getRetryTimes();
            switch(retryTimes) {
                case 0:
                case 1:
                case 2:
                    activeTimeMillis += 5000L;
                    LOGGER.warn(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "Event retry", event.getParams().getTaskId(), "", "(-1) ms", (event.getType().getValue() == 0 ? "slice_id:" + event.getFileSlice().getSliceID() + " upload" : "merge") + " fail, freezing a while to " + new Date(activeTimeMillis)));
                    break;
                case 3:
                case 4:
                    activeTimeMillis += 300000L;
                    LOGGER.warn(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "Event retry", event.getParams().getTaskId(), "", "(-1) ms", (event.getType().getValue() == 0 ? "slice_id:" + event.getFileSlice().getSliceID() + " upload" : "merge") + " fail, freezing a long timeMillis to " + new Date(activeTimeMillis)));
                    break;
                default:
                    LOGGER.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "Event retry", event.getParams().getTaskId(), "", "(-1) ms", (event.getType().getValue() == 0 ? "slice_id:" + event.getFileSlice().getSliceID() + " upload" : "merge") + " fail, retry times reach " + retryTimes + ", task fail"));
                    this.latch.countDown();
                    this.exec.shutdownNow();
                    return;
            }

            LOGGER.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "Event retry", event.getParams().getTaskId(), "", "(-1) ms", (event.getType().getValue() == 0 ? "slice_id:" + event.getFileSlice().getSliceID() + " upload" : "merge") + " fail, freeze event...and try again....current retry times " + retryTimes));
            event.setActiveTimeTimeMillis(activeTimeMillis);
            event.addRetryTimes();
            this.queue.put(event);
        } catch (InterruptedException var5) {
            LOGGER.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "Event retry", event.getParams().getTaskId(), "", "(-1) ms", (event.getType().getValue() == 0 ? "slice_id:" + event.getFileSlice().getSliceID() + " upload" : "merge") + " retry Interrupted"));
        }

    }

    private void retryEvent(Event event, Message message) {
        try {
            if (message.getErr_no() == 26603) {
                LOGGER.warn(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "Event retry", event.getParams().getTaskId(), "", "(-1) ms", (event.getType().getValue() == 0 ? "slice_id:" + event.getFileSlice().getSliceID() + " upload" : "merge") + " fail, access frequency over limit, sleep for a while and retry"));
                Thread.sleep(10000L);
                this.queue.put(event);
            } else {
                this.retryEvent(event);
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public void modifySliceHM(String slice_id, boolean bool) {
        this.slice_hm.put(slice_id, bool);
    }

    public boolean isSendAll() throws LfasrException {
        if (this.exec.isShutdown()) {
            throw new LfasrException("upload fail");
        } else {
            boolean isSend = true;
            if (this.slice_hm == null) {
                return isSend;
            } else {
                int sendNum = 0;
                Iterator iter = this.slice_hm.entrySet().iterator();

                while(iter.hasNext()) {
                    Entry entry = (Entry)iter.next();
                    if (entry.getValue().toString().equalsIgnoreCase("false")) {
                        isSend = false;
                    } else {
                        ++sendNum;
                    }
                }

                LOGGER.debug(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "EventHandler", "", "", "(-1) ms", "upload file " + sendNum + "/" + this.slice_hm.size()));
                return isSend;
            }
        }
    }

    public void await() throws InterruptedException {
        this.latch.await();
    }

    public void shutdownNow() {
        this.exec.shutdownNow();
    }

    public class ProcessorThread implements Runnable {
        public ProcessorThread() {
        }

        public void run() {
            while(true) {
                Event event = null;

                try {
                    event = (Event)EventHandler.this.queue.take();
                } catch (InterruptedException var12) {
                    return;
                }

                if (!event.canActive()) {
                    EventHandler.this.queue.add(event);
                } else {
                    HttpWorker worker;
                    String result;
                    if (event.getType().getValue() == EventType.LFASR_FILE_DATA_CONTENT.getValue()) {
                        worker = new HttpWorker();
                        result = worker.handle(event);
                        String slice_id = event.getFileSlice().getSliceID();
                        Message responseObj = null;

                        try {
                            responseObj = (Message)JSON.parseObject(result, Message.class);
                        } catch (Exception var9) {
                            EventHandler.this.retryEvent(event);
                            continue;
                        }

                        if (responseObj == null) {
                            EventHandler.this.retryEvent(event);
                        } else {
                            int codex = responseObj.getOk();
                            if (codex == 0) {
                                try {
                                    LocalPersistenceFile.writeNIO(LfasrClientImp.SERV_STORE_PATH_VAL + "/" + EventHandler.this.upParams.getTaskId() + ".dat", slice_id);
                                    EventHandler.this.modifySliceHM(slice_id, true);
                                } catch (LfasrException var8) {
                                    EventHandler.LOGGER.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "EventHandler", event.getParams().getTaskId(), "", "(-1) ms", "write meta info to " + LfasrClientImp.SERV_STORE_PATH_VAL + "/" + EventHandler.this.upParams.getTaskId() + ".dat" + " error"), var8);
                                }

                                EventHandler.LOGGER.debug(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "EventHandler", event.getParams().getTaskId(), "", "(-1) ms", "upload slice send success.file:" + EventHandler.this.upParams.getFile().getAbsolutePath() + ", slice_id:" + slice_id));
                            } else {
                                EventHandler.this.retryEvent(event, responseObj);
                            }
                        }
                    } else {
                        try {
                            Thread.sleep(1000L);
                        } catch (InterruptedException var11) {
                            ;
                        }

                        worker = new HttpWorker();
                        result = worker.merge(event);
                        Message responseObjx = null;

                        try {
                            responseObjx = (Message)JSON.parseObject(result, Message.class);
                        } catch (Exception var10) {
                            EventHandler.this.retryEvent(event);
                            continue;
                        }

                        if (responseObjx == null) {
                            EventHandler.this.retryEvent(event);
                        } else {
                            int code = responseObjx.getOk();
                            if (code == 0) {
                                LocalPersistenceFile.deleteFile(new File(LfasrClientImp.SERV_STORE_PATH_VAL + "/" + EventHandler.this.upParams.getTaskId() + ".dat"));
                                EventHandler.LOGGER.info(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "EventHandler", event.getParams().getTaskId(), "", "(-1) ms", "merge success"));
                                EventHandler.this.exec.shutdownNow();
                                EventHandler.this.latch.countDown();
                                return;
                            }

                            EventHandler.this.retryEvent(event, responseObjx);
                        }
                    }
                }
            }
        }
    }
}

