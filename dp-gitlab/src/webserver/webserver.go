package webserver

import (
	"constant"
	"encoding/gob"
	"filter"
	"github.com/NoahShen/go-gitlab-client"
	"github.com/go-martini/martini"
	"github.com/martini-contrib/render"
	"github.com/martini-contrib/sessions"
	"log"
	"net/http"
)

type UserInfo struct {
	GitUser      *gogitlab.User
	GitlabClient *gogitlab.Gitlab
}

func StartServer() {
	gob.Register(&UserInfo{})

	m := martini.Classic()
	m.Use(render.Renderer(render.Options{
		Directory: "../../web/templates",
	}))
	store := sessions.NewCookieStore([]byte("secretDpGitLab"))
	store.Options(sessions.Options{
		MaxAge: int(constant.SessionMaxAge),
	})
	m.Use(sessions.Sessions(constant.SessionKey, store))

	m.Use(filter.LoginFilter())

	m.Group("/action", func(r martini.Router) {
		r.Post("/login", loginWithNameAndPwd)
		r.Get("/projects", getProject)
	})

	m.Run()

}

func getProject(session sessions.Session, r render.Render, l *log.Logger) {
	userInfo := session.Get(constant.UserInfoKey).(*UserInfo)
	gitlabClient := userInfo.GitlabClient
	projects, err := gitlabClient.Projects()
	if err != nil {
		panic(err)
	}
	json := make(map[string]interface{})
	json["projects"] = projects
	r.JSON(200, json)
}

func loginWithNameAndPwd(r *http.Request, res http.ResponseWriter, params martini.Params, session sessions.Session, render render.Render, l *log.Logger) {
	err := r.ParseForm()
	if err != nil {
		l.Printf("ParseForm error: %v\n", err)
		render.HTML(200, "error", "登录失败！")
		return
	}

	username := r.PostFormValue("username")
	//password := r.PostFormValue("password")

	l.Printf("Start logging in, username = %s \n", username)
	//gitlabClient, err := gogitlab.NewGitlabByLogin(constant.GitLabHost, constant.GitLabApiPath, username, password)
	//if err != nil {
	//	l.Printf("Login error: %v\n", err)
	//	render.HTML(200, "error", "登录失败！")
	//	return
	//}
	//gitUser, err := gitlabClient.CurrentUser()
	//if err != nil {
	//	l.Printf("Get current error: %v\n", err)
	//	render.HTML(200, "error", "登录失败！")
	//	return
	//}
	uInfo := &UserInfo{}
	session.Set(constant.UserInfoKey, uInfo)
	render.HTML(200, "dashboard", uInfo)
}
