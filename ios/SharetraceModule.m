#import "SharetraceModule.h"

#if __has_include(<React/RCTBridge.h>)
#import <React/RCTBridge.h>
#import <React/RCTLog.h>
#import <React/RCTEventEmitter.h>
#elif __has_include("RCTBridge.h")
#import "RCTBridge.h"
#import "RCTLog.h"
#import "RCTEventEmitter.h"
#elif __has_include("React/RCTBridge.h")
#import "React/RCTBridge.h"
#import "React/RCTLog.h"
#import "React/RCTEventEmitter.h"
#endif

@interface SharetraceModule ()
@property (nonatomic, strong)NSDictionary *wakeUpTraceDict;
@property (nonatomic, assign)BOOL hasLoad;
@end

@implementation SharetraceModule

static NSString * const key_code = @"code";
static NSString * const key_msg = @"msg";
static NSString * const key_paramsData = @"paramsData";
static NSString * const key_channel = @"channel";

static NSString * const SharetraceWakeupEvent = @"SharetraceWakeupEvent";

RCT_EXPORT_MODULE(SharetraceModule)

+ (id)allocWithZone:(NSZone *)zone {
    static SharetraceModule *sharedInstance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [super allocWithZone:zone];
        sharedInstance.wakeUpTraceDict = [[NSDictionary alloc] init];
    });
    return sharedInstance;
}

+ (void)start {
    [Sharetrace initWithDelegate:[SharetraceModule allocWithZone:nil]];
}

+ (BOOL)handleSchemeLinkURL:(NSURL * _Nullable)url {
    return [Sharetrace handleSchemeLinkURL:url];
}

+ (BOOL)handleUniversalLink:(NSUserActivity * _Nullable)userActivity {
    return [Sharetrace handleUniversalLink:userActivity];
}

RCT_EXPORT_METHOD(getInstallTrace:(RCTResponseSenderBlock)callback)
{
    [Sharetrace getInstallTrace:^(AppData * _Nullable appData) {
        if (appData == nil) {
            NSDictionary* dict = [SharetraceModule parseToResultDict:-1 :@"Extract data fail." :@"" :@""];
            NSArray *params = @[dict];
            callback(params);
            return;
        }

        NSDictionary* dict = [SharetraceModule parseToResultDict:200 :@"Success" :appData.paramsData :appData.channel];
        NSArray *params = @[dict];
        callback(params);

    } :^(NSInteger code, NSString * _Nonnull msg) {
        NSDictionary* dict = [SharetraceModule parseToResultDict:code :msg :@"" :@""];
        NSArray *params = @[dict];
        callback(params);
    }];
}

RCT_EXPORT_METHOD(getWakeUp:(RCTResponseSenderBlock)callback)
{
    if (!self.hasLoad) {
        if (self.wakeUpTraceDict != nil && self.wakeUpTraceDict.count != 0) {
            NSArray *params = @[self.wakeUpTraceDict];
            callback(params);
            self.wakeUpTraceDict = nil;
        } else {
            callback(@[[NSNull null]]);
        }
        self.hasLoad = YES;
    }
}

+ (NSDictionary*)parseToResultDict:(NSInteger)code :(NSString*)msg :(NSString*)paramsData :(NSString*)channel {
    NSMutableDictionary* dict = [[NSMutableDictionary alloc] init];
    dict[key_code] = [NSNumber numberWithInteger:code];
    dict[key_msg] = msg;
    dict[key_paramsData] = paramsData;
    dict[key_channel] = channel;
    return dict;
}

- (void)getWakeUpTrace:(AppData *)appData {
    if (appData == nil) {
        [self sendEventWithName:SharetraceWakeupEvent body:nil];
        return;
    }
    
    NSDictionary* dict = [SharetraceModule parseToResultDict:200 :@"Success" :appData.paramsData :appData.channel];
    if ([self bridge] != nil) {
        [self sendEventWithName:SharetraceWakeupEvent body:dict];
        self.wakeUpTraceDict = nil;
    } else {
        @synchronized(self) {
            self.wakeUpTraceDict = dict;
        }
    }
}

- (NSArray<NSString *> *)supportedEvents {
    return @[SharetraceWakeupEvent];
}

@end
