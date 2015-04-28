(ns clj-try.core-test
  (:require [clojure.test :refer :all]
            [clj-try.core :refer :all]))

(defn check-exception
  [ex-type { ex :error } msg]
  (is (instance? ex-type ex))
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
      (check-exception NullPointerException result "Test Error"))))

; ** Try first macro tests **

(deftest bind-error-first-pass
  (testing "Testing bind-error->. Pass case."
    (let [result (bind-error-> (str "aaa") { :value "bbb" })]
      (is (= result { :value "bbbaaa" })))))

(deftest bind-error-first-error
  (testing "Testing bind-error->. Error case."
    (let [result (bind-error-> (str "aaa") { :error "Test Error" })]
      (is (= result { :error "Test Error" })))))

(deftest bind-error-first-exception
  (testing "Testing bind-error->. Exception case."
    (let [result (bind-error->
                  (str "aaa")
                  { :error (NullPointerException. "Test Error") })]
      (check-exception NullPointerException result "Test Error"))))

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
                  (str (/ 100 0)) ;Throws div by zero exception.
                  (str "aaa"))]
      (check-exception ArithmeticException result "Divide by zero"))))

; ** Try last macro tests **

(deftest bind-error-last-pass
  (testing "Testing bind-error->>. Pass case."
    (let [result (bind-error->> (str "aaa") { :value "bbb" })]
      (is (= result { :value "aaabbb" })))))

(deftest bind-error-last-error
  (testing "Testing bind-error->>. Error case."
    (let [result (bind-error->> (str "aaa") { :error "Test Error" })]
      (is (= result { :error "Test Error" })))))

(deftest bind-error-last-exception
  (testing "Testing bind-error->>. Exception case."
    (let [result (bind-error->>
                  (str "aaa")
                  { :error (NullPointerException. "Test Error") })]
      (check-exception NullPointerException result "Test Error"))))

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
                  (str (/ 100 0)) ;Throws div by zero exception.
                  (str "aaa"))]
      (check-exception ArithmeticException result "Divide by zero"))))

; ** Try as macro tests **

(deftest bind-error-as-pass
  (testing "Testing bind-error-as->. Pass case."
    (let [result (bind-error-as-> (str "aaa" % "ccc") % { :value "bbb" })]
      (is (= result { :value "aaabbbccc" })))))

(deftest bind-error-as-error
  (testing "Testing bind-error-as->. Error case."
    (let [result (bind-error-as-> (str "aaa" % "ccc") % { :error "Test Error" })]
      (is (= result { :error "Test Error" })))))

(deftest bind-error-as-exception
  (testing "Testing bind-error-as->. Exception case."
    (let [result (bind-error-as-> (str "aaa" % "ccc") %
                  { :error (NullPointerException. "Test Error") })]
      (check-exception NullPointerException result "Test Error"))))

(deftest try-as-macro-pass
  (testing "Testing try-as->. Pass case."
    (let [result (try-as-> "aaa" %
                  (str % "bbb")
                  (str "ccc" %)
                  (str % "ddd"))]
      (is (= result { :value "cccaaabbbddd" })))))

(deftest try-as-macro-exception
  (testing "Testing try-as->. Exception case."
    (let [result (try-as-> "ddd" %
                  (str % "ccc")
                  (str (/ 100 0)) ;Throws div by zero exception.
                  (str "aaa" %))]
      (check-exception ArithmeticException result "Divide by zero"))))
