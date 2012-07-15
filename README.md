# railscast-downloader

A tool to download all railscasts videos - including pro and revised

## Usage

Log into railscasts.com using. You will need to copy the value for the "token" cookie and store it in a file named: "token".

Download [the precompiled jar file](http://github.com/downloads/bayan/railscast-downloader/railscast-downloader.jar).

Start downloading by running the following command:

    java -jar railscast-downloader.jar

By default, mp4 files will be downloaded. You can override this by passing in an alternative format:

    java -jar railscast-downloader.jar ogv

Currently available formats on railscasts.com are: mp4, m4v, webm and ogv
