This folder contains a set of web scrapers for various sites, as
well as seachers.  Some searchers are specific for a site,
but there is also a more general google searcher that has so
far only been used to search for linkedin profiles. In order to
be used for other sites some small additions would need to be made.
----------------------------------------------------------------------
In general, the (siteName)_searcher.py will search the site for a 
given list of terms and collect a list of urls that match those search
terms.  These urls can then be processed by the (siteName)_processor.py
of the same site. **Note: The difference between whitepages_searcher.py
and whitepages_reverse_address_searcher.py is the type of search terms 
that they use.  However, they both return the same profiles from 
whitepages.com and so should both be used with whitepages_processor.py.**
-----------------------------------------------------------------------
I tried to keep the method names the same for all searchers/processors 
no matter the site, however, there are differences in the arguments that
can be passed through to methods of the same name (ie: some set_search()
methods take in a list, while others take in individual variables).
------------------------------------------------------------------------
All of these programs use scrape.py to get the raw html from the websites.
It is a module that was found online and which I saw in a project of one
of Prof. Singh's previous students.  However, after downloading the file,
I made some small adjustments to it, and therefore this version of scrape.py
should be used if possible. 
Just to note it, the changes to scrape.py are that these lines are
commented out:
547, 550, 558-573
**New Changes**: lines 85-94 modified
