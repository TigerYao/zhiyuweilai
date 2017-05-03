package com.zhiyuweilai.tiger.robotbook.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

/**
 * Created by yaohu on 2017/5/3.
 */

public class IntentUtils {
    public static void starIntent(Context ctx, String url)throws Exception{
        Intent intent = Intent.parseUri(url, Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PackageManager pm = ctx.getPackageManager();
        ResolveInfo resolve = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        List<ResolveInfo> appList = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        ResolveInfo startResolve = null;
        if (!hasDefaultActivity(resolve, appList)) {
            startResolve = appList.get(0);
        }
        if (startResolve != null) {
            intent.setPackage(startResolve.activityInfo.packageName);
        }
        try {
            ctx.startActivity(intent);

        } catch (Exception ae) {}
    }
    // If the list contains the above resolved activity, then it can't be
    // ResolverActivity itself.
    public static boolean hasDefaultActivity(ResolveInfo resolved, List<ResolveInfo> appList) {
        if (resolved == null || appList == null || appList.size() < 1)
            return false;
        for (int i = 0; i < appList.size(); i++) {
            ResolveInfo tmp = appList.get(i);
            if (tmp.activityInfo.name.equals(resolved.activityInfo.name)
                    && tmp.activityInfo.packageName.equals(resolved.activityInfo.packageName)) {
                return true;
            }
        }
        return false;
    }

}
