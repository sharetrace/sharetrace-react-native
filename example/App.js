/**
 * Sample React Native App
 *
 * adapted from App.js generated by the following command:
 *
 * react-native init example
 *
 * https://github.com/facebook/react-native
 */

import React, {Component} from 'react';
import {StyleSheet, Text, View, Button} from 'react-native';
import SharetraceModule from 'sharetrace-react-native';

export default class App extends Component<{}> {
  state = {
    status: 'starting',
    message: '--',
  };
  componentDidMount() {
    SharetraceModule.init();
    //该方法用于监听app通过univeral link或scheme拉起后获取唤醒参数
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
    
    // 设置自定义请求超时
    // SharetraceModule.getInstallTraceWithTimeout(12, (map) => {
    //   if (map) {
    //     this.setState({
    //       status: 'getInstallTrace native callback received',
    //       message: JSON.stringify(map),
    //     });
    //   } else {
    //     this.setState({
    //       status: 'getInstallTrace native callback received',
    //       message: 'map is null',
    //     });
    //   }
    // });
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

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginLeft: 10,
    marginRight: 10,
    marginBottom: 25,
  },
});
