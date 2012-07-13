(ns railscast-downloader.core
  (:gen-class)
  (:require [clj-http.client :as client]
            [net.cgrand.enlive-html :as enlive]))

(declare ^:dynamic *token*)

(def root-uri "http://railscasts.com")

(defn get-as-stream
  [uri]
  (client/get uri {:as :stream
                   :cookies {"token" {:path "/" :value *token*}}}))

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

(defn episode-links
  [page]
  (map #(str root-uri (get-in % [:attrs :href]))
       (enlive/select page [:.episode :h2 :a])))

(defn media-link
  [uri media-format]
  (let [page (html-resource uri)
        selector [:ul.downloads :li [:a (enlive/pred #(= media-format (enlive/text %)))]]
        link (enlive/select page selector)]
    (-> link first :attrs :href)))

(defn -main
  [& args]
  (let [media-format (or (first args) "mp4")]
    (binding [*token* (clojure.string/trim (slurp "token"))]
      (doseq [episode-uri (mapcat episode-links (html-pages))]
        (let [uri (media-link episode-uri media-format)
              source (:body (get-as-stream uri))
              target (java.io.File. (last (clojure.string/split uri #"/")))]
          (clojure.java.io/copy source target))))))
