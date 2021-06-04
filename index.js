import { NativeModules, NativeEventEmitter } from 'react-native';

const SharetraceModule = NativeModules.SharetraceModule;
const eventEmitter = new NativeEventEmitter(SharetraceModule);
const eventListeners = {};

export default class Sharetrace {

    /**
     * 获取安装参数，默认请求超时为10秒
     * @param {Function} callback = (result）=> {} 
     */
    static getInstallTrace (callback) {
        SharetraceModule.getInstallTrace (
            result => {
                callback(result)
            }
        )
    }

    /**
     * 获取安装参数，可以自定义请求超时，单位：秒
     * @param {int} timeoutSecond 自定义请求超时, 单位：秒
     * @param {Function} callback = (result）=> {} 
     */
    static getInstallTraceWithTimeout (timeoutSecond, callback) {
        SharetraceModule.getInstallTraceWithTimeout(timeoutSecond, result => {
            callback(result)
          }
        )
      }

    /**
     * 增加universal link或scheme一键调起参数回调的监听Listener
     * @param {Function} callback = (result）=> {} 
     */
    static addWakeUpListener (callback) {
        SharetraceModule.getWakeUp(
            result => {
                callback(result)
            }
        );

        eventListeners[callback] = eventEmitter.addListener(
            "SharetraceWakeupEvent",
            result => {
                callback(result)
            }
        )
    }

    /**
     * 移除监听Listener
     * @param {Function} callback = (Object) => { }
     */
    static removeWakeUpListener (callback) {
        let eventListener = eventListeners[callback];
        if (!eventListener) {
            return
        }
        eventListener.remove();
        eventListeners[callback] = null;
    }
}
