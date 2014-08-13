//
//  AppDelegate.h
//  oschina_learn
//
//  Created by panda on 14-7-10.
//  Copyright (c) 2014年 com.afayear. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NewsBase.h"
#import "TweetBase.h"
#import "ProfileBase.h"
#import "SettingBase.h"

@interface AppDelegate : UIResponder <UIApplicationDelegate>

@property (strong, nonatomic) UIWindow *window;
@property (strong, nonatomic) NewsBase *newsBase;
@property (strong, nonatomic) TweetBase *tweetBase;
@property (strong, nonatomic) ProfileBase *profileBase;
@property (strong, nonatomic) SettingBase *settingBase;

@end
