(ns clj-try.core-test
  (:require [clojure.test :refer :all]
            [clj-try.core :refer :all]))

(defn check-exception
  [{ ex :error } msg]
  (is (instance? NullPointerException ex))
  (is (= (.getMessage ex) msg)))

(deftest try-macro-pass
  (testing "Testing try> macro. Pass case."
    (let [result (try> (str "12345"))]
      (is (= result { :value "12345" })))))

(deftest try-macro-exception
  (testing "Testing try> macro. Exception case."
    (let [result (try> (throw (NullPointerException. "Test Error")))]
      (check-exception result "Test Error"))))

(deftest bind-error-last-pass
  (testing "Testing bind-error->>. Pass case."
    (let [result (bind-error->> (str "aaa") { :value "bbb"} )]
      (is (= result { :value "aaabbb" })))))

(deftest bind-error-last-error
  (testing "Testing bind-error->>. Error case."
    (let [result (bind-error->> (str "aaa") { :error "Test Error"} )]
      (is (= result { :error "Test Error" })))))

(deftest bind-error-last-exception
  (testing "Testing bind-error->>. Exception case."
    (let [result (bind-error->>
                  (str "aaa")
                  { :error (NullPointerException. "Test Error")} )]
      (check-exception result "Test Error"))))
