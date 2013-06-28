import os, sys, time, socket, urllib2, re
from scrape import *

###################################################
#linkedin_processor.py
#
#A class with data members to store the information
#that can be gathered from a linkedIn profile. Also
#contains methods for going to a given linkedin url
#and retrieving the data found there.
################################################### 

class Linkedin_Processor:

    def __init__(self):
        self.first_name = ''
        self.last_name = ''
        self.location = ''
        self.education = ''
        self.profile_picture_url = ''
        self.title = ''

    # ####################################
    # cleanse_data()
    # 
    # Make the data all lowercase.
    # ####################################
    def cleanse_data(self):
        self.first_name = self.first_name.lower()
        self.last_name = self.last_name.lower()
        self.location = self.location.lower()
        self.education = self.education.lower()
        self.title = self.title.lower()


    # #####################################
    # clear_data()
    #
    # Clears all of the data from the class
    # to prepare it for a new url.
    # #####################################
    def clear_data(self):
        self.first_name = ''
        self.last_name = ''
        self.location = ''
        self.profile_picture_url = ''
        self.title = ''
        self.education = ''


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

        #Find the name
        start_index = 0
        start_index = page.find('<span class="given-name">', start_index)
        if start_index != -1:
            end_index = page.find('</span>', start_index)
            self.first_name = page[start_index+25:end_index]
            self.first_name = html_decode(self.first_name)
        else:
            self.first_name = ''
        start_index = 0
        start_index = page.find('<span class="family-name">', start_index)
        if start_index != -1:
            end_index = page.find('</span>', start_index)
            self.last_name = page[start_index+26:end_index]
            self.last_name = html_decode(self.last_name)
        else:
            self.last_name = ''

        #Find the location
        start_index = 0
        start_index = page.find('<dd class="locality">', start_index)
        if start_index != -1:
            end_index = page.find('</dd>', start_index)
            self.location = page[start_index+21:end_index]
            self.location = html_decode(self.location)
        else:
            self.location = ''
        
        #Find the education
        all_education_start_index = 0
        all_education_start_index = page.find('<dd class="summary-education" style="display:block">', all_education_start_index)
        if all_education_start_index != -1:
            schools = []
            education_start_index = page.find('<ul>', all_education_start_index)
            education_end_index = page.find('</ul>', education_start_index)
            start_index = education_start_index
            while start_index < education_end_index:
                start_index = page.find('<li>', start_index)
                if start_index > education_end_index:
                    break
                end_index = page.find('</li>', start_index)
                school = page[start_index+4:end_index]
                schools.append(school)
                start_index = end_index
            #end while
            self.education = schools[0]
            self.education = html_decode(self.education)
        else:
            self.education = ''

        #Find the profile picture url
        profile_picture_start_index = 0
        profile_picture_start_index = page.find('id="profile-picture">', profile_picture_start_index)
        if profile_picture_start_index != -1:
            start_index = page.find('img src="', profile_picture_start_index)
            end_index = page.find('" class=', start_index)
            self.profile_picture_url = page[start_index+9:end_index]
        else:
            self.profile_picture_url = ''

        #Find the title
        start_index = 0
        start_index = page.find('<p class="title" style="display:block">', start_index)
        if start_index != -1:
            end_index = page.find('</p>', start_index)
            self.title = page[start_index+39:end_index]
            self.title = html_decode(self.title)
        else:
            self.title = ''

        self.cleanse_data()
    #end process_url

def clean(text):
    return re.sub('\s{2,}', '', text)

def html_decode(text):
    text = text.replace('&amp;', '&')
    text = text.replace('&quot;', '"')
    text = text.replace('&lt;', '<')
    text = text.replace('&gt;', '>')
    text = text.replace('&nbsp;', ' ')
    return text
