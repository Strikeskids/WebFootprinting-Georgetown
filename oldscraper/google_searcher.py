import sys, os, time, socket, urllib2, re
from scrape import *

####################################################
#google_searcher.py
#
#A class that searches google for a set of terms
#in a specific website (i.e. linkedin, facebook)
#and stores/has the ability to return a list of urls
#that are found.
####################################################

class Google_Searcher:

    def __init__(self):
        self.url_list = []
        self.url = 'http://www.google.com/search?hl=en&lr=&num=100&q='
        self.site_string = ''
        self.site = ''

    # #######################################
    # reset_url()
    #
    # Resets the url to the base url without
    # any search terms or website information
    # #######################################
    def reset_url(self):
        self.url = 'http://www.google.com/search?hl=en&lr=&num=100&q='
    

    # ###############################################
    # set_search(search__list, site)
    #
    # Adds the search terms and website specification
    # to the search url
    # ###############################################
    def set_search(self, search_list, site):
        for term in search_list:
            self.url = self.url + term + '+'
        self.site = site
        if site == 'LINKEDIN':
            self.url = self.url + 'site%3Alinkedin.com'
            self.site_string = '<a href="http://www.linkedin.com/'
        elif site == 'FACEBOOK':
            self.url = self.url + 'site%3Afacebook.com'
            self.site_string = '<a href="http://www.facebook.com/'

    # ################################################
    # search()
    #
    # Grabs the content of the search url and then
    # goes through it to extract the urls of the sites
    # that are found.  Places these urls into
    # self.url_list
    # ################################################
    def search(self):
        
        start_num = 0
        self.url = self.url + '&start=' + str(start_num)
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

        try:
            #Protect against automated query checking
            while page.find('automated queries', start_index) != -1:
                print 'Automated queries, sleeping...'
                time.sleep(120)
                print 'Trying again...'
                s.go(self.url)
                page = s.content
            while page.find(self.site_string, start_index) != -1:
                #Find each link and add it to the list
                start_index = page.find(self.site_string, start_index)
                end_index = page.find('" class=', start_index)
                link_url = page[start_index+9:end_index]
                self.url_list.append(link_url)
                if self.site == 'LINKEDIN':
                    self.check_linkedin_ignores(link_url)
                start_index =  end_index
                
                #Check if there is a next page
                if page.find(self.site_string, start_index) == -1:
                    if page.find('Next', start_index) != -1:
                        start_num = start_num + 100
                        if start_num == 100:
                            self.url = self.url[:-1]
                        else:
                            self.url == self.url[:-3]
                        self.url = self.url + str(start_num)
                        time.sleep(1)
                        s.go(self.url)
                        page = s.content
                        start_index = 0
                    else:
                        break
             #end while   
        except socket.gaierror:
            print 'SOCKET ERROR'
        except ValueError:
            print 'VALUE ERROR'


    # ####################################
    # clear_url_list()
    # 
    # Clears the url list.
    # ####################################
    def clear_url_list(self):
        del self.url_list[:]

    # #############################################
    # check_linkedin_ignores(link_url)
    #
    # Checks the links against various 
    # linkedin pages (directories, companies, etc.)
    # that should not be added to the list.
    # #############################################
    def check_linkedin_ignores(self, link_url):
        if re.search('company', link_url):
            self.url_list.pop()
        elif re.search('dir', link_url):
            self.url_list.pop()
        elif re.search('jobs', link_url):
            self.url_list.pop()
        elif re.search('groups', link_url):
            self.url_list.pop()
        elif re.search('answers', link_url):
            self.url_list.pop()
        elif re.search('title', link_url):
            self.url_list.pop()
