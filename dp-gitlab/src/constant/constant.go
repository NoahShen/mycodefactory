package constant

import (
	"time"
)

const (
	SessionKey    = "DPGitToolsSession"
	SessionMaxAge = time.Hour * 12 // 12 hours
	UserInfoKey   = "UserInfoKey"
	GitLabHost    = "http://code.dianpingoa.com/"
	GitLabApiPath = "/api/v3"
)
