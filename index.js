import { NativeModules, NativeEventEmitter } from 'react-native';

const SharetraceModule = NativeModules.SharetraceModule;
const eventEmitter = new NativeEventEmitter(SharetraceModule);
const eventListeners = {};

export default class Sharetrace {

    static getInstallTrace (callback) {
        SharetraceModule.getInstallTrace (
            result => {
                callback(result)
            }
        )
    }

    /**
     * 增加universal link或scheme一键调起参数回调的监听Listener
     * @param {Function} callback = (result）=> {} 数据为空时返回null
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
