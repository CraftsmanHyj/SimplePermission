# 更新日志



## 1.0.2

+ PermissionCallback回调方法修改为IPermissionCallback

+ PermissionCallbackImpl方法修改为SimplePermissionCallback

+ 添加判断是否有权限的方法：hasPermissions(@NonNull Activity activity, IPermissionInfo permInfo)方法

+ 添加请求权限方法：requestPermissions(@NonNull Activity activity, @NonNull IPermissionCallback callback, int requestCode, String... perms)

+ 添加onActivityResult(Activity activity, int requestCode)回调方法，
  在去应用设置界面去设置后，返回后回调此方法，可以处理设置结果

  ```java
  Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
  Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
  intent.setData(uri);
  Activity.startActivityForResult(intent, REQEUSTCODE);
  ```

+ 详情看DEMO



## 1.0.1

+ SimplePermission立项