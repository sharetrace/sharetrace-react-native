# sharetrace-react-native

# React-Native接入

`sharetrace-react-native` 是 sharetrace 官方开发的 React Native 插件，使用该插件可以方便快速地集成 sharetrace 相关功能。


请先从[ShareTrace控制台](https://www.sharetrace.com/)获取`AppKey`。

### 一、安装

通过命令行安装，切换到项目根目录执行：

```cmd
npm install sharetrace-react-native --save
```

**React Native < 0.6**
暂不支持，建议升级React Native版本后再接入Sharetrace。

**React Native >= 0.6**
如果当前项目所使用的React Native的版本大于0.6，支持[Autolinking](https://github.com/react-native-community/cli/blob/master/docs/autolinking.md), 即在执行`npm install sharetrace-react-native --save`后，项目会自动link 原生模块，无需手动执行link命令。

### 二、配置

#### Android配置
找到项目的`AndroidManifest.xml`文件，在`<application>...</application>`中增加以下配置

```xml
<meta-data
        android:name="com.sharetrace.APP_KEY"
        android:value="SHARETRACE_APPKEY"/>
```

> **请将 SHARETRACE_APPKEY 替换成 sharetrace 为应用分配的 appkey**

#### iOS配置
在Info.plist中增加以下配置

```xml
	<key>com.sharetrace.APP_KEY</key>
	<string> SHARETRACE_APPKEY </string>
```
> **请将 SHARETRACE_APPKEY 替换成 sharetrace 为应用分配的 appkey**

### 三、获取安装携带的参数

在需要获取安装参数的位置，导入插件：

```javascript
import SharetraceModule from 'sharetrace-react-native';
```

获取安装参数：

```javascript
SharetraceModule.getInstallTrace((map) => {
  if (map) {
    Alert.alert('InstallTrace', JSON.stringify(map));
  } 
});
```

### 四、一键调起

Sharetrace支持通过标准的Scheme和Universal Links(iOS>=9)，接入Sharetrace SDK后，在各种浏览器，包括微信，微博等内置浏览器一键调起app，并传递网页配置等自定义动态参数。配置只需简单几个步骤即可，如下：

#### 4.1 开启一键调起功能
登录Sharetrace的管理后台，找到iOS配置，开启相关功能和填入配置
![5_apple_config_on.png](https://res.sharetrace.com/img/5_apple_config_on.png)

其中Team Id可以在[Apple开发者](https://developer.apple.com/account/#/membership/)后台查看

#### 4.2 开启Associated Domains服务

##### 方法一（推荐）：Xcode一键开启

（这里以Xcode 12为例，其他Xcode版本类似)

![5_1_domains.png](https://res.sharetrace.com/img/5_1_domains.png)

在如下图所示位置填入Sharetrace后台提供的applinks

![5_applinks_value.png](https://res.sharetrace.com/img/5_applinks_value.png)


##### 方法二：通过Apple开发者管理后台手动开启

登录到[Apple管理后台](https://developer.apple.com/account),在Identifiers找到所需开启到App ID

![5_apple_dev_config.png](https://res.sharetrace.com/img/5_apple_dev_config.png)


#### 4.3 Scheme配置

找到项目Info配置，填入后台分配的Scheme, 如下图:

![ios_scheme_value.png](https://res.sharetrace.com/img/ios_scheme_value.png)

#### 4.4 代码配置

##### iOS 配置
找到AppDelegate文件，参考以下配置

**Objective-C**
``` objc
#import <sharetrace-react-native/SharetraceModule.h>

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
  [SharetraceModule start];
#ifdef FB_SONARKIT_ENABLED
  InitializeFlipper(application);
#endif
...
  return YES;
}

// Universal Link
- (BOOL)application:(UIApplication *)application continueUserActivity:(NSUserActivity *)userActivity restorationHandler:(void (^)(NSArray<id<UIUserActivityRestoring>> * _Nullable))restorationHandler {
  if ([SharetraceModule handleUniversalLink:userActivity]) {
    return YES;
  }

  //其他代码
  return YES;
}

//iOS9以下 Scheme
- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication annotation:(id)annotation {
  if ([SharetraceModule handleSchemeLinkURL:url]) {
      return YES;
  }
    
  //其他代码
  return YES;
}

//iOS9以上 Scheme
- (BOOL)application:(UIApplication *)app openURL:(NSURL *)url options:(nonnull NSDictionary *)options {
  if ([SharetraceModule handleSchemeLinkURL:url]) {
      return YES;
  }
    
  //其他代码
  return YES;
}

@end
```

#### 4.5 获取一键调起参数
``` javascript
import React, {Component} from 'react';
import {StyleSheet, Text, View, Button} from 'react-native';
import SharetraceModule from 'sharetrace-react-native';

export default class App extends Component<{}> {
  state = {
    status: 'starting',
    message: '--',
  };
  componentDidMount() {
    this.receiveWakeupListener = (map) => {
      if (map) {
        this.setState({
          status: 'wakeup native callback received',
          message: JSON.stringify(map),
        });
      }
    };
    SharetraceModule.addWakeUpListener(this.receiveWakeupListener);
  }

  componentWillUnMount() {
    SharetraceModule.removeWakeUpListener(this.receiveWakeupListener);
  }

  getInstallTrace() {
    SharetraceModule.getInstallTrace((map) => {
      if (map) {
        this.setState({
          status: 'getInstallTrace native callback received',
          message: JSON.stringify(map),
        });
      } else {
        this.setState({
          status: 'getInstallTrace native callback received',
          message: 'map is null',
        });
      }
    });
  }

  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>Sharetrace Example</Text>
        <Text style={styles.instructions}>STATUS: {this.state.status}</Text>
        <Text style={styles.welcome}>Result</Text>
        <Text style={styles.instructions}>{this.state.message}</Text>
        <Button
          title="getInstallTrace"
          onPress={() => this.getInstallTrace()}
        />
      </View>
    );
  }
}
```

### 五、配置安装方式
SDK 集成完成后，按照`sharetrace`控制台接入流程完成后续的配置。

