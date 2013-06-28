import sys, os, time, socket, urllib2, re
from scrape import *

##################################################
#whitepages_reverse_address_searcher.py
#
#A class that searches whitepages.com using its
#reverse address search which takes in a street
#address and either a city and state, or zip code
#and returns a list of urls for people who match
#that address.
##################################################

class Whitepages_Reverse_Address_Searcher:

    def __init__(self):
        self.url_list = []
        self.url = 'http://www.whitepages.com/search/ReverseAddress?street='
        

    # ######################################
    # reset_url()
    # 
    # Resets the url to the base url without
    # any search terms.
    # ######################################
    def reset_url(self):
        self.url = 'http://www.whitepages.com/search/ReverseAddress?street='

    # #################################################
    # set_search(street_address, zip_code, city, state)
    # 
    # Adds the search terms to the search url. The
    # street address is necessary, as is either the 
    # zip code or the city and state. The other can be
    # an empty string.
    # #################################################
    def set_search(self, street_address, zip_code, city, state):
        street_address = street_address.replace('&', '%26')
        street_address = street_address.replace(',', '%2C')
        street_address = street_address.replace('#', '%23')
        street_address = street_address.replace(' ', '+')
        street_address = street_address.replace('/', '%2F')
        self.url = self.url + street_address
        self.url = self.url + '&where='

        if zip_code != '':
            self.url = self.url + zip_code + '+'
        
        if city != '':
            city = city.replace('&', '%26')
            city = city.replace(',', '%2C')
            city = city.replace(' ', '+')
            self.url = self.url + city + '+'

        if state != '':
            state = state.replace(' ', '+')
            self.url = self.url + state + '+'

        self.url = self.url[:-1]


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
                time.sleep(5)
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
        result_string = '<div id="result_'
        result_string2 = 'data-href="'
        end_string = '">'
        begin_url = 'http://www.whitepages.com'

        while page.find(result_string, start_index) != -1:
            
            start_index = page.find(result_string, start_index)
            start_index = page.find(result_string2, start_index)
            end_index = page.find(end_string, start_index)

            link_url = begin_url + page[start_index+11:end_index]
            link_url = html_decode(link_url)
            self.url_list.append(link_url)
            start_index = end_index

            


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
