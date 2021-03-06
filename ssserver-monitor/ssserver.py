# coding=utf8

from pyquery import PyQuery as pq
from lxml import etree
import urllib
import json
import copy


import smtplib  
from email.mime.text import MIMEText

import os
import logging

current_path = os.path.split(os.path.realpath(__file__))[0]
logging.basicConfig(filename = current_path + '/ssserver.log', level = logging.INFO, filemode = 'a', format = '%(asctime)s - %(levelname)s: %(message)s')


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
            updated_server[prop] = "<span style='color:red'>%s</span>" % new_server[prop]
            changed = True

    if changed:
        return updated_server

def server_infos_to_str(new_servers):
    server_infos_str = ""
    for idx, item in enumerate(new_servers):
        server_infos_str += server_info_to_str(item)
    return server_infos_str

def server_info_to_str(server_info):
    return u'server_info=%s \n<br><br>' % (json.dumps(server_info, sort_keys=False, indent=4, ensure_ascii=False))



mailto_list=["ssserversuber@126.com"] 
mail_host="smtp.126.com"  #设置服务器
mail_user="ssserverchecker"    #用户名
mail_pass="159357ABCD"   #口令 
mail_postfix="126.com"  #发件箱的后缀
  
def send_mail(to_list, sub, content):
    me = "%s<%s@%s>" % (mail_user, mail_user, mail_postfix)
    msg = MIMEText(content, _subtype='html', _charset='utf8')
    msg['Subject'] = sub    #设置主题
    msg['From'] = me  
    msg['To'] = ";".join(to_list)  
    try:  
        s = smtplib.SMTP()  
        s.connect(mail_host)  #连接smtp服务器
        s.login(mail_user,mail_pass)  #登陆服务器
        s.sendmail(me, to_list, msg.as_string())  #发送邮件
        s.close()  
        return True  
    except Exception, e:  
        logging.error(str(e))  
        return False

new_server_infos = []

if __name__ == '__main__': 
    logging.info("==================Start checking sserver==================")
    last_server_infos = []
    exist = os.path.exists(current_path + '/ssserver.json')
    all_content = ""
    if exist:
        sserver_file_for_read = open(current_path + '/ssserver.json', 'r')
        all_content = sserver_file_for_read.read()
        sserver_file_for_read.close()
        if len(all_content) > 0:
            last_server_infos = json.loads(all_content)

    logging.info("Last server info：\n" + server_infos_to_str(last_server_infos).encode('utf-8'))

    doc = pq(url="http://boafanx.tabboa.com/boafanx-ss/")
    doc(".post-content p").filter(lambda i: pq(this).find("strong").length > 0).each(get_server)
    logging.info("New server info：\n" + server_infos_to_str(new_server_infos).encode('utf-8'))

    logging.info("Checking server...")
    updated_info = compare_server_info(new_server_infos, last_server_infos)
    mail_content = ""
    server_changed = False
    if len(updated_info.get("updated_server_msg","")) > 0:
        logging.info("updated server：")
        logging.info(updated_info["updated_server_msg"].encode('utf-8'))
        mail_content += u"updated server：<br><br>%s<br>" % updated_info["updated_server_msg"]
        server_changed = True
    if len(updated_info.get("new_server_msg", "")) > 0:
        logging.info("added server：")
        logging.info(updated_info["new_server_msg"].encode('utf-8'))
        mail_content += u"new server：<br><br>%s<br>" % updated_info["new_server_msg"]
        server_changed = True

    if not server_changed:
        logging.info("No updating message!")
    else:
        logging.info(u"send mail:\n%s" % mail_content)
        if send_mail(mailto_list, "Shadowsocks updated", mail_content):
            logging.info("Send mail success!")
        else:
            logging.info("Send mail failed!")


    sserver_file_for_write = open(current_path + '/ssserver.json', 'w')
    sserver_file_for_write.write(json.dumps(new_server_infos, sort_keys = False, indent = 4, ensure_ascii = False).encode('utf-8'))
    sserver_file_for_write.close()
    logging.info("==================Checking sserver complete==================")


