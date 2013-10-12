The fastest tool to download ALL [railscasts.com](http://railscasts.com) videos in any format.

Includes pro and revised videos.

Skips existing files.

## Prerequisites

This script requires the ability to run a [Clojure](http://clojure.org/downloads) file, which in turn depends on the Java run time.

### Running a [Clojure](http://clojure.org/downloads) clj file

Download the latest [Clojure](http://clojure.org/downloads) jar file.

Alias _clj_ to _java -cp clojure-1.5.1.jar clojure.main_

Run the clj file like this:

    clj my-script.clj

## Usage

Log into [railscasts.com](http://railscasts.com).

Click the [Manage Subscription](https://railscasts.com/subscriptions/current) link.

Copy the "RSS Feed" link.

Run the railscast-download.clj script from the command line:

    clj railscast-download.clj -rss your-rss-link -type media-format

Where _media-format_ is one of: mp4, m4v, webm or ogv

The default media format is mp4 - the -type argument can be left out in this case.
