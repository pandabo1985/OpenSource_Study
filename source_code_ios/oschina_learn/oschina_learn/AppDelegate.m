//
//  AppDelegate.m
//  oschina_learn
//
//  Created by panda on 14-7-10.
//  Copyright (c) 2014年 com.afayear. All rights reserved.
//

#import "AppDelegate.h"

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    
    self.newsBase = [[NewsBase alloc] initWithNibName:@"NewsBase" bundle:nil];
    UINavigationController *newsNav = [[UINavigationController alloc] initWithRootViewController:self.newsBase];
    
    self.tweetBase = [[TweetBase alloc] initWithNibName:@"TweetBase" bundle:nil];
    UINavigationController *tweetNav = [[UINavigationController alloc] initWithRootViewController:self.tweetBase];
    
    
    
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    self.window.backgroundColor = [UIColor whiteColor];
    [self.window makeKeyAndVisible];
    return YES;
}



@end
