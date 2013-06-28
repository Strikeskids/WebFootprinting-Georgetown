import sys, os, time, socket, urllib2, re
from scrape import *

###############################################
#whitepages_searcher.py
#
#A class that searches whitepages.com for at
#least a last name along with an optional first
#name and zip code or state. This class stores
#a list of urls that are found.
###############################################

class Whitepages_Searcher:

    def __init__(self):
        self.url_list = []
        self.url = 'http://www.whitepages.com/dir/'
        

    # ######################################
    # reset_url()
    # 
    # Resets the url to the base url without
    # any search terms.
    # ######################################
    def reset_url(self):
        self.url = 'http://www.whitepages.com/dir/'


    # ###############################################
    # set_search(last_name, first_name, location)
    # 
    # Adds the search terms to the search url. The
    # last name is necessary, the others can be given
    # as empty strings.
    # ###############################################
    def set_search(self, last_name, first_name, location):
        if location != '':
            self.url = self.url + location.replace(' ', '-') + '/'
        else:
            self.url = self.url + 'a-z/'
        self.url = self.url + last_name.replace(' ', '-') + '/'
        if first_name != '':
            self.url = self.url + first_name.replace(' ', '-')

    # ###############################################
    # search()
    # 
    # Grabs the site specificed by the search url and
    # goes through it, extracting the urls of the 
    # sites that are found. Places these urls into 
    # self.url_list. Keeps going to next result page
    # as long as possible.
    # ###############################################
    def search(self):

        first_page = True
        agent = "User-Agent: Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3"

        #Connect to the search url
        getPage = 1
        while getPage == 1:
            try:
                s = Session(agent)
                time.sleep(0.25)
                s.go(self.url)
                page = s.content
                getPage = 0
            except socket.gaierror:
                sleep(10)
                print 'SOCKET ERROR'
                getPage = 1
            except ValueError:
                print 'VALUE ERROR'
                page = ' '
                getPage = 0

        start_index = 0
        result_string = 'class="result'
        result_string2 = 'data-href="'
        end_string = '">'
        begin_url = 'http://www.whitepages.com'

        try:
            while page.find(result_string, start_index) != -1:
                
                start_index = page.find(result_string, start_index)
                start_index = page.find(result_string2, start_index)
                end_index = page.find(end_string, start_index)

                link_url = begin_url + page[start_index+11:end_index]
                link_url = html_decode(link_url)
                self.url_list.append(link_url)
                start_index = end_index

                if page.find(result_string, start_index) == -1:
                    if page.find('next_button_inactive', start_index) == -1:
                          start_index = page.find('end_pagination', start_index)
                          if start_index == -1:
                              break
                          
                          start_index = page.find('<span class="current">', start_index)
                          start_index = page.find('<a href="', start_index)
                          end_index = page.find('" ', start_index)
                          
                          self.url = begin_url + page[start_index+9:end_index]
                          time.sleep(1)
                          s.go(self.url)
                          page = s.content
                          start_index = 0
                          first_page = False


            #end while
        except socket.gaierror:
            print 'SOCKET ERROR'
        except ValueError:
            print 'VALUE ERROR'


    # #########################
    # clear_url_list()
    #
    # Clears the url list.
    # #########################
    def clear_url_list(self):
        del self.url_list[:]


def html_decode(text):
    text = text.replace('&amp;', '&')
    text = text.replace('&quot;', '"')
    text = text.replace('&lt;', '<')
    text = text.replace('&gt;', '>')
    return text
