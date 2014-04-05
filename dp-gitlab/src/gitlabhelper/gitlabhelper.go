package gitlabhelper

import (
	"encoding/json"
	"flag"
	"fmt"
	"github.com/NoahShen/go-gitlab-client"
	"io/ioutil"
	"os"
	"time"
)

func Login(username, password string) (*Gitlab, error) {

	client := &http.Client{}

	return &Gitlab{
		BaseUrl: baseUrl,
		ApiPath: apiPath,
		Token:   token,
		Client:  client,
	}
}
