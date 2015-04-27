(ns clj-try.core-test
  (:require [clojure.test :refer :all]
            [clj-try.core :refer :all]))

(defn check-exception
  [{ ex :error } msg]
  (is (instance? NullPointerException ex))
  (is (= (.getMessage ex) msg)))

(deftest try-macro-pass
  (testing "Try macro. Pass case."
    (let [result (try> (str "12345"))]
      (is (= result { :value "12345" })))))

(deftest try-macro-failure
  (testing "Try macro. Exception case."
    (let [result (try> (throw (NullPointerException. "Test Error")))]
      (check-exception result "Test Error"))))
