(defproject railscast-downloader "1.1.0"
  :description "Login, scrape and download videos from Railscasts"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [clj-http "0.4.3"]
                 [enlive "1.0.1"]]
  :plugins [[lein-swank "1.4.4"]]
  :main railscast-downloader.core)
