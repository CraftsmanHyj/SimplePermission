# SimplePermission
处理需要动态申请的用户权限。对于拒绝、不再询问有做再次调起的逻辑处理，支持国产定制系统。

支持一次申请当个、多个动态权限；

支持拒绝后弹窗询问再次请求及提示语自定义；

[更新日志]( https://github.com/CraftsmanHyj/SimplePermission/blob/master/docs/UpdateLog.md )

[下载Demo查看效果](https://github.com/CraftsmanHyj/SimplePermission/raw/master/docs/Demo.apk)

![](https://github.com/CraftsmanHyj/SimplePermission/blob/master/docs/DemoQR.png)

# 添加依赖

**Step 1.** 在项目的根目录`build.gradle`文件中加入JitPack仓库

```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

**Step 2.** 在当前APP`Demo/build.gradle`文件中加入库的依赖

version：[![](https://jitpack.io/v/CraftsmanHyj/SimplePermission.svg)](https://jitpack.io/#CraftsmanHyj/SimplePermission)

```groovy
dependencies {
    implementation 'com.github.CraftsmanHyj:SimplePermission:version'
}
```



# 使用示例

**Step 1.** 定义申请权限枚举类

```java
public enum PermsEnm implements IPermissionInfo {
    CAMER(Manifest.permission.CAMERA),
    LOCATION_CONTACTS(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS);

    private int code;
    private String[] perms;

    PermsEnm(String... perms) {
        this.code = PermConstant.getReqeustCode();
        this.perms = perms;
    }

    @Override
    public int getRequestCode() {
        return code;
    }

    @Override
    public String[] getPermissions() {
        return perms;
    }
}
```



**Step 2.** 注册回调

```java
@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    //用户的允许、拒绝的统一回调
    PermissionManager.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
}
```



**Step 3.** 拒绝且不再提示逻辑处理

当执行拒绝且不再提示的逻辑之后，会弹出再次询问，跳转到设置界面去设置权限的逻辑；结果接收处理

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    //从应用权限设置页面返回，可以从这里获取到设置的结果
    PermissionManager.onActivityResult(this, requestCode);
}
```



**Step 4.** 执行申请逻辑

**Activity**中使用方法：

```java
PermissionManager.requestPermissions(this, new SimplePermissionCallback<AppCompatActivity>(this) {
    @Override
    public void onPermissionGranted(int reqeustCode, List<String> perms) {
        ToastUtils.showToast(PermissionActivity.this, "activity 已经获取了相机权限");
    }

    @Override
    public void onPermissionDenied(int reqeustCode, List<String> perms) {
        ToastUtils.showToast(PermissionActivity.this, "activity 拒绝使用相机");
    }

    @Override
    public String getPermissionSetMsg(int reqeustCode) {
        return "activity 扫码需要使用相机权限";
    }
}, PermsEnm.CAMER);
```

**Fragment**中使用方法

```java
PermissionManager.requestPermissions(getActivity(), new SimplePermissionCallback<Fragment>(this) {
    @Override
    public void onPermissionGranted(int reqeustCode, List<String> perms) {
        ToastUtils.showToast(getContext(), "frame fragment 已经获取了相机权限");
    }

    @Override
    public void onPermissionDenied(int reqeustCode, List<String> perms) {
        ToastUtils.showToast(getContext(), "frame fragment 拒绝使用相机");
    }

    @Override
    public String getPermissionSetMsg(int reqeustCode) {
        return "frame fragment 扫码需要使用相机权限";
    }
}, PermsEnm.CAMER);
```

