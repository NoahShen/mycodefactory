package webserver

import (
	"fmt"
	"github.com/go-martini/martini"
	"github.com/martini-contrib/render"
	"github.com/martini-contrib/sessions"
	"time"
)

const (
	sessionKey     = "dpgittoolssession"
	sessionMaxAge  = time.Hour * 12 // 12 hours
	gitLabTokenKey = "gitLabToken"
)

func StartServer() {
	m := martini.Classic()
	m.Use(render.Renderer(render.Options{
		Directory: "../../web/templates",
	}))
	store := sessions.NewCookieStore([]byte("secretDpGitLab"))
	store.Options(sessions.Options{
		MaxAge: int(sessionMaxAge),
	})

	m.Use(sessions.Sessions(sessionKey, store))

	m.Get("/", func(session sessions.Session, r render.Render) {
		tokenKey := session.Get(gitLabTokenKey)
		if tokenKey == nil {
			token := "123""
			session.Set(gitLabTokenKey, token)
			fmt.Printf("Set toke: %s\n", token)
		}
		fmt.Printf("Get toke: %s\n", tokenKey)

		r.HTML(200, "index", "Noah")
	})

	m.Run()

}
