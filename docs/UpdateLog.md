# 更新日志

## 5_1.0.5	2022-07-28

1. 优化特殊权限的判断逻辑
2. 解决新版存储权限在Android 6上崩溃的问题

## 4_1.0.4	2022-07-13

1. sdk更新为kotlin实现
2. 增加了蓝牙、安装包、悬浮窗、系统设置、忽扰权限、电池优化的权限申请兼容处理
3. 权限申请的方式修改为`registerForActivityResult`的方式

## 3_1.0.3	2020-03-03

1. 增加GPS定位检测功能
   ```PermissionManager.openGPSLocation()```
2. 小米、华为、oppo、vivo、魅族手机支持直接跳转到权限授权界面
3. 优化已知bug

## 2_1.0.2	2019-11-09

1. PermissionCallback回调方法修改为IPermissionCallback

2. PermissionCallbackImpl方法修改为SimplePermissionCallback

3. 添加判断是否有权限的方法：hasPermissions(@NonNull Activity activity, IPermissionInfo permInfo)方法

4. 添加请求权限方法：requestPermissions(@NonNull Activity activity, @NonNull IPermissionCallback callback, int requestCode, String... perms)

5. 添加onActivityResult(Activity activity, int requestCode)回调方法，
   在去应用设置界面去设置后，返回后回调此方法，可以处理设置结果

   ```java
   Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
   Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
   intent.setData(uri);
   Activity.startActivityForResult(intent, REQEUSTCODE);
   ```

   详情看DEMO

## 1_1.0.1	2019-09-21

+ SimplePermission立项