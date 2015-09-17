(ns ^:figwheel-always gol_web.core
  (:require [gol_web.logic :as logic]
            [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

;; define your app data so that it doesn't get over-written on reload


(defonce original-board (atom [[0 1 0 0 0] [1 0 0 1 1] [1 1 0 0 1] [0 1 0 0 0] [1 0 0 0 1]]))
(defonce evolutions (atom []))

(defn update-gameboard
  [raw-val]
  (->> (for [line (clojure.string/split raw-val "\n")]
         (mapv js/parseInt line))
       vec
       (reset! original-board)))

(defn build-gameboard
  [board]
  (for [line board]
    [:div.row
     (for [cell line]
       [:div.col-xs-1.well {:style    {:text-align "center" :margin "3px" :padding "0px"}} cell])]))

(defn begin-evolving
  [num-of-evolutions]
  (reset! evolutions (loop [evol (logic/evolve @original-board)
                            counter 1
                            result []]
                       (println result)
                       (if (= counter (inc num-of-evolutions))
                         result
                         (recur (logic/evolve evol) (inc counter) (conj result {:num counter :val evol}))))))

(defn show-evolutions
  []
  [:div.row (for [evolution @evolutions]
              (do (println evolution)
                  [:div.col-sm-3 {:style {:padding-bottom "15px"}}
                   [:h5 (str "Evolution #" (:num evolution))]
                   (build-gameboard (:val evolution))]))])

(defn main-page []
  [:div
   [:div.jumbotron
    [:h1 "Welcome"
     [:small " to the Game of Life"]]
    [:p "as told by Alex Faber"]
    [:ol
     [:li "Any live cell with fewer than two live neighbours dies (under- population)"]
     [:li "Any live cell with two or three live neighbours lives on to the next generation (survival)"]
     [:li "Any live cell with more than three live neighbours dies (overcrowding)"]
     [:li "Any dead cell with exactly three live neighbours becomes a live cell (reproduction)"]]]
   [:div.row
    [:div.col-sm-2
     [:h5 "Adjust"]
     [:div.form-group
      [:textarea.form-control {:style     {:height "150px" :width "100%"}
                               :on-focus  #(update-gameboard (-> % .-target .-value))
                               :on-change #((update-gameboard (-> % .-target .-value))
                                            (begin-evolving 12))}
       "01000\n10011\n11001\n01000\n10001"]]]
    [:div.col-sm-10
     [:h5 "Original Gameboard"]
     (let [gameboard (build-gameboard @original-board)]
       (if (empty? gameboard)
         "Gameboard Isn't Populated"
         gameboard))]]
   [:hr]
   [:div.row
    [:div.col-xs-12 {:style    {:text-align "center"}
                     :on-click #(begin-evolving 12)}
     [:button.btn.btn-success "Evolve (12 times)"]]]
   [:hr]
   [show-evolutions]])

(reagent/render-component [main-page]
                          (. js/document (getElementById "app")))


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )

