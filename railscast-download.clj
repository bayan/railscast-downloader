(require 'clojure.xml)

(let [log-agent (agent nil)]
  (defn log
    [& messages]
    (send log-agent (fn [_] (apply println messages)))
    (await log-agent)))

(defn get-uris
  [uri format]
  (let [xml (-> (slurp uri)
                (.getBytes "UTF-8")
                java.io.ByteArrayInputStream.
                clojure.xml/parse
                xml-seq)]
    (map #(-> % (get-in [:attrs :url]) (clojure.string/replace #"mp4$" format))
         (filter #(= :enclosure (:tag %)) xml))))

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
  (doall (pmap download (get-uris rss format)))
  (shutdown-agents))
