(ns leonardo.bot
  (:require [irclj.core :as ircb]
            [irclj.events :as irce]
            [irclj.connection :as conn]
            [clojure.string :as string]
            [leonardo.message-scorer :as s]
            [leonardo.message-classifier :as c]
            [leonardo.users :as u]))

(def users-ref (ref (u/init-users)))

(defn notice
  [irc target msg]
  (conn/write-irc-line irc "NOTICE" target (conn/end msg)))

(defn notify-maybe
  [users irc m reasons score]
  (let [nick (:nick m)
        verbose (u/flag-enabled? users nick :verbose)
        vdedo (u/flag-enabled? users nick :vdedo)
        do-log (if (>= score 0) (and verbose (not vdedo)) verbose)
        reason-strings (for [reason reasons] (reason c/classifier-descriptions))]
    (when do-log
      (notice irc nick (str (string/join ", " reason-strings) " (" score " points)")))
    users))

(defn score-message
  [users irc m]
  (let [message (:text m)
        nick (:nick m)
        score (s/score-sentence message)
        reasons (c/classify-sentence message)]
    (-> users
        (u/incr-points nick score)
        (u/incr-reasons nick reasons)
        (notify-maybe irc m reasons score))))

(defn points-command
  [irc m args]
  (let [nick (or args (:nick m))
        points (u/get-points @users-ref nick)
        message (str nick " has " points " points.")]
    (notice irc (:nick m) message)))

(defn verbose-command
  [irc m args]
  (let [nick (:nick m)]
    (dosync
     (alter users-ref u/toggle-flag nick :verbose)
     (alter users-ref u/disable-flag nick :vdedo))
    (if (u/flag-enabled? @users-ref nick :verbose)
      (notice irc nick "Will notice you of every point change.")
      (notice irc nick "Point change notices disabled."))))

(defn vdeductions-command
  [irc m args]
  (let [nick (:nick m)]
    (dosync
     (alter users-ref u/enable-flag nick :verbose)
     (alter users-ref u/toggle-flag nick :vdedo))
    (if (u/flag-enabled? @users-ref nick :vdedo)
      (notice irc nick "Will notice you only of negative point changes.")
      (notice irc nick "Will notice you of every point change."))))

(defn vlog-command
  [irc m args]
  (notice irc (:nick m) ".vlog mode is now always enabled."))

(defn rank
  [points]
  (cond
   (= points 1337) "Clueful 3l33t"
   (>= points  1000) "Clueful Elite"
   (>= points   500) "Super Clueful"
   (>= points   200) "Extremely Clueful"
   (>= points    50) "Very Clueful"
   (>= points    10) "Clueful"
   (>= points   -10) "Neutral"
   (>= points  -500) "Needs Work"
   (>= points -1000) "Not Clueful"
   (>= points -1500) "Lamer"
   :else "Idiot"))

(defn whois-command
  [irc m args]
  (let [who args
        points (u/get-points @users-ref who)
        reasons (:reasons (@users-ref who))
        rank (rank points)]
    (notice irc (:nick m) (str who " has " points " points and holds the rank of " rank "."))
    (notice irc (:nick m) (str who "'s stats: " reasons))))

(def commands
  {:ping (fn [irc m args] (ircb/reply irc m "Pong."))
   :points points-command
   :verbose verbose-command
   :vdeductions vdeductions-command
   :vlog vlog-command
   :whois whois-command
   :whoami (fn [irc m args] (whois-command irc m (:nick m)))})

(defn handle-command
  [irc m]
  (let [parts (string/split (:text m) #" ")
        command (first parts)
        args (rest parts)]
    (when (= \@ (get command 0))
      (let [command-name (subs command 1)
            command-keyword (keyword command-name)
            function (command-keyword commands)]
        (when function
          (function irc m args))))))

(defn privmsg
  [irc m]
  (let [message (:text m)
        nick (:nick m)
        score (s/score-sentence message)
        reasons (c/classify-sentence message)]
    (dosync (alter users-ref u/add-user nick))
    (if (= \@ (get message 0))
      (handle-command irc m)
      (dosync (alter users-ref score-message irc m)))))

(defn make-bot
  []
  (ircb/connect "irc.cluenet.org" 6667
                "Leonardo"
                :real-name "Leonardo da Vinci"
                :callbacks {:privmsg privmsg
                            :raw-log irce/stdout-callback}))

(defn start-bot
  []
  (let [irc (make-bot)]
    (ircb/join irc "#leonardo")))