/**
 * 文件名: HttpWorker.java
 * 版权：Copyright 2017-2022 CMCC All Rights Reserved.
 * 描述:
 */
package voice2text.config.xunfei;

/**
 * @description:
 * @author: Shenshuaihu
 * @version: 1.0
 * @data: 2019-04-26 11:57
 */
import com.iflytek.msp.cpdb.lfasr.event.Event;
import com.iflytek.msp.cpdb.lfasr.http.HttpUtil;
import com.iflytek.msp.cpdb.lfasr.model.FileSlice;
import com.iflytek.msp.cpdb.lfasr.model.UploadParams;
import voice2text.api.LfasrClientImp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class HttpWorker {
    private static String url_prepare;
    private static String url_upload;
    private static String url_meger;
    private static String url_result;
    private static String url_progress;
    private static String url_version;

    static {
        url_prepare = LfasrClientImp.SERV_LFASR_HOST_VAL + "/" + "prepare";
        url_upload = LfasrClientImp.SERV_LFASR_HOST_VAL + "/" + "upload";
        url_meger = LfasrClientImp.SERV_LFASR_HOST_VAL + "/" + "merge";
        url_result = LfasrClientImp.SERV_LFASR_HOST_VAL + "/" + "getResult";
        url_progress = LfasrClientImp.SERV_LFASR_HOST_VAL + "/" + "getProgress";
        url_version = LfasrClientImp.SERV_LFASR_HOST_VAL + "/" + "getVersion";
    }

    public HttpWorker() {
    }

    public String prepare(UploadParams upParams) {
        HashMap<String, String> params = new HashMap();
        params.put("app_id", upParams.getSignature().getAppID());
        params.put("secret_key", upParams.getSignature().getSecretKey());
        params.put("signa", upParams.getSignature().getSigna());
        params.put("ts", upParams.getSignature().getTs());
        params.put("file_len", String.valueOf(upParams.getFile().length()));
        params.put("file_name", upParams.getFile().getName());
        params.put("lfasr_type", String.valueOf(upParams.getLfasrType().getValue()));
        params.put("slice_num", String.valueOf(upParams.getSliceNum()));
        params.put("client_version", upParams.getClientVersion());
        params.put("check_length", "" + upParams.getCheckLength());
        HashMap<String, String> p = upParams.getParams();
        if (p != null) {
            Iterator iter = p.entrySet().iterator();

            while(iter.hasNext()) {
                Entry entry = (Entry)iter.next();
                params.put(entry.getKey().toString(), entry.getValue().toString());
            }
        }

        return HttpUtil.post(url_prepare, params);
    }

    public String handle(Event event) {
        FileSlice fileSlice = event.getFileSlice();
        UploadParams params = event.getParams();
        HashMap<String, String> hm = new HashMap();
        hm.put("app_id", params.getSignature().getAppID());
        hm.put("signa", params.getSignature().getSigna());
        hm.put("ts", params.getSignature().getTs());
        hm.put("slice_id", fileSlice.getSliceID());
        hm.put("task_id", params.getTaskId());
        return HttpUtil.postMulti(url_upload, hm, fileSlice.getBody());
    }

    public String merge(Event event) {
        UploadParams params = event.getParams();
        HashMap<String, String> map = new HashMap();
        map.put("app_id", params.getSignature().getAppID());
        map.put("signa", params.getSignature().getSigna());
        map.put("ts", params.getSignature().getTs());
        map.put("task_id", params.getTaskId());
        map.put("file_name", params.getFile().getName());
        return HttpUtil.post(url_meger, map);
    }

    public String getResult(UploadParams params) {
        HashMap<String, String> map = new HashMap();
        map.put("app_id", params.getSignature().getAppID());
        map.put("signa", params.getSignature().getSigna());
        map.put("ts", params.getSignature().getTs());
        map.put("task_id", params.getTaskId());
        return HttpUtil.post(url_result, map);
    }

    public String getProgress(UploadParams params) {
        HashMap<String, String> map = new HashMap();
        map.put("app_id", params.getSignature().getAppID());
        map.put("signa", params.getSignature().getSigna());
        map.put("ts", params.getSignature().getTs());
        map.put("task_id", params.getTaskId());
        return HttpUtil.post(url_progress, map);
    }

    public String getVersion(UploadParams params) {
        HashMap<String, String> map = new HashMap();
        map.put("app_id", params.getSignature().getAppID());
        map.put("signa", params.getSignature().getSigna());
        map.put("ts", params.getSignature().getTs());
        map.put("client_version", params.getClientVersion());
        return HttpUtil.post(url_version, map);
    }
}

