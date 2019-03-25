#### 最新版本

模块|Bugly
---|---
最新版本|[![Download](https://jitpack.io/v/like5188/Bugly.svg)](https://jitpack.io/#like5188/Bugly)

## 功能介绍
1、基于腾讯Bugly的封装

2、包括的功能：异常上报、应用升级、热更新、渠道打包(walle)

3、详细配置步骤参考bugly官方文档。

## 使用方法：

1、引用

在Project的gradle中加入：
```groovy
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```
在Module的gradle中加入：
```groovy
    dependencies {
        compile 'com.github.like5188:Bugly:版本号'
    }
```

2、在project的build.gradle文件中添加：
```java
    dependencies {
        // tinkersupport插件, 其中lastest.release指拉取最新版本，也可以指定明确版本号，例如1.0.4
        classpath "com.tencent.bugly:tinker-support:1.1.5"
        classpath "com.meituan.android.walle:plugin:1.1.6"
    }
```

3、在app的build.gradle文件中添加：
```java
    defaultConfig {
        multiDexEnabled true
    }
```

4、异常上报、应用升级功能配置：

① 在Application的onCreate()方法中调用BuglyUtils.init(context, "你的appId", isDebug)方法进行初始化后，就能异常上报和自动检测更新了。

② 在Application的attachBaseContext()方法中调用MultiDex.install(context)

③ 也可以调用BuglyUtils.checkUpgrade()方法手动进行新版本检测。

5、热更新功能配置：

① 在app目录下增加文件：tinker-support.gradle。内容如下：
```java
    apply plugin: 'com.tencent.bugly.tinker-support'

    def bakPath = file("${buildDir}/bakApk/")

    /**
     * 此处填写每次构建生成的基准包目录
     */
    def baseApkDir = "app-0322-10-30-41"

    /**
     * 对于插件各参数的详细解析请参考
     */
    tinkerSupport {

        // 开启tinker-support插件，默认值true
        enable = true

        // 指定归档目录，默认值当前module的子目录tinker
        autoBackupApkDir = "${bakPath}"

        // 是否启用覆盖tinkerPatch配置功能，默认值false
        // 开启后tinkerPatch配置不生效，即无需添加tinkerPatch
        overrideTinkerPatchConfiguration = true

        // 编译补丁包时，必需指定基线版本的apk，默认值为空
        // 如果为空，则表示不是进行补丁包的编译
        // @{link tinkerPatch.oldApk }
        baseApk = "${bakPath}/${baseApkDir}/app-release.apk"

        // 对应tinker插件applyMapping
        baseApkProguardMapping = "${bakPath}/${baseApkDir}/app-release-mapping.txt"

        // 对应tinker插件applyResourceMapping
        baseApkResourceMapping = "${bakPath}/${baseApkDir}/app-release-R.txt"

        // 构建基准包和补丁包都要指定不同的tinkerId，并且必须保证唯一性
        tinkerId = "patch-3.0"

        // 构建多渠道补丁时使用
        // buildAllFlavorsDir = "${bakPath}/${baseApkDir}"

        // 是否启用加固模式，默认为false.(tinker-spport 1.0.7起支持）
        // isProtectedApp = true

        // 是否开启反射Application模式
        enableProxyApplication = true

        // 是否支持新增非export的Activity（注意：设置为true才能修改AndroidManifest文件）
        supportHotplugComponent = true

    }

    /**
     * 一般来说,我们无需对下面的参数做任何的修改
     * 对于各参数的详细介绍请参考:
     * https://github.com/Tencent/tinker/wiki/Tinker-%E6%8E%A5%E5%85%A5%E6%8C%87%E5%8D%97
     */
    tinkerPatch {
        //oldApk ="${bakPath}/${appName}/app-release.apk"
        ignoreWarning = false
        useSign = true
        dex {
            dexMode = "jar"
            pattern = ["classes*.dex"]
            loader = []
        }
        lib {
            pattern = ["lib/*/*.so"]
        }

        res {
            pattern = ["res/*", "r/*", "assets/*", "resources.arsc", "AndroidManifest.xml"]
            ignoreChange = []
            largeModSize = 100
        }

        packageConfig {
        }
        sevenZip {
            zipArtifact = "com.tencent.mm:SevenZip:1.1.10"
    //        path = "/usr/local/bin/7za"
        }
        buildConfig {
            keepDexApply = false
            //tinkerId = "1.0.1-base"
            //applyMapping = "${bakPath}/${appName}/app-release-mapping.txt" //  可选，设置mapping文件，建议保持旧apk的proguard混淆方式
            //applyResourceMapping = "${bakPath}/${appName}/app-release-R.txt" // 可选，设置R.txt文件，通过旧apk文件保持ResId的分配
        }
    }
```

② 在app的build.gradle文件中添加：
```java
  // 热更新
  apply from: 'tinker-support.gradle'
```

③ 在Application的attachBaseContext()方法中调用BuglyUtils.initTinker()进行初始化。

④ 打基线包baseApk：

    需要修改上述文件的tinkerId = "base-1.0"。

    并用assembleRelease或者assembleReleaseChannels（打渠道包时）命令打包。

    生成文件中的app-release.apk就是基线包。用于加固或者发布。

    直接用基线包、或者加固后的包安装后，然后启动app，上报给bugly。这样才能上传补丁包。

    注意：需要把app-release.apk、app-release-mapping.txt、app-release-R.txt三个文件保存好。这是打补丁的基础。

⑤ 打补丁包：

    需要修改上述文件的def baseApkDir = "app-0322-10-30-41"和tinkerId = "patch-1.0"。

    并用buildTinkerPatchRelease命令打包。

    生成文件中的patch_signed_7zip.apk就是补丁包。

6、如果需要渠道打包功能。需要做如下配置：

① 在app目录下增加两个个文件：

文件1：multiple-channel.gradle：
```java
    apply plugin: 'walle'

    walle {
        /**
         * 可使用以下变量:
         *      projectName - 项目名字
         *      appName - App模块名字
         *      packageName - applicationId (App包名packageName)
         *      buildType - buildType (release/debug等)
         *      channel - channel名称 (对应渠道打包中的渠道名字)
         *      versionName - versionName (显示用的版本号)
         *      versionCode - versionCode (内部版本号)
         *      buildTime - buildTime (编译构建日期时间)
         *      fileSHA1 - fileSHA1 (最终APK文件的SHA1哈希值)
         *      flavorName - 编译构建 productFlavors 名
         */
        // 指定渠道包的输出路径
        apkOutputFolder = new File("${project.buildDir}/outputs/channels")
        // 定制渠道包的APK的文件名称
        apkFileNameFormat = '${appName}-${packageName}-${channel}-${buildType}-v${versionName}-${versionCode}-${buildTime}.apk'
        // 渠道配置文件
        channelFile = new File("${project.getProjectDir()}/channel")
    }
```

文件2：channel：

在目录"${project.getProjectDir()}/channel"下，增加对应的渠道配置文件channel：
```java
    xiaomi # 小米
    meizu
```

② 在app的build.gradle文件中添加：
```java
  // 多渠道使用walle示例（注：多渠道使用）
  apply from: 'multiple-channel.gradle'
```

③ 使用命令：gradlew clean assembleReleaseChannels 打渠道包