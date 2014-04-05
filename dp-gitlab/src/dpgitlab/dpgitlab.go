package main

import (
	"fmt"
	"github.com/NoahShen/go-gitlab-client"
	"os"
)

type Config struct {
	Host    string `json:"host"`
	ApiPath string `json:"api_path"`
	Token   string `json:"token"`
}

func main() {

	os.Setenv("HTTP_PROXY", "http://peiqi.shen:159357Dp87@remoteapp.dianping.com:808")
	os.Setenv("HTTPS_PROXY", "http://peiqi.shen:159357Dp87@remoteapp.dianping.com:808")

	gitlab, err := gogitlab.NewGitlabByLogin("http://code.dianpingoa.com/", "/api/v3", "peiqi.shen", "159357Dp87")
	if err != nil {
		fmt.Printf("error: %+v\n", err)
		return
	}

	fmt.Printf("Token: %s\n", gitlab.Token)

	projects, _ := gitlab.Projects()
	for _, project := range projects {
		fmt.Printf("Project: %v\n", project)

	}

}
