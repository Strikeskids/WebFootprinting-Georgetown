import os, sys, time, socket, urllib2, re
from scrape import *

######################################################
#zillow_processor.py
#
#A class with data members to store the information
#that can be gathered from a zillow profile. Also
#contains methods for going to a given zillow url
#and retrieving the data found there.
#####################################################

class Zillow_Processor:

    def __init__(self):
        self.latitude = ''
        self.longitude = ''
        self.street_address = ''
        self.city = ''
        self.state = ''
        self.zip_code = ''
        self.country = ''
        self.home_value = ''
        self.number_bedrooms = ''
        self.number_bathrooms = ''
        self.square_feet = ''
        self.lot_size = ''
        self.property_type = ''
        self.year_built = ''

    # ####################################
    # cleanse_data()
    # 
    # Make the data all lowercase.
    # ####################################
    def cleanse_data(self):
        self.street_address = self.street_address.lower()
        self.city = self.city.lower()
        self.state = self.state.lower()
        self.country = self.country.lower()
        self.lot_size = self.lot_size.lower()
        self.property_type = self.property_type.lower()


    # #####################################
    # clear_data()
    #
    # Clears all of the data from the class
    # to prepare it for a new url.
    # #####################################
    def clear_data(self):
        self.latitude = ''
        self.longitude = ''
        self.street_address = ''
        self.city = ''
        self.state = ''
        self.zip_code = ''
        self.country = ''
        self.home_value = ''
        self.number_bedrooms = ''
        self.number_bathrooms = ''
        self.square_feet = ''
        self.lot_size = ''
        self.property_type = ''
        self.year_built = ''


    # #########################################
    # process_url(url)
    #
    # Goes to the given url and grabs the site.
    # Then parses through the site to get the 
    # data.
    # #########################################
    def process_url(self, url):
        getPage = 1
        agent = "User-Agent: Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3"
        while getPage == 1:
            try:
                s = Session(agent)
                time.sleep(0.05)
                s.go(url)
                page = s.content
                getPage = 0
            except socket.gaierror:
                sleep(10)
                print 'SOCKET ERROR'
            except ValueError:
                print 'VALUE ERROR'
                page = ' '
                getPage = 0
        #end while
        
        #clean page of extra white space
        page = clean(page)
        page = html_decode(page)

        start_index = 0


        #find latitude
        if page.find('property="og:latitude" content="', start_index) != -1:
            start_index = page.find('property="og:latitude" content="', start_index)
            end_index = page.find('"/>', start_index)
            self.latitude = page[start_index+32:end_index]
            self.latitude = self.latitude.replace('.', '')
        else:
            self.latitude = ''

        #find longitude
        start_index = 0
        if page.find('property="og:longitude" content="', start_index) != -1:
            start_index = page.find('property="og:longitude" content="', start_index)
            end_index = page.find('"/>', start_index)
            self.longitude = page[start_index+33:end_index]
            self.longitude = self.longitude.replace('.', '')
        else:
            self.longitude = ''

        #find street address
        start_index = 0
        if page.find('property="og:street-address" content="', start_index) != -1:
            start_index = page.find('property="og:street-address" content="', start_index)
            end_index = page.find('"/>', start_index)
            self.street_address = page[start_index+38:end_index]
        else:
            self.street_address = ''

        #find city
        start_index = 0
        if page.find('property="og:locality" content="', start_index) != -1:
            start_index = page.find('property="og:locality" content="', start_index)
            end_index = page.find('"/>', start_index)
            self.city = page[start_index+32:end_index]
        else:
            self.city = ''

        #find state
        start_index = 0
        if page.find('property="og:region" content="', start_index) != -1:
            start_index = page.find('property="og:region" content="', start_index)
            end_index = page.find('"/>', start_index)
            self.state = page[start_index+30:end_index]
        else:
            self.state = ''

        #find zip code
        start_index = 0
        if page.find('property="og:postal-code" content="', start_index) != -1:
            start_index = page.find('property="og:postal-code" content="', start_index)
            end_index = page.find('"/>', start_index)
            self.zip_code = page[start_index+35:end_index]
        else:
            self.zip_code = ''

        #find country
        start_index = 0
        if page.find('property="og:country-name" content="', start_index) != -1:
            start_index = page.find('property="og:country-name" content="', start_index)
            end_index = page.find('"/>', start_index)
            self.country = page[start_index+36:end_index]
        else:
            self.country = ''

        #find home value
        start_index = 0
        if page.find('<span class="label">Zestimate<sup>&reg;</sup>', start_index) != -1:
            start_index = page.find('<span class="label">Zestimate<sup>&reg;</sup>', start_index)
            start_index = page.find('class="value">', start_index)
            end_index = page.find('<a', start_index)

            if (end_index - start_index) > 50:
                end_index = page.find('</span>', start_index)
            elif end_index == -1:
                end_index = page.find('</span>', start_index)
            self.home_value = page[start_index+14:end_index]
            if self.home_value == 'None':
                self.home_value = ''
        else:
            self.home_value = ''


        #find number of bedrooms
        start_index = 0
        if page.find('<th scope="row">Bedrooms', start_index) != -1:
            start_index = page.find('<th scope="row">Bedrooms', start_index)
            start_index = page.find('<td>', start_index)
            end_index = page.find('</td>', start_index)
            self.number_bedrooms = page[start_index+4:end_index]
        else:
            self.number_bedrooms = ''
        if self.number_bedrooms == '--':
            self.number_bedrooms = ''

        #find number of bathrooms
        start_index = 0
        if page.find('<th scope="row">Bathrooms', start_index) != -1:
            start_index = page.find('<th scope="row">Bathrooms', start_index)
            start_index = page.find('<td>', start_index)
            end_index = page.find('</td>', start_index)
            self.number_bathrooms = page[start_index+4:end_index]
        else:
            self.number_bathrooms = ''
        if self.number_bathrooms == '--':
            self.number_bathrooms = ''

        #find square feet
        start_index = 0
        if page.find('<th scope="row">Sqft', start_index) != -1:
            start_index = page.find('<th scope="row">Sqft', start_index)
            start_index = page.find('<td>', start_index)
            end_index = page.find('</td>', start_index)
            self.square_feet = page[start_index+4:end_index]
        else:
            self.square_feet = ''
        if self.square_feet == '--':
            self.square_feet = ''

        #find lot size
        start_index = 0
        if page.find('<th scope="row">Lot size:', start_index) != -1:
            start_index = page.find('<th scope="row">Lot size:', start_index)
            start_index = page.find('<td>', start_index)
            end_index = page.find('</td>', start_index)
            self.lot_size = page[start_index+4:end_index]
        else:
            self.lot_size = ''
        if self.lot_size == '--':
            self.lot_size = ''

        #find property type
        start_index = 0
        if page.find('<th scope="row">Property type:', start_index) != -1:
            start_index = page.find('<th scope="row">Property type:', start_index)
            start_index = page.find('<td>', start_index)
            end_index = page.find('</td>', start_index)
            self.property_type = page[start_index+4:end_index]
        else:
            self.property_type = ''
        if self.property_type == '--':
            self.property_type = ''

        #find year built
        start_index = 0
        if page.find('<th scope="row">Year built:', start_index) != -1:
            start_index = page.find('<th scope="row">Year built:', start_index)
            start_index = page.find('<td>', start_index)
            end_index = page.find('</td>', start_index)
            self.year_built = page[start_index+4:end_index]
        else:
            self.year_built = ''
        if self.year_built == '--':
            self.year_built = ''

        self.cleanse_data()


def clean(text):
    return re.sub('\s{2,}', '', text)

def html_decode(text):
    text = text.replace('&amp;', '&')
    text = text.replace('&quot;', '"')
    text = text.replace('&lt;', '<')
    text = text.replace('&gt;', '>')
    text = text.replace('&nbsp;', ' ')
    return text
    
