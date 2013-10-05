import os 
import os.path 
import shutil
import zipfile


def copy_file(source_file,  target_file): 
	if os.path.isfile(source_file):
		target_parent_path = os.path.dirname(target_file)
		if not os.path.exists(target_parent_path):  
			os.makedirs(target_parent_path)  
		open(target_file, "wb").write(open(source_file, "rb").read()) 
				
				
def remove_dir(target_dir): 
	if os.path.exists(target_dir):
		shutil.rmtree(target_dir)


def packing_dir(dirname, target_file):
    filelist = []
    if os.path.isfile(dirname):
        filelist.append(dirname)
    else :
        for root, dirs, files in os.walk(dirname):
            for name in files:
                filelist.append(os.path.join(root, name))

	zf = zipfile.ZipFile(target_file, "w", zipfile.zlib.DEFLATED)
    for file in filelist:
		if file != target_file:
			arcname = file[len(dirname):]
			zf.write(file,arcname)
    zf.close()
