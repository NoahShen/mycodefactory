from fabric.api import run, env, put, cd, sudo
import os
import time


env.user = 'wgou'
env.hosts = ['172.31.48.99']  
env.password = "O7^mezif"

__deployment_dir_name = "deployment_files"

__Packing_Dir = "D:\\work\\gdo-helios-workspace-64\\coke\\packingdir2\\";
__Packing_File_Name = "packing.zip";

__PROJECT_PATH_ON_QA = "/a01/apps/register-coke/"

				
def clear_old_files():
	run("rm -r " + __deployment_dir_name)
	run("mkdir " + __deployment_dir_name)
	
def upload_files():
	remotepath = "~/" + __deployment_dir_name + "/" + __Packing_File_Name
	localpath = __Packing_Dir + __Packing_File_Name
	put(localpath, remotepath)

def unpacking_overwrite_files():
	with cd(__deployment_dir_name):
		run("unzip " + __Packing_File_Name)
		sudo("sudo -u covapp cp -r webroot " + __PROJECT_PATH_ON_QA)

def restart_server():
	sudo("sudo /etc/init.d/register-coke.init stop")
	sudo("sudo /etc/init.d/register-coke.init start", pty=False)
	
def deploy():
	clear_old_files()
	upload_files();
	unpacking_overwrite_files()
	restart_server()

