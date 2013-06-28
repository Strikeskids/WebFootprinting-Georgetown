import os, sys, time, socket, urllib2, re
from scrape import *


#################################################
#zillow_searcher.py
#
#Takes in an address with as many fields from
#street address, city, state, zip code, as 
#possible, and searchers zillow in order to
#get the url for the zillow page of that address.
#################################################

class Zillow_Searcher:

    def __init__(self):
        self.url_list = []
        self.search_url = 'http://www.zillow.com/homes/'

    # ######################################
    # reset_url()
    # 
    # Resets the url to the base url without
    # any search terms.
    # ######################################
    def reset_url(self):
        self.search_url = 'http://www.zillow.com/homes/'


    # ########################################################
    # set_search(address_information)
    # 
    # Takes in a list of address information (street address,
    # city, state, zip code) and sets the search url to
    # include this information.
    # ########################################################
    def set_search(self, address_information):
        for information in address_information:
            information = information.lower()
            information = information.replace('-', '.dash.')
            information = information.replace(' ','-')
            information = information.replace('/', '-')
            information = information.replace('#', '.num.')
            information = information.replace('bl.', 'blvd.')
            self.search_url += information + '-'
        self.search_url = self.search_url[:-1]
        self.search_url += '_rb/'

    # ##########################################
    # search()
    #
    # Goes and searches zillow for the specified 
    # address. Stores the address of the url for
    # that addresses zillow page.
    # ##########################################
    def search(self):
        
        agent = "User-Agent: Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3"

        #Connect to the search url
        getPage = 1
        while getPage == 1:
            try:
                s = Session(agent)
                time.sleep(0.25)
                s.go(self.search_url)
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
        search_string = 'id=\\"property-zpid\\" class=\\"hide\\">'
        end_string = '</div>'
        begin_url = 'http://www.zillow.com/homedetails/'
        end_url = '_zpid/'

 
        if page.find(search_string, start_index) != -1:
            start_index = page.find(search_string, start_index)
            end_index = page.find(end_string, start_index)
            return_url = begin_url + page[start_index+36:end_index] + end_url
            self.url_list.append(return_url)
        else:
            del self.url_list[:]


    # ##########################
    # clear_return_url()
    #
    # Clears the return url.
    # ##########################
    def clear_url_list(self):
        del self.url_list[:]
            
