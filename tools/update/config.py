import json

IMAGE = "image"
TAG = "tag"


class Configuration:

    def __init__(self):
        with open("config.json") as file:
            data = json.load(file)
            self.version = data["version"]
            self.availability_api_endpoint = data["availability_api_uri_endpoint"]
            self.release_uri = data["release_uri"]
            self.check_time = data["check_time"]
            self.docker_images = data["docker_images"]






