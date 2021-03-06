(ns clj-try.core)

;; Result types.

(defprotocol TryResult
  "A protocol for handling success / failure states."
  (value [this] "Value on success")
  (error [this] "Value on error"))

(defrecord Success [value]
  clojure.lang.IDeref
  TryResult
  (value [this] (:value this))
  (error [this] nil)
  (deref [this] (:value this)))

(defrecord Failure [error]
  clojure.lang.IDeref
  TryResult
  (value [this] nil)
  (error [this] (:error this))
  (deref [this]
    (let [err (:error this)]
      (if (instance? Throwable err)
        (throw err)
        (throw (Exception. (str err)))))))

(defmethod clojure.core/print-method Success [item writer]
  ((get-method print-method clojure.lang.IRecord) item writer))

(defmethod clojure.core/print-method Failure [item writer]
  ((get-method print-method clojure.lang.IRecord) item writer))

;; Try macro definitions.

(defmacro try>
  "Takes an expression and wraps it in a try / catch
  returning the value or exception in a map."
  [fn]
  `(try
     (Success. (do ~fn))
     (catch Exception ex#
       (Failure. ex#))))

(defmacro bind-error->>
  "Bind error last. Determines if to run the expression or return the
  previous result if an error occurred."
  [fn value]
  `(let [{val# :value err# :error :as all#} ~value]
     (if (nil? err#)
       (try> (->> val# ~fn))
       all#)))

(defmacro try->>
  "Try last. Similar to the thread last macro, but wrapping each expression
  in a try / catch and passing the result as the last argument of the following
  expression. If an error occurs, the chain will be short circuited returning
  the first error occurred."
  [val & fns]
  (let [fcns (for [f fns] `(bind-error->> ~f))]
    `(->> (try> ~val)
          ~@fcns)))

(defmacro bind-error->
  "Bind error first. Determines if to run the expression or return the
  previous result if an error occurred."
  [fn value]
  `(let [{val# :value err# :error :as all#} ~value]
     (if (nil? err#)
       (try> (-> val# ~fn))
       all#)))

(defmacro try->
  "Try first. Similar to the thread first macro, but wrapping each expression
  in a try / catch and passing the result as the first argument of the following
  expression. If an error occurs, the chain will be short circuited returning
  the first error occurred."
  [val & fns]
  (let [fcns (for [f fns] `(bind-error-> ~f))]
    `(->> (try> ~val)
          ~@fcns)))

(defmacro bind-error-as->
  "Bind error first. Determines if to run the expression or return the
  previous result if an error occurred."
  [fn name value]
  `(let [{val# :value err# :error :as all#} ~value]
     (if (nil? err#)
       (try> (as-> val# ~name ~fn))
       all#)))

(defmacro try-as->
  "Try as. Similar to the thread as macro, but wrapping each expression
  in a try / catch and passing the result as the specified argument of the following
  expression. If an error occurs, the chain will be short circuited returning
  the first error occurred."
  [val name & fns]
  (let [fcns (for [f fns] `(bind-error-as-> ~f ~name))]
    `(->> (try> ~val)
          ~@fcns)))

;; Try result helpers.

(defn success
  "Returns a new Success item."
  [value]
  (->Success value))

(defn failure
  "Returns a new Failure item."
  [error]
  (->Failure error))

(defn val?
  "Returns true if the result is a success."
  [try-result]
  (instance? Success try-result))

(defn err?
  "Returns true if the result is a failure."
  [try-result]
  (instance? Failure try-result))

(defn err
  "Returns the error if there is a failure, else returns nil."
  [try-result]
  (if (err? try-result) (:error try-result) nil))

(defn val-or
  "If the result is a success, the value is returned,
  else the value passed as the default is returned."
  [try-result default]
  (if (err? try-result)
    default
    (:value try-result)))

(defn val-or-nil
  "If the result is a success, the value is returned, else nil is returned."
  [try-result]
  (val-or try-result nil))
