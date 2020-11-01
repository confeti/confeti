import scrapy

from confeti_scraper.items import ConfetiScraperItem

class ConfetiSpiderSpider(scrapy.Spider):
    name = 'confeti_spider'
    allowed_domains = ['https://jug.ru/speakers/']
    start_urls = ['https://jug.ru/speakers/']

    def parse(self, response):
        item = ConfetiScraperItem()
        with open('speakers.txt','w') as speakers:
            for reporter in response.css('.ap_col_wrap a::text').getall():
                speakers.write(reporter+'\n')
        return None