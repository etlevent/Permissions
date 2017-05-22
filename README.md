# Permissions
-----------
**Permissions**是基于Java AOP(Aspect Oriented Programming)及APT(Annotation processing tool)技术实现的Android M动态权限申请
## How To Use
定义了如下注解：
```
@RequestPermission
@PermissionDenied
@PermissionGranted
@PermissionNeverAskAgain
```
- 在需要申请权限的方法上添加注解`@RequestPermission`，申明需要的权限及requestCode，如下：
```
    @Override
    public void onClick(View v) {
      testRequestPermission();
    }
    @RequestPermission(value = Manifest.permission.WRITE_EXTERNAL_STORAGE, requestCode = 1001)
    void testRequestPermission() {
        Toast.makeText(this, "permission method", Toast.LENGTH_SHORT).show();
    }
```
- 申请权限后同样在方法上添加`@PermissionGranted`，同时申明requestCode，即可实现权限被允许时的逻辑
```
    @PermissionGranted(1001)
    void permissionGranted() {
        Toast.makeText(this, "permissionGranted", Toast.LENGTH_SHORT).show();
        testRequestPermission();
    }
```
- 方法上添加注解`@PermissionDenied`，同时申明requestCode，即可实现权限被拒绝时的逻辑
```
    @PermissionDenied(1001)
    void permissionDenied() {
        Toast.makeText(this, "permissionDenied", Toast.LENGTH_SHORT).show();
    }
```
- 方法上添加注解`@PermissionNeverAskAgain`，同时申明requestCode，则可实现用户勾选**Never Ask Again**后的相关逻辑，通常提示用户权限的用途及跳转到权限管理界面等等。
*Tips: * 若没有添加此注解会回调到`@PermissionDenied`的方法
```
    @PermissionNeverAskAgain(1001)
    void permissionNeverAskAgain() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        startActivity(intent);
    }
```
- 重写[Activity]或[Fragment]`onRequestPermissionsResult`
由于对Aspectj不是很熟悉，目前这套代码有一个比较鸡肋的地方就是需要重写[Activity]或[Fragment]的`onRequestPermissionsResult`方法(只需重写方法调用super方法即可)，建议**在Activity或Fragment的基类中重写此方法**；
```
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
```
目前暂时无法实现Aspectj对于Activity没有被重写的方法的捕获，如果有**大神**实现过相关功能，可以一起讨论学习下
- gradle配置：项目在未发布到[Bintrayjcenter](https://bintray.com/bintray/jcenter)中，因此可以引用我的私有仓库来完成，在**Project**的**build.gradle**中添加
```
buildscript {
    repositories {
        jcenter()
        maven {
            url 'https://dl.bintray.com/dingling/Maven'
        }
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:2.3.2'
        classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
        classpath 'cherry.android.plugins:permissions-plugin:1.0.0'
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        jcenter()
        maven {
            url 'https://dl.bintray.com/dingling/Maven'
        }
    }
}
```
在**app**的**build.gradle**中添加
```
apply plugin: 'com.neenbedankt.android-apt'
apply plugin: 'permissions.plugin'

dependencies {
    compile 'cherry.android:permissions-api:1.0.0'
    compile 'cherry.android:permissions-annotations:1.0.0'
    apt 'cherry.android:permissions-compiler:1.0.0'
}
```
或者可以到我的[github](https://github.com/CherryLius/Permissions)中clone源代码

部分代码参照[PermissionsDispatcher](https://github.com/hotchemi/PermissionsDispatcher)，表示感谢，THANKS

# 结束语
项目代码比较简单，学习AOP过程的一个小产品，主要是希望能有大神指点讨论，欢迎提Issues.