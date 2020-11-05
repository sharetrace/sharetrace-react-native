#if __has_include(<React/RCTBridgeModule.h>)
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#elif __has_include("RCTBridgeModule.h")
#import "RCTBridgeModule.h"
#import "RCTEventEmitter.h"
#elif __has_include("React/RCTBridgeModule.h")
#import "React/RCTBridgeModule.h"
#import "React/RCTEventEmitter.h"
#endif

#import <Foundation/Foundation.h>
#import <SharetraceSDK/SharetraceSDK.h>

@interface SharetraceModule : RCTEventEmitter <SharetraceDelegate>

+ (id<SharetraceDelegate> _Nonnull)allocWithZone:(NSZone *_Nullable)zone;

/**
 * SharetraceSDK 初始化
 */
+ (void)start;

/**
 * 处理 URI Schemes 逻辑
 * @param url 通过Schemes调起时，系统回调回来的URL
 * @return bool Sharetrace是否成功识别该URL
 */
+ (BOOL)handleSchemeLinkURL:(NSURL * _Nullable)url;

/**
 * 处理 Universal link 逻辑
 * @param userActivity 通过Universal link调起时，包含系统回调回来的URL信息的NSUserActivity
 * @return bool Sharetrace是否成功识别该URL
 */
+ (BOOL)handleUniversalLink:(NSUserActivity * _Nullable)userActivity;

@end
