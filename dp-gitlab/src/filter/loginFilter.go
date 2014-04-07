package filter

import (
	"constant"
	"github.com/NoahShen/render"
	"github.com/go-martini/martini"
	"github.com/martini-contrib/sessions"
	"log"
	"net/http"
	"strings"
)

func LoginFilter() martini.Handler {
	return func(res http.ResponseWriter, r *http.Request, c martini.Context, l *log.Logger, session sessions.Session, render render.Render) {
		if "POST" == r.Method && strings.HasPrefix(r.RequestURI, "/action/login") {
			c.Next()
			return
		}
		userInfo := session.Get(constant.UserInfoKey)
		if userInfo != nil {
			c.Next()
			return
		}
		l.Printf("User has not logged in, redirect to login page\n")
		render.HTML(200, "index", nil)
	}
}
