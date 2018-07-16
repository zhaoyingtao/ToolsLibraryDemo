# ToolsLibraryDemo [ ![Download](https://api.bintray.com/packages/zhaoyingtao/maven/tools_library/images/download.svg) ](https://bintray.com/zhaoyingtao/maven/tools_library/_latestVersion)
## Description
这是一个开发工具类，包含了基本的开发公共utils类  
## Adding to project  
在app的build.gradle中添加  
`compile 'com.bintray.library:tools_library:1.0.1'`  
在application中添加  
```
//初始化崩溃日志收集xxx-path为错误日志的存储路径
CrashHandler.getInstance().init(this, "xxx-path");
ToolsConstant.applicationContext = this;
```
## Project function  
 类  | 功能  
 ---- | ----- 
 BitMapUtils  | BitMap的工具类  
 ImageUtil  | 图片加载工具类  
 LogUtil  | 打印日志 
 MD5Encrypt  | MD5 加密 
 MeasureUtils  | 关于手机宽高状态栏等、listview、gridview重新计算高度等 
 PermissionsUtils  | 权限动态申请，主要是参考，因为每个公司要求不一样 
 SystemUtil  | 手机系统信息获取工具
 ToastUtils  | Toast的公共类
 UnzipUtil  | Android Zip压缩解压缩  
 WheelsUtils  | 滚轮选择器-主要做参考，产品设计样式不一样
 CrashHandler | 崩溃日志收集类  
 
 ## Attention point  
 依赖库引入了其他第三方依赖库(可能会存在引入冲突问题)
 ```
  compile 'com.contrarywind:Android-PickerView:3.2.7'
   //毛玻璃效果实现(Glide)圆角图片等等---自带glide
  compile 'jp.wasabeef:glide-transformations:3.3.0'
 ```
