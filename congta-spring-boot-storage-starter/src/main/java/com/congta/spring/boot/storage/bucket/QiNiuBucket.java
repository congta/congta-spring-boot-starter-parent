package com.congta.spring.boot.storage.bucket;

import com.congta.spring.boot.shared.ex.ExceptionHelper;
import com.congta.spring.boot.shared.ex.OpCode;
import com.congta.spring.boot.storage.config.StorageBucketConfig;
import com.congta.spring.boot.storage.config.StorageBucketLevel;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.Etag;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangfucheng on 2021/6/28.
 */
public class QiNiuBucket implements OperableBucket {

    private static Logger log = LoggerFactory.getLogger(QiNiuBucket.class);

    private static final Function<String, Region> REGION_FUNCTION = r -> {
        switch (r.toLowerCase()) {
            case "region0":
            case "huadong":
                return Region.huadong();
            case "region1":
            case "huabei":
                return Region.huabei();
            default:
                log.warn("unknown qiniu region {}", r);
                throw ExceptionHelper.build(OpCode.STORAGE_ERROR, "unknown " + r);
        }
    };

    private String bucketName;
    private Auth auth;
    private String domain;
    private StorageBucketLevel level;

    private Pair<String, Long> upToken = Pair.of("", 0L);
    private BucketManager bucketManager;
    private UploadManager uploadManager;

    public QiNiuBucket(String bucketName, Auth auth, StorageBucketConfig bucketConfig) {
        this.bucketName = bucketName;
        this.auth = auth;
        this.domain = bucketConfig.getDomain();
        String levelStr = StringUtils.defaultIfBlank(bucketConfig.getLevel(), "public");
        this.level = StorageBucketLevel.valueOf(levelStr.toUpperCase());
        Configuration cfg = new Configuration(REGION_FUNCTION.apply(bucketConfig.getRegion()));

        bucketManager = new BucketManager(auth, cfg);
        uploadManager = new UploadManager(cfg);
    }

    public String getUpToken(Auth auth) {
        long now = System.currentTimeMillis();
        if (now - upToken.getRight() > 3000000) {
            // 3600s过期，超过3000s即更换
            upToken = Pair.of(auth.uploadToken(bucketName), now);
        }
        return upToken.getLeft();
    }

    @Override
    public String upload(byte[] file, String ns, String key) {
        boolean isReplace = StringUtils.isNotBlank(key);
        if (StringUtils.isBlank(key)) {
            key = Etag.data(file);
        }
        if (StringUtils.isNotBlank(ns) && !StringUtils.startsWith(key, ns + "/")) {
            key = ns + "/" + key;
        }
        try {
            if (!isReplace && exist(key)) {
                return key;
            }
            String upToken = getUpToken(auth);
            Response response = uploadManager.put(file, key, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
            return putRet.key;
        } catch (QiniuException e) {
            throw ExceptionHelper.build(OpCode.STORAGE_ERROR, "上传文件失败", e, false);
        }
    }

    @Override
    public boolean exist(String key) {
        try {
            bucketManager.stat(bucketName, key);
            return true;
        } catch (QiniuException e) {
            if (e.code() != 612) {
                throw ExceptionHelper.build(OpCode.STORAGE_ERROR, "storage qn error", e);
            }
            return false;
        }
    }

    /**
     * url format: https://${domain}/${key}?imageView2/3/w/${width}/h/${height}
     */
    @Override
    public String getUrl(String key, int width, int height) {
        if (key == null || key.startsWith("http://") || key.startsWith("https://")) {
            return key;
        }
        String url = String.format("https://%s/%s", domain, key);
        if (width <= 0 && height <= 0) {
            return url;
        }
        url += "?imageView2/3";
        if (width > 0) {
            url += "/w/" + width;
        }
        if (height > 0) {
            url += "/h/" + height;
        }
        return getFinalUrl(url);
    }

    private String getFinalUrl(String url) {
        if (level == StorageBucketLevel.PRIVATE) {
            return auth.privateDownloadUrl(url, 86400);
        } else {
            // style 是 ns 的属性，在 StorageService 里面添加
            return url;
        }
    }
}
