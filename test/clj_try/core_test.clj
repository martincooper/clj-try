(ns clj-try.core-test
  (:require [clojure.test :refer :all]
            [clj-try.core :refer :all]))

(defn check-exception
  [ex-type { ex :error :as all} msg]
  (is (instance? ex-type ex))
  (is (= (.getMessage ex) msg)))

; ** Try macro tests **

(deftest try-macro-pass-with-func
  (testing "Testing try> macro with function. Pass case."
    (let [result (try> (str "12345"))]
      (is (= result (success "12345"))))))

(deftest try-macro-pass-with-expr
  (testing "Testing try> macro with expression. Pass case."
    (let [result (try> "12345")]
      (is (= result (success "12345"))))))

(deftest try-macro-exception
  (testing "Testing try> macro. Exception case."
    (let [result (try> (throw (NullPointerException. "Test Error")))]
      (check-exception NullPointerException result "Test Error"))))

; ** Try first macro tests **

(deftest bind-error-first-pass
  (testing "Testing bind-error->. Pass case."
    (let [result (bind-error-> (str "aaa") (success "bbb"))]
      (is (= result (success "bbbaaa"))))))

(deftest bind-error-first-error
  (testing "Testing bind-error->. Error case."
    (let [result (bind-error-> (str "aaa") (failure "Test Error"))]
      (is (= result (failure "Test Error"))))))

(deftest bind-error-first-exception
  (testing "Testing bind-error->. Exception case."
    (let [result (bind-error->
                  (str "aaa")
                  (failure (NullPointerException. "Test Error")))]
      (check-exception NullPointerException result "Test Error"))))

(deftest try-first-macro-pass
  (testing "Testing try->. Pass case."
    (let [result (try->
                  (str "ccc")
                  (str "bbb")
                  (str "aaa"))]
      (is (= result (success "cccbbbaaa"))))))

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
    (let [result (bind-error->> (str "aaa") (success "bbb"))]
      (is (= result (success "aaabbb"))))))

(deftest bind-error-last-error
  (testing "Testing bind-error->>. Error case."
    (let [result (bind-error->> (str "aaa") (failure "Test Error"))]
      (is (= result (failure "Test Error"))))))

(deftest bind-error-last-exception
  (testing "Testing bind-error->>. Exception case."
    (let [result (bind-error->>
                  (str "aaa")
                  (failure (NullPointerException. "Test Error")))]
      (check-exception NullPointerException result "Test Error"))))

(deftest try-last-macro-pass
  (testing "Testing try->>. Pass case."
    (let [result (try->>
                  (str "ccc")
                  (str "bbb")
                  (str "aaa"))]
      (is (= result (success "aaabbbccc"))))))

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
    (let [result (bind-error-as-> (str "aaa" % "ccc") % (success "bbb"))]
      (is (= result (success "aaabbbccc"))))))

(deftest bind-error-as-error
  (testing "Testing bind-error-as->. Error case."
    (let [result (bind-error-as-> (str "aaa" % "ccc") % (failure "Test Error"))]
      (is (= result (failure "Test Error" ))))))

(deftest bind-error-as-exception
  (testing "Testing bind-error-as->. Exception case."
    (let [result (bind-error-as-> (str "aaa" % "ccc") %
                                  (failure (NullPointerException. "Test Error")))]
      (check-exception NullPointerException result "Test Error"))))

(deftest try-as-macro-pass
  (testing "Testing try-as->. Pass case."
    (let [result (try-as-> "aaa" %
                  (str % "bbb")
                  (str "ccc" %)
                  (str % "ddd"))]
      (is (= result (success "cccaaabbbddd"))))))

(deftest try-as-macro-exception
  (testing "Testing try-as->. Exception case."
    (let [result (try-as-> "ddd" %
                  (str % "ccc")
                  (str (/ 100 0)) ;Throws div by zero exception.
                  (str "aaa" %))]
      (check-exception ArithmeticException result "Divide by zero"))))

;; Result helper tests.

(deftest new-success
  (testing "Testing new success."
    (is (= (->Success "12345") (success "12345")))))

(deftest new-failure
  (testing "Testing new failure."
    (is (= (->Failure "12345") (failure "12345")))))

(deftest is-val-positive
  (testing "Testing val? on success."
    (is (= (val? (->Success "12345")) true))))

(deftest is-val-negative
  (testing "Testing val? on failure."
    (is (= (val? (->Failure "12345")) false))))

(deftest is-err-positive
  (testing "Testing err? on success."
    (is (= (err? (->Success "12345")) false))))

(deftest is-err-negative
  (testing "Testing err? on failure."
    (is (= (err? (->Failure "12345")) true))))

(deftest is-val-or-positive
  (testing "Testing val-or on success."
    (is (= (val-or (->Success "12345") "9876") "12345"))))

(deftest is-val-or-negative
  (testing "Testing val-or on failure."
    (is (= (val-or (->Failure "12345") "9876") "9876"))))

(deftest is-val-or-nil-positive
  (testing "Testing val-or-nil on success."
    (is (= (val-or-nil (->Success "12345")) "12345"))))

(deftest is-val-or-nil-negative
  (testing "Testing val-or-nil on failure."
    (is (= (val-or-nil (->Failure "12345")) nil))))
