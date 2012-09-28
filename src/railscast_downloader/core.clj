(ns railscast-downloader.core
  (:gen-class)
  (:require [clj-http.client :as client]
            [net.cgrand.enlive-html :as enlive]
            clojure.xml))

(declare ^:dynamic *token*)
(declare ^:dynamic *rss-uri*)
(declare ^:dynamic *file-type*)

(def root-uri "http://railscasts.com")
(def log-agent (agent nil))

(defn log
  [& messages]
  (send log-agent (fn [_] (apply println messages))))

(defn get-as-stream
  [uri]
  (client/get uri {:as :stream
                   :cookies {"token" {:path "/" :value (str *token*)}}}))

(defn html-resource
  [uri]
  (enlive/html-resource (:body (get-as-stream uri))))

(defn html-pages
  ([] (html-pages "/"))
  ([relative-uri]
     (let [page (html-resource (str root-uri relative-uri))
           next-page (first (enlive/select page [:.pagination :a.next_page]))]
       (if next-page
         (lazy-seq (cons page (html-pages (get-in next-page [:attrs :href]))))
         (list page)))))

(defn media-link
  [uri media-format]
  (let [page (html-resource uri)
        selector [:ul.downloads :li [:a (enlive/pred #(= media-format (enlive/text %)))]]
        link (enlive/select page selector)]
    (-> link first :attrs :href)))

(defn episode-links
  [page]
  (map #(media-link (str root-uri (get-in % [:attrs :href])) *file-type*)
       (enlive/select page [:.episode :h2 :a])))

(defn media-links-from-rss-feed []
  (let [xml (-> *rss-uri*
                slurp
                (.getBytes "UTF-8")
                java.io.ByteArrayInputStream.
                clojure.xml/parse
                xml-seq)
        xml (filter #(= :enclosure (:tag %)) xml)]
    (map (comp :url :attrs) xml)))

(defn download-media-file
  [uri]
  (let [filename (last (clojure.string/split uri #"/"))
        target (java.io.File. filename)]
    (if (.exists target)
      (log filename "already exists")
      (do
        (clojure.java.io/copy (:body (get-as-stream uri)) target)
        (log filename "downloaded successfully")))))

(defn download-all []
  (let [uris (if (and *rss-uri* (= *file-type* "mp4"))
               (media-links-from-rss-feed)
               (mapcat episode-links (html-pages)))]
    (doseq [_ (pmap download-media-file uris)])))

(defn -main
  [& args]
  (let [arg-map (apply hash-map args)]
    (binding [*token*     (arg-map "-token")
              *rss-uri*   (.replace (str (arg-map "-rss")) "\"" "")
              *file-type* (or (arg-map "-type") "mp4")]
      (download-all))
    (await log-agent)
    (shutdown-agents)))
