object Configs {
    const val COMPILE_SDK_VERSION = 37
    const val TARGET_SDK_VERSION = 36
    const val MIN_SDK_VERSION = 26
    const val CORE_APP_ID = "com.metacto.coreApp"
    const val SAMPLE_APP_FRAMEWORK_NAME = "sampleAppShared"
    const val SAMPLE_APP_NAMESPACE = "com.sampleApp.app"
    const val SAMPLE_APP_SHARED_NAMESPACE = "com.sampleApp.app.shared"
    const val DEV_APP_ID = "com.sampleApp.app.dev"
    const val PROD_APP_ID = "com.sampleApp.app.prod"
    val MY_ATLAS_VERSION_CODE by lazy { getGitCommitCount() }
    val MY_ATLAS_VERSION_NAME by lazy { getVersionName() }
    const val IOS_DEPLOYMENT_TARGET = "14.1"
}