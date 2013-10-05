import subprocess
import xml.dom.minidom
import file_utils
import os 

__PROJECT_PATH = "D:\\work\\gdo-helios-workspace-64\\coke\\tif-cca\\";
    
__Entry_Type = [{
		"prefix": "src\\main\\java\\", 
		"type": "java",
		"targetDir": __PROJECT_PATH + "target\\tif-cca-coke-13.9.2-SNAPSHOT\\WEB-INF\\classes\\"
	},{
		"prefix": "src\\main\\webapp\\jsp\\", 
		"type": "jsp",
		"targetDir": __PROJECT_PATH + "target\\tif-cca-coke-13.9.2-SNAPSHOT\\jsp\\"
	},{
		"prefix": "src\\main\\webapp\\script\\", 
		"type": "js",
		"targetDir": __PROJECT_PATH + "target\\tif-cca-coke-13.9.2-SNAPSHOT\\js\\"
	},
]
    

__Filters = [
	".",
	".classpath",
	".project",
	".settings",
	"ccatif.log",
	"target",
]

__Packing_Dir = "D:\\work\\gdo-helios-workspace-64\\coke\\packingdir2\\";

__Packing_Outpu_File = __Packing_Dir + "packing.zip";

def get_svn_stat_xml(dir):
	p = subprocess.Popen("cmd /c getsvnchangedlist.bat " + dir, 
						stdout=subprocess.PIPE, 
						shell=True)
	(output, err) = p.communicate()
	return output

def get_changed_entries(dir):
	stat_xml = get_svn_stat_xml(dir)
	doc = xml.dom.minidom.parseString(stat_xml)
	changed_entries = []
	for node in doc.getElementsByTagName("entry"):
		path =  node.getAttribute("path")
		entry = parse_changed_entry(path.encode("UTF-8"))
		changed_entries.append(entry)
	return changed_entries

def parse_changed_entry(changed_path):
	for entry_type in __Entry_Type:
		prefix = entry_type["prefix"]
		type = entry_type["type"]
		extName = "." + type
		if (changed_path.startswith(prefix) and changed_path.endswith(extName)):
			return {"entry_type": entry_type, "path": changed_path[len(prefix):]}
	return {"entry_type": None, "path": changed_path}

def filter(changed_entries):
	filtered_list = []
	for entry in changed_entries:
		path = entry["path"]
		if path not in __Filters:
			filtered_list.append(entry)
	return filtered_list		


def copy_built_files(entries):
	file_utils.remove_dir(__Packing_Dir)
	for entry in entries:
		entry_type = entry["entry_type"]
		if entry_type["type"] == "java":
			class_filepath = entry["path"].replace(".java", ".class")
			built_file = os.path.join(entry["entry_type"]["targetDir"], class_filepath);
			copy_to = os.path.join(__Packing_Dir, "webroot\\WEB-INF\\classes\\", class_filepath)
			file_utils.copy_file(built_file, copy_to)
		elif entry_type["type"] == "jsp":
			built_file = os.path.join(entry["entry_type"]["targetDir"], entry["path"]);
			copy_to = os.path.join(__Packing_Dir, "webroot\\jsp\\", entry["path"])
			file_utils.copy_file(built_file, copy_to)
		elif entry_type["type"] == "js":
			built_file = os.path.join(entry["entry_type"]["targetDir"], entry["path"]);
			copy_to = os.path.join(__Packing_Dir, "webroot\\js\\", entry["path"])
			file_utils.copy_file(built_file, copy_to)
		
def packing_changed_files():
	changed_entries = get_changed_entries(__PROJECT_PATH)
	filtered_entries =  filter(changed_entries)
	for e in filtered_entries:
		print "copying " + e["path"]
	copy_built_files(filtered_entries)
	file_utils.packing_dir(__Packing_Dir, __Packing_Outpu_File)
	
if __name__=='__main__':
	packing_changed_files()