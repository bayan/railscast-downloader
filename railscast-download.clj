(require 'clojure.xml)

(let [log-agent (agent nil)]
  (defn log
    [& messages]
    (send log-agent (fn [_] (apply println messages)))
    (await log-agent)))

(defn extract-uris
  [uri format]
  (let [uri  (str uri "?ext=" format)
        xml  (xml-seq (clojure.xml/parse uri))
        tags (filter #(= :enclosure (:tag %)) xml)]
    (map #(get-in % [:attrs :url]) tags)))

(defn download
  [uri]
  (let [filename (last (clojure.string/split uri #"/"))
        target (java.io.File. filename)]
    (if (.exists target)
      (log filename "already exists")
      (do
        (clojure.java.io/copy (clojure.java.io/input-stream uri) target)
        (log filename "downloaded successfully")))))

(let [args   (apply hash-map *command-line-args*)
      rss    (clojure.string/replace (get args "-rss" "") "\"" "")
      format (get args "-type" "mp4")]
  (dorun (pmap download (extract-uris rss format)))
  (shutdown-agents))
