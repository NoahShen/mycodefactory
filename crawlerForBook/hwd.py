from pyquery import PyQuery as pq
from lxml import etree
import urllib

hwd_file = open('/Users/noahshen/temp/hwd.txt', 'w')

def get_title(index, node):
    chapterItem = pq(node)
    title = chapterItem.attr('title')
    chapterUrl = chapterItem.attr("href")

    if index >= 1055:
        print "row:%d, title: %s, url=%s \n" % (index, title, chapterUrl)
    	chapterDoc = pq(url=chapterUrl)
        titleStr = ("%s\n" % (title)).encode("UTF-8")
        hwd_file.write(titleStr)
    	pElements = chapterDoc(".content").children()
        pElements.each(filterContentP)
    	

def filterContentP(index, node):
    paragraph = pq(node)
    if paragraph.hasClass("navi"):
        return False
        
    elements = paragraph.contents()
    for e in elements:
        write_content(e)


def write_content(e):
	if isinstance(e, basestring):
		content = ("%s\n\n" % e).encode("UTF-8")
		hwd_file.write(content)

doc = pq(url="http://www.mossiella.com/")
doc(".box ul a").each(get_title)
hwd_file.close()
