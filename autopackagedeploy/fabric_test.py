from fabric.api import run, env

env.user = 'wgou'
env.hosts = ['172.31.48.99']  
env.password = "O7^mezif"
	
def runtest():
	run('uname -a')