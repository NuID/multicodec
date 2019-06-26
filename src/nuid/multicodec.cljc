(ns nuid.multicodec
  (:require
   [multiformats.varint :as varint]
   [multiformats.codec :as codec]
   [nuid.bytes :as bytes]))

(def key->code (comp varint/encode codec/key->code))

(defmulti prefixed :multiformats.codec/key)
(defmethod prefixed :cbor
  [{:keys [multiformats.codec/key
           multiformats.codec/raw]}]
  (bytes/concat (key->code key) raw))

(defn unprefixed
  [bytes]
  (with-open [out (java.io.ByteArrayOutputStream.)]
    (let [[_ prefix-length] (varint/read-bytes bytes 0)]
      (.write out bytes prefix-length (- (count bytes) prefix-length))
      (.toByteArray out))))
