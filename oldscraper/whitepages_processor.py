import os, sys, time, socket, urllib2, re
from scrape import *

######################################################
#whitepages_processor.py
#
#A class with data members to store the information
#that can be gathered from a whitepages profile. Also
#contains methods for going to a given whitepages url
#and retrieving the data found there.
#####################################################

class Whitepages_Processor:

    def __init__(self):
        self.first_name = ''
        self.last_name = ''
        self.street_address = ''
        self.city = ''
        self.state = ''
        self.zip_code = ''
        self.phone_number = ''
        self.age_range = ''
        self.associated_people = []
        self.prior_location = ''
        self.job = ''
        

    # ####################################
    # cleanse_data()
    # 
    # Make the data all lowercase.
    # ####################################
    def cleanse_data(self):
        self.first_name = self.first_name.lower()
        self.last_name = self.last_name.lower()
        self.street_address = self.street_address.lower()
        self.city = self.city.lower()
        self.state = self.state.lower()
        for person in self.associated_people:
            person = person.lower()
        self.prior_location = self.prior_location.lower()
        self.job = self.job.lower()


    # #####################################
    # clear_data()
    #
    # Clears all of the data from the class
    # to prepare it for a new url.
    # #####################################
    def clear_data(self):
        self.first_name = ''
        self.last_name = ''
        self.street_address = ''
        self.city = ''
        self.state = ''
        self.zip_code = ''
        self.age_range = ''
        del self.associated_people[:]
        self.prior_location = ''
        self.phone_number = ''
        self.job = ''
        


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
                sleep(30)
                print 'SOCKET ERROR'
            except ValueError:
                print 'VALUE ERROR'
                page = ' '
                getPage = 0
        #end while
        
        #clean page of extra white space
        page = clean(page)
        page = html_decode(page)

        #find names
        start_index = 0
        start_index = page.find('<span class="given-name">', start_index)
        if start_index != -1:
            end_index = page.find('</span>', start_index)
            self.first_name = page[start_index+25:end_index]
        else:
            self.first_name = ''
        
        start_index = 0
        start_index = page.find('<span class="family-name">', start_index)
        if start_index != -1:
            end_index = page.find('</span>', start_index)
            self.last_name = page[start_index+26:end_index]
        else:
            self.last_name = ''

        #find street address
        start_index = 0
        start_index = page.find('<div class="address adr"><p>', start_index)
        if start_index != -1:
            end_index = page.find('<span ', start_index)
            difference = end_index - start_index
            if difference < 200:
                self.street_address = page[start_index+28:end_index]
            else:
                end_index = page.find('</p>', start_index)
                self.street_address = page[start_index+28:end_index]
        start_index = page.find('class="street-address">', start_index)
        if start_index != -1:
            end_index = page.find('</span>', start_index)
            self.street_address = self.street_address + page[start_index+23:end_index]
        self.street_address = self.street_address.replace('<p>', '')
        self.street_address = self.street_address.replace('</p>', '')
        

        #find city
        start_index = 0
        start_index = page.find('<span class="locality">', start_index)
        if start_index != -1:
            end_index = page.find('</span>', start_index)
            self.city = page[start_index+23:end_index]
        else:
            self.city = ''

        #find state
        start_index = 0
        start_index = page.find('<span class="region">', start_index)
        if start_index != -1:
            end_index = page.find('</span>', start_index)
            self.state = page[start_index+21:end_index]
        else:
            self.state = ''

        #find zip code
        start_index = 0
        start_index = page.find('<span class="postal-code">', start_index)
        if start_index != -1:
            end_index = page.find('</span>', start_index)
            self.zip_code = page[start_index+26:end_index]
        else:
            self.zip_code = ''

        #find phone number
        start_index = 0
        start_index = page.find("<p class='single_result_phone landline'>", start_index)
        if start_index != -1:
            end_index = page.find('</p>', start_index)
            self.phone_number = page[start_index+40:end_index]
        else:
            self.phone_number = ''

        #find age range
        start_index = 0
        start_index = page.find('Age:</strong></td>', start_index)
        if start_index != -1:
            if page.find('<td><span class="age-range">', start_index) != -1:
                start_index = page.find('<td><span class="age-range">', start_index)
                end_index = page.find('</span>', start_index)
                self.age_range = page[start_index+28:end_index]
            else:
                start_index = page.find('<td>', start_index)
                if start_index != -1:
                    end_index = page.find('</td>', start_index)
                    self.age_range = page[start_index+4:end_index]
                else:
                    self.age_range = ''      
        else:
            self.age_range = ''

        #find associated people
        start_index = 0
        if page.find('Associated people: </strong></td>', start_index) != -1:
            start_index = page.find('Associated people: </strong></td>', start_index)
            start_index = page.find('<td>', start_index)
            end_index = page.find('</td>', start_index)
            all_people = page[start_index+4:end_index]
            self.associated_people = all_people.split(',')
        elif page.find('<td class="label"><strong>Associated: </strong></td>', start_index) != -1:
            start_index = page.find('<td class="label"><strong>Associated: </strong></td>', start_index)
            start_index = page.find('<td>', start_index)
            end_index = page.find('</td>', start_index)
            all_people = page[start_index+4:end_index]
            self.associated_people = all_people.split(',')
        else:
            self.associated_people.append('')

        #find prior location
        start_index = 0
        start_index = page.find('Prior: </strong></td>', start_index)
        if start_index != -1:
            start_index = page.find('<td><div>', start_index)
            end_index = page.find('<em>', start_index)
            if end_index == -1:
                end_index = page.find('<br/>', start_index)
            self.prior_location = page[start_index+9:end_index]
        else:
            self.prior_location = ''

        #find job
        start_index = 0
        start_index = page.find('<td class="label"><strong>Job: </strong></td>', start_index)
        if start_index != -1:
            start_index = page.find('class="org">', start_index)
            if start_index != -1:
                end_index = page.find('</a>', start_index)
                job_place = page[start_index+12:end_index]
            else:
                job_place = ''
            start_index = page.find('<span class="title">', start_index)
            if start_index != -1:
                end_index = page.find('</span>', start_index)
                job_position = page[start_index+20:end_index]
            else:
                job_position = ''
            self.job = job_position + " at " + job_place
        else:
            self.job = ''


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
