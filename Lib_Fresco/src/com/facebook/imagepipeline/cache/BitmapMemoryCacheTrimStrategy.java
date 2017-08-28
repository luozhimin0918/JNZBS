/*
 * Copyright (c) 2015-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.facebook.imagepipeline.cache;

import android.os.Build;

import com.facebook.common.logging.FLog;
import com.facebook.common.memory.MemoryTrimType;

/**
 * CountingMemoryCache eviction strategy appropriate for bitmap caches.
 *
 * <p>If run on KK or below, then this TrimStrategy behaves exactly as
 * NativeMemoryCacheTrimStrategy. If run on Lollipop, then BitmapMemoryCacheTrimStrategy will trim
 * cache in one additional case: when OnCloseToDalvikHeapLimit trim type is received, cache's
 * eviction queue will be trimmed according to OnCloseToDalvikHeapLimit's suggested trim ratio.
 */
public class BitmapMemoryCacheTrimStrategy implements CountingMemoryCache.CacheTrimStrategy {
  private static final String TAG = "BitmapMemoryCacheTrimStrategy";

  @Override
  public void trimCache(CountingMemoryCache<?, ?, ?> cache, MemoryTrimType trimType) {
    switch (trimType) {
      case OnCloseToDalvikHeapLimit:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          final double cacheTrimTarget =
              1 - MemoryTrimType.OnCloseToDalvikHeapLimit.getSuggestedTrimRatio();
          final int cacheSizeTarget = (int) (cacheTrimTarget * cache.getSizeInBytes());
          cache.trimCacheTo(Integer.MAX_VALUE, cacheSizeTarget);
        }
        break;
      case OnAppBackgrounded:
      case OnSystemLowMemoryWhileAppInForeground:
      case OnSystemLowMemoryWhileAppInBackground:
        cache.clearEvictionQueue();
        break;
      default:
        FLog.wtf(TAG, "unknown trim type: %s", trimType);
        break;
    }
  }
}
