AlexTube
========

## Problem

My son watches a variety of content on YouTube, but it's hard to lock down what he has access to.
We have a few channels we trust, but if he clicks on related videos, he can wind up viewing content
we don't want him to see.

Unfortunately, it does not appear that YouTube has a "whitelist" ability for channels. 

## Solution

This project builds an Android application that is intended to whitelist YouTube channels.  It was
built to side-load on a Kindle Fire HDX, but it *should* work on any Android device with an OS 
version >= 4.2.

## Layout

The UI has three sections:

1. The top bar shows icons representing the channels I'm whitelisting.
2. The right hand column shows icons for the videos in the selected channel.
3. The main window is the YouTube player.

## Functionality

When the application loads, it fetches the channels.  When a channel is clicked on, the video list
is updated.  When a video is clicked on, the video is changed.

## Limitations

* This solution uses the embeded YouTube API inside a WebView container, not the native YouTube 
player.  This was done because the native YouTube player requires the Google Services infrastructure,
which proved challenging to side-load on my Kindle.
* Related videos are still shown at the end of a video (and sometimes in the middle).  These links 
don't work.  I haven't spent much time getting them to work, but there seemed to be cross origin
issues.  If they could work, I'd still want to validate the videos are in my whitelisted channels.
* On the Kindle, the closed captioning is defaulting to on.  I've added code to address this, but 
I'm not sure if it works.  This behavior doesn't happen in the emulator.

## API KEY

To avoid putting my API Key in Github, the Google API Key is read from app/src/main/res/raw/api.key.
This file is not in source control and will need to be populated before a build will work.

https://console.developers.google.com/apis?project=alextube

## Channel Ids

The list of whitelisted channel ids is defined in MainActivity.  If you are looking for a channel
id, but only have a user name, you can use the video list api:

    https://developers.google.com/youtube/v3/docs/videos/list

to query data about a video owned by the user.  This data will include the associated channel id.
Once you have a video id, put it into the api explorer as the "id" parameter and set "part"
 to "snippet".

## Considered but discarded

* Add settings to configure the channels and the api key.  Both good ideas, but not worth the time.
* Break Activity into fragments.  This eases reuse, but adds complexity.  Since I don't see 
reusing components, I decided against doing this.
* Dealing with orientation switching.  I didn't want to figure this out, so it is fixed at
landscape.

## TODO

* Test
* Handle sleep/wake, back button, etc.
* Loading indicators
* Explore "related" videos filtered by channel id

    
