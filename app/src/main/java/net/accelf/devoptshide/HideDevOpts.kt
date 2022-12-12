package net.accelf.devoptshide

import android.content.ContentResolver
import android.provider.Settings
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class HideDevOpts : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: LoadPackageParam?) {
        if (lpparam == null) {
            return
        }

        listOf(Settings.Secure::class.java, Settings.Global::class.java).forEach { parent ->
            findAndHookMethod(
                parent,
                "getInt",
                ContentResolver::class.java,
                String::class.java,
                callback,
            )

            findAndHookMethod(
                parent,
                "getInt",
                ContentResolver::class.java,
                String::class.java,
                Int::class.java,
                callback,
            )
        }
    }

    companion object {
        private val names = listOf(
            Settings.Global.ADB_ENABLED,
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
        )

        private val callback = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam?) {
                if (
                    param == null
                    || param.args[1] !is String
                    || !names.contains(param.args[1] as String)
                ) {
                    return
                }

                param.result = 0
            }
        }
    }
}
