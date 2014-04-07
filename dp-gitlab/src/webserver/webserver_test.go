package webserver

import (
	"os"
	"testing"
)

func TestServer(t *testing.T) {
	os.Setenv("HTTP_PROXY", "http://peiqi.shen:159357Dp87@remoteapp.dianping.com:808")
	os.Setenv("HTTPS_PROXY", "http://peiqi.shen:159357Dp87@remoteapp.dianping.com:808")

	StartServer()
}
