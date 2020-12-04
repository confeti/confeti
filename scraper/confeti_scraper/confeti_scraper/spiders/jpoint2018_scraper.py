import re

import scrapy
from confeti_scraper.items import ConferenceInfo, Reports, Speakers, ContactInfo
from scrapy.http import Request

from confeti_scraper import materials_form

class Jpoint2018ScraperSpider(scrapy.Spider):
    name = 'jpoint2018_scraper'
    allowed_domains = ['2018.jpoint.ru']
    start_urls = ['http://2018.jpoint.ru/en/']

    def __init__(self):
        self.COMPLEXITY_VALUES ={
                                  'Get ready, will burn':0,
                                  'Introduction to technology':1,
                                  'For practicing engineers':2,
                                  "Hardcore. Really hard and demanding talk, you'll understand only if you're an experienced engineer.":3,
                                  None: -1
                                }

        self.conference = ConferenceInfo()
        self.next_page = ''   

    def parse(self, response):
        self.conference['name'] = 'Jpoint'
        self.conference['year'] = '2018'
        self.conference['logo'] = ''.join((response.url[:-4], response.xpath('//div[@class="col-md-6 logo-container"]//img/@src').get()))
        self.conference['url'] = response.url
        self.conference['location'] = ''
        self.next_page = ''.join((response.url,'#schedule'))
        return Request(self.next_page, callback=self.parse_reports)

    def parse_reports(self, response):
        for combined_frame in response.xpath('//div[@class="pt-col-talk pt-col-keynote"]'):
            report_dict = {}
            material_links = []
            report_link = ''.join(('https://2018.jpoint.ru', combined_frame.xpath('.//a[@class="event_talk_link"]/@href').get()))
            complexity_text = combined_frame.xpath('.//span[@class="rating_icons"]//img/@alt').get()
            report_dict['complexity'] = {'value':self.COMPLEXITY_VALUES[complexity_text], 'name':complexity_text}
            report_dict['tags'] = []
            for material in combined_frame.xpath('.//div[@class="text-lg-right cell-helper"]//a/@href').getall():
                material_links.append(materials_form.materials_form(material))
            report_dict['source'] = material_links
            report_dict['language'] = combined_frame.xpath('.//span[@class="cell-lang"]/text()').get().strip()
            yield Request(
                            report_link,
                            callback=self.parse_authors,
                            meta = {'report_dict': report_dict}       
                         )
        
        for frame in response.xpath('//div[@class="pt-col-talk"]'):
            report_dict = {}
            material_links = []
            try:
                report_link = ''.join(('https://2018.jpoint.ru', frame.xpath('.//a[@class="event_talk_link"]/@href').get()))
            except TypeError:
                continue
            complexity_text = frame.xpath('.//span[@class="rating_icons"]//img/@alt').get()
            report_dict['complexity'] = {'value':self.COMPLEXITY_VALUES[complexity_text], 'name':complexity_text}
            report_dict['tags'] = [tag.strip() for tag in frame.xpath('.//span[@class="tags"]//nobr/text()').getall()]
            for material in frame.xpath('.//div[@class="text-lg-right cell-helper"]//a/@href').getall():
                material_links.append(materials_form.materials_form(material))
            report_dict['source'] = material_links
            report_dict['language'] = frame.xpath('.//span[@class="cell-lang"]/text()').get().strip()
            yield Request(
                            report_link,
                            callback=self.parse_authors,
                            meta = {'report_dict': report_dict}       
                         )

    def parse_authors(self, response):
        report = Reports()
        report['complexity'] = response.meta['report_dict']['complexity']
        report['language'] = response.meta['report_dict']['language']
        report['source'] = response.meta['report_dict']['source']
        report['tags'] = response.meta['report_dict']['tags']
        report['title'] = response.xpath('//h1[@class="talk_title"]/text()').get().strip()
        report['description'] = response.xpath('//main[@class="talk-main"]//p/text()').get().strip()
        speakers_list = []
        for speaker_sec in response.xpath('//div[@class="talk-speakers"]'):
            speaker = Speakers()
            contact_info = ContactInfo()
            speaker['name'] = speaker_sec.xpath('.//h5[@class="speaker-info_name"]/text()').get().strip()
            speaker['avatar'] = speaker_sec.xpath('.//img[@class="img-fluid"]/@src').get()
            speaker['bio'] = speaker_sec.xpath('.//div[@class="speaker-info_bio"]//p/text()').get().strip()
            contact_info['company'] = (speaker_sec.xpath('.//h6[@class="speaker-info_company"]/text()').get(), self.conference['year'])
            contact_info['twitterUsername'] =  speaker_sec.xpath('.//div[@class="speaker_profiles"]//a[@class="twitter_link"]/@href').get()
            speaker['contactInfo']=contact_info
            speakers_list.append(speaker)
        report['speakers'] = speakers_list
        self.conference['report'] = report
        yield self.conference
