(ns clj-try.core-test
  (:require [clojure.test :refer :all]
            [clj-try.core :refer :all]))

(defn check-exception
  [{ ex :error } msg]
  (is (instance? NullPointerException ex))
  (is (= (.getMessage ex) msg)))

; ** Try macro tests **

(deftest try-macro-pass-with-func
  (testing "Testing try> macro with function. Pass case."
    (let [result (try> (str "12345"))]
      (is (= result { :value "12345" })))))

(deftest try-macro-pass-with-expr
  (testing "Testing try> macro with expression. Pass case."
    (let [result (try> "12345")]
      (is (= result { :value "12345" })))))

(deftest try-macro-exception
  (testing "Testing try> macro. Exception case."
    (let [result (try> (throw (NullPointerException. "Test Error")))]
      (check-exception result "Test Error"))))

; ** Try first macro tests **

(deftest bind-error-first-pass
  (testing "Testing bind-error->. Pass case."
    (let [result (bind-error-> (str "aaa") { :value "bbb"} )]
      (is (= result { :value "bbbaaa" })))))

(deftest bind-error-first-error
  (testing "Testing bind-error->. Error case."
    (let [result (bind-error-> (str "aaa") { :error "Test Error"} )]
      (is (= result { :error "Test Error" })))))

(deftest bind-error-first-exception
  (testing "Testing bind-error->. Exception case."
    (let [result (bind-error->
                  (str "aaa")
                  { :error (NullPointerException. "Test Error")} )]
      (check-exception result "Test Error"))))

(deftest try-first-macro-pass
  (testing "Testing try->. Pass case."
    (let [result (try->
                  (str "ccc")
                  (str "bbb")
                  (str "aaa"))]
      (is (= result { :value "cccbbbaaa" })))))

(deftest try-first-macro-exception
  (testing "Testing try->. Exception case."
    (let [result (try->
                  (str "ccc")
                  (throw (NullPointerException. "Test Error"))
                  (str "aaa"))]
      (check-exception result "Test Error"))))

; ** Try last macro tests **

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

(deftest try-last-macro-pass
  (testing "Testing try->>. Pass case."
    (let [result (try->>
                  (str "ccc")
                  (str "bbb")
                  (str "aaa"))]
      (is (= result { :value "aaabbbccc" })))))

(deftest try-last-macro-exception
  (testing "Testing try->>. Exception case."
    (let [result (try->>
                  (str "ccc")
                  (throw (NullPointerException. "Test Error"))
                  (str "aaa"))]
      (check-exception result "Test Error"))))
