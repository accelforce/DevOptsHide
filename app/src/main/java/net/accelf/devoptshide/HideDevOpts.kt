package net.accelf.devoptshide

import android.content.ContentResolver
import android.provider.Settings
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class HideDevOpts : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: LoadPackageParam?) {
        if (lpparam == null) {
            return
        }
        XposedBridge.log("Found package: ${lpparam.packageName}")

        findAndHookMethod(
            Settings.Secure::class.java,
            "getInt",
            ContentResolver::class.java,
            String::class.java,
            Int::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    if (param == null || param.args[1] !is String || (param.args[1] as String) != Settings.Global.DEVELOPMENT_SETTINGS_ENABLED) {
                        return
                    }

                    param.result = 0
                }
            })
    }
}
