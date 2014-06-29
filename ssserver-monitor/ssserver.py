# coding=utf8

from pyquery import PyQuery as pq
from lxml import etree
import urllib
import json
import copy

def get_server(index, node):
    server_elem = pq(node)
    content = server_elem.find("strong");
    title = content.text();
    if not title.startswith(u'免费服务器'):
        return
    server_info_text = server_elem.next("pre code").text()
    new_server_info = json.loads(server_info_text)
    new_server_info["title"] = title
    new_server_infos.append(new_server_info)

def compare_server_info(new_server_infos, last_server_infos):
    new_server_message = ""
    changed_server_message = ""
    for idx, item in enumerate(new_server_infos):
        # 新增的服务器
        if idx > (len(last_server_infos) - 1): 
            new_server_message += server_infos_to_str(new_server_infos[idx:])
            break
        changed_server = compare_server_detail(item, last_server_infos[idx])
        if changed_server:
            changed_server_message += server_info_to_str(changed_server)

    updated_info = {}
    if len(changed_server_message) > 0:
        updated_info["updated_server_msg"] = changed_server_message
    if len(new_server_message) > 0:
        updated_info["new_server_msg"] = new_server_message
    return updated_info

def compare_server_detail(new_server, old_server):
    updated_server = copy.deepcopy(old_server)
    properties = ["server_port", "title", "local_port", "server", "timeout", "password", "method"]
    changed = False
    for prop in properties:
        if old_server[prop] != new_server[prop]:
            updated_server[prop] = "%s_update" % new_server[prop]
            changed = True

    if changed:
        return updated_server

def server_infos_to_str(new_servers):
    server_infos_str = ""
    for idx, item in enumerate(new_servers):
        server_infos_str += server_info_to_str(item)
    return server_infos_str

def server_info_to_str(server_info):
    return u'服务器信息=%s \n' % (json.dumps(server_info, sort_keys=False, indent=4, ensure_ascii=False))


last_server_infos = []

sserver_file_for_read = open('sserver.json', 'r')
all_content = sserver_file_for_read.read()
if len(all_content) > 0:
    last_server_infos = json.loads(all_content)

print "上次获取的服务器："
print server_infos_to_str(last_server_infos)

print "获取最新的服务器信息..."
new_server_infos = []
doc = pq(url="http://boafanx.tabboa.com/boafanx-ss/")
doc(".post-content p").filter(lambda i: pq(this).find("strong").length > 0).each(get_server)
print server_infos_to_str(new_server_infos)

print "比较服务器信息...\n"
updated_info = compare_server_info(new_server_infos, last_server_infos)
server_changed = False
if len(updated_info.get("updated_server_msg","")) > 0:
    print "更新的服务器："
    print updated_info["updated_server_msg"]
    server_changed = True
if len(updated_info.get("new_server_msg", "")) > 0:
    print "新增的服务器："
    print updated_info["new_server_msg"]
    server_changed = True

if not server_changed:
    print "无更新信息！"

sserver_file_for_write = open('sserver.json', 'w')
sserver_file_for_write.write(json.dumps(new_server_infos, sort_keys=False, indent=4, ensure_ascii=False).encode('utf-8'))
sserver_file_for_write.close()