# railscast-downloader

A tool to download all railscasts videos - including pro and revised

Files that already exist will not be downloaded again

## Usage

Download [the precompiled jar file](http://github.com/downloads/bayan/railscast-downloader/railscast-downloader.jar).

Follow the instructions below depending on which format(s) you wish to download.


### Downloading m4v, webm and ogv formats

Downloading formats other than mp4 requires scraping the railscasts website.

Log into [railscasts.com](http://railscasts.com) using your [github](http://github.com) account.

Copy the value for the "token" cookie.

Start downloading by running the following command:

java -jar railscast-downloader.jar -type _media-format_ -token _your-token_


### Downloading mp4 videos

It is far more efficient to use the rss feed to synchronize the mp4 videos.

Log into [railscasts.com](http://railscasts.com).

Click the [Manage Subscription](https://railscasts.com/subscriptions/current) link.

Copy the "RSS Feed" link.

Start downloading by running the following command:

java -jar railscast-downloader.jar -rss _your-rss-link_
