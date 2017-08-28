package com.jyh.gxcjzbs.common.utils.imageutils;

import android.net.Uri;

import com.facebook.cache.common.CacheKey;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.request.ImageRequest;

/**
 * 项目名:CJDJCZBS
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2016/11/28.
 */

public class FrescoUtils {
    /**
     * Fresco判断图片是否已经下载过
     * @param loadUri
     * @return
     */
    public static boolean isDownloaded(Uri loadUri) {
        if (loadUri == null) {
            return false;
        }
        ImageRequest imageRequest = ImageRequest.fromUri(loadUri);
        CacheKey cacheKey = DefaultCacheKeyFactory.getInstance()
                .getEncodedCacheKey(imageRequest);
        return ImagePipelineFactory.getInstance()
                .getMainDiskStorageCache().hasKey(cacheKey);
    }
}
