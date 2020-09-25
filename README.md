# sharetrace-react-native

## 一、安装与配置

`$ npm install sharetrace-react-native --save`

请先从[Sharetrace](https://www.sharetrace.com/)获取`AppKey`

### Android配置
找到项目的`AndroidManifest.xml`文件，在`<application>...</application>`中增加以下配置
``` xml
    <meta-data
        android:name="com.sharetrace.APP_KEY"
        android:value="填入从Sharetrace获取到的AppKey"/>
```

### iOS配置
在Info.plist中增加以下配置
``` xml
	<key>com.sharetrace.APP_KEY</key>
	<string>填入从Sharetrace获取到的AppKey</string>
```

## 二、接口使用
```javascript
import SharetraceModule from 'sharetrace-react-native';

SharetraceModule.getInstallTrace((map) => {
  if (map) {
    Alert.alert('InstallTrace', JSON.stringify(map));
  } 
});
```

## 三、导出并测试
通过以上配置后，分别导出Android的apk包和iOS的ipa，登录[Sharetrace](https://www.sharetrace.com/)并根据页面指引完成配置和在线测试。

