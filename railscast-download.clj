(require 'clojure.xml)

(let [log-agent (agent nil)]
  (defn log
    [& messages]
    (send log-agent (fn [_] (apply println messages)))
    (await log-agent)))

(defn extract-uris
  [uri format]
  (let [xml  (xml-seq (clojure.xml/parse uri))
        tags (filter #(= :enclosure (:tag %)) xml)]
    (map #(clojure.string/replace (get-in % [:attrs :url]) #"mp4$" format) tags)))

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
  (doall (pmap download (extract-uris rss format)))
  (shutdown-agents))
