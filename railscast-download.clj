(require 'clojure.xml)

(def log
  (let [log-agent (agent nil)]
    (fn [& messages]
      (send log-agent (fn [_] (apply println messages)))
      (await log-agent))))

(defn media-links-from-rss-feed [uri file-type]
  (let [xml (-> uri
                slurp
                (.getBytes "UTF-8")
                java.io.ByteArrayInputStream.
                clojure.xml/parse
                xml-seq)]
    (map #(-> % (get-in [:attrs :url]) (clojure.string/replace #"mp4$" file-type))
         (filter #(= :enclosure (:tag %)) xml))))

(defn download-media-file
  [uri]
  (let [filename (last (clojure.string/split uri #"/"))
        target (java.io.File. filename)]
    (if (.exists target)
      (log filename "already exists")
      (do
        (clojure.java.io/copy (clojure.java.io/input-stream uri) target)
        (log filename "downloaded successfully")))))

(let [arg-map (apply hash-map *command-line-args*)
      rss-uri (.replace (str (arg-map "-rss")) "\"" "")
      file-type (or (arg-map "-type") "mp4")]
  (doall (pmap download-media-file
               (media-links-from-rss-feed rss-uri file-type)))
  (shutdown-agents))
