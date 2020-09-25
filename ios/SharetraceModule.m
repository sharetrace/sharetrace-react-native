#import "SharetraceModule.h"
#import <SharetraceSDK/SharetraceSDK.h>

@implementation SharetraceModule

static NSString * const key_code = @"code";
static NSString * const key_msg = @"msg";
static NSString * const key_data = @"data";
static NSString * const key_paramsData = @"paramsData";
static NSString * const key_resumePage = @"resumePage";

RCT_EXPORT_MODULE()

+ (id)allocWithZone:(NSZone *)zone {
    static SharetraceModule *sharedInstance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [super allocWithZone:zone];
        [Sharetrace start];
    });
    return sharedInstance;
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

        NSDictionary* dict = [SharetraceModule parseToResultDict:200 :@"Success" :appData.paramsData :appData.resumePage];
        NSArray *params = @[dict];
        callback(params);

    } :^(NSInteger code, NSString * _Nonnull msg) {
        NSDictionary* dict = [SharetraceModule parseToResultDict:code :msg :@"" :@""];
        NSArray *params = @[dict];
        callback(params);
    }];
}

+ (NSDictionary*)parseToResultDict:(NSInteger)code :(NSString*)msg :(NSString*)paramsData :(NSString*)resumePage {
    NSMutableDictionary* dict = [[NSMutableDictionary alloc] init];
    dict[key_code] = [NSNumber numberWithInteger:code];
    dict[key_msg] = msg;
    
    NSMutableDictionary* data = [[NSMutableDictionary alloc] init];
    data[key_paramsData] = paramsData;
    data[key_resumePage] = resumePage;
    dict[key_data] = data;
    return dict;
}

@end
